package compiler;

import constructs.Construct;

public class SimplePattern implements TokenPattern {
	public Token[] tokenOrder;
	
	public int pointer = 0;

	private ConstructFactory factory;

	public SimplePattern(Token[] tokens, ConstructFactory factory) {
		this.tokenOrder = tokens;
		this.factory = factory;
	}

	@Override
	public int tryNextToken(Symbol symbol) {
		if (symbol.getToken().equals(tokenOrder[pointer])) {
			factory.addToAssembly(symbol);
			if (factory.hasEnded())
				return PATTERN_ENDED;
			return TOKEN_APROVED;
		}
		return TOKEN_ILLEAGAL;
	}

	@Override
	public Construct getProduct() {
		return factory.getProduct();
	}

}
