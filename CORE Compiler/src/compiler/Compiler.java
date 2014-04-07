package compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import constructs.Definition;
import constructs.Definition.ConstrainType;
import constructs.Expression;

public class Compiler {

	private Lexer lexer;
	private DefinitionReader definition;
	private ExpressionReader expression;

	public Compiler() {
		definition = new DefinitionReader();
	}

	public static void main(String[] args) {
		Compiler c = new Compiler();
		String text = new String();
		try {
			File file = new File(args[0]);
			FileReader fr = new FileReader(file);
			char[] buff = new char[(int) file.length()];
			System.err.println("Reading file...");
			fr.read(buff);
			fr.close();
			text = new String(buff);
		} catch (IOException e) {
			e.printStackTrace();
		}
		c.parse(text);
	}

	private void parse(String text) {
		lexer = new Lexer(Token.values());
		System.err.println("Scanning...");
		lexer.scann(text);
		System.err.println("Parsing...");
		while (lexer.hasNext()) {
			Symbol symbol = lexer.nextToken();
			definition.read(symbol, lexer);
		}
	}

	public class DefinitionReader implements Interpreter<Definition> {

		private HashMap<String, Definition> definitions;
		
		private Definition last;
		
		@Override
		public Definition read(Symbol symbol, Lexer lexer) {
			switch (symbol.getToken()) {
			case SQUARE_BRACES_LEFT: {
				Definition definition = new Definition(ConstrainType.EXPRESSIVE, null);
				definition.setValue(expression.read(lexer.nextToken(), lexer));
				return definition;
			}
			case CONSTRAIN: {
				return definition.read(lexer.nextToken(), lexer);
			}
			case NAME: {
				Definition definition = new Definition(ConstrainType.NAMED, null);
				definition.setValue(symbol.getValue());
				definitions.put(symbol.getValue(), definition);
				last = definition;
				return definition;
			}
			default: {
				return null;
			}
			}
		}

	}
	
	public class ExpressionReader implements Interpreter<Expression> {

		private Expression expression;
		
		@Override
		public Expression read(Symbol symbol, Lexer lexer) {
			if (expression.hasEnded()) {
				expression = new Expression();
			}
			switch (symbol.getToken()) {
			case NUMBER:
				Integer inte = Integer.parseInt(symbol.getValue());
				expression.integer(inte);
				return read(lexer.nextToken(), lexer);
			default:
				// End of parsing
				expression.end();
				return expression;
			}
		}

	}

}
