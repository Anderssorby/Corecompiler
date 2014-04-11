package compiler;

import java.util.Scanner;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	private Token[] tokens;

	private String[] ignore = { "\n", " ", "\t", "\r", "\r\n" };

	private Vector<Symbol> table;

	private int index;

	public Lexer(Token[] tokens) {
		this.tokens = tokens;
	}

	public void scann(String text) {
		table = new Vector<Symbol>();
		reset();
		int pos = 0;
		Scanner scanner = new Scanner(text);
		main: while (pos < text.length()) {
			for (String ign:ignore) {
				Pattern p = Pattern.compile(Pattern.quote(ign));
				Matcher matcher = p.matcher(text.substring(pos));
				if (matcher.lookingAt()) {
					pos += matcher.end();
					continue main;
				}
			}
			for (Token token : tokens) {
				MatchResult m = token.match(text.substring(pos));
				if (m != null) {
					String value = m.group();
					Symbol symbol = new Symbol(token, value);
					table.add(symbol);
					pos += m.end();
					continue main;
				} else {
					continue;
				}
			}
			// weird symbol
			pos++;
		}
	}

	public Symbol nextToken() {
		Symbol symbol = table.get(index);
		index++;
		return symbol;
	}

	public boolean hasNext() {
		return index < table.size();
	}

	public void reset() {
		index = 0;
	}
}
