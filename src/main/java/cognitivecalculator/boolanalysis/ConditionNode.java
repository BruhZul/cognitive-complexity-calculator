package cognitivecalculator.boolanalysis;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;

public class ConditionNode {
    private Expression expression;
    private BinaryExpr.Operator operator;
    private int complexity;
    private ConditionNode leftSon;
    private ConditionNode rightSon;

    public ConditionNode(Expression expression, BinaryExpr.Operator operator, int complexity,
                         ConditionNode leftSon, ConditionNode rightSon) {
        this.expression = expression;
        this.operator = operator;
        this.complexity = complexity;
        this.leftSon = leftSon;
        this.rightSon = rightSon;
    }

    public Expression getExpression() {
        return expression;
    }

    public BinaryExpr.Operator getOperator() {
        return operator;
    }

    public int getComplexity() {
        return complexity;
    }

    public ConditionNode getLeftSon() {
        return leftSon;
    }

    public ConditionNode getRightSon() {
        return rightSon;
    }
}
