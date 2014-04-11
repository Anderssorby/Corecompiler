package compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import constructs.Definition;
import constructs.Expression.ArithmeticExpression;
import constructs.FormConstruct;
import constructs.ListConstruct;
import constructs.Definition.ConstrainType;
import constructs.Expression;

public class Compiler {

	private Lexer lexer;
	private DefinitionReader definition;
	private ExpressionReader expressionReader;

	public Compiler() {
		definition = new DefinitionReader();
		expressionReader = new ExpressionReader();
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
		while (lexer.hasNext()) System.out.println(lexer.nextToken().getToken().name()+", ");
		lexer.reset();
		System.err.println("Parsing...");
		Vector<Definition> top = new Vector<Definition>();
		try {
			while (lexer.hasNext()) {
				Symbol symbol = lexer.nextToken();
				top.add(definition.read(symbol, lexer));
			}
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
		System.out.println(top);
	}

	public class DefinitionReader implements Interpreter<Definition> {

		private HashMap<String, Definition> definitions = new HashMap<String, Definition>();

		private Vector<Definition> last = new Vector<Definition>();

		@Override
		public Definition read(Symbol symbol, Lexer lexer) throws SyntaxError {
			switch (symbol.getToken()) {
			case SQUARE_BRACES_LEFT: {
				Definition definition = new Definition(
						ConstrainType.EXPRESSIVE, null);
				definition.setValue(expressionReader.read(lexer.nextToken(), lexer));
				last.add(definition);
				return definition;
			}
			case CONSTRAIN: {
				Definition hold = last.lastElement();
				hold.constrain(read(lexer.nextToken(), lexer));
				return hold;
			}
			case NAME: {
				Definition definition = new Definition(ConstrainType.NAMED,
						null);
				definition.setValue(symbol.getValue());
				definitions.put(symbol.getValue(), definition);
				last.add(definition);
				return definition;
			}
			case CURLY_BRACES_LEFT: {
				ListConstruct list = new ListConstruct(null);
				last.add(list);
				list.add(read(lexer.nextToken(), lexer));
				removeTail(last, list);
				return list;
			}
			case COMMA: {
				Definition definition = read(lexer.nextToken(), lexer);
				removeTail(last, definition);
				return definition;
			}
			case CURLY_BRACES_RIGHT: {
				// find back
				ListConstruct list = null;
				for (int i = last.size()-1; i >= 0; i--) {
					if (last.get(i) instanceof ListConstruct) {
						list = (ListConstruct) last.get(i);
						break;
					}
				}
				if (list==null) {
					throw new SyntaxError("Unexpected }");
				}
				removeTail(last, list);
				return list;
			}
			case PARENTHESIS_LEFT: {
				FormConstruct list = new FormConstruct(null);
				last.add(list);
				list.add(read(lexer.nextToken(), lexer));
				removeTail(last, list);
				return list;
			}
			default: {
				return null;
			}
			}
		}

	}

	public class ExpressionReader implements Interpreter<Expression> {

		private ArithmeticExpression expression;
		
		public ExpressionReader() {
			expression = Expression.createArithmeticExpression();
		}

		@Override
		public Expression read(Symbol symbol, Lexer lexer) throws SyntaxError {
			switch (symbol.getToken()) {
			case NUMBER:
				Integer inte = Integer.parseInt(symbol.getValue());
				expression.integer(inte);
				return read(lexer.nextToken(), lexer);
			case PLUS:
				expression.plus();
				return read(lexer.nextToken(), lexer);
			case MINUS: {
				expression.minus();
				return read(lexer.nextToken(), lexer);
			}
			case MULTIPLY: {
				expression.multiply();
				return read(lexer.nextToken(), lexer);
			}
			case DIVIDE: {
				expression.divide();
				return read(lexer.nextToken(), lexer);
			}
			case PERCENT: {
				expression.modulo();
				return read(lexer.nextToken(), lexer);
			}
			case PARENTHESIS_LEFT: {
				expression.startParenthesis();
				return read(lexer.nextToken(), lexer);
			}
			case PARENTHESIS_RIGHT: {
				expression.endParenthesis();
				return read(lexer.nextToken(), lexer);
			}
			default: {
				// End of parsing
				expression.end();
				Expression ex = expression;
				expression = Expression.createArithmeticExpression();
				return ex;
			}
			}
		}

	}

	public <T> void removeTail(Vector<T> last, T element) {
		int l = last.indexOf(element);
		for (int i = last.size()-1; i > l; i--) {
			last.remove(i);
		}
	}

}
