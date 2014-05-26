package compiler;

import constructs.Construct;

public interface ConstructFactory<E extends Construct> {

	public E getProduct();

	public E addToAssembly(Symbol symbol);

	public boolean hasEnded();

}
