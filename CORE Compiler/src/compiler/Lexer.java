package compiler;

import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	private Token[] tokens;

	private String[] ignore = {"\r\n", "\n", " ", "\t", "\r"};

	private Vector<Symbol> table;

	private int index;

	public Lexer(Token[] tokens) {
		this.tokens = tokens;
	}

	public void scann(String text) {
		clean();
		int pos = 0;
		int line = 1;
		main: while (pos < text.length()) {
			for (String ign : ignore) {
				Pattern p = Pattern.compile(Pattern.quote(ign));
				Matcher matcher = p.matcher(text.substring(pos));
				if (matcher.lookingAt()) {
					if (isNewline(ign)) line++;
					pos += matcher.end();
					continue main;
				}
			}
			int mlen = 0;
			Symbol symbol = null;
			for (Token token : tokens) {
				MatchResult m = token.match(text.substring(pos));
				if (m != null) {
					String value = m.group();
					if (m.end() > mlen) {
						mlen = m.end();
						symbol = new Symbol(token, value);
						symbol.setLine(line);
					}
				} else {
					continue;
				}
			}
			pos += mlen;
			table.add(symbol);
		}
	}

	public boolean isNewline(String ign) {
		return ign.equals("\n")||ign.equals("\r")||ign.equals("\r\n");
	}

	public Symbol nextToken() {
		Symbol symbol = table.get(index);
		index++;
		return symbol;
	}

	public boolean hasNext() {
		return index < table.size();
	}

	/**
	 * Winds back the pointer. 
	 */
	public void reset() {
		index = 0;
	}

	/**
	 * goes back one step
	 */
	public void back() {
		index--;
	}

	/**
	 * creates a fresh lexer
	 */
	protected void clean() {
		table = new Vector<Symbol>();
		reset();
	}
}
