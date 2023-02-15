# Cognitive Complexity Calculator

This tool computes the Cognitive Complexity, as defined by SonarSource, for `.java` files.

After building the project, the `.jar` can be executed from terminal:

```> java -jar CognitiveComplexityCalculator-1.0.jar <input directory> <output directory>\filename.csv```

Where `<input directory>` represents the directory of all `.java` file to analyse, including all the files in the sub-directories.
`<output directory>` represents the directory in which the output file is created.

The output file is a `.csv` file which contains the Cognitive Complexity value for each method.
Specificically it contains:
- Absolute Module Path: The path of the class containing the method
- Module Position: The line in the `.java` file where the method starts
- Module declaration: The method signature and return type
- Max Nesting: The maximum level of nesting reached by the method (considering as 1 the starting level)
- Cognitive Complexity

The white paper explaining how the metric is calculated can be found here: 

https://www.sonarsource.com/docs/CognitiveComplexity.pdf

Note that this tool does not take into account the increments in complexity due to recurvive method calls. At the time this tool was written, even SonarQube did not take these calls into account, meaning that the final result was equivalent.

The tool was tested and all found bugs were solved, but there could still be problems.

The tool has the following dependencies:
- com.github.javaparser:javaparser-symbol-solver-core:3.16.3
- org.apache.commons:commons-csv:1.8
