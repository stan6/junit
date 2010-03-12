package org.junit.tests.experimental.theories.testobjects;

import org.junit.experimental.theories.CopyStrategy;

/**
 * The class to be used to test if the exception is correctly thrown when the 
 * DataPoint fails to be copied.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public class MutableWithFailingCopyStrategy implements CopyStrategy {

	public Object copyDataPoint(Object toBeReplicated) throws Exception {
		throw new Exception();
	}

}
