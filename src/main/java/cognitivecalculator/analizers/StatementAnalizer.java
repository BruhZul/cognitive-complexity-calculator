package cognitivecalculator.analizers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import cognitivecalculator.boolanalysis.AnalysisResult;
import cognitivecalculator.boolanalysis.BinaryExprAnalizer;
import cognitivecalculator.enums.Statements;
import cognitivecalculator.resultdatastr.MethodResult;

import java.util.List;
import java.util.Optional;

public class StatementAnalizer {
    public StatementAnalizer() {
    }
    
    public void analizeStatement(Node node, MethodResult methodResult) {
        String stmtTypeString = node.getClass().getSimpleName();
        Statements stmtType = Statements.getEnumFromString(stmtTypeString);
        switch (stmtType) {
            case BREAK_STMT:
                this.analizeBreakStmt(node, methodResult);
                break;
            case CONTINUE_STMT:
                this.analizeContinueStmt(node, methodResult);
                break;
            case RETURN_STMT:
                this.analizeReturnStmt(node, methodResult);
                break;
            case THROW_STMT:
                this.analizeThrowStmt(node, methodResult);
                break;
            case YIELD_STMT:
                this.analizeYieldStmt(node, methodResult);
                break;
            case ASSERT_STMT:
                this.analizeAssert(node, methodResult);
                break;
            case BLOCK_STMT:
                methodResult.increaseCognitiveComplexityNesting();
                methodResult.increaseNesting();
                decomposeBlockStmt((BlockStmt) node, methodResult);
                methodResult.decreaseNesting();
                methodResult.decreaseCognitiveComplexityNesting();
                break;
            case DO_STMT:
                analizeDoStmt(node, methodResult);
                break;
            case FOR_EACH_STMT:
                analizeForEachStmt(node, methodResult);
                break;
            case FOR_STMT:
                analizeForStmt(node, methodResult);
                break;
            case IF_STMT:
                analizeIfStmt(node, methodResult,false);
                break;
            case LOCAL_CLASS_DECLARATION_STMT:
                analizeLocalClassDeclarationStmt(node, methodResult);
                break;
            case LABELED_STMT:
                this.analizeLabeledStmt(node, methodResult);
                break;
            case SWITCH_STMT:
                this.analizeSwitchStmt(node, methodResult);
                break;
            case SYNCHRONIZED_STMT:
                this.analizeSynchronizedStmt(node, methodResult);
                break;
            case TRY_CATCH_STMT:
                this.analizeTryStmt(node, methodResult);
                break;
            case WHILE_STMT:
                this.analizeWhileStmt(node, methodResult);
                break;
            case EXPRESSION_STMT:
                analizeExpressionStmt(node, methodResult);
                break;
            case EXPLICIT_CONSTRUCTORS:
                analizeExplicitConstructor(node, methodResult);
                break;
            case STATEMENT_NOT_FOUND:
                break;
        }
    }

    private void analizeBreakStmt(Node node, MethodResult methodResult) {
        BreakStmt breakStmt = (BreakStmt) node;
        Optional<SimpleName> label = breakStmt.getLabel();
        if (label.isPresent()) {
            methodResult.increaseCognitiveComplexity();
        }
    }

    private void analizeExplicitConstructor(Node node, MethodResult methodResult) {
        ExplicitConstructorInvocationStmt constructor = (ExplicitConstructorInvocationStmt) node;
        NodeList<Expression> args = constructor.getArguments();
        boolean isComplex = false;
        for (Expression arg : args) {
            if (arg instanceof ConditionalExpr || arg instanceof MethodCallExpr || arg instanceof ObjectCreationExpr
                    || arg instanceof BinaryExpr || arg instanceof UnaryExpr) {
                if (arg instanceof BinaryExpr) {
                    BinaryExpr.Operator operator = ((BinaryExpr) arg).getOperator();
                    if (operator.equals(BinaryExpr.Operator.AND) || operator.equals(BinaryExpr.Operator.OR)
                            || operator.equals(BinaryExpr.Operator.XOR)) {
                        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(arg, true, methodResult);
                        if (expComplexity.getBooleanComplexityValue() > 1 || expComplexity.isMethodAnalysis()) {
                            isComplex = true;
                        }
                    }
                } else if (arg instanceof UnaryExpr) {
                    UnaryExpr.Operator operator = ((UnaryExpr) arg).getOperator();
                    if (operator.equals(UnaryExpr.Operator.LOGICAL_COMPLEMENT)) {
                        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(arg, true, methodResult);
                        if (expComplexity.getBooleanComplexityValue() > 1 || expComplexity.isMethodAnalysis()) {
                            isComplex = true;
                        }
                    }
                } else {
                    isComplex = true;
                }
            }
        }
    }

    private void analizeAssert(Node node, MethodResult methodResult) {
        AssertStmt stmt = (AssertStmt) node;
        Expression condition = stmt.getCheck();
        AnalysisResult analysisResult = new BinaryExprAnalizer().calculateExpressionComplexity(condition, true, methodResult);
        int expCompl = analysisResult.getBooleanComplexityValue();
        methodResult.increaseCognitiveComplexityFlat(expCompl);
    }

    private void analizeContinueStmt(Node node, MethodResult methodResult) {
        ContinueStmt conStmt = (ContinueStmt) node;
        Optional<SimpleName> label = conStmt.getLabel();
        if (label.isPresent()) {
            methodResult.increaseCognitiveComplexity();
        }
    }

    private void analizeReturnStmt(Node node, MethodResult methodResult) {
        ReturnStmt stmt = (ReturnStmt) node;
        Optional<Expression> expr = stmt.getExpression();
        if (expr.isPresent()) {
            new ExpressionAnalizer().analizeExpression(expr.get(), methodResult);
        }
    }

    private void analizeThrowStmt(Node node, MethodResult methodResult) {
        ThrowStmt stmt = (ThrowStmt) node;
        Expression expr = stmt.getExpression();
        new ExpressionAnalizer().analizeExpression(expr, methodResult);
    }

    private void analizeYieldStmt(Node node, MethodResult methodResult) {
        YieldStmt stmt = (YieldStmt) node;
        Expression expr = stmt.getExpression();
        new ExpressionAnalizer().analizeExpression(expr, methodResult);
    }

    private void analizeDoStmt(Node node, MethodResult methodResult) {
        DoStmt doStmt = (DoStmt) node;
        Expression expr = doStmt.getCondition();
        Statement stmt = doStmt.getBody();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();
        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr, true, methodResult);
        methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());

        methodResult.increaseNesting();

        if (stmt instanceof BlockStmt) {
            decomposeBlockStmt(stmt, methodResult);
        } else {
            analizeStatement(stmt, methodResult);
        }

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting(); //when he passes to the next statement, the nesting level is correct
    }

    private void analizeForEachStmt(Node node, MethodResult methodResult) {
        ForEachStmt forEachStmt = (ForEachStmt) node;
        Statement stmt = forEachStmt.getBody();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();

        methodResult.increaseNesting();

        if (stmt instanceof BlockStmt)
            this.decomposeBlockStmt(stmt, methodResult);
        else
            this.analizeStatement(stmt, methodResult);

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeForStmt(Node node, MethodResult methodResult) {
        ForStmt forStmt = (ForStmt) node;
        Statement stmt = forStmt.getBody();
        Optional<Expression> expr = forStmt.getCompare();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();

        if (expr.isPresent()) {
            AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr.get(), true, methodResult);
            methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());
        }

        methodResult.increaseNesting();

        if (stmt instanceof BlockStmt)
            this.decomposeBlockStmt(stmt, methodResult);
        else
            this.analizeStatement(stmt, methodResult);

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeIfStmt(Node node, MethodResult methodResult, boolean complex) {
        IfStmt ifStmt = (IfStmt) node;
        Expression expr = ifStmt.getCondition();
        Statement thenStmt = ifStmt.getThenStmt();
        Optional<Statement> elseStmt = ifStmt.getElseStmt();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();

        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr, true, methodResult);
        methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());
        if (complex) {
            expComplexity.setMethodAnalysis(true);
        } else if (expComplexity.getBooleanComplexityValue() > 0 || expComplexity.isMethodAnalysis()) {
            complex = true;
        }

        methodResult.increaseNesting();

        if (thenStmt instanceof BlockStmt) {
            this.decomposeBlockStmt(thenStmt, methodResult);
        } else {
            this.analizeStatement(thenStmt, methodResult);
        }

        methodResult.decreaseNesting();
        if (elseStmt.isPresent()) {
            methodResult.increaseCognitiveComplexityFlat(1);
            Statement stmt = elseStmt.get();
            if (stmt instanceof BlockStmt) {
                methodResult.increaseNesting();
                this.decomposeBlockStmt(stmt, methodResult);
                methodResult.decreaseCognitiveComplexityNesting();
                methodResult.decreaseNesting();
            } else {
                if (stmt instanceof IfStmt) {
                    methodResult.decreaseCognitiveComplexityNesting();
                    methodResult.decreaseCognitiveComplexity();
                    this.analizeIfStmt(stmt, methodResult, complex);
                } else {
                    methodResult.increaseNesting();
                    methodResult.decreaseNesting();
                    methodResult.decreaseCognitiveComplexityNesting();
                }
            }
        } else {
            methodResult.decreaseCognitiveComplexityNesting();
        }
    }

    private void analizeLocalClassDeclarationStmt(Node node, MethodResult methodResult) {
        LocalClassDeclarationStmt localClass = (LocalClassDeclarationStmt) node;
        methodResult.increaseNesting();
        List<Node> localNodes = localClass.getChildNodes();
        for (Node localNode : localNodes) {
            List<Node> childNodes = localNode.getChildNodes();
            for (Node childNode : childNodes) {
                new InternalClassAnalizer(methodResult.getCurrentScopeCognitive(),
                        localClass.getClassDeclaration().getName().toString()).analizeInternalClassEntity(childNode,
                        methodResult);
            }
        }
        methodResult.decreaseNesting();
    }

    private void analizeLabeledStmt(Node node, MethodResult methodResult) {
        LabeledStmt labeledStmt = (LabeledStmt) node;
        Statement stmnt = labeledStmt.getStatement();
        this.analizeStatement(stmnt, methodResult);
    }

    private void analizeSwitchStmt(Node node, MethodResult methodResult) {
        SwitchStmt switchStmt = (SwitchStmt) node;
        Expression selector = switchStmt.getSelector();
        NodeList<SwitchEntry> entries = switchStmt.getEntries();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();

        //switchExprComplexity = new ExpressionAnalizer().analizeExpression(selector, nesting); //expr maybe not

        if (entries.isNonEmpty()) {
            int ctr = 0;
            for (SwitchEntry entry : entries) {
                NodeList<Statement> stmts = entry.getStatements();

                methodResult.increaseNesting();
                for (Statement stmt : stmts) {
                    if (stmt instanceof BlockStmt)
                        this.decomposeBlockStmt(stmt, methodResult);
                    else
                        this.analizeStatement(stmt, methodResult);
                }
                methodResult.decreaseNesting();
            }
        }
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeSynchronizedStmt(Node node, MethodResult methodResult) {
        SynchronizedStmt synchronizedStmt = (SynchronizedStmt) node;
        Statement stmt = synchronizedStmt.getBody();
        methodResult.increaseNesting();

        if (stmt instanceof BlockStmt) {
            this.decomposeBlockStmt(stmt, methodResult);
        } else {
            this.analizeStatement(stmt, methodResult);
        }
        methodResult.decreaseNesting();
    }

    private void analizeWhileStmt(Node node, MethodResult methodResult) {
        WhileStmt whileStmt = (WhileStmt) node;
        Expression expr = whileStmt.getCondition();
        Statement stmt = whileStmt.getBody();

        methodResult.increaseCognitiveComplexity();
        methodResult.increaseCognitiveComplexityNesting();

        AnalysisResult expComplexity = new BinaryExprAnalizer().calculateExpressionComplexity(expr, true, methodResult);
        methodResult.increaseCognitiveComplexityFlat(expComplexity.getBooleanComplexityValue());

        methodResult.increaseNesting();

        if (stmt instanceof BlockStmt)
            this.decomposeBlockStmt(stmt, methodResult);
        else
            this.analizeStatement(stmt, methodResult);

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeExpressionStmt(Node node, MethodResult methodResult) {
        ExpressionStmt exprStmt = (ExpressionStmt) node;
        Expression expr = exprStmt.getExpression();

        new ExpressionAnalizer().analizeExpression(expr, methodResult);
    }

    private void analizeTryStmt(Node node, MethodResult methodResult) {
        TryStmt tryStmt = (TryStmt) node;
        Statement tryBlockStmt = tryStmt.getTryBlock();
        NodeList<CatchClause> catchClauses = tryStmt.getCatchClauses();
        Optional<BlockStmt> finallyBlock = tryStmt.getFinallyBlock();

        methodResult.increaseNesting();
        int ctr = finallyBlock.isPresent() ? 1 : 0;
        if (tryBlockStmt instanceof BlockStmt) {
            this.decomposeBlockStmt(tryBlockStmt, methodResult);
        } else {
            this.analizeStatement(tryBlockStmt, methodResult);
        }
        methodResult.decreaseNesting();

        if (catchClauses.isNonEmpty()) {
            for (CatchClause catchClause : catchClauses) {
                ctr++;
                methodResult.increaseCognitiveComplexity();
                methodResult.increaseCognitiveComplexityNesting();
                Statement stmt = catchClause.getBody();
                methodResult.increaseNesting();
                if (stmt instanceof BlockStmt) {
                    this.decomposeBlockStmt(stmt, methodResult);
                } else {
                    this.analizeStatement(stmt, methodResult);
                }
                methodResult.decreaseNesting();
                methodResult.decreaseCognitiveComplexityNesting();
            }
        }

        if (finallyBlock.isPresent()) {
            Statement stmt = finallyBlock.get();
            methodResult.increaseNesting();
            if (stmt instanceof BlockStmt) {
                this.decomposeBlockStmt(stmt, methodResult);
            } else {
                this.analizeStatement(stmt, methodResult);
            }
            methodResult.decreaseNesting();
        }
    }

    public void decomposeBlockStmt(Statement stmt, MethodResult methodResult) {
        List<Node> list = stmt.getChildNodes();
        for (Node node : list) {
            this.analizeStatement(node, methodResult);
        }
    }
}
