package compiler;

import java.io.File;
import java.util.Vector;

public class Compiler {

	private Lexer lexer;
		
	private Vector<CompilationUnit> units = new Vector<CompilationUnit>();
	
	public Compiler() {
		lexer = new Lexer(Token.values());
	}

	public static void main(String[] args) {
		Compiler c = new Compiler();
		File file = new File(args[0]);
		c.createCompilationUnit(file);
	}

	public CompilationUnit createCompilationUnit(File file) {
		CompilationUnit unit = new CompilationUnit(this, file);
		unit.loadFile();
		unit.parse(lexer);
		units.add(unit);
		return unit;
	}
	
	

}
