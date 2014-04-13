package compiler;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import constructs.Definition;
import constructs.Definition.ConstrainType;
import constructs.Expression;
import constructs.FormConstruct;
import constructs.ListConstruct;
import constructs.Scope;

public class CompilationUnit {
	
	private DefinitionReader definition;
	private ExpressionReader expressionReader;
	private Compiler compiler;
	private String fileName;
	private String name;
	
	public CompilationUnit(Compiler compiler, String name) {
		this.compiler = compiler;
		this.fileName = name;
		this.name = name.replaceFirst("\\.[A-Za-z]+$", "");
		definition = new DefinitionReader(this);
		expressionReader = new ExpressionReader(this);
	}
	
	public void parse(String text, Lexer lexer) {
		System.err.println("Scanning...");
		lexer.scann(text);
		while (lexer.hasNext())
			System.out.println(lexer.nextToken().getToken().name() + ", ");
		lexer.reset();
		System.err.println("Parsing...");
		Vector<Definition> top = new Vector<Definition>();
		try {
			while (lexer.hasNext()) {
				Symbol symbol = lexer.nextToken();
				Definition c = definition.read(symbol, lexer);
				top.add(c);
				if (c instanceof ImportRule) {
					ImportRule rule = (ImportRule) c;
					compiler.createCompilationUnit(new File(rule.getTargetPathName()));
					
				}
			}
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
		System.out.println(top);
	}
	
	public class DefinitionReader extends Reader<Definition> {

		private HashMap<String, Definition> definitions = new HashMap<String, Definition>();

		private Vector<Definition> last = new Vector<Definition>();

		private Vector<Scope> scopes = new Vector<Scope>();
	
		public DefinitionReader(CompilationUnit unit) {
			super(unit);
			scopes.add(new Scope(null, null));
		}

		@Override
		public Definition read(Symbol symbol, Lexer lexer) throws SyntaxError {
			switch (symbol.getToken()) {
			case SQUARE_BRACES_LEFT: {
				Scope current = scopes.lastElement();
				Definition definition = new Definition(ConstrainType.EXPRESSIVE);
				current.register(definition);
				definition.setValue(unit.expressionReader.read(lexer.nextToken(),
						lexer));
				last.add(definition);
				return read(lexer.nextToken(), lexer);
			}
			case SQUARE_BRACES_RIGHT: {
				Definition definition = last.lastElement();
				if (definition.getType() != ConstrainType.EXPRESSIVE) {
					throw new SyntaxError(symbol, unit);
				}
				return definition;
			}
			case CONSTRAIN: {
				Definition hold = last.lastElement();
				Scope current = scopes.lastElement();
				Scope next = new Scope(hold, current);
				scopes.add(next);
				hold.constrain(read(lexer.nextToken(), lexer));
				removeTail(scopes, current);
				removeTail(last, hold);
				return hold;
			}
			case NAME: {
				Scope current = scopes.lastElement();
				Definition definition = new Definition(ConstrainType.NAMED);
				definition.setValue(symbol.getValue());
				current.register(definition);
				definitions.put(symbol.getValue(), definition);
				last.add(definition);
				return definition;
			}
			case CURLY_BRACES_LEFT: {
				Scope current = scopes.lastElement();
				ListConstruct list = new ListConstruct();
				last.add(list);
				current.register(list);
				list.add(read(lexer.nextToken(), lexer));
				removeTail(scopes, current);
				removeTail(last, list);
				return list;
			}
			case COMMA: {
				Scope current = scopes.lastElement();
				Definition definition = read(lexer.nextToken(), lexer);
				removeTail(scopes, current);
				removeTail(last, definition);
				return definition;
			}
			case CURLY_BRACES_RIGHT: {
				// find back
				ListConstruct list = null;
				for (int i = last.size() - 1; i >= 0; i--) {
					if (last.get(i) instanceof ListConstruct) {
						list = (ListConstruct) last.get(i);
						break;
					}
				}
				if (list == null) {
					throw new SyntaxError(symbol, unit);
				}
				removeTail(last, list);
				return list;
			}
			case PARENTHESIS_LEFT: {
				Scope current = scopes.lastElement();
				FormConstruct list = new FormConstruct();
				last.add(list);
				current.register(list);
				list.add(read(lexer.nextToken(), lexer));
				removeTail(scopes, current);
				removeTail(last, list);
				return list;
			}
			case PARENTHESIS_RIGHT: {
				FormConstruct list = null;
				for (int i = last.size() - 1; i >= 0; i--) {
					if (last.get(i) instanceof FormConstruct) {
						list = (FormConstruct) last.get(i);
						break;
					}
				}
				if (list == null) {
					throw new SyntaxError(symbol, unit);
				}
				removeTail(last, list);
				return list;
			}
			default: {
				throw new SyntaxError(symbol, unit);
			}
			}
		}

	}

	public class ExpressionReader extends Reader<Expression> {

		private Expression expression;

		public ExpressionReader(CompilationUnit unit) {
			super(unit);
			expression = Expression.createExpression();
		}

		@Override
		public Expression read(Symbol symbol, Lexer lexer) throws SyntaxError {
			switch (symbol.getToken()) {
			case STRING_LITTERAL: {
				loadExpression();
				expression.string(symbol.getValue());
				return read(lexer.nextToken(), lexer);
			}
			case NAME: {
				loadExpression();
				expression.name(symbol.getValue());
				return read(lexer.nextToken(), lexer);
			}
			case NUMBER: {
				loadExpression();
				Integer inte = Integer.parseInt(symbol.getValue());
				expression.integer(inte);
				return read(lexer.nextToken(), lexer);
			}
			case PLUS: {
				loadExpression();
				expression.plus();
				return read(lexer.nextToken(), lexer);
			}
			case MINUS: {
				loadExpression();
				expression.minus();
				return read(lexer.nextToken(), lexer);
			}
			case MULTIPLY: {
				loadExpression();
				expression.multiply();
				return read(lexer.nextToken(), lexer);
			}
			case DIVIDE: {
				loadExpression();
				expression.divide();
				return read(lexer.nextToken(), lexer);
			}
			case PERCENT: {
				loadExpression();
				expression.modulo();
				return read(lexer.nextToken(), lexer);
			}
			case PARENTHESIS_LEFT: {
				loadExpression();
				expression.startParenthesis();
				return read(lexer.nextToken(), lexer);
			}
			case PARENTHESIS_RIGHT: {
				loadExpression();
				expression.endParenthesis();
				return read(lexer.nextToken(), lexer);
			}
			default: {
				lexer.back();
				// End of parsing
				expression.end();
				return expression;
			}
			}
		}

		public Expression loadExpression() {
			if (expression == null || expression.hasEnded()) {
				expression = Expression.createExpression();
			}
			return expression;
		}

	}

	public static <T> void removeTail(Vector<T> last, T element) {
		int l = last.indexOf(element);
		if (l != -1)
			for (int i = last.size() - 1; i > l; i--) {
				last.remove(i);
			}
	}

	public String getFileName() {
		return fileName;
	}

	public String getName() {
		return name;
	}
}