package org.junit.tests.experimental.theories.testobjects;

import org.junit.experimental.theories.CopyStrategy;

/**
 * Passed in Mutable objects are correctly replicated for the testing purpose.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public class MutableWithCopyStrategy implements CopyStrategy {

	public Object copyDataPoint(Object toBeReplicated) throws Exception {
		Mutable toBeCopied= (Mutable)toBeReplicated;
		Mutable toBeReturned= new Mutable(toBeCopied);
		return toBeReturned;
	}
}
