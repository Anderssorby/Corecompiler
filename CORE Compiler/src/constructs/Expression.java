package constructs;

import java.util.Vector;

import compiler.SyntaxError;

public abstract class Expression implements Construct {

	protected Vector<Element> mapping = new Vector<Element>();
	protected Vector<Object> values = new Vector<Object>();
	protected boolean ended = false;

	protected interface Element {
		
	};
	protected enum ArithmeticElement implements Element {
		PLUS, MINUS, MODULO, PARENTESIS_START, PARENTESIS_END, INTEGER, FLOAT, DOUBLE, MULTIPLY, DIVISION, END
	};
	public static class ArithmeticExpression extends Expression {

		private ArithmeticExpression() {
			
		}
		
		public void end() {
			ended = true;
			mapping.add(ArithmeticElement.END);
			values.add(null);
		}
		
		public boolean hasEnded() {
			return ended;
		}

		public void integer(Integer inte) {
			// TODO Auto-generated method stub
			mapping.add(ArithmeticElement.INTEGER);
			values.add(inte);
		}

		public void multiply() {
			// TODO Auto-generated method stub
			mapping.add(ArithmeticElement.MULTIPLY);
			values.add(null);
		}

		public void divide() {
			mapping.add(ArithmeticElement.DIVISION);
			values.add(null);
		}

		public void modulo() {
			// TODO Auto-generated method stub
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
	}

	public static ArithmeticExpression createArithmeticExpression() {
		return new ArithmeticExpression();
	}


	public void end() {
		ended = true;
	}
	
	public boolean hasEnded() {
		return ended;
	}

	
}
