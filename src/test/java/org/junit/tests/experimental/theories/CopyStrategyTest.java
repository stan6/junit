package org.junit.tests.experimental.theories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.tests.experimental.theories.testobjects.Mutable;
import org.junit.tests.experimental.theories.testobjects.MutableWithCopyStrategy;
import org.junit.tests.experimental.theories.testobjects.MutableWithFailingCopyStrategy;

/**
 * Series of unit tests testing the CopyStrategy extension.
 * 
 * @author 	<a href="mailto:sybaik2@illinois.edu">Sang Yong Baik</a>
 * @author 	<a href="mailto:stan6@illinois.edu">Shin Hwei Tan</a>
 * @since	JUnit 4.8b3
 */
public class CopyStrategyTest {
	private JUnitCore junit;
	
	@Before
	public void init() {
		junit= new JUnitCore();
	}
	
	/**
	 * Unit test testing the default behavior expected when there is no 
	 * CopyStrategy to be invoked on a DataPoint.
	 */
	@Test
	public void testDataPointWithNoCopyStrategy() {
		Result defaultJUnitDataPoint= junit.run(TestDataPointWithNoCopyStrategy.class);		
		assertTrue(TestDataPointWithNoCopyStrategy.DATAPOINT_IS_MUTATED);
		assertTrue(defaultJUnitDataPoint.wasSuccessful());
	}
	
	/**
	 * Theory execution with DataPoint that does not use CopyStrategy extension.
	 */
	@RunWith(Theories.class)
	public static class TestDataPointWithNoCopyStrategy {
		public static boolean DATAPOINT_IS_MUTATED= false;
		@DataPoint
		public static Mutable A= new Mutable();
		@DataPoint
		public static Mutable B= new Mutable();
		@DataPoint
		public static Mutable a() {
			return new Mutable();
		}
		@DataPoint
		public static Mutable b() {
			return new Mutable();
		}
		/**
		 * The theory asserts that the test object Mutable mutates after the 
		 * first use of the respective DataPoint.
		 * 
		 * @param m test DataPoint
		 * @param n test DataPoint
		 */
		@Theory
		public void test1(Mutable m, Mutable n) {
			if (m.isMutated() && n.isMutated()) {
				DATAPOINT_IS_MUTATED= true;
			}
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Unit test testing the expected behavior when there is a CopyStrategy 
	 * to be invoked on a DataPoint.
	 */
	@Test
	public void testDataPointWithCopyStrategy() {
		Result extentionTestOnDataPoint= junit.run(TestDataPointWithCopyStrategy.class);	
		assertTrue(extentionTestOnDataPoint.wasSuccessful());
	}
	
	/**
	 * Theory execution with DataPoint that does use CopyStrategy extension.
	 */
	@RunWith(Theories.class)
	public static class TestDataPointWithCopyStrategy {
		@DataPoint(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable A= new Mutable();
		@DataPoint(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable B= new Mutable();
		@DataPoint(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable a() {
			return new Mutable();
		}
		@DataPoint(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable b() {
			return new Mutable();
		}
		
		/**
		 * The theory asserts that the test object Mutable remains unaltered 
		 * throughout the execution of Theory.
		 * 
		 * @param m test DataPoint
		 * @param n test DataPoint
		 */
		@Theory
		public void test2(Mutable m, Mutable n) {
			assertFalse(m.isMutated());
			assertFalse(n.isMutated());
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Unit test testing the default behavior expected when there is no 
	 * CopyStrategy to be invoked on DataPoints.
	 */
	@Test
	public void testDataPointsWithNoCopyStrategy() {
		Result defaultJUnitDataPoints= junit.run(TestDataPointsWithNoCopyStrategy.class);		
		assertTrue(TestDataPointsWithNoCopyStrategy.DATAPOINTS_ARE_MUTATED);
		assertTrue(defaultJUnitDataPoints.wasSuccessful());
	}
	
	/**
	 * Theory execution with DataPoints that does not use CopyStrategy extension.
	 */
	@RunWith(Theories.class) 
	public static class TestDataPointsWithNoCopyStrategy {
		public static boolean DATAPOINTS_ARE_MUTATED= false;
		@DataPoints
		public static Mutable[] AS= {
			new Mutable(),
			new Mutable(),
			new Mutable()
		};
		@DataPoints
		public static Mutable[] as() {
			return new Mutable[] {
				new Mutable(),
				new Mutable(),
				new Mutable()
			};
		}
		/**
		 * The theory asserts that the test object Mutable mutates after the 
		 * first use of the respective DataPoint.
		 * 
		 * @param m test DataPoint within DataPoints
		 * @param n test DataPoint within DataPoints
		 */
		@Theory
		public void test3(Mutable m, Mutable n) {
			if (m.isMutated() && n.isMutated()) {
				DATAPOINTS_ARE_MUTATED= true;
			}
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Unit test testing the expected behavior when there is a CopyStrategy to 
	 * be invoked on DataPoints.
	 */
	@Test 
	public void testDataPointsWithCopyStrategy() {
		Result extentionTestOnDataPoints= junit.run(TestDataPointsWithCopyStrategy.class);	
		assertTrue(extentionTestOnDataPoints.wasSuccessful());
	}
	
	/**
	 * Theory execution with DataPoints that does use CopyStrategy extension.
	 */
	@RunWith(Theories.class) 
	public static class TestDataPointsWithCopyStrategy {
		@DataPoints(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable[] AS= {
			new Mutable(),
			new Mutable(),
			new Mutable()
		};
		@DataPoints(copyStrategy= MutableWithCopyStrategy.class)
		public static Mutable[] as() {
			return new Mutable[] {
				new Mutable(),
				new Mutable(),
				new Mutable()
			};
		}
		/**
		 * The theory asserts that the test object Mutable remains unaltered 
		 * throughout the execution of Theory.
		 * 
		 * @param m test DataPoint within DataPoints
		 * @param n test DataPoint within DataPoints
		 */
		@Theory
		public void test4(Mutable m, Mutable n) {
			assertFalse(m.isMutated());
			assertFalse(n.isMutated());
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Unit test that checks whether exceptions are correctly thrown in the 
	 * case of failure caused by a DataPoint 
	 */
	@Test
	public void testDataPointExceptionHandle() {
		Result extentionTestOnDataPointFailures= junit.run(TestDataPointExceptionHandle.class);	
		assertExceptions(extentionTestOnDataPointFailures);
	}
	
	/**
	 * Theory execution intended to fail using DataPoint that cannot copy using 
	 * the provided faulty CopyStrategy
	 */
	@RunWith(Theories.class)
	public static class TestDataPointExceptionHandle {
		@DataPoint(copyStrategy= MutableWithFailingCopyStrategy.class)
		public static Mutable Af= new Mutable();

		@DataPoint(copyStrategy= MutableWithFailingCopyStrategy.class)
		public static Mutable af() {
			return new Mutable();
		}
		/**
		 * This theory method should never execute as the DataPoint should not be successfully duplicated
		 * 
		 * @param m test DataPoint with failing CopyStrategy
		 * @param n test DataPoint with failing CopyStrategy
		 */
		@Theory 
		public void test5(Mutable m, Mutable n) {
			//These assertions should never get a chance to run
			assertFalse(m.isMutated());
			assertFalse(n.isMutated());
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Unit test that checks whether exceptions are correctly thrown in the 
	 * case of failure caused by DataPoints
	 */
	@Test
	public void testDataPointsExceptionHandle() {
		Result extentionTestOnDataPointsFailure= junit.run(TestDataPointsExceptionHandle.class);
		assertExceptions(extentionTestOnDataPointsFailure);
	}
	
	/**
	 * Theory execution intended to fail using DataPoints that cannot copy 
	 * using the provided faulty CopyStrategy
	 */
	@RunWith(Theories.class)
	public static class TestDataPointsExceptionHandle {
		@DataPoints(copyStrategy= MutableWithFailingCopyStrategy.class)
		public static Mutable[] ASF= {
			new Mutable(),
			new Mutable(),
			new Mutable()
		};
		@DataPoints(copyStrategy= MutableWithFailingCopyStrategy.class)
		public static Mutable[] asf() {
			return new Mutable[] {
				new Mutable(),
				new Mutable(),
				new Mutable()
			};
		}
		/**
		 * This theory method should never execute as the DataPoints should not 
		 * be successfully duplicated
		 * 
		 * @param m test DataPoint within DataPoints with failing CopyStrategy
		 * @param n test DataPoint within DataPoints with failing CopyStrategy
		 */
		@Theory
		public void test6(Mutable m, Mutable n) {
			//These assertions should never get a chance to run
			assertFalse(m.isMutated());
			assertFalse(n.isMutated());
			m.mutate();
			n.mutate();
			assertTrue(m.isMutated());
			assertTrue(n.isMutated());
		}
	}
	
	/**
	 * Helper function that asserts the exception thrown is equivalent to 
	 * ParametrizedAssertionError.
	 * 
	 * @param result result of the JUnit execution
	 */
	private static void assertExceptions(Result result) {
		String exceptionName= ParameterizedAssertionError.class.getName();
		for (Failure failure: result.getFailures()) {
			String failureName= failure.getException().getClass().getName();
			assertEquals(exceptionName, failureName);
		}
		assertFalse(result.wasSuccessful());
	}
}
