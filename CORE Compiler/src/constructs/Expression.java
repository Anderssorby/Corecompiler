package constructs;

import java.util.Vector;

public class Expression implements Construct {

	private Vector<Element> mapping = new Vector<Element>();
	private Vector<Object> values = new Vector<Object>();
	private boolean ended = false;

	protected enum Element {
		PLUS, MINUS, MODULO, PARENTESIS_START, PARENTESIS_END, INTEGER, FLOAT, DOUBLE, MULTIPLY, DIVISION, END
	};

	protected boolean isOperational(Element element, boolean includeMinus) {
		switch (element) {
		case MODULO: case MULTIPLY:
		case DIVISION:
			return true;
		case MINUS:
			return includeMinus;
		default:
			return false;
		}
	}

	public void plus() {
		Element previous = mapping.lastElement();
		if (isOperational(previous, true)) {
			throw new IllegalArgumentException();
		}
		mapping.add(Element.PLUS);
		values.add(null);
	}

	public void minus() {
		Element previous = mapping.lastElement();
		if (isOperational(previous, true)) {
			throw new IllegalArgumentException();
		}
		mapping.add(Element.MINUS);
		values.add(null);
	}

	public void end() {
		ended = true;
		mapping.add(Element.END);
		values.add(null);
	}
	
	public boolean hasEnded() {
		return ended;
	}

	public void integer(Integer inte) {
		// TODO Auto-generated method stub
		mapping.add(Element.INTEGER);
		values.add(inte);
	}
	
}
