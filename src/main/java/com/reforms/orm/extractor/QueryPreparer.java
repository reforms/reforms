package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.IParamNameConverter;
import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.PriorityValues;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.ColumnAliasParser;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.PrepareStatementValuesSetter;
import com.reforms.orm.dao.filter.page.IPageFilter;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.tree.QueryTree;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.query.UpdateQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.term.value.FilterExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;
import com.reforms.sql.expr.term.value.ValueExpression;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.IPriorityValues.PV_FILTER;
import static com.reforms.orm.dao.IPriorityValues.PV_UPDATE;
import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.sql.expr.term.ExpressionType.ET_SET_CLAUSE_EXPRESSION;
import static com.reforms.sql.expr.term.value.ValueExpressionType.*;

/**
 * Подготовка Query к тому виду, в котором она будет отправлена в PrepareStatement
 * @author evgenie
 */
@ThreadSafe
public class QueryPreparer {

    public QueryPreparer() {
    }

    /**
     * TODO подумать.
     * Примечание: В результате работы экземпляр selectQuery будет изменен!!!
     *             Это не очень хорошо, я подумаю над альтернативным решением
     * TODO оптимизация - требуется оптимизация по работе с деревом выражений, например,
     *                    когда отсутствуют динамические фильтры и nullable-статические
     * TODO рефакторинг - сложно написано
     * @param selectQuery
     * @param filters
     * @return
     */
    public PrepareStatementValuesSetter prepareSelectQuery(SelectQuery selectQuery, IFilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        // TODO: порядок важен.
        preparePage(selectQuery, filters);
        prepareScheme(selectQuery);
        return prepareValues(selectQuery, filters);
    }

    public PrepareStatementValuesSetter prepareUpdateQuery(UpdateQuery updateQuery, IUpdateValues updateValues, IFilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        prepareScheme(updateQuery);
        IPriorityValues priorValues = null;
        if (updateValues.isEmpty()) {
            priorValues = filters;
        } else if (filters.isEmpty()) {
            priorValues = updateValues;
        } else {
            priorValues = new PriorityValues(PV_UPDATE, updateValues, filters);
        }
        return prepareValues(updateQuery, priorValues);
    }

    private void preparePage(SelectQuery selectQuery, IFilterValues filters) {
        IPageFilter pageFilter = filters.getPageFilter();
        if (pageFilter != null && pageFilter.hasPageFilter()) {
            PageModifier pageModifer = OrmConfigurator.getInstance(PageModifier.class);
            pageModifer.changeSelectQuery(selectQuery, pageFilter);
        }
    }

    private void prepareScheme(Expression query) {
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        ISchemeManager schemeManager = getInstance(ISchemeManager.class);
        for (TableExpression tableExpr : tableExprExtractor.extractTableExpressions(query)) {
            if (tableExpr.hasSchemeName()) {
                String schemeKey = tableExpr.getSchemeName();
                String originScheme = schemeManager.getSchemeName(schemeKey);
                if (originScheme != null) {
                    tableExpr.setSchemeName(originScheme);
                }
            } else if (schemeManager.getDefaultSchemeName() != null) {
                tableExpr.setSchemeName(schemeManager.getDefaultSchemeName());
            }
        }
    }

    /**
     * @param query
     * @param values
     * @return
     */
    private PrepareStatementValuesSetter prepareValues(Expression query, IPriorityValues values) {
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        PrepareStatementValuesSetter fpss = new PrepareStatementValuesSetter(paramSetterFactory);
        ValueExpressionExtractor filterExprExtractor = new ValueExpressionExtractor();
        List<ValueExpression> filterExprs = filterExprExtractor.extractFilterExpressions(query);
        if (filterExprs.isEmpty()) {
            return fpss;
        }
        // Нумерация с 1цы
        int questionCount = 0;
        QueryTree queryTree = QueryTree.build(query);
        PredicateModifier predicateModifier = new PredicateModifier(queryTree);
        ColumnAliasParser filterValueParser = getInstance(ColumnAliasParser.class);
        IParamNameConverter paramNameConverter = getInstance(IParamNameConverter.class);
        for (ValueExpression valueFilterExpr : filterExprs) {
            int priority = getPriorType(valueFilterExpr, queryTree);
            if (VET_FILTER == valueFilterExpr.getValueExprType()) {
                FilterExpression filterExpr = (FilterExpression) valueFilterExpr;
                String filterName = filterExpr.getFilterName();
                ColumnAlias filterDetails = filterValueParser.parseColumnAlias(filterName);
                if (filterDetails == null || filterDetails.getAliasType() == null) {
                    Object filterValue = values.getPriorityValue(priority, filterName);
                    if (filterValue == null && filterExpr.isStaticFilter() && !filterExpr.isQuestionFlag()) {
                        throw new IllegalStateException("Не возможно установить фильтр '" + filterName + "' для null значения");
                    }
                    // Статический фильтр с null значением
                    if (filterValue == null && filterExpr.isStaticFilter() && filterExpr.isQuestionFlag()) {
                        predicateModifier.changeStaticFilter(filterExpr);
                    } else
                    // Динамический фильтр
                    if (isEmptyValue(filterValue) && filterExpr.isDynamicFilter()) {
                        predicateModifier.changeDynamicFilter(filterExpr);
                    } else {
                        int newParamCount = fpss.addFilterValue(filterValue);
                        if (filterValue != null) {
                            filterExpr.setPsQuestionCount(newParamCount);
                        }
                    }
                } else {
                    String shortFilterName = filterDetails.getJavaAliasKey();
                    int paramNameType = values.getParamNameType(priority);
                    String preapredName = paramNameConverter.convertName(paramNameType, shortFilterName);
                    Object filterValue = values.getPriorityValue(priority, preapredName);
                    if (filterValue == null) {
                        filterValue = values.getPriorityValue(priority, filterName);
                    }
                    if (filterValue == null && filterExpr.isStaticFilter() && !filterExpr.isQuestionFlag()) {
                        throw new IllegalStateException("Не возможно установить фильтр '" + filterName + "' для null значения");
                    }
                    // Статический фильтр с null значением
                    if (filterValue == null && filterExpr.isStaticFilter() && filterExpr.isQuestionFlag()) {
                        predicateModifier.changeStaticFilter(filterExpr);
                    } else
                    // Динамический фильтр
                    if (isEmptyValue(filterValue) && filterExpr.isDynamicFilter()) {
                        predicateModifier.changeDynamicFilter(filterExpr);
                    } else {
                        int newParamCount = fpss.addFilterValue(filterDetails.getAliasPrefix(), filterValue);
                        if (filterValue != null) {
                            filterExpr.setPsQuestionCount(newParamCount);
                        }
                    }
                }
            } else if (VET_QUESTION == valueFilterExpr.getValueExprType()) {
                Object filterValue = values.getPriorityValue(priority, ++questionCount);
                if (filterValue == null) {
                    throw new IllegalStateException("Значение null недопустимо для 'QuestionExpression'");
                }
                fpss.addFilterValue(filterValue);
            } else if (VET_PAGE_QUESTION == valueFilterExpr.getValueExprType()) {
                Object filterValue = null;
                if (values instanceof IFilterValues) {
                    IFilterValues filters = (IFilterValues) values;
                    IPageFilter pageFilter = filters.getPageFilter();
                    if (pageFilter != null) {
                        PageQuestionExpression pageQuestionExpr = (PageQuestionExpression) valueFilterExpr;
                        if (pageQuestionExpr.isLimitType()) {
                            filterValue = pageFilter.getPageLimit();
                        }
                        if (pageQuestionExpr.isOffsetType()) {
                            filterValue = pageFilter.getPageOffset();
                        }
                    }
                }
                if (filterValue == null) {
                    throw new IllegalStateException("Значение null недопустимо для 'PageQuestionExpression'");
                }
                fpss.addFilterValue(filterValue);
            } else {
                throw new IllegalStateException("Не возможно установить фильтр для типа '" + valueFilterExpr.getValueExprType() + "'");
            }
        }
        return fpss;
    }

    private int getPriorType(Expression expr, QueryTree queryTree) {
        Expression parentExpr = queryTree.getParentExpressionFor(expr);
        if (parentExpr != null) {
            if (ET_SET_CLAUSE_EXPRESSION == parentExpr.getType()) {
                return PV_UPDATE;
            }
            // TODO проверить.
            Expression parentOfParentExpr = queryTree.getParentExpressionFor(parentExpr);
            if (parentOfParentExpr != null && ET_SET_CLAUSE_EXPRESSION == parentOfParentExpr.getType()) {
                return PV_UPDATE;
            }
        }
        return PV_FILTER;
    }

    private boolean isEmptyValue(Object filterValue) {
        if (filterValue == null) {
            return true;
        }
        if (filterValue instanceof Collection<?>) {
            return ((Collection<?>) filterValue).isEmpty();
        }
        if (filterValue instanceof Iterable<?>) {
            return !((Iterable<?>) filterValue).iterator().hasNext();
        }
        if (filterValue.getClass().isArray()) {
            return Array.getLength(filterValue) == 0;
        }
        return false;
    }

}
