package constructs;

public class Definition implements Construct {
	
	public class Scope {
		
	}
	
	public enum ConstrainType {
		NAMED, EXPRESSIVE, LIST, FORM
	}

	private Scope scope;
	private ConstrainType type;
	private Object value;
	
	public Definition(ConstrainType type, Scope scope) {
		this.type = type;
		this.scope = scope;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
