package com.softeno.java.compiler;

import com.softeno.java.constructs.Construct;

public interface Interpreter<O extends Construct> {
	
	public O read(Symbol symbol, Lexer lexer);
}
