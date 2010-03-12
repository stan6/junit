package org.junit.tests.experimental.theories.testobjects;

/**
 * Simple test object to see whether the current object has been mutated or not
 * by keeping a mutation counter which gets incremented once the theory mutates 
 * the provided object.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public class Mutable {
	
	protected int mutationCount= 0;

	public Mutable() {}
	
	public Mutable(Mutable toBeCopied) {
		this.mutationCount= toBeCopied.getMutationCount();
	}
	
	public void mutate() {
		mutationCount++;
	}
	
	public boolean isMutated() {
		return mutationCount > 0;
	}
	
	public int getMutationCount() {
		return mutationCount;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Mutable)) {
			return false;
		}
		Mutable other= (Mutable)obj;
		return mutationCount == other.mutationCount; 
	}
	
	@Override
	public int hashCode() {
		return mutationCount;
	}

}
