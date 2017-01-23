package com.reforms.orm.tree;

import java.util.IdentityHashMap;

import com.reforms.sql.expr.query.SelectQuery;
import com.reforms.sql.expr.term.Expression;
import com.reforms.sql.expr.term.value.StringExpression;
import com.reforms.sql.expr.viewer.SqlBuilder;

/**
 * Дерево выражений
 * @author evgenie
 */
public class SelectQueryTree extends IdentityHashMap<Expression, ChildExpressions> {

    private StringExpression headExpr = new StringExpression("HEAD");

    private ParentExpressions parentExprs = new ParentExpressions();

    public StringExpression getHeadExpr() {
        return headExpr;
    }

    public void add(Expression childExpr, Expression parentExpr) {
        ChildExpressions childExprs = super.get(parentExpr);
        if (childExprs == null) {
            childExprs = new ChildExpressions();
            put(parentExpr, childExprs);
        }
        parentExprs.put(childExpr, parentExpr);
    }

    public Expression getParentExpressionFor(Expression childExpr) {
        return parentExprs.get(childExpr);
    }

    public static SelectQueryTree build(SelectQuery selectQuery) {
        final SelectQueryTree tree = new SelectQueryTree();
        final LocalStack stack = new LocalStack();
        stack.push(tree.getHeadExpr());
        selectQuery.view(new SqlBuilder() {

            @Override
            public SqlBuilder appendExpression(Expression childExpr) {
                Expression parentExpr = stack.peek();
                if (childExpr != null) {
                    tree.add(childExpr, parentExpr);
                    stack.push(childExpr);
                    super.appendExpression(childExpr);
                    stack.pop();
                }
                return this;
            }
        });
        return tree;
    }

}
