package com.softeno.java.constructs;

import java.util.Vector;

public class Definition implements Constraint, Construct {

	public enum ConstrainType {
		NAMED, EXPRESSIVE, LIST, FORM, META
	}

	private final ConstrainType type;
	private Object value;
	private Vector<Constraint> constraints = new Vector<Constraint>();
	private Definition evaluative;
	private FormConstruct formative;

	public Definition(ConstrainType type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void constrain(Constraint definition) {
		constraints.add(definition);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(type.toString() + "[" + value + "]");
		if (!constraints.isEmpty()) {
			sb.append("{");
			for (Constraint def : constraints)
				sb.append(def);
			sb.append("}");
		}
		return sb.toString();
	}

	public ConstrainType getType() {
		return type;
	}

	public void setEvaluative(Definition evaluative) {
		if (evaluative.type != ConstrainType.EXPRESSIVE) {
			throw new IllegalArgumentException("Evaluative must be expressive");
		}
		this.evaluative = evaluative;
	}

	public Definition getEvaluative() {
		return evaluative;
	}

	public void setFormative(FormConstruct list) {
		this.formative = list;
	}

	public FormConstruct getFormative() {
		return formative;
	}

	@Override
	public String getValue() {
		return value.toString();
	}

}
