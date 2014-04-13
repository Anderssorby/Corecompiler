package constructs;

import java.util.Vector;

public class Definition implements Constraint, Construct {
	
	public enum ConstrainType {
		NAMED, EXPRESSIVE, LIST, FORM
	}

	private final ConstrainType type;
	private Object value;
	private Vector<Definition> constraints = new Vector<Definition>();
	
	public Definition(ConstrainType type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public void constrain(Definition definition) {
		constraints.add(definition);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(type.toString()+"["+value+"]");
		if (!constraints.isEmpty()) {
			sb.append("{");
			for (Definition def:constraints) sb.append(def);
			sb.append("}");
		}
		return sb.toString();
	}

	public ConstrainType getType() {
		return type;
	}

}
