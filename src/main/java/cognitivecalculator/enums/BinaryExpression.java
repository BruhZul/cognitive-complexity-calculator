package cognitivecalculator.enums;

public enum BinaryExpression {
	BINARY_EXPR("BinaryExpr"),
	UNARY_EXPR("UnaryExpr"),
	ENCLOSED_EXPR("EnclosedExpr"),
	EXPRESSION_NOT_FOUND("ExpressionNotFound");
	
	private String expression;
	
	private BinaryExpression(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return this.expression;
	}
	
	public static BinaryExpression getExpressionTypeFromString(String expressionSimpleName) {
		BinaryExpression expressionType = EXPRESSION_NOT_FOUND;
		
		for(BinaryExpression type: BinaryExpression.values()) {
			if(type.expression.equals(expressionSimpleName)) {
				expressionType = type;
			}
		}
		return expressionType;
	}
}
