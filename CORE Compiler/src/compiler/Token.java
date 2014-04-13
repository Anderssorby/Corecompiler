package compiler;
import java.nio.CharBuffer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum Token {
	NAME("[_a-z$][_a-z0-9$]*", true), CONSTRAIN("#"),
	CURLY_BRACES_LEFT("{"), CURLY_BRACES_RIGHT("}"),
	PARENTHESIS_LEFT("("), PARENTHESIS_RIGHT(")"),
	SQUARE_BRACES_LEFT("["), SQUARE_BRACES_RIGHT("]"),
	DOT("."), PLUS("+"), MINUS("-"),
	COMMA(","), NUMBER("[0-9]+(\\.[0-9]+)?", true), MULTIPLY("*"),
	DIVIDE("/"), PERCENT("%"), META_CONSTRAINT("@[_a-z$][_a-z0-9$]*", true),
	
	STRING_LITTERAL(new MatchingEngine() {

		@Override
		public MatchResult match(String input) {
			int l = input.length();
			char exmark = 0;
			int eh = 0;
			final CharBuffer result = CharBuffer.allocate(l);
			boolean esc = false;
			for (int i = 0; i < l; i++) {
				char c = input.charAt(i);
				if (i==0) {
					if (c=='"'||c=='\'') {
						exmark = c;
						continue;
					} else {
						return null;
					}
				}
				if ((c != exmark && c != '\\' )|| esc) {
					result.append(c);
					esc = false;
				} else {
					if (c==exmark) {
						eh = i+1;
						break;
					} else if (c=='\\') {
						esc = true;
					}
				}
			}
			final int end = eh;
			return new MatchResult() {

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
				
			};
		}
		
	}),
	/*
	 * Boolean operators 
	 */
	
	 GRATER_THAN(">"), LESS_THAN("<"), BOOLEAN_AND("&&"), BOOLEAN_OR("||"), NOT("!")
	 
	;
	
	private Pattern pattern;
	private MatchingEngine engine;
	
	Token(String regex, boolean isRegex) {
		this.engine = new DefaultMatcher();
		if (isRegex) {
			this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} else {
			this.pattern = Pattern.compile(Pattern.quote(regex), Pattern.CASE_INSENSITIVE);
		}
	}
	
	Token(String regex) {
		this(regex, false);
	}
	
	Token(MatchingEngine m) {
		this.engine = m;
	}
	
	interface MatchingEngine {
		public MatchResult match(String input);
	}
	
	public class DefaultMatcher implements MatchingEngine {
		private DefaultMatcher() {
			
		}
		
		@Override
		public MatchResult match(String input) {
			Matcher matcher = pattern.matcher(input);
			if (matcher.lookingAt())
			{
				return matcher.toMatchResult();
			}
			else {
				return null;
			}
		}
	}
	
	public MatchResult match(String input) {
		return engine.match(input);
	}
	
	public Pattern getPattern()
	{
		return pattern;
	}
}
