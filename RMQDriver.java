import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Random;

import rmq.*;
import driver.*;

public class RMQDriver {
	public static void main(String[] args) {
		/* We should either get 1 or 2 arguments. The arguments will be the
		 * name of the RMQ class to run and (optionally) a random seed.
		 */
		if (args.length < 1 || args.length > 2 || !areAssertionsOn()) {
			usage();
			return;
		}
		
		/* Parse the parameters into configuration settings for the test. */
		RMQFactory theirFactory = createRMQFactoryFor(args[0]);
		Random rand = selectRandom(args);
		
		/* Get a reference factory so we can double-check answers. */
		RMQFactory ourFactory = createRMQFactoryFor("oursolution.SparseTable");
		
		/* Run some tests! */
		runSmallTests(theirFactory, ourFactory, rand);
		runLargeTests(theirFactory, ourFactory, rand);
		System.out.println("All tests completed!");
	}
	
	/**
	 * Returns whether assertions are enabled.
	 * 
	 * @return Whether assertions are enabled.
	 */
	private static boolean areAssertionsOn() {
		boolean assertionsEnabled = false;
		assert assertionsEnabled = true : "This never fails and just sets assertionsEnabled to true.";
		return assertionsEnabled;
	}
	
	/**
	 * Prints out a usage message about how to run the program.
	 */
	private static void usage() {
		System.out.println("Usage: java -ea RMQDriver rmq-class-name [random-seed]");
		System.out.println("  The '-ea' option enables assertions, which are necessary in our tests.");
		System.out.println("  Remember to prefix the name of the rmq class with 'rmq.', as in");
		System.out.println("      rmq.PrecomputedRMQ or rmq.FisherHeunRMQ");
		System.out.println("  The random seed can be any arbitrary long. This is useful for ensuring");
		System.out.println("      repeatability in testing.");
	}
	
	/**
	 * Given the name of an RMQ class, returns an RMQ factory that creates objects of
	 * that type.
	 * 
	 * @param classname The name of the class to load.
	 */
	private static RMQFactory createRMQFactoryFor(String classname) {
		try {
			/* Load the class, if we can. */ 
			final Class<?> clazz = Class.forName(classname);
			
			/* Get a constructor that takes in a long[]. */
			final Constructor<?> ctor = clazz.getConstructor(float[].class);
			
			return new RMQFactory() {
				@Override
				public RMQ create(float[] elems) {
					try {
						return (RMQ) ctor.newInstance(elems);
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
						
						/* Silence compiler warnings; this code is unreachable. */
						return null;
					}
				}
			};
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		/* Silence compiler warnings; this code is unreachable. */
		return null;
	}
	
	/**
	 * Given the command-line arguments, decides which random number generator
	 * to use. By default, we use a new Random() based on the system time,
	 * but this can be configured by the command-line arguments.
	 * 
	 * @param args The arguments list.
	 * @return A random number generator.
	 */
	private static Random selectRandom(String[] args) {
		/* If the random generator isn't specified, use the system time to seed
		 * the random generator.
		 */
		if (args.length == 1) return new Random();
		
		/* Otherwise, use the specified seed. */
		try {
			return new Random(Long.parseLong(args[1]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.exit(-1);
			
			/* Unreachable. */
			return null;
		}
	}
	
	/**
	 * Prints a message indicating the start of a test.
	 * 
	 * @param message The message to display.
	 */
	private static void startTest(String message) {
		System.out.println("================ " + message + " ================");
	}
	
	/** Constant controlling the maximum array size in the small array tests. */
	private static final int MAX_SMALL_ARRAY_SIZE = 200;
	
	/** Constant controlling how many tests of each size should be run in small tests. */
	private static final int NUM_TRIALS_PER_SMALL_SIZE = 100;
	
	/**
	 * Runs tests comparing student code to our code on smallish arrays.
	 * 
	 * @param theirFactory A factory for creating objects of the student's type.
	 * @param ourFactory A factory for creating reference objects of our type.
	 * @param rand A random source.
	 */
	private static void runSmallTests(RMQFactory theirFactory,
			                          RMQFactory ourFactory,
			                          Random rand) {
		startTest("Small Array Tests");
		
		for (int arrSize = 0; arrSize < MAX_SMALL_ARRAY_SIZE; arrSize++) {
			System.out.println("Testing size " + arrSize);
			
			/* Run a bunch of trials on this array size. */
			for (int trialNum = 0; trialNum < NUM_TRIALS_PER_SMALL_SIZE; trialNum++) {
				float[] elems = randomArrayOfSize(rand, arrSize);
				RMQ theirs = theirFactory.create(Arrays.copyOf(elems, arrSize));
				RMQ ours   = ourFactory.create(elems);
				testRMQ(theirs, ours, elems, rand, 10 * arrSize);
			}
		}
	}
	
	/** Constants controlling the bounds on the large array tests. */
	private static final int MIN_LARGE_ARRAY_SIZE = 1000;
	private static final int MAX_LARGE_ARRAY_SIZE = 5000;
	
	/** Constant controlling how many tests of each size should be run in large tests. */
	private static final int NUM_TRIALS_PER_LARGE_SIZE = 1000;
	
	/** Step size between the large array sizes. */
	private static final int LARGE_ARRAY_STEP_SIZE = 1000;
	
	/**
	 * Runs tests comparing student code to our code on larger arrays.
	 * 
	 * @param theirFactory A factory for creating objects of the student's type.
	 * @param ourFactory A factory for creating reference objects of our type.
	 * @param rand A random source.
	 */
	private static void runLargeTests(RMQFactory theirFactory,
			                          RMQFactory ourFactory,
			                          Random rand) {
		startTest("Large Array Tests");
		
		for (int arrSize = MIN_LARGE_ARRAY_SIZE; arrSize <= MAX_LARGE_ARRAY_SIZE;
			 arrSize += LARGE_ARRAY_STEP_SIZE) {
			System.out.println("Testing size " + arrSize);
			
			/* Run a bunch of trials on this array size. */
			for (int trialNum = 0; trialNum < NUM_TRIALS_PER_LARGE_SIZE; trialNum++) {
				float[] elems = randomArrayOfSize(rand, arrSize);
				RMQ theirs = theirFactory.create(Arrays.copyOf(elems, arrSize));
				RMQ ours   = ourFactory.create(elems);
				testRMQ(theirs, ours, elems, rand, 10 * arrSize);
			}
		}
	}
	
	/**
	 * Generates a random array of the given size.
	 * 
	 * @param rand The random number source.
	 * @param size The size of the array to generate.
	 * @return An array of that size whose elements are randomly generated.
	 */
	private static float[] randomArrayOfSize(Random rand, int size) {
		float[] result = new float[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = rand.nextFloat();
		}
		return result;
	}
	
	/**
	 * Tests an RMQ structure against our reference by running the specified number of trials
	 * on it. If any of the trials fail, the method triggers an assertion error.
	 * 
	 * @param theirs The student implementation.
	 * @param ours Our own solution
	 * @param elems The array of elements used by the RMQ structures.
	 * @param rand A random generator to use for creating probes.
	 * @param numTests The number of tests to run.
	 */
	private static void testRMQ(RMQ theirs, RMQ ours, float[] elems, Random rand,
			                    int numTests) {
		for (int trialNum = 0; trialNum < numTests; trialNum++) {
			/* Choose i and j for the probe. */
			int i = rand.nextInt(elems.length);
			int j = i + rand.nextInt(elems.length - i);
			
			/* Evaluate RMQ on the points. */
			int ourSoln   = ours.rmq(i, j);
			int theirSoln = theirs.rmq(i, j);
			
			/* Confirm the provided student output is valid. */
			assert theirSoln >= 0 && theirSoln < elems.length : "RMQ(" + i + ", " + j + ") on array of length " + elems.length + " returned " + theirSoln;
			assert elems[theirSoln] == elems[ourSoln] : "Your RMQ structure produced the wrong answer.";
		}
	}
}
