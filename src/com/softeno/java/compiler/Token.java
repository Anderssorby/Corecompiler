package com.softeno.java.compiler;

import java.nio.CharBuffer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.softeno.java.compiler.TokenPattern.TOKEN_APROVED;
import static com.softeno.java.compiler.TokenPattern.TOKEN_ILLEGAL;

public class Token implements PatternComponent {
	
	private Pattern pattern;
	private String value;
	private MatchingEngine engine;

	Token(String regex, boolean isRegex) {
		if (isRegex) {
			this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);
		} else {
			this.value = regex;
			this.pattern = Pattern.compile(Pattern.quote(regex),
					Pattern.CASE_INSENSITIVE);
		}
		this.engine = new DefaultMatcher(pattern);
	}

	Token(String regex) {
		this(regex, false);
	}

	Token(MatchingEngine m) {
		this.engine = m;
	}

	interface MatchingEngine {
		public MatchResult match(String input);

		public int getMinimalLength();
	}

	@Override
	public String toString() {
		return value;
	}

	public MatchResult match(String input) {
		return engine.match(input);
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public int recognise(Symbol symbol, int i) {
		return this.equals(symbol) ? TOKEN_APROVED : TOKEN_ILLEGAL;
	}
	
	public boolean equals(Token token) {
		return value.equals(token.value);
	}
	
	public static class DefaultMatcher implements MatchingEngine {

		private Pattern pattern;
		private int minimalLength;
		private DefaultMatcher(Pattern pattern) {
			this.pattern = pattern;
			this.minimalLength = pattern.pattern().length();
		}

		@Override
		public MatchResult match(String input) {
			Matcher matcher = pattern.matcher(input);
			if (matcher.lookingAt()) {
				return matcher.toMatchResult();
			} else {
				return null;
			}
		}

		@Override
		public int getMinimalLength() {
			return minimalLength;
		}
	}
	
	public enum SpecialToken implements PatternComponent { /* NAME("[_a-z$][_a-z0-9$]*", true), CONSTRAIN("#"), CURLY_BRACES_LEFT("{"), CURLY_BRACES_RIGHT(
			"}"), PARENTHESIS_LEFT("("), PARENTHESIS_RIGHT(")"), SQUARE_BRACES_LEFT(
			"["), SQUARE_BRACES_RIGHT("]"), DOT("."), PLUS("+"), MINUS("-"), COMMA(
			","), NUMBER("[0-9]+(\\.[0-9]+)?", true), MULTIPLY("*"), DIVIDE("/"), PERCENT(
			"%"), META_CONSTRAINT("@[_a-z$][_a-z0-9$]*", true),*/

	STRING_LITTERAL(new MatchingEngine() {

		@Override
		public MatchResult match(String input) {
			int l = input.length();
			char exmark = 0;
			int eh = 0;
			final CharBuffer result = CharBuffer.allocate(l);
			boolean esc = false;
			int line = 0;
			for (int i = 0; i < l; i++) {
				char c = input.charAt(i);
				if (c == '\n') {
					line++;
				}
				if (i == 0) {
					if (c == '"' || c == '\'') {
						exmark = c;
						continue;
					} else {
						return null;
					}
				}
				if ((c != exmark && c != '\\') || esc) {
					result.append(c);
					esc = false;
				} else {
					if (c == exmark) {
						eh = i + 1;
						break;
					} else if (c == '\\') {
						esc = true;
					}
				}
			}
			final int end = eh;
			final int fline = line;
			return new TokenMatchResult() {

				@Override
				public int start() {
					return 0;
				}

				@Override
				public int start(int group) {
					return 0;
				}

				@Override
				public int end() {
					return end;
				}

				@Override
				public int end(int group) {
					return 0;
				}

				@Override
				public String group() {
					return result.toString();
				}

				@Override
				public String group(int group) {
					return null;
				}

				@Override
				public int groupCount() {
					return 0;
				}

				@Override
				public int lineCount() {
					return fline;
				}

			};
		}

		@Override
		public int getMinimalLength() {
			return 0;
		}

	}),
	/*
	 * Boolean operators
	 */

	/*GRATER_THAN(">"), LESS_THAN("<"), BOOLEAN_AND("&&"), BOOLEAN_OR("||"), NOT(
			"!")*/

	;
	private Pattern pattern;
	private MatchingEngine engine;
	
	SpecialToken(String regex, boolean isRegex) {
		if (isRegex) {
			this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);
		} else {
			this.pattern = Pattern.compile(Pattern.quote(regex),
					Pattern.CASE_INSENSITIVE);
		}
		this.engine = new DefaultMatcher(pattern);
	}

	SpecialToken(String regex) {
		this(regex, false);
	}

	SpecialToken(MatchingEngine m) {
		this.engine = m;
	}
	
	public MatchResult match(String input) {
		return engine.match(input);
	}

	@Override
	public int recognise(Symbol symbol, int i) {
		return this.equals(symbol) ? TOKEN_APROVED : TOKEN_ILLEGAL;
	}
	}
}
