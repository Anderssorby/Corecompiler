package compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import constructs.Construct;
import constructs.Definition;
import constructs.Definition.ConstrainType;
import constructs.Expression;
import constructs.FormConstruct;
import constructs.ListConstruct;
import constructs.Scope;

public class CompilationUnit {

	private DefinitionReader definitionReader;
	private ExpressionReader expressionReader;
	private Compiler compiler;
	private File file;
	private String name;
	private String text;

	public CompilationUnit(Compiler compiler, File file) {
		this.compiler = compiler;
		this.file = file;
		this.name = file.getName().replaceFirst("\\.[A-Za-z]+$", "");
		definitionReader = new DefinitionReader(this);
		expressionReader = new ExpressionReader(this);
	}

	public void parse(Lexer lexer) {
		System.out.println("Scanning...");
		lexer.scann(text);
		while (lexer.hasNext())
			System.out.println(lexer.nextToken().getToken().name() + ", ");
		lexer.reset();
		System.out.println("Parsing...");
		Vector<Construct> top = new Vector<Construct>();
		try {
			while (lexer.hasNext()) {
				Construct c = readNext(lexer);
				top.add(c);
				if (c instanceof MetaConstraint) {
					MetaConstraint rule = (MetaConstraint) c;
					String pathname = rule.getFormative().get(0).getValue();
					compiler.createCompilationUnit(new File(pathname));

				}
			}
		} catch (SyntaxError e) {
			e.printStackTrace();
		}
		System.out.println(top);
	}

	public Construct readNext(Lexer lexer) throws SyntaxError {
		Symbol symbol = lexer.nextToken();
		Definition definition = definitionReader.read(symbol, lexer);
		if (definition == null) {
			Expression ex = expressionReader.read(symbol, lexer);
			definition = new Definition(ConstrainType.EXPRESSIVE);
			definition.setValue(ex);
			return ex;
		}
		return definition;
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
		public Definition read(Symbol symbol, Lexer lexer) {
			switch (symbol.getToken()) {
			case SQUARE_BRACES_LEFT: {
				Scope current = scopes.lastElement();
				Definition definition = new Definition(ConstrainType.EXPRESSIVE);
				// TODO generalize evaluatives and formatives
				Definition las = last.lastElement();
				if (las.getType() == ConstrainType.NAMED) {
					las.setEvaluative(definition);
				}
				current.register(definition);
				last.add(definition);
				definition.setValue(readNext(lexer));
				readNext(lexer);
				removeTail(last, definition);
				return definition;
			}
			case SQUARE_BRACES_RIGHT: {

				// TODO fix for recursion
				Definition definition = last.lastElement();
				if (definition.getType() != ConstrainType.EXPRESSIVE) {
					throw new SyntaxError(symbol, unit);
				}
				return definition;
			}
			case CONSTRAIN: {
				Definition hold = last.lastElement();
				Scope current = scopes.lastElement();
				Scope scope = new Scope(hold, current);
				scopes.add(scope);
				Definition next = (Definition) readNext(lexer);
				if (next == null) {
					throw new SyntaxError(symbol, unit);
				}
				hold.constrain(next);
				removeTail(scopes, current);
				return hold;
			}
			case NAME: {
				Scope current = scopes.lastElement();
				Definition definition = new Definition(ConstrainType.NAMED);
				definition.setValue(symbol.getValue());
				current.register(definition);
				definitions.put(symbol.getValue(), definition);
				last.add(definition);
				readNext(lexer);
				removeTail(last, definition);
				return definition;
			}
			case CURLY_BRACES_LEFT: {
				Scope current = scopes.lastElement();
				ListConstruct list = new ListConstruct();
				last.add(list);
				current.register(list);
				while (lexer.hasNext()) {
					Definition next = (Definition) readNext(lexer);
					removeTail(last, list);
					if (next == null)
						break;
					list.add(next);
				}
				readNext(lexer);
				return list;
			}
			case COMMA: {
				// Scope current = scopes.lastElement();
				// Definition definition = readNext(lexer);
				// removeTail(scopes, current);
				// removeTail(last, definition);
				return null;
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
				return null;
			}
			case PARENTHESIS_LEFT: {
				Scope current = scopes.lastElement();
				FormConstruct list = new FormConstruct();
				// TODO generalize evaluatives and formatives
				Definition las = last.lastElement();
				if (las.getType() == ConstrainType.NAMED) {
					las.setFormative(list);
				}
				last.add(list);
				current.register(list);
				while (lexer.hasNext()) {
					Definition next = (Definition) readNext(lexer);
					removeTail(last, list);
					if (next == null)
						break;
					list.add(next);
				}
				readNext(lexer);
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
				return null;
			}
			case META_CONSTRAINT: {
				MetaConstraint constraint = new MetaConstraint(
						symbol.getValue());
				readNext(lexer);
				return constraint;
			}
			default: {
				return null;
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
		public Expression read(Symbol symbol, Lexer lexer) {
			switch (symbol.getToken()) {
			case STRING_LITTERAL: {
				loadExpression();
				expression.string(symbol.getValue());
				return expression;
			}
			case NAME: {
				loadExpression();
				expression.constraint(definitionReader.read(symbol, lexer));
				return expression;
			}
			case NUMBER: {
				loadExpression();
				Integer inte = Integer.parseInt(symbol.getValue());
				expression.integer(inte);
				return expression;
			}
			case PLUS: {
				loadExpression();
				expression.plus();
				return expression;
			}
			case MINUS: {
				loadExpression();
				expression.minus();
				return expression;
			}
			case MULTIPLY: {
				loadExpression();
				expression.multiply();
				return expression;
			}
			case DIVIDE: {
				loadExpression();
				expression.divide();
				return expression;
			}
			case PERCENT: {
				loadExpression();
				expression.modulo();
				return expression;
			}
			case PARENTHESIS_LEFT: {
				loadExpression();
				expression.startParenthesis();
				return expression;
			}
			case PARENTHESIS_RIGHT: {
				loadExpression();
				expression.endParenthesis();
				return expression;
			}
			default: {
				lexer.back();
				// End of parsing
				expression.end();
				Expression ex = expression;
				expression = null;
				return ex;
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
		return file.getName();
	}

	public String getName() {
		return name;
	}

	public void loadFile() {
		try {
			FileReader fr = new FileReader(file);
			char[] buff = new char[(int) file.length()];
			System.out.println("Reading file...");
			fr.read(buff);
			fr.close();
			text = new String(buff);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}