package cognitivecalculator;

import cognitivecalculator.io.InputManager;
import cognitivecalculator.io.OutputManager;
import cognitivecalculator.resultdatastr.MethodResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java -jar CognitiveComplexityCalculator-1.0.jar <input directory/file path> <output file path>");
            throw new IOException("");
        } else {
            System.out.println("START");
            System.out.println();


            String inputpath = args[0];
            File inputDirectory = new File(inputpath);
            if (!(inputDirectory.isDirectory() || (inputDirectory.isFile() &&
                    inputDirectory.getName().endsWith(".java")))) {
                System.err.println("Error: Input directory or file could not be found");
                throw new IOException("");
            }

            System.out.println();
            String outputpath = args[1];

            List<List<MethodResult>> result = new InputManager().computeCognitiveComplexity(inputDirectory);

            if(new OutputManager(outputpath).generateCSVfile(result)){
                System.out.println("DONE");
            }else{
                System.out.println("The tool was not able to generate the .csv file. Is the output directory written correctly?");
            }


        }
    }
}
