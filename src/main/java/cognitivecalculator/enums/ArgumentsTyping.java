package cognitivecalculator.enums;

public enum ArgumentsTyping {
    OBJECT("ObjectCreationExpr"),
    INTEGER("IntegerLiteralExpr"),
    DOUBLE("DoubleLiteralExpr"),
    CHAR("CharLiteralExpr"),
    BOOLEAN("BooleanLiteralExpr"),
    LONG("LongLiteralExpr"),
    STRING("StringLiteralExpr"),
    TEXTBLOCK("TextBlockLiteralExpr"),
    NULL("NullLiteralExpr"),
    OTHER("other");

    private String arg;

    private ArgumentsTyping(String argType){
        arg = argType;
    }

    public String getArg() {
        return arg;
    }

    public static ArgumentsTyping getArgumentTypeFromString(String type){
        ArgumentsTyping res = OTHER;

        for(ArgumentsTyping pos : ArgumentsTyping.values()){
            if(pos.arg.equals(type)){
                res = pos;
            }
        }

        return res;
    }
}
