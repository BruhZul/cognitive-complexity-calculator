package cognitivecalculator.analizers;

import cognitivecalculator.resultdatastr.MethodResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class FileManager {
    private String absolutePath;
    private String packageName;

    public FileManager() {}

    public List<MethodResult> computeCognitiveComplexityForFile(File file) throws IOException {
        List<List<MethodResult>> partial = new ArrayList<>();
        List<MethodResult> result = new LinkedList<>();
        this.absolutePath = file.getAbsolutePath();
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);
        NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
        Optional<PackageDeclaration> packageDec = compilationUnit.getPackageDeclaration();
        if(packageDec.isPresent()){
            packageName = packageDec.get().getName().toString();
        }

        NodeList<BodyDeclaration<?>> members;
        for(TypeDeclaration<?> type : types){
            List<MethodResult> lis = new ClassAnalizer(this, packageName).computeComplexityForClass(type,false);
            if(!lis.isEmpty()){
                partial.add(lis);
            }
        }

        for(List<MethodResult> classResult : partial){
            result.addAll(classResult);
        }
        return result;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getPackageName() {
        return packageName;
    }
}
