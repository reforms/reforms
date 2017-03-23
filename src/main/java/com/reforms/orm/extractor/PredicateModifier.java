package com.reforms.orm.extractor;

import com.reforms.orm.tree.QueryTree;
import com.reforms.sql.expr.statement.WhereStatement;
import com.reforms.sql.expr.term.ArgListExpression;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.SearchGroupExpression;
import com.reforms.sql.expr.term.casee.CaseExpression;
import com.reforms.sql.expr.term.casee.ElseExpression;
import com.reforms.sql.expr.term.casee.WhenThenExpression;
import com.reforms.sql.expr.term.predicate.BetweenPredicateExpression;
import com.reforms.sql.expr.term.predicate.ComparisonOperator;
import com.reforms.sql.expr.term.predicate.ComparisonPredicateExpression;
import com.reforms.sql.expr.term.predicate.NullablePredicateExpression;

import java.util.List;

import static com.reforms.sql.expr.term.ExpressionType.*;
import static com.reforms.sql.expr.term.predicate.ComparisonOperator.*;
import static com.reforms.sql.parser.SqlWords.SW_NOT;

/**
 * TODO доработка - проверить математические операции
 * @author evgenie
 */
public class PredicateModifier {

    private QueryTree queryTree;

    public PredicateModifier(QueryTree queryTree) {
        this.queryTree = queryTree;
    }

    /**
     * TODO доработка - Будет больше if условий, не только для типа ET_COMPARISON_PREDICATE_EXPRESSION
     * Пример: было 'id = :id_filter?' стало при id_filter = null -> 'id IS NULL'
     * @param filterExpr
     */
    public void changeStaticFilter(Expression filterExpr) {
        Expression predicateExpr = queryTree.getParentExpressionFor(filterExpr);
        if (predicateExpr == null) {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' на NullablePredicate - не найден владелец");
        }
        if (ET_COMPARISON_PREDICATE_EXPRESSION == predicateExpr.getType()) {
            ComparisonPredicateExpression compExpr = (ComparisonPredicateExpression) predicateExpr;
            Expression baseExpr = getBaseExpressionFromCompExpr(filterExpr, compExpr);
            ComparisonOperator cmpType = compExpr.getCompOperatorType();
            if (COT_EQUALS != cmpType && COT_NOT_EQUALS != cmpType && COT_JAVA_NOT_EQUALS != cmpType) {
                throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr
                        + "' на NullablePredicate - неподдерживаемый тип операции '" + cmpType + "'");
            }
            NullablePredicateExpression nullablePredicateExpr = new NullablePredicateExpression();
            nullablePredicateExpr.setExpression(baseExpr);
            if (COT_EQUALS != cmpType) {
                nullablePredicateExpr.setNotWord(SW_NOT);
            }
            Expression predicateOwnerExpr = queryTree.getParentExpressionFor(predicateExpr);
            if (predicateOwnerExpr == null) {
                throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr
                        + "' на NullablePredicate - не найден владелец для '" + predicateExpr + "'");
            }
            predicateOwnerExpr.changeExpression(predicateExpr, nullablePredicateExpr);
        } else {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr
                    + "' на NullablePredicate - неизвестный предикат '" + predicateExpr + "'");
        }
    }

    private Expression getBaseExpressionFromCompExpr(Expression filterExpr, ComparisonPredicateExpression compExpr) {
        if (compExpr.getLeftExpr() == filterExpr) {
            return compExpr.getRightExpr();
        }
        if (compExpr.getRightExpr() == filterExpr) {
            return compExpr.getLeftExpr();
        }
        throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' на NullablePredicate - не найден сам фильтр");
    }

    public void changeDynamicFilter(Expression filterExpr) {
        Expression predicateExpr = queryTree.getParentExpressionFor(filterExpr);
        if (predicateExpr == null) {
            throw new IllegalStateException("Не возможно удалить фильтр '" + filterExpr + "' - не найден владелец");
        }
        if (ET_COMPARISON_PREDICATE_EXPRESSION == predicateExpr.getType()) {
            changePredicateExpr(filterExpr, predicateExpr);
            return;
        }
        if (ET_ARG_LIST_EXPRESSION == predicateExpr.getType()) {
            Expression parentValueListExpr = queryTree.getParentExpressionFor(predicateExpr);
            if (parentValueListExpr != null) {
                if (ET_IN_PREDICATE_EXPRESSION == parentValueListExpr.getType()) {
                    changeInPredicate(filterExpr, (ArgListExpression) predicateExpr, parentValueListExpr);
                    return;
                }
                if (ET_COMPARISON_PREDICATE_EXPRESSION == parentValueListExpr.getType()) {
                    changeComparisonValuesPredicate(filterExpr, (ArgListExpression) predicateExpr,
                            (ComparisonPredicateExpression) parentValueListExpr);
                    return;
                }
                if (ET_FUNC_EXPRESSION == parentValueListExpr.getType()) {
                    changeDynamicFilter(parentValueListExpr);
                    return;
                }
            }
        }
        if (ET_LIKE_PREDICATE_EXPRESSION == predicateExpr.getType()) {
            changeLikePredicateExpr(filterExpr, predicateExpr);
            return;
        }
        if (ET_BETWEEN_PREDICATE_EXPRESSION == predicateExpr.getType()) {
            changeBetweenPredicateExpr(filterExpr, (BetweenPredicateExpression) predicateExpr);
            return;
        }
        if (ET_ARG_EXPRESSION == predicateExpr.getType()) {
            changeDynamicFilter(predicateExpr);
            return;
        }
        if (ET_FUNC_EXPRESSION == predicateExpr.getType()) {
            changeDynamicFilter(predicateExpr);
            return;
        }
        if (ET_NULLABLE_PREDICATE_EXPRESSION == predicateExpr.getType()) {
            changeDynamicFilter(predicateExpr);
            return;
        }
        if (ET_SEARCH_GROUP_EXPRESSION == predicateExpr.getType()) {
            changeSearchGroup(filterExpr, (SearchGroupExpression) predicateExpr);
            return;
        }
        if (ET_WHERE_STATEMENT == predicateExpr.getType()) {
            changeWhereStatement(filterExpr, (WhereStatement) predicateExpr);
            return;
        }
        if (ET_SEARCH_GROUP_EXPRESSION == predicateExpr.getType()) {
            changeSearchGroup(filterExpr, (SearchGroupExpression) predicateExpr);
            return;
        }
        if (ET_NOT_EXPRESSION == predicateExpr.getType()) {
            changeDynamicFilter(predicateExpr);
            return;
        }
        if (ET_WHEN_THEN_EXPRESSION == predicateExpr.getType()) {
            changeWhenThenExpr(filterExpr, (WhenThenExpression) predicateExpr);
            return;
        }

        throw new IllegalStateException("Не поддерживаемое выражение для изменения фильтра '" + filterExpr + "' в '" + predicateExpr
                + "' с типом '" + predicateExpr.getType() + "'");
    }

    private void changePredicateExpr(Expression filterExpr, Expression compExpr) {
        Expression parentCompExpr = queryTree.getParentExpressionFor(compExpr);
        if (parentCompExpr == null) {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' не найден владелец для '" + compExpr + "'");
        }
        parentCompExpr.changeExpression(compExpr, null);
        changeDynamicFilter(compExpr);
    }

    private void changeSearchGroup(Expression compExpr, SearchGroupExpression searchGroupExpr) {
        int compExprIndex = searchGroupExpr.getExprIndex(compExpr);
        if (compExprIndex == -1) {
            throw new IllegalStateException("Не возможно изменить фильтр - не найдено выражение '" + compExpr + "' в '"
                    + searchGroupExpr + "'");
        }
        int exprCount = searchGroupExpr.size();
        if (exprCount == 1) {
            searchGroupExpr.removeExpr(0);
            changeDynamicFilter(searchGroupExpr);
            return;
        }
        int condFlowExprIndex = compExprIndex - 1;
        if (condFlowExprIndex < 0) {
            searchGroupExpr.removeExpr(1);
            searchGroupExpr.removeExpr(0);
        } else {
            searchGroupExpr.removeExpr(compExprIndex);
            searchGroupExpr.removeExpr(condFlowExprIndex);
        }
    }

    private void changeWhenThenExpr(Expression compExpr, WhenThenExpression whenThenExpr) {
        CaseExpression caseExpr = (CaseExpression) queryTree.getParentExpressionFor(whenThenExpr);
        if (caseExpr == null) {
            throw new IllegalStateException("Не возможно изменить фильтр - не найдено выражение '" + compExpr + "' в '"
                    + whenThenExpr + "' - не найден предок");
        }
        List<WhenThenExpression> whenThenExprs = caseExpr.getWhenThenExprs();
        whenThenExprs.remove(whenThenExpr);
        if (whenThenExprs.isEmpty()) {
            if (caseExpr.hasElseExpr()) {
                Expression caseParentExpr = queryTree.getParentExpressionFor(caseExpr);
                ElseExpression elseExpr = caseExpr.getElseExpr();
                Expression resultExpr = elseExpr.getResultExpr();
                if (caseExpr.isWrapped()) {
                    resultExpr.setWrapped(true);
                }
                caseParentExpr.changeExpression(caseExpr, resultExpr);
            } else {
                changeDynamicFilter(caseExpr);
            }
        }
    }

    private void changeInPredicate(Expression filterExpr, ArgListExpression valueListExpr, Expression inPredicateExpr) {
        List<Expression> valueExprs = valueListExpr.getArgExprs();
        valueExprs.remove(filterExpr);
        if (valueExprs.isEmpty()) {
            changeDynamicFilter(inPredicateExpr);
        }
    }

    private void changeComparisonValuesPredicate(Expression filterExpr, ArgListExpression valueListExpr,
            ComparisonPredicateExpression compPredicateExpr) {
        ArgListExpression valueListColumnExpr = (ArgListExpression) getBaseExpressionFromCompExpr(valueListExpr, compPredicateExpr);
        List<Expression> columnNames = valueListColumnExpr.getArgExprs();
        List<Expression> columnValues = valueListExpr.getArgExprs();
        int columnValueIndex = columnValues.indexOf(filterExpr);
        if (columnValueIndex == -1) {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' не найдено выражение '" + filterExpr
                    + "' в '" + valueListExpr + "'");
        }
        if (columnNames.size() <= columnValueIndex) {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' индекс '" + columnValueIndex
                    + "' превышает количество выражений в списке '" + columnNames + "'");
        }
        columnValues.remove(columnValueIndex);
        columnNames.remove(columnValueIndex);
        if (columnNames.isEmpty()) {
            changeDynamicFilter(compPredicateExpr);
        }
    }

    private void changeLikePredicateExpr(Expression filterExpr, Expression likePredicateExpr) {
        changeDynamicFilter(likePredicateExpr);
    }

    private void changeBetweenPredicateExpr(Expression filterExpr, BetweenPredicateExpression betweenPredicateExpr) {
        Expression leftExpr = betweenPredicateExpr.getLeftExpression();
        Expression rightExpr = betweenPredicateExpr.getRightExpression();
        betweenPredicateExpr.changeExpression(filterExpr, null);
        if (leftExpr.isChangedExpression() && rightExpr.isChangedExpression()) {
            changeDynamicFilter(betweenPredicateExpr);
        }
    }

    private void changeWhereStatement(Expression filterExpr, WhereStatement whereExpr) {
        Expression parentWhereExpr = queryTree.getParentExpressionFor(whereExpr);
        if (parentWhereExpr == null) {
            throw new IllegalStateException("Не возможно изменить фильтр '" + filterExpr + "' не найден владелец для '" + whereExpr + "'");
        }
        parentWhereExpr.changeExpression(whereExpr, null);
    }
}

/** отступ  */
