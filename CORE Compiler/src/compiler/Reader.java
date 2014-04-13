package compiler;

import constructs.Construct;

public abstract class Reader<O extends Construct> implements Interpreter<O> {
	
	protected CompilationUnit unit;


	public Reader(CompilationUnit unit) {
		this.unit = unit;
	}
}
