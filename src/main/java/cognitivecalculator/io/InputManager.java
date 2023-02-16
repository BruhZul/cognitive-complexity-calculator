package cognitivecalculator.io;

import cognitivecalculator.analizers.FileManager;
import cognitivecalculator.resultdatastr.MethodResult;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InputManager {
    public InputManager(){}

    public List<List<MethodResult>> computeCognitiveComplexity(File directoryOrFile) throws IOException {
        List<List<MethodResult>> result = new LinkedList<>();
        List<File> allJavaFiles = this.getAllJavaFilesInDirectory(directoryOrFile);
        this.printAllFile(allJavaFiles);

        System.out.println();
        List<MethodResult> methodResults;
        for(File file: allJavaFiles){
            System.out.println("--> " + file.getName());
            methodResults = new FileManager().computeCognitiveComplexityForFile(file);
            result.add(methodResults);
        }
        return result;
    }

    private List<File> getAllJavaFilesInDirectory(File directoryOrFile){
        List<File> javaFileList = new LinkedList<>();

        if(directoryOrFile.isDirectory()) {
            File[] files = directoryOrFile.listFiles();
            for(File file: files) {
                List<File> subFiles = this.getAllJavaFilesInDirectory(file);
                javaFileList.addAll(subFiles);
            }
        }else if(directoryOrFile.isFile()) {
            if(directoryOrFile.getName().endsWith(".java")) {
                javaFileList.add(directoryOrFile);
            }
        }

        return javaFileList;
    }

    private void printAllFile(List<File> files){
        System.out.println("JAVA FILES FOUND:");
        for(File f: files){
            System.out.println(f.getName());
        }
        System.err.println("Total number of files: " + files.size());
    }
}
