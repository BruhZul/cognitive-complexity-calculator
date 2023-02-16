package cognitivecalculator.enums;

public enum LogicalOperator {
	AND("&&"),
	OR("||"),
	NOT("!"),
	XOR("^"),
	OTHER("Other operator");

	private String operator;
	
	private LogicalOperator(String operator) {
		this.operator = operator;
	}
	
	public String getOperator() {
		return this.operator;
	}
	
	public static LogicalOperator getEnumFromString(String logicalOperator) {
		LogicalOperator operator = null;
		
		for(LogicalOperator type: LogicalOperator.values()) {
			if(type.operator.equals(logicalOperator)) {
				return type;
			}
		}
		return operator;
	}
}
