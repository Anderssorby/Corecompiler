package com.softeno.java.compiler;

public class SyntaxError extends RuntimeException {

	public SyntaxError(String message) {
		super(message);
	}

	public SyntaxError(Symbol symbol, CompilationUnit unit) {
		super("Unexpected " + symbol.getToken().toString() + " ("
				+ unit.getFileName() + ":" + symbol.line() + ")");
	}
}
