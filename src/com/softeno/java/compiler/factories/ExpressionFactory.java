package com.softeno.java.compiler.factories;

import com.softeno.java.compiler.PatternComponent;
import com.softeno.java.compiler.Symbol;
import com.softeno.java.constructs.Construct;
import com.softeno.java.constructs.Expression;

public class ExpressionFactory implements ConstructFactory<Expression> {

	private Expression expression;

	
	public ExpressionFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Expression getProduct() {
		// TODO Auto-generated method stub
		return expression;
	}

	@Override
	public boolean addToAssembly(Symbol symbol) {
		switch (symbol.getToken().toString()) {
		case "STRING_LITTERAL": {
			loadExpression();
			expression.string(symbol.getValue());
//			return expression;
			return true;
		}
		case "NAME": {
			loadExpression();
//			expression.constraint(definitionReader.read(symbol, lexer));
//			return expression;
			return true;
		}
		case "NUMBER": {
			loadExpression();
			Integer inte = Integer.parseInt(symbol.getValue());
			expression.integer(inte);
//			return expression;
			return true;
		}
		case "PLUS": {
			loadExpression();
			expression.plus();
//			return expression;
			return true;
		}
		case "MINUS": {
			loadExpression();
			expression.minus();
//			return expression;
			return true;
		}
		case "MULTIPLY": {
			loadExpression();
			expression.multiply();
//			return expression;
			return true;
		}
		case "DIVIDE": {
			loadExpression();
			expression.divide();
//			return expression;
			return true;
		}
		case "PERCENT": {
			loadExpression();
			expression.modulo();
//			return expression;
			return true;
		}
		case "PARENTHESIS_LEFT": {
			loadExpression();
			expression.startParenthesis();
//			return expression;
			return true;
		}
		case "PARENTHESIS_RIGHT": {
			loadExpression();
			expression.endParenthesis();
//			return expression;
			return true;
		}
		default: {
//			lexer.back();
			// End of parsing
			expression.end();
			Expression ex = expression;
			expression = null;
//			return ex;
			return false;
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

	@Override
	public void setPattern(PatternComponent[] pattern) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addToAssembly(Construct construct) {
		// TODO Auto-generated method stub
		
	}

}
