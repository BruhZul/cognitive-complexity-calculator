package cognitivecalculator.resultdatastr;

public class MethodResult {
    private CognitiveComplexity cognitiveComplexity;
    private String methodClassPath;
    private int position;
    private String methodDeclaration;
    private int maxNesting;
    private int nesting;

    public MethodResult(){
        this.cognitiveComplexity = new CognitiveComplexity();
        this.maxNesting = 1;
        this.nesting = 1;
    }

    public MethodResult(CognitiveComplexity cognitiveComplexity) {
        this.cognitiveComplexity = cognitiveComplexity;
    }

    public void increaseNesting(){
        nesting++;
        if(nesting > maxNesting){
            maxNesting = nesting;
        }
    }

    public void decreaseNesting(){
        nesting--;
    }

    public String getMethodClassPath() {
        return methodClassPath;
    }

    public void setMethodClassPath(String methodClassPath) {
        this.methodClassPath = methodClassPath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(String methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    //Cognitive Complexity intercface
    public void increaseCognitiveComplexityNesting(){
        this.cognitiveComplexity.increaseNesting();
    }

    public void decreaseCognitiveComplexityNesting(){
        this.cognitiveComplexity.decreaseNesting();
    }

    public void increaseCognitiveComplexity(){
        this.cognitiveComplexity.increaseComplexity();
    }

    public void decreaseCognitiveComplexity(){ this.cognitiveComplexity.decreaseComplexity(); }

    public void increaseCognitiveComplexityFlat(int value){
        this.cognitiveComplexity.increaseMultiple(value);
    }

    //public void addMethodForRecursion(MethodStr method){
    //    this.cognitiveComplexity.addMethod(method);
    //}

    public void setCurrentScopeForRecursion(String method){
        this.cognitiveComplexity.setCurrentClassScope(method);
    }

    public String getCurrentScopeCognitive(){
        return this.cognitiveComplexity.getCurrentClassScope();
    }

    public MethodStr getCurrentMethod(){
        return this.cognitiveComplexity.getMethod();
    }

    public int getFinalCognitiveComplexity(){
        return this.cognitiveComplexity.getComplexity();
    }

    public int getMaxNesting(){
        return this.maxNesting;
    }
}
