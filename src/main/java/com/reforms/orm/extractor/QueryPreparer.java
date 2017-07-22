package com.reforms.orm.extractor;

import com.reforms.ann.ThreadSafe;
import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.dao.IParamNameConverter;
import com.reforms.orm.dao.IPriorityValues;
import com.reforms.orm.dao.PriorityValues;
import com.reforms.orm.dao.batch.Batcher;
import com.reforms.orm.dao.batch.IBatcher;
import com.reforms.orm.dao.bobj.update.IInsertValues;
import com.reforms.orm.dao.bobj.update.IUpdateValues;
import com.reforms.orm.dao.column.ColumnAlias;
import com.reforms.orm.dao.column.ColumnAliasParser;
import com.reforms.orm.dao.filter.CallableValueSetter;
import com.reforms.orm.dao.filter.IFilterValues;
import com.reforms.orm.dao.filter.IPsValuesSetter;
import com.reforms.orm.dao.filter.PsValuesSetter;
import com.reforms.orm.dao.filter.param.ParamSetterFactory;
import com.reforms.orm.dao.paging.IPageFilter;
import com.reforms.orm.tree.QueryTree;
import com.reforms.sql.expr.query.*;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.value.FilterExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;
import com.reforms.sql.expr.term.value.QuestionExpression;
import com.reforms.sql.expr.term.value.ValueExpression;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import static com.reforms.orm.OrmConfigurator.getInstance;
import static com.reforms.orm.dao.IPriorityValues.*;
import static com.reforms.orm.dao.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.sql.expr.term.ExpressionType.ET_SET_CLAUSE_EXPRESSION;
import static com.reforms.sql.expr.term.ExpressionType.ET_VALUES_EXPRESSION;
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
     * @param selectQuery SELECT запрос
     * @param filters     фильтры
     * @return объект для установки значений в PS
     */
    public IPsValuesSetter prepareSelectQuery(SelectQuery selectQuery, IFilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        // TODO: порядок важен.
        IPageFilter newPageFilter = preparePage(selectQuery, filters);
        prepareScheme(selectQuery);
        return prepareValues(selectQuery, filters, newPageFilter);
    }

    /**
     * Подготовка хранимой процедуры
     * @param callQuery   call запрос
     * @param filters     фильтры
     * @return объект для установки значений в PS
     */
    public CallableValueSetter prepareCallQuery(CallQuery callQuery, IFilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        prepareScheme(callQuery);
        // Если выборка типа курсор, нужно в хранимку добавить результат выбора ?
        if (callQuery.getValuesExpr() != null) {
            QuestionExpression questionExpr = new QuestionExpression();
            callQuery.setQuestionExpr(questionExpr);
        }
        callQuery.setJdbcView(true);
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        CallableValueSetter fpss = new CallableValueSetter(callQuery.hasReturnType(), paramSetterFactory);
        prepareValues(callQuery.getFuncExpr(), filters, null, fpss);
        return fpss;
    }

    public IPsValuesSetter prepareUpdateQuery(UpdateQuery updateQuery, IUpdateValues updateValues, IFilterValues filters) {
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

    public IBatcher prepareUpdateQueryWithBatch(UpdateQuery updateQuery, IUpdateValues updateValues) {
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        Batcher batcher = new Batcher(updateValues, new PsValuesSetter(paramSetterFactory));
        prepareScheme(updateQuery);
        prepareValues(updateQuery, batcher, null, batcher);
        return batcher;
    }

    public IBatcher prepareInsertQueryWithBatch(InsertQuery insertQuery, IInsertValues insertValues) {
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        Batcher batcher = new Batcher(insertValues, new PsValuesSetter(paramSetterFactory));
        prepareScheme(insertQuery);
        prepareValues(insertQuery, batcher, null, batcher);
        return batcher;
    }

    public IPsValuesSetter prepareDeleteQuery(DeleteQuery deleteQuery, IFilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        // TODO: порядок важен.
        prepareScheme(deleteQuery);
        return prepareValues(deleteQuery, filters);
    }

    public IPsValuesSetter prepareInsertQuery(InsertQuery insertQuery, IInsertValues values) {
        // TODO: порядок важен.
        prepareScheme(insertQuery);
        return prepareValues(insertQuery, values);
    }

    private IPageFilter preparePage(SelectQuery selectQuery, IFilterValues filters) {
        IPageFilter pageFilter = filters.getPageFilter();
        if (pageFilter != null && pageFilter.hasPageFilter()) {
            PageModifier pageModifer = OrmConfigurator.getInstance(PageModifier.class);
            return pageModifer.changeSelectQuery(selectQuery, pageFilter);
        }
        return null;
    }

    private void prepareScheme(Expression query) {
        SchemaPreparer schemaPreparer = getInstance(SchemaPreparer.class);
        ExpressionScanner scanner = new ExpressionScanner();
        scanner.scan(query, schemaPreparer);
    }

    private IPsValuesSetter prepareValues(Expression query, IPriorityValues values) {
        return prepareValues(query, values, null);
    }

    private IPsValuesSetter prepareValues(Expression query, IPriorityValues values, IPageFilter pageFilter) {
        ParamSetterFactory paramSetterFactory = getInstance(ParamSetterFactory.class);
        IPsValuesSetter fpss = new PsValuesSetter(paramSetterFactory);
        prepareValues(query, values, pageFilter, fpss);
        return fpss;
    }

    private void prepareValues(Expression query, IPriorityValues values, IPageFilter pageFilter, IPsValuesSetter fpss) {
        ValueExpressionExtractor filterExprExtractor = new ValueExpressionExtractor();
        List<ValueExpression> filterExprs = filterExprExtractor.extractFilterExpressions(query);
        if (filterExprs.isEmpty()) {
            return;
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
                    int paramNameType = values.getParamNameType(priority);
                    String preapredName = paramNameConverter.convertName(paramNameType, filterName);
                    Object filterValue = values.getPriorityValue(priority, preapredName);
                    boolean nullNotAllowed = filterValue == null && !(PV_UPDATE == priority || PV_INSERT == priority);
                    if (nullNotAllowed && filterExpr.isStaticFilter() && !filterExpr.isQuestionFlag()) {
                        throw new IllegalStateException("Не возможно установить фильтр '" + filterName + "' для null значения");
                    }
                    // Статический фильтр с null значением
                    if (nullNotAllowed && filterExpr.isStaticFilter() && filterExpr.isQuestionFlag()) {
                        predicateModifier.changeStaticFilter(filterExpr);
                    } else
                        // Динамический фильтр
                        if (isEmptyValue(filterValue) && filterExpr.isDynamicFilter()) {
                            predicateModifier.changeDynamicFilter(filterExpr);
                        } else {
                            int newParamCount = fpss.addFilterValue(null, filterValue);
                            if (!nullNotAllowed) {
                                filterExpr.setPsQuestionCount(newParamCount);
                            }
                        }
                } else {
                    String shortFilterName = filterDetails.getJavaAliasKey();
                    int paramNameType = values.getParamNameType(priority);
                    String preapredName = paramNameConverter.convertName(paramNameType, shortFilterName);
                    Object filterValue = values.getPriorityValue(priority, preapredName);
                    boolean nullNotAllowed = filterValue == null && !(PV_UPDATE == priority || PV_INSERT == priority);
                    if (nullNotAllowed) {
                        filterValue = values.getPriorityValue(priority, filterName);
                    }
                    if (nullNotAllowed && filterExpr.isStaticFilter() && !filterExpr.isQuestionFlag()) {
                        throw new IllegalStateException("Не возможно установить фильтр '" + filterName + "' для null значения");
                    }
                    // Статический фильтр с null значением
                    if (nullNotAllowed && filterExpr.isStaticFilter() && filterExpr.isQuestionFlag()) {
                        predicateModifier.changeStaticFilter(filterExpr);
                    } else
                        // Динамический фильтр
                        if (isEmptyValue(filterValue) && filterExpr.isDynamicFilter()) {
                            predicateModifier.changeDynamicFilter(filterExpr);
                        } else {
                            int newParamCount = fpss.addFilterValue(filterDetails.getAliasPrefix(), filterValue);
                            if (!nullNotAllowed) {
                                filterExpr.setPsQuestionCount(newParamCount);
                            }
                        }
                }
            } else if (VET_QUESTION == valueFilterExpr.getValueExprType()) {
                Object filterValue = values.getPriorityValue(priority, ++questionCount);
                boolean nullNotAllowed = filterValue == null && !(PV_UPDATE == priority || PV_INSERT == priority);
                if (nullNotAllowed) {
                    throw new IllegalStateException("Значение null недопустимо для 'QuestionExpression'");
                }
                fpss.addFilterValue(null, filterValue);
            } else if (VET_PAGE_QUESTION == valueFilterExpr.getValueExprType()) {
                Object filterValue = null;
                if (pageFilter != null) {
                    PageQuestionExpression pageQuestionExpr = (PageQuestionExpression) valueFilterExpr;
                    if (pageQuestionExpr.isLimitType()) {
                        filterValue = pageFilter.getPageLimit();
                    }
                    if (pageQuestionExpr.isOffsetType()) {
                        filterValue = pageFilter.getPageOffset();
                    }
                }
                if (filterValue == null) {
                    throw new IllegalStateException("Значение null недопустимо для 'PageQuestionExpression'");
                }
                fpss.addFilterValue(null, filterValue);
            } else {
                throw new IllegalStateException("Не возможно установить фильтр для типа '" + valueFilterExpr.getValueExprType() + "'");
            }
        }
    }

    private int getPriorType(Expression expr, QueryTree queryTree) {
        Expression parentExpr = queryTree.getParentExpressionFor(expr);
        if (parentExpr != null) {
            if (ET_SET_CLAUSE_EXPRESSION == parentExpr.getType()) {
                return PV_UPDATE;
            }
            // TODO проверить.
            Expression parentOfParentExpr = queryTree.getParentExpressionFor(parentExpr);
            if (parentOfParentExpr != null) {
                if (ET_SET_CLAUSE_EXPRESSION == parentOfParentExpr.getType()) {
                    return PV_UPDATE;
                }
                if (ET_VALUES_EXPRESSION == parentOfParentExpr.getType()) {
                    return PV_INSERT;
                }
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
