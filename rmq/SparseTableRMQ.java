package rmq;
import java.util.*;
/**
 * An &lt;O(n log n), O(1)&gt; implementation of RMQ that uses a sparse table
 * to do lookups efficiently.
 *
 * You will implement this class for problem 3.ii of Problem Set One.
 */
public class SparseTableRMQ implements RMQ {
	private int[][] sparseTable;
	private int[] logs;
	private ArrayList<Integer> powers;
	private float[] elements;  // Store the array

	/**
	 * Constructs an array that at each index i holds the largest value k such
	 * that 2^k is less than or equal to i + 1
	 * 
	 * @n the number of indices i to calculate this up to 
	 */
	private void CalculateLogs(int n) {
		logs = new int[n];
		int k = 0;
		int twotok = 1;
		for (int i=0; i < n; i++) {
			// Check if we can bump k up
			if (twotok*2 <= i + 1) {
				k++;
				twotok *= 2;
			}
			logs[i] = k;
		}
	}

	/**
	 * Constructs an array that at each index i holds 2^i. After this is
	 * computed, raising 2 to a power can be done in constant time.
	 * 
	 * @n the max that 2^i can go up to 
	 */
	private void CalculatePowers(int n) {
		powers = new ArrayList<Integer>();
		int power = 1;
		while (power <= n) {
			powers.add(power);
			power *= 2;
		}
	}

	/**
	 * Helper function for getting which index has the minimum value in 
	 * the array
	 * @param index1 the first index in question
	 * @param index2 the second
	 * @return the index that represents the min
	 */
	private int MinIndex(int index1, int index2) {
		return elements[index1] <= elements[index2] ? index1 : index2;
	}
	
	/**
	 * Creates a new SparseTableRMQ structure to answer queries about the
	 * array given by elems.
	 *
	 * @elems The array over which RMQ should be computed.
	 */
	public SparseTableRMQ(float[] elems) {
		int n = elems.length;
		if (n==0) return;  // No preprocessing in this case
		// Copy elems to permanent storage for MinIndex function in rmq
		elements = new float[n];
		System.arraycopy(elems, 0, elements, 0, n);
		// Calculate logs and powers up front
		CalculateLogs(n);
		CalculatePowers(n);
		// Construct sparse table
		int logn = (int)(Math.log(n) / Math.log(2));
		sparseTable = new int[n][logn+1];
		// Initialize first column of table
		for (int start = 0; start < n; start++) {
			sparseTable[start][0] = start;
		}
		// Build dynamically
		for (int k = 1; k < logn + 1; k++) {
			int intervalLen = powers.get(k);
			for (int start = 0; start < n - intervalLen + 1; start++) {
				int startNext = start + intervalLen/2;
				sparseTable[start][k] = MinIndex(sparseTable[start][k-1], sparseTable[startNext][k-1]);
			}
		} 
	}

	/**
	 * Evaluates RMQ(i, j) over the array stored by the constructor, returning
	 * the index of the minimum value in that range.
	 */
	@Override
	public int rmq(int i, int j) {
		int k = logs[j-i];
		int twotok = powers.get(k);
		return MinIndex(sparseTable[i][k], sparseTable[j-twotok+1][k]);
	}
}
