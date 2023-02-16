package cognitivecalculator.enums;

public enum Expressions {
	VARIABLE_DECLARATION_EXPR("VariableDeclarationExpr"),
	LAMBDA_EXPR("LambdaExpr"), //
	METHOD_CALL_EXPR("MethodCallExpr"), //
	ASSIGN_EXPR("AssignExpr"), //
	CONDITIONAL_EXPR("ConditionalExpr"), //
	ENCLOSED_EXPR("EnclosedExpr"), //
	OBJECT_CREATION_EXPR("ObjectCreationExpr"), //
	BINARY_EXPR("BinaryExpr"), //
	METHOD_REFERENCE("MethodReferenceExpr"), //
	UNARY_EXPR("UnaryExpr"), //
	SWITCH_EXPR("SwitchExpr"),
	EXPRESSION_NOT_FOUND("ExpressionNotFound");
	
	private String expression;
	
	private Expressions(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return this.expression;
	}
	
	public static Expressions getExpressionTypeFromString(String expressionSimpleName) {
		Expressions expressionType = EXPRESSION_NOT_FOUND;
		
		for(Expressions type: Expressions.values()) {
			if(type.expression.equals(expressionSimpleName)) {
				expressionType = type;
			}
		}
		return expressionType;
	}
}
