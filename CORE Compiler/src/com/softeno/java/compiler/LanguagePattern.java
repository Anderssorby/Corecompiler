package com.softeno.java.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.softeno.java.compiler.factories.ConstructFactory;
import com.softeno.java.compiler.factories.DefinitionFactory;
import com.softeno.java.compiler.factories.ExpressionFactory;
import com.softeno.java.compiler.factories.FormFactory;
import com.softeno.java.compiler.factories.ListFactory;
import com.softeno.java.constructs.Construct;

public class LanguagePattern extends TokenPattern {

	/**
	 * 
	 * @author Anders
	 * 
	 */
	public static class PatternReference implements PatternComponent {
		private String reference;

		private PatternReference(String reference) {
			this.reference = reference;
		}

		public LanguagePattern getReference() {
			return LanguagePattern.get(reference);
		}

		@Override
		public int recognise(Symbol token, int point) {
			return getReference().recognise(token, point);
		}
	}

	private static HashMap<String, LanguagePattern> values;

	/**
	 * Lazy pattern setup
	 */
	static {
		values = new HashMap<String, LanguagePattern>();

		values.put("NAMED", new LanguagePattern(DefinitionFactory.class,
				Token.NAME));

		values.put("EXPRESSION", new LanguagePattern(ExpressionFactory.class,
				Token.PLUS, Token.MINUS));

		values.put(
				"EXPRESSIVE",
				new LanguagePattern(DefinitionFactory.class, TokenPattern.and(
						Token.SQUARE_BRACES_LEFT, ref("EXPRESSION"),
						Token.SQUARE_BRACES_RIGHT)));

		values.put(
				"LIST",
				new LanguagePattern(ListFactory.class, TokenPattern.and(
						Token.CURLY_BRACES_LEFT, ref("STANDARD_LIST"),
						Token.CURLY_BRACES_RIGHT)));

		values.put(
				"FORM",
				new LanguagePattern(FormFactory.class, TokenPattern.and(
						Token.PARENTHESIS_LEFT, ref("STANDARD_LIST"),
						Token.PARENTHESIS_RIGHT)));

		values.put("META", new LanguagePattern(DefinitionFactory.class,
				Token.META_CONSTRAINT));

		values.put(
				"CONSTRAINT",
				new LanguagePattern(DefinitionFactory.class, TokenPattern.or(
						ref("NAMED"), ref("META"), ref("FORM"), ref("LIST"),
						ref("EXPRESSIVE")), TokenPattern.and(ref("CONSTRAINT"),
						Token.CONSTRAIN, ref("CONSTRAINT"))));

		values.put(
				"STANDARD_LIST",
				new LanguagePattern(ListFactory.class, TokenPattern.or(
						ref("CONSTRAINT"), TokenPattern.and(ref("CONSTRAINT"),
								Token.COMMA, ref("STANDARD_LIST")))));

	}

	private Class<? extends ConstructFactory<?>> factoryClass;
	private ConstructFactory<?> factory;

	private PatternOR combinedPattern;

	/**
	 * 
	 * @param factoryClass
	 * @param components
	 */
	LanguagePattern(Class<? extends ConstructFactory<?>> factoryClass,
			PatternComponent... components) {
		this.factoryClass = factoryClass;
		this.combinedPattern = TokenPattern.or(components);
		startFactory();
	}

	private void startFactory() {
		try {
			this.factory = factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to match the next token to the pattern. The method will return
	 * TOKEN_APPROVED (0) as long as the Pattern is still valid. If the method
	 * returns PATTERN_ENDED or TOKEN_ILLEGAL the pattern has either ended or is
	 * incorrect.
	 * 
	 * @param symbol
	 * @return
	 */
	public int tryNextToken(Symbol symbol) {
		int code = combinedPattern.recognise(symbol, 0);
		if (code == PATTERN_ENDED || code == TOKEN_APROVED) {
			PatternComponent lookup = combinedPattern.getLookUp();
			if (code == PATTERN_ENDED && lookup instanceof LanguagePattern) {
				LanguagePattern lp = (LanguagePattern) lookup;
				factory.addToAssembly(lp.fetchProduct());
			} else {
				factory.addToAssembly(symbol);
			}
			if (factory.hasEnded())
				return PATTERN_ENDED;
			return TOKEN_APROVED;
		} else {
			return TOKEN_ILLEGAL;
		}
	}

	public Construct fetchProduct() {
		Construct product = factory.getProduct();
		startFactory();
		return product;
	}

	public static Collection<LanguagePattern> searchValues() {
		Collection<LanguagePattern> searchValues = new ArrayList<LanguagePattern>();
		searchValues.add(ref("CONSTRAINT").getReference());
		return searchValues;
	}

	@Override
	public int recognise(Symbol symbol, int point) {
		return tryNextToken(symbol);

	}

	public static LanguagePattern get(String string) {
		return values.get(string);
	}

	public static PatternReference ref(String string) {
		return new PatternReference(string);
	}

	@Override
	public PatternComponent getLookUp() {
		return combinedPattern.getLookUp();
	}

}
