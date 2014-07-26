package com.softeno.java.compiler.factories;

import com.softeno.java.compiler.PatternComponent;
import com.softeno.java.compiler.Symbol;
import com.softeno.java.constructs.Construct;

public interface ConstructFactory<E extends Construct> {

	public void setPattern(PatternComponent[] pattern);
	
	public E getProduct();

	public boolean addToAssembly(Symbol symbol);

	public boolean hasEnded();

	public void addToAssembly(Construct construct);

}
