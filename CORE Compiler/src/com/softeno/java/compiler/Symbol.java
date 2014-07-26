package com.softeno.java.compiler;

public class Symbol {

	private Token token;
	private String value;
	private int line;

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
	
	public void setLine(int line) {
		this.line = line;
	}

	public int line() {
		return line;
	}

}
