package cognitivecalculator.resultdatastr;

import java.util.ArrayList;
import java.util.List;

public class MethodStr {
    private String methodName;
    private List<String> parameters;

    public MethodStr(){
        parameters = new ArrayList<>();
    }

    public void addParameter(String par){
        parameters.add(par);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
