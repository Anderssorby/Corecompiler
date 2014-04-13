package constructs;

import java.util.Vector;

import compiler.SyntaxError;

public class Expression implements Construct {

	protected Vector<Element> mapping = new Vector<Element>();
	protected Vector<Object> values = new Vector<Object>();
	protected boolean ended = false;

	protected interface Element {
		
	}
	protected enum ArithmeticElement implements Element {
		PLUS, MINUS, MODULO, PARENTESIS_START, PARENTESIS_END, INTEGER, FLOAT, DOUBLE, MULTIPLY, DIVISION, END
	}
	protected enum CommonElement implements Element {
		NAME, STRING
	}
	
	private Expression() {
		
	}
	
	public void integer(Integer inte) {
		mapping.add(ArithmeticElement.INTEGER);
		values.add(inte);
	}
	
	public void insertFloat(Float inte) {
		mapping.add(ArithmeticElement.INTEGER);
		values.add(inte);
	}

	public void multiply() {
		mapping.add(ArithmeticElement.MULTIPLY);
		values.add(null);
	}

	public void divide() {
		mapping.add(ArithmeticElement.DIVISION);
		values.add(null);
	}

	public void modulo() {
		mapping.add(ArithmeticElement.MODULO);
		values.add(null);
	}
	
	public void startParenthesis() {
		mapping.add(ArithmeticElement.PARENTESIS_START);
		values.add(null);
	}

	public void endParenthesis() {
		mapping.add(ArithmeticElement.PARENTESIS_END);
		values.add(null);
	}
	
	protected boolean isOperational(ArithmeticElement element, boolean includeMinus) {
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

	public void plus() throws SyntaxError {
		ArithmeticElement previous = (ArithmeticElement) mapping.lastElement();
		if (isOperational(previous, true)) {
			throw new SyntaxError("Expected a nonoperational token");
		}
		mapping.add(ArithmeticElement.PLUS);
		values.add(null);
	}

	public void minus() throws SyntaxError {
		ArithmeticElement previous = (ArithmeticElement) mapping.lastElement();
		if (isOperational(previous, true)) {
			throw new SyntaxError("Expected a nonoperational token");
		}
		mapping.add(ArithmeticElement.MINUS);
		values.add(null);
	}
	
	public void string(String s) {
		values.add(s);
	}

	public static Expression createExpression() {
		return new Expression();
	}

	public void end() {
		ended = true;
	}
	
	public boolean hasEnded() {
		return ended;
	}

	public void name(String value) {
		mapping.add(null);
		values.add(value);
	}

	
}
