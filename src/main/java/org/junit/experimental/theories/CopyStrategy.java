package org.junit.experimental.theories;


/**
 * CopyStrategy is an interface provided to guarantee immutability among DataPoint.
 * Any class that implements this interface can invoke copy constructor on the 
 * passed in DataPoint object.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public interface CopyStrategy {
	
	/**
	 * Replicates the passed in DataPoint object by user specific means.
	 * 
	 * @param dataPoint	DataPoint object intended to be replicated 
	 * @return copy of current DataPoint
	 * @throws Exception general exception is thrown if replicating object has 
	 *         failed 
	 */
	public Object copyDataPoint(Object dataPoint) throws Exception;
	
}
