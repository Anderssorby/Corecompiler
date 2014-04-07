package compiler;
import java.util.Vector;
import java.util.regex.MatchResult;


public class Lexer {

	private Token[] tokens;
	
	private Vector<Symbol> table;

	private int index;

	public Lexer(Token[] tokens) {
		this.tokens = tokens;
	}
	
	public void scann(String text) {
		reset();
		int pos = 0;
		main:
			while (pos < text.length()) {
			for (Token token:tokens) {
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
		table = new Vector<Symbol>();
	}
}
