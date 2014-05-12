package compiler;

import constructs.Construct;

public interface Interpreter<O extends Construct> {
	
	public O read(Symbol symbol, Lexer lexer);
}
