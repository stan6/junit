/**
 * 
 */
package org.junit.experimental.theories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.experimental.theories.internal.CopyStrategyFailureException;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class Theories extends BlockJUnit4ClassRunner {
	public Theories(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		validateDataPointFields(errors);
	}
	
	private void validateDataPointFields(List<Throwable> errors) {
		Field[] fields= getTestClass().getJavaClass().getDeclaredFields();
		
		for (Field each : fields)
			if (each.getAnnotation(DataPoint.class) != null && !Modifier.isStatic(each.getModifiers()))
				errors.add(new Error("DataPoint field " + each.getName() + " must be static"));
	}
	
	@Override
	protected void validateConstructor(List<Throwable> errors) {
		validateOnlyOneConstructor(errors);
	}
	
	@Override
	protected void validateTestMethods(List<Throwable> errors) {
		for (FrameworkMethod each : computeTestMethods())
			if(each.getAnnotation(Theory.class) != null)
				each.validatePublicVoid(false, errors);
			else
				each.validatePublicVoidNoArg(false, errors);
	}
	
	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> testMethods= super.computeTestMethods();
		List<FrameworkMethod> theoryMethods= getTestClass().getAnnotatedMethods(Theory.class);
		testMethods.removeAll(theoryMethods);
		testMethods.addAll(theoryMethods);
		return testMethods;
	}

	@Override
	public Statement methodBlock(final FrameworkMethod method) {
		return new TheoryAnchor(method);
	}

	// TODO: this should be static
	public class TheoryAnchor extends Statement {
		private int successes= 0;

		private FrameworkMethod fTestMethod;

		private List<AssumptionViolatedException> fInvalidParameters= new ArrayList<AssumptionViolatedException>();

		public TheoryAnchor(FrameworkMethod method) {
			fTestMethod= method;
		}

		@Override
		public void evaluate() throws Throwable {
			runWithAssignment(Assignments.allUnassigned(
					fTestMethod.getMethod(), getTestClass()));

			if (successes == 0)
				Assert
						.fail("Never found parameters that satisfied method assumptions.  Violated assumptions: "
								+ fInvalidParameters);
		}

		protected void runWithAssignment(Assignments parameterAssignment)
				throws Throwable {
			if (!parameterAssignment.isComplete()) {
				runWithIncompleteAssignment(parameterAssignment);
			} else {
				runWithCompleteAssignment(parameterAssignment);
			}
		}

		protected void runWithIncompleteAssignment(Assignments incomplete)
				throws InstantiationException, IllegalAccessException,
				Throwable {
			for (PotentialAssignment source : incomplete
					.potentialsForNextUnassigned()) {
				runWithAssignment(incomplete.assignNext(source));
			}
		}

		protected void runWithCompleteAssignment(final Assignments complete)
				throws InstantiationException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException, Throwable {
			new BlockJUnit4ClassRunner(getTestClass().getJavaClass()) {
				@Override
				protected void collectInitializationErrors(
						List<Throwable> errors) {
					// do nothing
				}

				@Override
				public Statement methodBlock(FrameworkMethod method) {
					final Statement statement= super.methodBlock(method);
					return new Statement() {
						@Override
						public void evaluate() throws Throwable {
							try {
								statement.evaluate();
								handleDataPointSuccess();
							} catch (AssumptionViolatedException e) {
								handleAssumptionViolation(e);
							} catch (CopyStrategyFailureException e) {
								reportParameterizedError(e, 
										complete.getArgumentString(e.getFailedIndex()), 
										complete.getArgumentStrings(nullsOk()));
							} catch (Throwable e) {
								reportParameterizedError(e, null, complete
										.getArgumentStrings(nullsOk()));
							}
						}

					};
				}

				@Override
				protected Statement methodInvoker(FrameworkMethod method, Object test) {
					return methodCompletesWithParameters(method, complete, test);
				}

				@Override
				public Object createTest() throws Exception {
					return getTestClass().getOnlyConstructor().newInstance(
							complete.getConstructorArguments(nullsOk()));
				}
			}.methodBlock(fTestMethod).evaluate();
		}

		private Statement methodCompletesWithParameters(
				final FrameworkMethod method, final Assignments complete, final Object freshInstance) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						final Object[] values= complete.getMethodArguments(
								nullsOk());
						method.invokeExplosively(freshInstance, toBeInvoked(values, complete.getCopyStrategies()));
					} catch (CouldNotGenerateValueException e) {
						// ignore
					}
				}
			};
		}
		
		/**
		 * Using the passed in array of CopyStrategy classes, the method 
		 * replicates the matching value if it is replicable.
		 * 
		 * @param values DataPoint(s) values to be replicated
		 * @param copyStrategies array of CopyStrategy classes to be applied to passed in DataPoint(s)
		 * @return array of newly copied objects using passed in CopyStrategies
		 */
		protected Object[] toBeInvoked(Object[] values,	Class<CopyStrategy>[] copyStrategies) {	
			Object[] toBeInvoked= new Object[values.length];											
			for (int i= 0; i < values.length; i++) {
				if (isPrimitive(values[i]) || 
						copyStrategies[i] == CopyStrategy.class|| values[i] == null) {
					toBeInvoked[i]= values[i];
				} else {
					toBeInvoked[i]= getCopyStrategyInvokedObject(values[i], 
							copyStrategies[i], i);
				}	
			}
			return toBeInvoked;
		}
		
		/**
		 * The method creates a new object using the provided CopyStrategy.
		 * 
		 * @param value the actual DataPoint to be applied using CopyStrategy
		 * @param copyStrategy user provided copy constructor to replicate the 
		 *        provided DataPoint
		 * @param index index provided to point out which DataPoint has failed 
		 *        in the case of failure
		 * @return newly created object using provided CopyStrategy
		 * @throws CopyStrategyFailureException the object could not be 
		 *         successfully replicated by using given means
		 */
		protected Object getCopyStrategyInvokedObject(Object value,
				Class<? extends CopyStrategy> copyStrategy, int index) throws CopyStrategyFailureException {
			try {
				return copyStrategy.newInstance().copyDataPoint(value);
			} catch (Exception e) {
				throw new CopyStrategyFailureException(value, index, copyStrategy.getSimpleName());
			}
		}
				
		/**
		 * Checks if the current DataPoint object is a primitive.
		 * 
		 * @param value the DataPoint value to be checked
		 * @return true if the DataPoint is primitive, false otherwise
		 */
		protected boolean isPrimitive(Object value) {
			return value instanceof Number 
				|| value instanceof Boolean 
				|| value instanceof String 
				|| value instanceof Character;
		}

		protected void handleAssumptionViolation(AssumptionViolatedException e) {
			fInvalidParameters.add(e);
		}

		protected void reportParameterizedError(Throwable e, String param, Object... params)
				throws Throwable {
			if (params.length == 0)
				throw e;
			if(e instanceof CopyStrategyFailureException) {		
				throw new ParameterizedAssertionError((CopyStrategyFailureException) e, fTestMethod.getName(), param,
						params);
			}
			throw new ParameterizedAssertionError(e, fTestMethod.getName(),
					params);
		}

		private boolean nullsOk() {
			Theory annotation= fTestMethod.getMethod().getAnnotation(
					Theory.class);
			if (annotation == null)
				return false;
			return annotation.nullsAccepted();
		}

		protected void handleDataPointSuccess() {
			successes++;
		}
	}
}