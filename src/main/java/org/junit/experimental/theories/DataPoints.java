package org.junit.experimental.theories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataPoints {

	/**
	 * The user provided copy strategy intended to duplicate specified 
	 * DataPoints
	 */
	Class<? extends CopyStrategy> copyStrategy() default CopyStrategy.class;
}
