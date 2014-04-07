package compiler;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum Token {
	NAME("[_a-z][_a-z0-9]*", true), CONSTRAIN("#"),
	CURLY_BRACES_LEFT("{"), CURLY_BRACES_RIGHT("}"),
	PARENTHESIS_LEFT("("), PARENTHESIS_RIGHT(")"),
	SQUARE_BRACES_LEFT("["), SQUARE_BRACES_RIGHT("]"),
	DOT("."), PLUS("+"), MINUS("-"), GRATER_THAN(">"),
	COMMA(","), NUMBER("[0-9]+(\\.[0-9]+)?", true)
	;
	
	private Pattern pattern;
	
	Token(String regex, boolean isRegex) {
		if (isRegex) {
			this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			this.pattern = Pattern.compile(Pattern.quote(regex), Pattern.CASE_INSENSITIVE);
		}
	}
	
	Token(String regex) {
		this(regex, false);
	}
	
	public MatchResult match(String input) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find())
		{
			return matcher.toMatchResult();
		}
		else {
			return null;
		}
	}
	
	public Pattern getPattern()
	{
		return pattern;
	}
}
