package cognitivecalculator.analizers;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import cognitivecalculator.enums.ModuleDeclaration;
import cognitivecalculator.resultdatastr.MethodResult;
import cognitivecalculator.resultdatastr.MethodStr;

import java.util.Optional;

public class InternalClassAnalizer {
    private String classDef = "";

    public InternalClassAnalizer(String classPath, String subClassName) {
        classDef = classPath + "." + subClassName;
    }

    public void analizeInternalClassEntity(Node node, MethodResult methodResult){
        String entitySimpleName = node.getClass().getSimpleName();
        ModuleDeclaration type = ModuleDeclaration.getEntityTypeFromString(entitySimpleName);

        switch(type){
            case CLASS_OR_INTERFACE_DECLARATION:
            case ENUM_DECLARATION:
                this.analizeTypeDeclaration(node, methodResult);
                break;
            case FIELD_DECLARATION:
                this.analizeFieldDeclaration(node, methodResult);
                break;
            case INITIALIZER_DECLARATION:
                analizeInitializer(node, methodResult);
                break;
            case ENUM_CONST_DECLARATION:
                break;
            case CONSTRUCTOR_DECLARATION:
                analizeConstructorDeclaration(node, methodResult);
                break;
            case METHOD_DECLARATION:
                analizeMethodDeclaration(node, methodResult);
                break;
            case ENTITY_DECLARATION_NOT_FOUND:
                break;
        }
    }

    private void analizeTypeDeclaration(Node node, MethodResult methodResult){
        methodResult.increaseNesting();
        NodeList<BodyDeclaration<?>> body = null;
        InternalClassAnalizer subClassAnalizer = null;
        if(node instanceof ClassOrInterfaceDeclaration){
            ClassOrInterfaceDeclaration declaration = (ClassOrInterfaceDeclaration)node;
            body = declaration.getMembers();
            subClassAnalizer = new InternalClassAnalizer(classDef, declaration.getName().toString());
        }else if(node instanceof EnumDeclaration){
            EnumDeclaration declaration = (EnumDeclaration)node;
            body = declaration.getMembers();
            subClassAnalizer = new InternalClassAnalizer(classDef, declaration.getName().toString());
        }
        for(BodyDeclaration<?> entity : body){
            subClassAnalizer.analizeInternalClassEntity(entity, methodResult);
        }
        methodResult.decreaseNesting();
    }

    private void analizeFieldDeclaration(Node node, MethodResult methodResult){
        FieldDeclaration fieldDec = (FieldDeclaration)node;
        NodeList<VariableDeclarator> variables = fieldDec.getVariables();
        String stateValue = " w";
        for(VariableDeclarator varDec : variables){
            Optional<Expression> expr = varDec.getInitializer();
            if(expr.isPresent()){
                Expression eee = expr.get();
                if(eee instanceof ConditionalExpr || eee instanceof LambdaExpr){
                    stateValue = " y";
                }else if(eee instanceof MethodCallExpr || eee instanceof ObjectCreationExpr){
                    stateValue = " x";
                }
                new ExpressionAnalizer().analizeExpression(eee, methodResult);
            }
        }
    }

    private void analizeInitializer(Node node, MethodResult methodResult){
        InitializerDeclaration initializerDeclaration = (InitializerDeclaration)node;
        BlockStmt body = initializerDeclaration.getBody();
        methodResult.increaseNesting();
        methodResult.increaseCognitiveComplexityNesting();
        new StatementAnalizer().decomposeBlockStmt(body, methodResult);
        methodResult.decreaseCognitiveComplexityNesting();
        methodResult.decreaseNesting();
    }

    private void analizeConstructorDeclaration(Node node, MethodResult methodResult){
        ConstructorDeclaration declaration = (ConstructorDeclaration)node;
        methodResult.increaseNesting();
        methodResult.increaseCognitiveComplexityNesting();

        //for recursion
        MethodStr constructor = new MethodStr();
        constructor.setMethodName(declaration.getName().toString());
        for(Parameter par : declaration.getParameters()){
            constructor.addParameter(par.getType().toString());
        }
        methodResult.setCurrentScopeForRecursion(classDef);

        BlockStmt constructorBody = declaration.getBody();
        new StatementAnalizer().decomposeBlockStmt(constructorBody, methodResult);

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }

    private void analizeMethodDeclaration(Node node, MethodResult methodResult){
        MethodDeclaration declaration = (MethodDeclaration)node;
        methodResult.increaseCognitiveComplexityNesting();
        methodResult.increaseNesting();

        Optional<BlockStmt> methodBody = declaration.getBody();
        if(methodBody.isPresent()){
            //for recursion
            MethodStr constructor = new MethodStr();
            constructor.setMethodName(declaration.getName().toString());
            for(Parameter par : declaration.getParameters()){
                constructor.addParameter(par.getType().toString());
            }
            methodResult.setCurrentScopeForRecursion(classDef);

            new StatementAnalizer().decomposeBlockStmt(methodBody.get(), methodResult);
        }

        methodResult.decreaseNesting();
        methodResult.decreaseCognitiveComplexityNesting();
    }
}
