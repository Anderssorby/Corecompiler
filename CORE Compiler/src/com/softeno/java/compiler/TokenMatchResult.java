package com.softeno.java.compiler;

import java.util.regex.MatchResult;

public interface TokenMatchResult extends MatchResult {

	public int lineCount();
}
