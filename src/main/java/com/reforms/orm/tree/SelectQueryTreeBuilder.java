package com.reforms.orm.tree;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.viewer.SqlBuilder;

public class SelectQueryTreeBuilder extends SqlBuilder {

    private QueryTree tree = new QueryTree();
    private LocalStack stack;

    public QueryTree buildTree(SelectQuery selectQuery) {
        stack = new LocalStack();
        stack.push(tree.getHeadExpr());
        selectQuery.view(this);
        return tree;
    }

    @Override
    public SqlBuilder appendExpression(Expression childExpr) {
        Expression parentExpr = stack.peek();
        tree.add(childExpr, parentExpr);
        stack.push(childExpr);
        SqlBuilder builder = super.appendExpression(childExpr);
        stack.pop();
        return builder;
    }

}
