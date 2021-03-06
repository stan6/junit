/**
 * 
 */
package org.junit.experimental.theories.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


public class ParameterizedAssertionError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ParameterizedAssertionError(Throwable targetException,
			String methodName, Object... params) {
		super(String.format("%s(%s)", methodName, join(", ", params)),
				targetException);
	}
	
	/**
	 * Constructor initializes message when theory execution has failed due to 
	 * user provided CopyStrategy
	 * 
	 * @param targetException particular exception that has rendered the current 
	 *        execution to fail
	 * @param methodName the method that the current theory execution has failed
	 * @param param the particular DataPoint that has failed
	 * @param params the list of DataPoints that were running with the current 
	 *        execution of theory
	 */
	public ParameterizedAssertionError(CopyStrategyFailureException targetException, 
			String methodName, String param, Object... params) {
		super(String.format("%s(%s)", methodName, join(", ", params)) 
				+ " could not invoke copy strategy using parameter " + param, targetException);
	}

	@Override public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	public static String join(String delimiter, Object... params) {
		return join(delimiter, Arrays.asList(params));
	}

	public static String join(String delimiter,
			Collection<Object> values) {
		StringBuffer buffer = new StringBuffer();
		Iterator<Object> iter = values.iterator();
		while (iter.hasNext()) {
			Object next = iter.next();
			buffer.append(stringValueOf(next));
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}

	private static String stringValueOf(Object next) {
		try {
			return String.valueOf(next);
		} catch (Throwable e) {
			return "[toString failed]";
		}
	}
}