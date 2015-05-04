package com.softeno.java.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Anders
 * 
 */
public class PatternReference implements PatternComponent {
	private String reference;
	private int limit = -1;

	PatternReference(String reference, int depth) {
		Pattern pattern = Pattern.compile("([A-Z_]+)\\(([0-9]+|\\-)\\)");
		Matcher matcher =  pattern.matcher(reference);
		if (matcher.find())
		{
			String argument = matcher.group(2);
			if (argument.equals("-"))
			{
				this.limit = depth-1;
			} else
			{
				this.limit  = Integer.parseInt(matcher.group(2));
			}
			this.reference = matcher.group(1);
		} else {
			this.reference = reference;
		}
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