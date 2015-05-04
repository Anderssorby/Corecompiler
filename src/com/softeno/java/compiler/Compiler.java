package com.softeno.java.compiler;

import java.io.File;
import java.util.Vector;

public class Compiler {

	private Format format;
	private Vector<CompilationUnit> units = new Vector<CompilationUnit>();
	
	public Compiler(Format format) {
		this.format = format;
	}

	public static void main(String[] args) throws Exception {
		Format format = Format.parseFormatFile(new File("EBNF.txt"));
		Compiler c = new Compiler(format);
		if (verbose())
			format.printDefinitons();
		File file = new File(args[0]);
		c.createCompilationUnit(file);
	}

	public CompilationUnit createCompilationUnit(File file) {
		CompilationUnit unit = new CompilationUnit(this, file);
		unit.loadFile();
		unit.preprocess();
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

	public static boolean verbose() {
		return true;
	}

	public Format getFormat() {
		return format;
	}

}
