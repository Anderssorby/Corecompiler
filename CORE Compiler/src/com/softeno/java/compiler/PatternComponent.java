package com.softeno.java.compiler;
/**
 * Interface for unifying the Token and pattern type,
 * @author Anders
 *
 */
public interface PatternComponent {
	
	/**
	 * 
	 * @param token
	 * @param point
	 * @return 
	 */
	public boolean recognise(Symbol symbol, int point);
	
	
}
