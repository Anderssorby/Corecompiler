package compiler;

import constructs.Expression;

public class ExpressionFactory implements ConstructFactory<Expression> {

	private Expression expression;

	
	public ExpressionFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Expression getProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression addToAssembly(Symbol symbol) {
		switch (symbol.getToken()) {
		case STRING_LITTERAL: {
			loadExpression();
			expression.string(symbol.getValue());
			return expression;
		}
		case NAME: {
			loadExpression();
//			expression.constraint(definitionReader.read(symbol, lexer));
			return expression;
		}
		case NUMBER: {
			loadExpression();
			Integer inte = Integer.parseInt(symbol.getValue());
			expression.integer(inte);
			return expression;
		}
		case PLUS: {
			loadExpression();
			expression.plus();
			return expression;
		}
		case MINUS: {
			loadExpression();
			expression.minus();
			return expression;
		}
		case MULTIPLY: {
			loadExpression();
			expression.multiply();
			return expression;
		}
		case DIVIDE: {
			loadExpression();
			expression.divide();
			return expression;
		}
		case PERCENT: {
			loadExpression();
			expression.modulo();
			return expression;
		}
		case PARENTHESIS_LEFT: {
			loadExpression();
			expression.startParenthesis();
			return expression;
		}
		case PARENTHESIS_RIGHT: {
			loadExpression();
			expression.endParenthesis();
			return expression;
		}
		default: {
//			lexer.back();
			// End of parsing
			expression.end();
			Expression ex = expression;
			expression = null;
			return ex;
		}
		}
	}
	
	public Expression loadExpression() {
		if (expression == null || expression.hasEnded()) {
			expression = Expression.createExpression();
		}
		return expression;
	}

	@Override
	public boolean hasEnded() {
		// TODO Auto-generated method stub
		return false;
	}

}
