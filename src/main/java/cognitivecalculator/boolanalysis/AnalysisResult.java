package cognitivecalculator.boolanalysis;

public class AnalysisResult {
    private int booleanComplexityValue;
    private boolean methodAnalysis;

    public AnalysisResult(int booleanComplexityValue, boolean methodAnalysis) {
        this.booleanComplexityValue = booleanComplexityValue;
        this.methodAnalysis = methodAnalysis;
    }

    public int getBooleanComplexityValue() {
        return booleanComplexityValue;
    }

    public boolean isMethodAnalysis() {
        return methodAnalysis;
    }

    public void setMethodAnalysis(boolean methodAnalysis) {
        this.methodAnalysis = methodAnalysis;
    }

    public void setBooleanComplexityValue(int booleanComplexityValue) {
        this.booleanComplexityValue = booleanComplexityValue;
    }
}
