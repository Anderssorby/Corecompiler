package constructs;

import java.util.Vector;

public class ArrayConstruct extends Definition {

	private Vector<Definition> elements = new Vector<Definition>();

	public ArrayConstruct(ConstrainType type) {
		super(type);
	}

	public void add(Definition definition) {
		elements.add(definition);
	}

}