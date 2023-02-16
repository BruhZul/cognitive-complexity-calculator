package cognitivecalculator.boolanalysis;

import cognitivecalculator.resultdatastr.MethodResult;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import cognitivecalculator.analizers.ExpressionAnalizer;
import cognitivecalculator.enums.BinaryExpression;

public class BinaryExprAnalizer {
    private BinaryExpr.Operator currentOperator;

    public BinaryExprAnalizer() {
        currentOperator = null;
    }

    public AnalysisResult calculateExpressionComplexity(Node expression, boolean discount, MethodResult methodResult) {
        AnalysisResult result = new AnalysisResult(0, false);
        ConditionNode tree = createNode(expression,result, methodResult);
        if(tree != null){
            int complexity = analizeTreeComplexity(tree, discount);
            result.setBooleanComplexityValue(complexity);
        }

        return result;
    }

    private ConditionNode createNode(Node expr, AnalysisResult res, MethodResult methodResult) {
        String expressionSimpleName = expr.getClass().getSimpleName();
        BinaryExpression type = BinaryExpression.getExpressionTypeFromString(expressionSimpleName);
        switch (type) {
            case BINARY_EXPR:
                return nodeForBinary(expr, res, methodResult);
            case UNARY_EXPR:
                return nodeForUnary(expr, res, methodResult);
            case ENCLOSED_EXPR:
                return nodeForEnclosed(expr, res, methodResult);
            case EXPRESSION_NOT_FOUND:
                if(expr instanceof MethodCallExpr || expr instanceof ConditionalExpr){
                    res.setMethodAnalysis(true);
                    new ExpressionAnalizer().analizeExpression(expr, methodResult);
                }
                return new ConditionNode((Expression)expr, null, -1, null, null);
                //return null; //alternative
        }
        return null;
    }

    private ConditionNode nodeForBinary(Node node, AnalysisResult res, MethodResult methodResult){
        BinaryExpr expr = (BinaryExpr)node;
        BinaryExpr.Operator operator = expr.getOperator();
        if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)
                || operator.equals(BinaryExpr.Operator.XOR)) {
            Expression left = expr.getLeft();
            Expression right = expr.getRight();
            return new ConditionNode(expr, operator, -1, createNode(left, res,  methodResult),
                    createNode(right, res, methodResult));
        }else{
            createNode(expr.getLeft(), res,  methodResult);
            createNode(expr.getRight(), res,  methodResult);
            return new ConditionNode(expr, null, -1, null, null);
        }
    }

    private ConditionNode nodeForUnary(Node node, AnalysisResult res, MethodResult methodResult){
        UnaryExpr expr = (UnaryExpr)node;
        UnaryExpr.Operator operator = expr.getOperator();
        if(operator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)){
            AnalysisResult complexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr.getExpression(),
                    true,  methodResult);
            if(complexity.isMethodAnalysis()){
                res.setMethodAnalysis(true);
            }
            return new ConditionNode(expr, null, complexity.getBooleanComplexityValue(), null, null);
        }else{
            createNode(expr.getExpression(), res,  methodResult);
            return new ConditionNode(expr, null, -1, null, null);
        }
    }

    private ConditionNode nodeForEnclosed(Node node, AnalysisResult res, MethodResult methodResult){
        EnclosedExpr expr = (EnclosedExpr)node;
        return new ConditionNode(expr, null, -1, createNode(expr.getInner(), res,
                methodResult), null);
    }

    private int analizeTreeComplexity(ConditionNode tree, boolean discount){
        int result = 0;
        int nodeComplexity = tree.getComplexity();
        if(nodeComplexity != -1){   //means it is a not expression that has been already analyzed
            return nodeComplexity;
        }

        if(tree.getLeftSon() != null){
            result += analizeTreeComplexity(tree.getLeftSon(), discount);
        }
        BinaryExpr.Operator operator = tree.getOperator();
        if(operator != null){
            if(!operator.equals(this.currentOperator)){
                result++;
                this.currentOperator = operator;
            }else if(!discount){
                result++;
            }
        }
        if(tree.getRightSon() != null) {
            result += analizeTreeComplexity(tree.getRightSon(), discount);
        }

        return result;
    }
}
