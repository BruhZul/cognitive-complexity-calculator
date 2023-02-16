package cognitivecalculator.io;

import cognitivecalculator.resultdatastr.MethodResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputManager {
    private String outputDirectory;

    public OutputManager(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public boolean generateCSVfile(List<List<MethodResult>> modules) {
        FileWriter fwr;
        CSVPrinter csvPrinter;
        try {
            if (outputDirectory.endsWith(".csv")) {
                fwr = new FileWriter(outputDirectory);
            } else {
                fwr = new FileWriter(outputDirectory + ".csv");
            }
            csvPrinter = new CSVPrinter(fwr, CSVFormat.DEFAULT.withDelimiter(';'));

            csvPrinter.printRecord("Absolute Module Path", "Module Position", "Module Declaration", "Max nesting",
                    "Cognitive Complexity");

            for (List<MethodResult> module : modules) {
                for (MethodResult type : module) {
                    this.printModule(csvPrinter, type);
                }
            }
            fwr.flush();
            fwr.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void printModule(CSVPrinter printer, MethodResult module) throws IOException {
        if (module != null) {
            printer.printRecord(
                    module.getMethodClassPath(),
                    module.getPosition(),
                    module.getMethodDeclaration(),
                    module.getMaxNesting(),
                    module.getFinalCognitiveComplexity()
            );
        }
    }
}
