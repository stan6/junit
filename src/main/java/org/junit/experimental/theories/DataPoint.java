package org.junit.experimental.theories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataPoint {

	/**
	 * The user provided copy strategy intended to duplicate specified 
	 * DataPoint
	 */
	Class<? extends CopyStrategy> copyStrategy() default CopyStrategy.class;
}
