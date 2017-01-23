package com.reforms.sql.parser;

import com.reforms.sql.expr.term.*;

import java.util.ArrayList;

class ParenLevel extends ArrayList<Object> {

    private boolean useParen;

    ParenLevel(boolean useParen) {
        this.useParen = useParen;
    }

    Expression combine(boolean needWrap) {
        return combineFrom(needWrap, 0);
    }

    Expression combineFrom(boolean needWrap, int fromIndex) {
        Expression expr = combineExprFrom(needWrap, fromIndex);
        clearFrom(fromIndex);
        return expr;
    }

    private void clearFrom(int fromIndex) {
        if (fromIndex == 0) {
            clear();
        } else {
            for (int index = size() - 1; index >= fromIndex; index--) {
                remove(index);
            }
        }
    }

    private Expression combineExprFrom(boolean needWrap, int fromIndex) {
        int removeIndex = -1;
        for (int index = fromIndex; index < size(); index++) {
            if (isNotExprEmpty(index)) {
                NotExpression notExpression = castToNotExpression(index);
                Expression primaryExpr = castToExpression(index + 1);
                notExpression.setPrimaryExpr(primaryExpr);
                removeIndex = index + 1;
                break;
            }
        }
        if (removeIndex != -1) {
            remove(removeIndex);
        }
        return _combineExprFrom(needWrap, fromIndex);
    }

    private Expression _combineExprFrom(boolean needWrap, int fromIndex) {
        if (isCondition(fromIndex)) {
            return combineCondition(needWrap, fromIndex);
        }
        if (isMath(fromIndex)) {
            return combineMath(needWrap, fromIndex);
        }
        if (size() == fromIndex + 1) {
            Expression expr = castToExpression(fromIndex);
            if (needWrap) {
                expr.setWrapped(needWrap);
            }
            return expr;
        }
        throw makeException("Не является допустимым типом", null);
    }

    private Expression combineCondition(boolean needWrap, int fromIndex) {
        /** OLD VARIANT
        Expression combineSearchExprs = castToExpression(fromIndex);
        for (int index = fromIndex + 1; index < size(); index += 2) {
            ConditionFlowType conditionFlowType = castToCondition(index);
            Expression rightExpr = castToExpression(index + 1);
            SearchConditionFlowExpression searchExpr = new SearchConditionFlowExpression();
            searchExpr.setLeftExpr(combineSearchExprs);
            ConditionFlowTypeExpression conditionFlowTypeExpr = new ConditionFlowTypeExpression(conditionFlowType);
            searchExpr.setConditionFlowTypeExpr(conditionFlowTypeExpr);
            searchExpr.setRightExpr(rightExpr);
            combineSearchExprs = searchExpr;
        } */
        Expression combineSearchExprs = castToExpression(fromIndex);
        SearchGroupExpression groupExprs = null;
        for (int index = fromIndex + 1; index < size(); index += 2) {
            if (groupExprs == null) {
                groupExprs = new SearchGroupExpression();
                groupExprs.add(combineSearchExprs);
                combineSearchExprs = groupExprs;
            }
            ConditionFlowType conditionFlowType = castToCondition(index);
            ConditionFlowTypeExpression conditionFlowTypeExpr = new ConditionFlowTypeExpression(conditionFlowType);
            Expression rightExpr = castToExpression(index + 1);
            groupExprs.add(conditionFlowTypeExpr);
            groupExprs.add(rightExpr);
        }
        if (needWrap) {
            combineSearchExprs.setWrapped(needWrap);
        }
        return combineSearchExprs;
    }

    private Expression combineMath(boolean needWrap, int fromIndex) {
        Expression mathExprs = castToExpression(fromIndex);
        for (int index = fromIndex + 1; index < size(); index += 2) {
            MathOperator mathOperator = castToOperator(index);
            Expression rightExpr = castToExpression(index + 1);
            MathExpression mathExpr = new MathExpression();
            mathExpr.setFirstExpr(mathExprs);
            mathExpr.setMathOperator(mathOperator);
            mathExpr.setSecondExpr(rightExpr);
            mathExprs = mathExpr;
        }
        if (needWrap) {
            mathExprs.setWrapped(needWrap);
        }
        return mathExprs;
    }

    boolean isUseParen() {
        return useParen;
    }

    private boolean isCondition(int index) {
        return size() > index + 1 && get(index + 1) instanceof ConditionFlowType;
    }

    private boolean isMath(int index) {
        return size() > index + 1 && get(index + 1) instanceof MathOperator;
    }

    boolean isNotExprEmpty(int index) {
        int offset = 1;
        if (size() > index + offset && get(index) instanceof NotExpression) {
            NotExpression expr = (NotExpression) get(index);
            if (expr.getPrimaryExpr() == null) {
                return true;
            }
        }
        return false;
    }

    private Expression castToExpression(int index) {
        Object exprValue = get(index);
        if (!(exprValue instanceof Expression)) {
            throw makeException("Не является выражением Expression", exprValue);
        }
        return (Expression) exprValue;
    }

    private NotExpression castToNotExpression(int index) {
        Object exprValue = get(index);
        if (!(exprValue instanceof NotExpression)) {
            throw makeException("Не является выражением NotExpresssion", exprValue);
        }
        return (NotExpression) exprValue;
    }

    private ConditionFlowType castToCondition(int index) {
        Object condValue = get(index);
        if (!(condValue instanceof ConditionFlowType)) {
            throw makeException("Не является ConditionFlowType", condValue);
        }
        return (ConditionFlowType) condValue;
    }

    private MathOperator castToOperator(int index) {
        Object mathOperatorValue = get(index);
        if (!(mathOperatorValue instanceof MathOperator)) {
            throw makeException("Не является оператором MathOperator", mathOperatorValue);
        }
        return (MathOperator) mathOperatorValue;
    }

    private IllegalStateException makeException(String message, Object value) {
        throw new IllegalStateException(message + ". " + (value != null ? value.getClass().getSimpleName() : "null"));
    }
}
