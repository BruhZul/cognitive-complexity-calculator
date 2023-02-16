package cognitivecalculator.analizers;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import cognitivecalculator.enums.ModuleDeclaration;
import cognitivecalculator.resultdatastr.MethodResult;
import cognitivecalculator.resultdatastr.MethodStr;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClassAnalizer {
    private FileManager fileManager;
    private String classDef = "";

    public ClassAnalizer(FileManager fmng, String packag) {
        this.fileManager = fmng;
        this.classDef = packag;
    }

    public List<MethodResult> computeComplexityForClass(TypeDeclaration<?> type, boolean after){
        if(!after) {
            this.classDef = classDef + "." + type.getName().toString();
        }
        List<MethodResult> result = new LinkedList<>();

        NodeList<BodyDeclaration<?>> members = type.getMembers();
        List<List<MethodResult>> partialForSubClasses = new LinkedList<>();

        for(BodyDeclaration<?> member : members){
            partialForSubClasses.add(this.analizeEntity(member));
        }

        for(List<MethodResult> subClassList : partialForSubClasses){
            result.addAll(subClassList);
        }
        //The list will be empty if there are no methods inside
        return result;

        // This is in case one wants to deal with classes without methods
        //if(!result.isEmpty()) {
        //    return result;
        //}else{
        //    MethodResult part = new MethodResult();
        //    part.setMethodDeclaration("NaN");
        //    part.setMethodClassPath(classDef);
        //    part.setPosition(0);
        //    result.add(part);
        //    return result;
        //}
    }

    public List<MethodResult> analizeEntity(Node node){
        String entitySimpleName = node.getClass().getSimpleName();
        ModuleDeclaration type = ModuleDeclaration.getEntityTypeFromString(entitySimpleName);
        List<MethodResult> result = new LinkedList<>();

        switch(type){
            case CLASS_OR_INTERFACE_DECLARATION:
            case ENUM_DECLARATION:
                result = new ClassAnalizer(fileManager, classDef).computeComplexityForClass((TypeDeclaration<?>)node, true);
                break;
            case FIELD_DECLARATION:
            case ENUM_CONST_DECLARATION:
            case INITIALIZER_DECLARATION:
                break;
            case CONSTRUCTOR_DECLARATION:
                MethodResult constructorComplexity = new MethodResult();
                this.analizeConstructorDeclaration(node, constructorComplexity);
                result.add(constructorComplexity);
                break;
            case METHOD_DECLARATION:
                MethodResult methodComplexity = new MethodResult();
                this.analizeMethodDeclaration(node, methodComplexity);
                result.add(methodComplexity);
                break;
            case ENTITY_DECLARATION_NOT_FOUND:
                break;
        }
        //if(!result.isEmpty()) {
        //    return result;
        //}else{
        //    MethodResult part = new MethodResult();
        //    part.setMethodDeclaration("NaN");
        //    part.setMethodClassPath(classDef);
        //    part.setPosition(0);
        //    result.add(part);
        //    return result;
        //}
        return result;
    }

    private void analizeConstructorDeclaration(Node node, MethodResult constructorComplexity){
        ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)node;
        constructorComplexity.setMethodClassPath(classDef);
        constructorComplexity.setPosition(this.getModulePosition(constructorDeclaration));
        constructorComplexity.setMethodDeclaration(constructorDeclaration.getDeclarationAsString(false,
                false,true));

        //for recursion
        MethodStr constructor = new MethodStr();
        constructor.setMethodName(constructorDeclaration.getName().toString());
        for(Parameter par : constructorDeclaration.getParameters()){
            constructor.addParameter(par.getType().toString());
        }

        constructorComplexity.setCurrentScopeForRecursion(classDef);

        BlockStmt constructorBody = constructorDeclaration.getBody();

        StringBuilder nesting = new StringBuilder();
        new StatementAnalizer().decomposeBlockStmt(constructorBody, constructorComplexity);
    }

    private void analizeMethodDeclaration(Node node, MethodResult methodResult){
        MethodDeclaration methodDeclaration = (MethodDeclaration)node;
        methodResult.setMethodClassPath(classDef);
        methodResult.setPosition(this.getModulePosition(methodDeclaration));
        methodResult.setMethodDeclaration(methodDeclaration.getDeclarationAsString(false,
                false, true));


        Optional<BlockStmt> methodBody = methodDeclaration.getBody();
        if(methodBody.isPresent()){
            //for recursion
            MethodStr constructor = new MethodStr();
            constructor.setMethodName(methodDeclaration.getName().toString());
            for(Parameter par : methodDeclaration.getParameters()){
                constructor.addParameter(par.getType().toString());
            }
            methodResult.setCurrentScopeForRecursion(classDef);

            StringBuilder nesting = new StringBuilder();
            new StatementAnalizer().decomposeBlockStmt(methodBody.get(), methodResult);
        }
    }

    private int getModulePosition(BodyDeclaration<?> bodyDeclaration) {
        int line = -1;
        Optional<Position> position = bodyDeclaration.getBegin();
        if(position.isPresent())
            line = position.get().line;

        return line;
    }
}
