package cognitivecalculator.resultdatastr;

import java.util.Stack;

public class CognitiveComplexity {
    private int complexity;
    private int nesting;
    private Stack<MethodStr> methods;
    private String currentClassScope;

    public CognitiveComplexity(){
        complexity = 0;
        nesting = 1;
        methods = new Stack<>();
    }

    public void increaseComplexity(){
        complexity += nesting;
    }

    public void increaseMultiple(int number){
        complexity += number;
    }

    public void decreaseComplexity(){
        complexity -= nesting;
    }

    public void increaseNesting(){
        nesting++;
    }

    public void decreaseNesting(){
        nesting--;
    }

    public int getComplexity() {
        return complexity;
    }

    public void addMethod(MethodStr m){
        methods.push(m);
    }

    public MethodStr getMethod(){
        return methods.peek();
    }

    public void removeMethod(){
        methods.pop();
    }

    public String getCurrentClassScope() {
        return currentClassScope;
    }

    public void setCurrentClassScope(String currentClassScope) {
        this.currentClassScope = currentClassScope;
    }
}
