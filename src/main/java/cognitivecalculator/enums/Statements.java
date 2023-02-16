package cognitivecalculator.enums;

public enum Statements {
	IF_STMT("IfStmt"), //
	DO_STMT("DoStmt"), //
	WHILE_STMT("WhileStmt"), //
	FOR_STMT("ForStmt"), //
	FOR_EACH_STMT("ForEachStmt"), //
	SWITCH_STMT("SwitchStmt"), //
	TRY_CATCH_STMT("TryStmt"), //
	SYNCHRONIZED_STMT("SynchronizedStmt"), //
	LABELED_STMT("LabeledStmt"), //
	CONTINUE_STMT("ContinueStmt"), //
	BREAK_STMT("BreakStmt"), //
	RETURN_STMT("ReturnStmt"), //
	EXPRESSION_STMT("ExpressionStmt"),
	LOCAL_CLASS_DECLARATION_STMT("LocalClassDeclarationStmt"), //
	ASSERT_STMT("AssertStmt"), //
	BLOCK_STMT("BlockStmt"), //
	THROW_STMT("ThrowStmt"), //
	YIELD_STMT("YieldStmt"), //
	EXPLICIT_CONSTRUCTORS("ExplicitConstructorInvocationStmt"),
	STATEMENT_NOT_FOUND("StatementNotFound");
	
	private String statement;
	
	private Statements(String statement) {
		this.statement = statement;
	}
	
	public String getValue() {
		return this.statement;
	}
	
	public static Statements getEnumFromString(String statementSimpleName) {
		Statements statement = STATEMENT_NOT_FOUND;
		
		for(Statements type: Statements.values()) {
			if(type.statement.equals(statementSimpleName)) {
				return type;
			}
		}
		return statement;
	}
}
