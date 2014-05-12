package compiler;

import constructs.Construct;

public interface ConstructFactory {

	public Construct getProduct();

	public void addToAssembly(Symbol symbol);

	public boolean hasEnded();

}
