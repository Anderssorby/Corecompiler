package compiler;

import constructs.Construct;

public interface TokenPattern extends PatternComponent {
	
	public static int TOKEN_APROVED = 0x0;
	
	public static int TOKEN_ILLEGAL = 0x1;
	
	public static int PATTERN_ENDED = 0x2;
	
	/**
	 * Tries to match the next token to the pattern. The method will return TOKEN_APPROVED (0) as long as the
	 * Pattern is still valid. If the method returns false the pattern has either ended or is incorrect. 
	 * @param symbol
	 * @return
	 */
	public int tryNextToken(Symbol symbol);
		
	public Construct getProduct();

}
