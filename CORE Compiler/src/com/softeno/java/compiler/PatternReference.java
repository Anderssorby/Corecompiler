package com.softeno.java.compiler;

/**
 * 
 * @author Anders
 * 
 */
public class PatternReference implements PatternComponent {
	private String reference;

	PatternReference(String reference) {
		this.reference = reference;
	}

	public LanguagePattern getReference() {
		return LanguagePattern.get(reference);
	}

	@Override
	public int recognise(Symbol token, int point) {
		return getReference().recognise(token, point);
	}
	
	@Override
	public String toString() {
		return reference;
	}
}