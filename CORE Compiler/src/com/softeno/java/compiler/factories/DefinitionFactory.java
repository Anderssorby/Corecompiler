package com.softeno.java.compiler.factories;

import com.softeno.java.compiler.MetaConstraint;
import com.softeno.java.compiler.PatternComponent;
import com.softeno.java.compiler.Symbol;
import com.softeno.java.constructs.Construct;
import com.softeno.java.constructs.Definition;
import com.softeno.java.constructs.Definition.ConstrainType;
import com.softeno.java.constructs.FormConstruct;

public class DefinitionFactory implements ConstructFactory<Definition> {

	private Definition product;
	private boolean ended = false;

	@Override
	public Definition getProduct() {
		return product;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean addToAssembly(Symbol symbol) {
		switch (symbol.getToken().toString()) {
		case "NAME":
			product = new Definition(ConstrainType.NAMED);
			ended = true;
			return true;
		case "SQUARE_BRACES_LEFT": {
			// Scope current = scopes.lastElement();
			product = new Definition(ConstrainType.EXPRESSIVE);

			// TODO generalize evaluatives and formatives
			// Definition las = last.lastElement();
			// if (las.getType() == ConstrainType.NAMED) {
			// las.setEvaluative(definition);
			// }
			// current.register(definition);
			// last.add(definition);
			// definition.setValue(readNext(lexer));
			// readNext(lexer);
			// removeTail(last, definition);
			return true;
		}
		case "SQUARE_BRACES_RIGHT": {

			// TODO fix for recursion
			ended = true;
			return true;
		}
	/*	case CONSTRAIN: {
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
		// case NAME: {
		// Scope current = scopes.lastElement();
		// Definition definition = new Definition(ConstrainType.NAMED);
		// definition.setValue(symbol.getValue());
		// current.register(definition);
		// definitions.put(symbol.getValue(), definition);
		// last.add(definition);
		// readNext(lexer);
		// removeTail(last, definition);
		// return definition;
		// }
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
			return true;
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
			return true;
	//	}*/
		case "META_CONSTRAINT": {
			product = new MetaConstraint(symbol.getValue());
			return true;
		}
		default:
			return false;
		}
	}

	@Override
	public void setPattern(PatternComponent[] pattern) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToAssembly(Construct construct) {
		if (construct instanceof FormConstruct){
			product.setFormative((FormConstruct) construct);
		} else if (construct instanceof Definition) {
			product.setEvaluative((Definition) construct);
		}
		
	}

}
