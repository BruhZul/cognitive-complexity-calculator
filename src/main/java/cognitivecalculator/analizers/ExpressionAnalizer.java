package cognitivecalculator.analizers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.type.Type;
import cognitivecalculator.boolanalysis.AnalysisResult;
import cognitivecalculator.boolanalysis.BinaryExprAnalizer;
import cognitivecalculator.enums.Expressions;
import cognitivecalculator.resultdatastr.MethodResult;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.StringTokenizer;

public class ExpressionAnalizer {
    public ExpressionAnalizer() {
    }

    public void analizeExpression(Node node, MethodResult methodResult) {
        String expressionSimpleName = node.getClass().getSimpleName();
        Expressions type = Expressions.getExpressionTypeFromString(expressionSimpleName);

        switch (type) {
            case ASSIGN_EXPR:
                analizeAssignExpr(node, methodResult);
                break;
            case BINARY_EXPR:
                this.analizeBinaryExpr(node, methodResult);
                break;
            case CONDITIONAL_EXPR:
                this.analizeConditionalExpr(node, methodResult);
                break;
            case ENCLOSED_EXPR:
                EnclosedExpr expr = (EnclosedExpr) node;
                this.analizeExpression(expr.getInner(), methodResult);
                break;
            case LAMBDA_EXPR:
                analizeLambdaExpr(node, methodResult);
                break;
            case METHOD_CALL_EXPR: //TODO indirect recursion
                analizeMethodCallExpr(node, methodResult);
                break;
            case METHOD_REFERENCE:
                break;
            case OBJECT_CREATION_EXPR:
                analizeObjectDeclarationExpr(node, methodResult);
                break;
            case UNARY_EXPR:
                UnaryExpr exx = (UnaryExpr) node;
                this.analizeExpression(exx.getExpression(), methodResult);
                break;
            case VARIABLE_DECLARATION_EXPR:
                analizeVariableDeclarationExpr(node, methodResult);
                break;
            case SWITCH_EXPR:
                this.analizeSwitchExpr(node, methodResult);
                break;
            case EXPRESSION_NOT_FOUND:
                break;
        }
    }

    private void analizeBinaryExpr(Node node, MethodResult methodResult) {
        BinaryExpr expr = (BinaryExpr) node;
        BinaryExpr.Operator operator = expr.getOperator();
        if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)
                || operator.equals(BinaryExpr.Operator.XOR)) {
            AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr, true, methodResult);
            methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());
        } else {
            decomposeExprBlock(expr, methodResult);
        }
    }

    private void analizeLambdaExpr(Node node, MethodResult methodResult) {
        LambdaExpr lambdaExpr = (LambdaExpr) node;
        Statement lambdaBody = lambdaExpr.getBody();

        methodResult.increaseCognitiveComplexityNesting();

        methodResult.increaseNesting();
        if (lambdaBody instanceof BlockStmt) {
            new StatementAnalizer().decomposeBlockStmt(lambdaBody, methodResult);
        } else {
            if (!(lambdaBody instanceof ExpressionStmt)) {
                new StatementAnalizer().analizeStatement(lambdaBody, methodResult);
            }
        }
        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeConditionalExpr(Node node, MethodResult methodResult) {
        ConditionalExpr conditionalExpr = (ConditionalExpr) node;
        Expression condition = conditionalExpr.getCondition();
        methodResult.increaseCognitiveComplexity();

        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(condition, true, methodResult);
        methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());

        methodResult.increaseCognitiveComplexityNesting();
        this.analizeExpression(conditionalExpr.getThenExpr(), methodResult);
        this.analizeExpression(conditionalExpr.getElseExpr(), methodResult);
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeMethodCallExpr(Node node, MethodResult methodResult) {
        MethodCallExpr methodCallExpr = (MethodCallExpr) node;
        Optional<Expression> scope = methodCallExpr.getScope();
        if ((!scope.isPresent()) || scope.get().toString().equals("this") ||
                checkScope(methodResult.getCurrentScopeCognitive(), scope.get().toString())) {
            //TODO Recursion
        }
        this.decomposeExprBlock(methodCallExpr,  methodResult);
    }

    private void analizeAssignExpr(Node node, MethodResult methodResult) {
        AssignExpr assignExpr = (AssignExpr) node;
        Expression assExpr = assignExpr.getValue();
        this.analizeExpression(assExpr, methodResult);
    }

    private void analizeVariableDeclarationExpr(Node node, MethodResult methodResult) {
        VariableDeclarationExpr varDeclarationExpr = (VariableDeclarationExpr) node;
        NodeList<VariableDeclarator> varDecs = varDeclarationExpr.getVariables();
        Optional<Expression> expr;
        int i = 0;
        for(VariableDeclarator vdec : varDecs){
            expr = vdec.getInitializer();
            if(expr.isPresent()){
                if(i==0) {
                    this.analizeExpression(expr.get(), methodResult);
                }else{
                    this.analizeExpression(expr.get(), methodResult);
                }
            }
            i++;
        }
    }

    public void analizeObjectDeclarationExpr(Node node, MethodResult methodResult) {
        Optional<NodeList<BodyDeclaration<?>>> body = ((ObjectCreationExpr) node).getAnonymousClassBody();
        if (body.isPresent()) {
            NodeList<BodyDeclaration<?>> nodeList = body.get();
            for (BodyDeclaration<?> bodNode : nodeList) {
                methodResult.increaseNesting();
                new InternalClassAnalizer(methodResult.getCurrentScopeCognitive(), "").analizeInternalClassEntity(bodNode, methodResult);
                methodResult.decreaseNesting();
            }
        }
    }

    private void analizeSwitchExpr(Node node, MethodResult methodResult) {
        SwitchExpr expr = (SwitchExpr) node;
        NodeList<SwitchEntry> entries = expr.getEntries();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();
        if (entries.isNonEmpty()) {
            methodResult.increaseNesting();
            for (SwitchEntry entry : entries) {
                NodeList<Statement> block = entry.getStatements();
                for (Statement stmt : block) {
                    if (stmt instanceof BlockStmt)
                        new StatementAnalizer().decomposeBlockStmt(stmt, methodResult);
                    else
                        new StatementAnalizer().analizeStatement(stmt, methodResult);
                }
            }
            methodResult.decreaseNesting();
        }
        methodResult.decreaseCognitiveComplexityNesting();
    }

    public void decomposeExprBlock(Node node, MethodResult methodResult) {
        List<Node> childNodes = node.getChildNodes();
        for (Node childNode : childNodes) {
            if (!(childNode instanceof VariableDeclarator)) {
                this.analizeExpression(childNode, methodResult);
            } else {
                Optional<Expression> exx = ((VariableDeclarator) childNode).getInitializer();
                if (exx.isPresent()) {
                    this.analizeExpression(exx.get(), methodResult);
                }
                //List<Node> ccNodes = childNode.getChildNodes();
                //for (Node ccNode : ccNodes) {
                //    this.analizeExpression(ccNode, methodResult);
                //}
            }
        }
    }

    private boolean areSameArguments(List<String> params, NodeList<Type> args) { //TODO rifare con l'idea iniziale delle espressioni. vedere il float
        return false;
    }

    private void decreaseNesting(StringBuilder nesting) {
        nesting.delete(nesting.length() - 2, nesting.length());
    }

    private boolean checkScope(String scope, String partialScope) {
        StringTokenizer st = new StringTokenizer(scope, ".");
        Stack<String> scopeParts = new Stack<>();
        while (st.hasMoreTokens()) {
            scopeParts.push(st.nextToken());
        }
        String tmp;
        int ctr = 0;
        while (!(scopeParts.isEmpty())) {
            tmp = scopeParts.pop();
            ctr++;
        }
        return false;
    }
}
