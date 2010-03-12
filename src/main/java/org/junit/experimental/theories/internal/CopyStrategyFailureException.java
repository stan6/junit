package org.junit.experimental.theories.internal;

/**
 * Exception to be thrown when the particular DataPoint object could not be 
 * copied using provided CopyStrategy.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public class CopyStrategyFailureException extends RuntimeException {

	private static final long serialVersionUID= 1L;
	
	/**
	 * The DataPoint that has failed.
	 */
	private Object failedDataPoint;
	/**
	 * The index pointing to the DataPoint within the collection of DataPoint 
	 * amassed by Theory anchor.
	 */
	private int failedIndex;
	/**
	 * The name of the CopyStrategy that has caused failure.
	 */
	private String copyStrategyClassName;
	
	/**
	 * Constructor
	 * 
	 * @param toBeCopied the DataPoint that was intended to be copied
	 * @param index the index pointing to the current DataPoint within the 
	 *        array of DataPoint
	 * @param copyStrategy the name for user provided CopyStrategy that has 
	 *        caused failure
	 */
	public CopyStrategyFailureException(Object toBeCopied, int index, String copyStrategy) {
		super();
		this.failedDataPoint= toBeCopied;
		this.failedIndex= index;
		this.copyStrategyClassName= copyStrategy;
	}
	
	public Object getFailedDataPoint() {
		return failedDataPoint;
	}
	
	public int getFailedIndex() {
		return failedIndex;
	}
	
	public String getCustomStrategyClassName() {
		return copyStrategyClassName;
	}
}
