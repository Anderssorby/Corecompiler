package com.softeno.java.compiler;

import java.io.File;
import java.util.Vector;

public class Compiler {

		
	private Vector<CompilationUnit> units = new Vector<CompilationUnit>();
	
	public Compiler() {
		
	}

	public static void main(String[] args) {
		Compiler c = new Compiler();
		File file = new File(args[0]);
		c.createCompilationUnit(file);
	}

	public CompilationUnit createCompilationUnit(File file) {
		CompilationUnit unit = new CompilationUnit(this, file);
		unit.loadFile();
		unit.parse();
		units.add(unit);
		return unit;
	}
	
	public CompilationUnit getCompilationUnit(Lexer lexer) {
		for (CompilationUnit unit: units) {
			if (unit.getLexer().equals(lexer))
				return unit;
		}
		return null;
	}

}
