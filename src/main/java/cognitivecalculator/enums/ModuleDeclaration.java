package cognitivecalculator.enums;

public enum ModuleDeclaration {

	CLASS_OR_INTERFACE_DECLARATION("ClassOrInterfaceDeclaration"),
	FIELD_DECLARATION("FieldDeclaration"),
	CONSTRUCTOR_DECLARATION("ConstructorDeclaration"),
	METHOD_DECLARATION("MethodDeclaration"),
	ENUM_DECLARATION("EnumDeclaration"),
	//RECORD_DECLARATION("RecordDeclaration"), It seems like it doesn't really exist
	ENUM_CONST_DECLARATION("EnumConstantDeclaration"),
	INITIALIZER_DECLARATION("InitializerDeclaration"),
	ENTITY_DECLARATION_NOT_FOUND("EntityDeclarationTypeNotFound");
	
	private String entity;
	
	private ModuleDeclaration(String entity) {
		this.entity = entity;
	}
	
	public String getEntityName() {
		return this.entity;
	}
	
	public static ModuleDeclaration getEntityTypeFromString(String entitySimpleName) {
		ModuleDeclaration entity = ENTITY_DECLARATION_NOT_FOUND;
		
		for(ModuleDeclaration type: ModuleDeclaration.values()) {
			if(type.getEntityName().equals(entitySimpleName)) {
				entity = type;
				break;
			}
		}
		
		return entity;
	}
}
