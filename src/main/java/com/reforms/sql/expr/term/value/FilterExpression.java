package com.reforms.sql.expr.term.value;

import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Фильтр значений, после знака равенства:
 * id = :id     - статический обязательный фильтр по id, null не допускается -> id = ?
 * id = :id?    - статический обязательный фильтр по id, null допускается    -> id IS NULL
 * id <> :id?   - статический обязательный фильтр по id, null допускается    -> id IS NOT NULL
 * id != :id?   - статический обязательный фильтр по id, null допускается    -> id IS NOT NULL
 * id = ::id    - динамический фильтр, может быть, а может и не быть.        -> id = ? или ничего
 * @author evgenie
 */
public class FilterExpression extends ValueExpression {

    private int colonCount;
    private String filterName;
    private boolean questionFlag;

    private int psQuestionCount;

    public FilterExpression(String filterValue) {
        super(filterValue, ValueExpressionType.VET_FILTER);
    }

    public int getColonCount() {
        return colonCount;
    }

    public void setColonCount(int colonCount) {
        this.colonCount = colonCount;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public boolean isQuestionFlag() {
        return questionFlag;
    }

    public void setQuestionFlag(boolean questionFlag) {
        this.questionFlag = questionFlag;
    }

    public int getPsQuestionCount() {
        return psQuestionCount;
    }

    public void setPsQuestionCount(int psQuestionCount) {
        this.psQuestionCount = psQuestionCount;
    }

    public boolean isStaticFilter() {
        return colonCount == 1;
    }

    public boolean isDynamicFilter() {
        return colonCount == 2;
    }

    @Override
    public void view(SqlBuilder sqlBuilder) {
        if (psQuestionCount == 1) {
            sqlBuilder.appendSpace();
            sqlBuilder.appendWord("?");
        } else if (psQuestionCount > 1) {
            sqlBuilder.appendWord("?");
            for (int index = 1; index < psQuestionCount; index++) {
                sqlBuilder.append(", ");
                sqlBuilder.appendWord("?");
            }
        } else {
            super.view(sqlBuilder);
        }
    }

}