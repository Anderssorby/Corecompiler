package com.softeno.java.compiler;

public class CharacterRange extends Token {

	private String start;
	private String end;

	CharacterRange(String start, String end) {
		super(start+"-"+end, true);
		this.start = start;
		this.end = end;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

}
