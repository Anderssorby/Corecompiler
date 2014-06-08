package com.softeno.java.constructs;

import java.util.Vector;

public class ArrayConstruct extends Definition {

	private Vector<Constraint> elements = new Vector<Constraint>();

	public ArrayConstruct(ConstrainType type) {
		super(type);
	}

	public void add(Constraint definition) {
		elements.add(definition);
	}
	
	public Constraint get(int index) {
		return elements.get(index);
	}

}