package compiler;

import constructs.Construct;

public enum SimplePattern implements TokenPattern {
	NAMED(new Token[] { Token.NAME }, new DefinitionFactory()),

	EXPRESSIVE(new Token[] { Token.SQUARE_BRACES_LEFT,
			Token.SQUARE_BRACES_RIGHT }, new DefinitionFactory()),

	STANDARD_LIST(new PatternComponent[] {}, new ListFactory()),

	LIST(new PatternComponent[] { Token.CURLY_BRACES_LEFT, STANDARD_LIST,
			Token.CURLY_BRACES_RIGHT }, new ListFactory()),

	FORM(new PatternComponent[] { Token.PARENTHESIS_LEFT, STANDARD_LIST, Token.PARENTHESIS_RIGHT },
			new FormFactory()),

	META(new PatternComponent[] { Token.META_CONSTRAINT }, new DefinitionFactory()),

	EXPRESSION(new Token[] { Token.PLUS }, new ExpressionFactory());
	public PatternComponent[] tokenOrder;

	public int pointer = 0;

	private ConstructFactory<?> factory;

	SimplePattern(PatternComponent[] tokens, ConstructFactory<?> factory) {
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
		return TOKEN_ILLEGAL;
	}

	@Override
	public Construct getProduct() {
		return factory.getProduct();
	}

}
