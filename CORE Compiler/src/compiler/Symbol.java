package compiler;

public class Symbol {

	private Token token;
	private String value;

	public Symbol(Token token) {
		this(token, null);
	}
	
	public Symbol(Token token, String value) {
		this.token = token;
		this.value = value;
	}

	public Token getToken() {
		return token;
	}

	public String getValue() {
		return value;
	}

}
