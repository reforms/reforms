package com.reforms.orm.filter;

import static com.reforms.orm.filter.FilterMap.EMPTY_FILTER_MAP;
import static com.reforms.sql.expr.term.value.ValueExpressionType.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import com.reforms.orm.OrmConfigurator;
import com.reforms.orm.OrmContext;
import com.reforms.orm.extractor.FilterExpressionExtractor;
import com.reforms.orm.extractor.TableExpressionExtractor;
import com.reforms.orm.filter.modifier.PageModifier;
import com.reforms.orm.filter.modifier.PredicateModifier;
import com.reforms.orm.filter.param.ParamSetterFactory;
import com.reforms.orm.scheme.ISchemeManager;
import com.reforms.orm.select.ColumnAlias;
import com.reforms.orm.tree.SelectQueryTree;
import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.from.TableExpression;
import com.reforms.sql.expr.term.value.FilterExpression;
import com.reforms.sql.expr.term.value.PageQuestionExpression;
import com.reforms.sql.expr.term.value.ValueExpression;

/**
 * Подготовка SelectQuery к тому виду, в котором она будет отправлена в PrepareStatement
 * @author evgenie
 */
public class SelectQueryPreparer {

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
    public FilterPrepareStatementSetter prepare(SelectQuery selectQuery, FilterValues filters) {
        if (filters == null) {
            filters = EMPTY_FILTER_MAP;
        }
        // TODO: порядок важен.
        preparePage(selectQuery, filters);
        prepareScheme(selectQuery);
        return prepareFilters(selectQuery, filters);
    }

    private void preparePage(SelectQuery selectQuery, FilterValues filters) {
        if (filters.hasPageFilter()) {
            PageModifier pageModifer = new PageModifier();
            pageModifer.changeSelectQuery(selectQuery, filters);
        }
    }

    private void prepareScheme(SelectQuery selectQuery) {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        TableExpressionExtractor tableExprExtractor = new TableExpressionExtractor();
        ISchemeManager schemeManager = rCtx.getSchemeManager();
        for (TableExpression tableExpr : tableExprExtractor.extractFilterExpressions(selectQuery)) {
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

    private FilterPrepareStatementSetter prepareFilters(SelectQuery selectQuery, FilterValues filters) {
        OrmContext rCtx = OrmConfigurator.get(OrmContext.class);
        ParamSetterFactory paramSetterFactory = rCtx.getParamSetterFactory();
        FilterPrepareStatementSetter fpss = new FilterPrepareStatementSetter(paramSetterFactory);
        FilterExpressionExtractor filterExprExtractor = new FilterExpressionExtractor();
        List<ValueExpression> filterExprs = filterExprExtractor.extractFilterExpressions(selectQuery);
        if (filterExprs.isEmpty()) {
            return fpss;
        }
        // Нумерация с 1цы
        int questionCount = 0;
        SelectQueryTree queryTree = SelectQueryTree.build(selectQuery);
        PredicateModifier predicateModifier = new PredicateModifier(queryTree);
        FilterValueParser filterValueParser = new FilterValueParser();
        for (ValueExpression valueFilterExpr : filterExprs) {
            if (VET_FILTER == valueFilterExpr.getValueExprType()) {
                FilterExpression filterExpr = (FilterExpression) valueFilterExpr;
                String filterName = filterExpr.getFilterName();
                ColumnAlias filterDetails = filterValueParser.parseFilterValue(filterName);
                if (filterDetails == null || filterDetails.getAliasType() == null) {
                    Object filterValue = filters.get(filterName);
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
                    String shortFilterName = filterDetails.getAliasKey();
                    Object filterValue = filters.get(shortFilterName);
                    if (filterValue == null) {
                        filterValue = filters.get(filterName);
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
                Object filterValue = filters.get(++questionCount);
                if (filterValue == null) {
                    throw new IllegalStateException("Значение null недопустимо для 'QuestionExpression'");
                }
                fpss.addFilterValue(filterValue);
            } else if (VET_PAGE_QUESTION == valueFilterExpr.getValueExprType()) {
                Object filterValue = null;
                PageQuestionExpression pageQuestionExpr = (PageQuestionExpression) valueFilterExpr;
                if (pageQuestionExpr.isLimitType()) {
                    filterValue = filters.getPageLimit();
                }
                if (pageQuestionExpr.isOffsetType()) {
                    filterValue = filters.getPageOffset();
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
