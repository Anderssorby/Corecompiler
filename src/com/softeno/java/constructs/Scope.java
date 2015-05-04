package com.softeno.java.constructs;

import java.util.Vector;

public class Scope {
	
	private Constraint owner;
	
	private Scope parent;
	
	private Vector<Constraint> variables = new Vector<Constraint>();
	
	public Scope(Constraint owner, Scope parent) {
		this.owner = owner;
		this.parent = parent;
	}
	
	public void register(Constraint variable) {
		variables.add(variable);
	}
	
	public Scope getParent() {
		return parent;
	}

	public Constraint getOwner() {
		return owner;
	}
}