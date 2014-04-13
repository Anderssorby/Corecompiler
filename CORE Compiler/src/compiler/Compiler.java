package compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
		CompilationUnit unit = new CompilationUnit(this, file.getName());
		try {
			FileReader fr = new FileReader(file);
			char[] buff = new char[(int) file.length()];
			System.err.println("Reading file...");
			fr.read(buff);
			fr.close();
			String text = new String(buff);
			unit.parse(text, lexer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		units.add(unit);
		return unit;
	}
	
	

}
