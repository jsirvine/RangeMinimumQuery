package rmq;

import java.util.ArrayList;

/**
 * An &lt;O(n), O(log n)&gt; implementation of the RMQ as a hybrid between
 * the sparse table (on top) and no-precomputation structure (on bottom)
 */
public class HybridRMQ implements RMQ {
	private int[] top;
	private int[] bottom;
	private float[] elements;
	private int[] logs;
	private ArrayList<Integer> powers;  // Compute once for time efficiency
	private int[][] sparseTable;
	private int n;  // size of array
	
	/**
	 * Helper function for getting which index has the minimum value in 
	 * the array.
	 * @param index1 the first index in question
	 * @param index2 the second
	 * @return the index that represents the min
	 */
	private int MinIndex(int index1, int index2) {
		return elements[index1] <= elements[index2] ? index1 : index2;
	}
	
	/**
	 * Initializes top array of blocks with mins of each block.
	 */
	private void InitializeTop() {
		int b = (int)(Math.log(n) / Math.log(2));
		int blocks = (int) Math.ceil((double)(n)/b);
		top = new int[blocks];
		for (int block = 0; block < blocks; block++) {
			int start = block*b;
			int minIndex = start;
			for (int i = start; i < Math.min(start + b, n); i++) {
				minIndex = MinIndex(minIndex, i);
			}
			top[block] = minIndex;
		}
	}
	
	/**
	 * Initializes bottom array to be indexes in order. 
	 */
	private void InitializeBottom() {
		bottom = new int[n];
    	for (int i=0; i < n; i++) {
    		bottom[i] = i;
    	}
	}
	
	/**
	 * Initializes both arrays.
	 */
	private void InitializeTopAndBottom() {
		InitializeTop();
		InitializeBottom();
	}	
	
	/**
	 * Constructs an array that at each index i holds the largest value k such
	 * that 2^k is less than or equal to i + 1
	 * 
	 * @n the number of indices i to calculate this up to 
	 */
	private void CalculateLogs() {
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
	private void CalculatePowers() {
		powers = new ArrayList<Integer>();
		int power = 1;
		while (power <= n) {
			powers.add(power);
			power *= 2;
		}
	}
	
	/**
	 * Builds sparse table of top layer dynamically in linear time.
	 */
	private void BuildSparseTable() {
		// Construct sparse table for top layer
		int m = top.length;
		if (m == 0) return;
		int logm = (int)(Math.log(m) / Math.log(2));
		sparseTable = new int[m][logm+1];
		// Initialize first column of table
		for (int start = 0; start < m; start++) {
			sparseTable[start][0] = top[start];
		}
		// Build dynamically
		for (int k = 1; k < logm + 1; k++) {
			int intervalLen = powers.get(k);
			for (int start = 0; start < m - intervalLen + 1; start++) {
				int startNext = start + intervalLen/2;
				sparseTable[start][k] = MinIndex(sparseTable[start][k-1], sparseTable[startNext][k-1]);
			}
		} 
	}
	
    /**
     * Creates a new HybridRMQ structure to answer queries about the
     * array given by elems.
     *
     * @elems The array over which RMQ should be computed.
     */
    public HybridRMQ(float[] elems) {
    	n = elems.length;
    	if (n <= 1) return;
		// Copy elems to permanent storage for MinIndex function in rmq
		elements = new float[n];
		System.arraycopy(elems, 0, elements, 0, n);
    	// initialize arrays and fill in bottom one
		InitializeTopAndBottom();
		CalculateLogs();
		CalculatePowers();
		BuildSparseTable();
    }

    /**
     * Finds the minimum of the bottom layer for query between two indices. 
     * @param i start index
     * @param j end index
     * @param b block size
     * @return the minimum.
     */
    private int BottomMin(int i, int j, int b) {
    	// Find min of bottom layer indices
    	int bottomMin = i;
    	int onePastFirstBlock = (int)(i/b)*b + b;
    	for (int k=i; k < Math.min(onePastFirstBlock, j+1); k++) {
    		bottomMin = MinIndex(bottomMin, k);
    	}
    	int startOfLastBlock = (int)(j/b)*b;
    	for (int k = Math.max(startOfLastBlock, i); k <= j; k++) {
    		bottomMin = MinIndex(bottomMin, k);
    	}
    	return bottomMin;
    }
    
    /**
     * Finds the minimum of the top layer between block indices.
     * @param topi index of start block
     * @param topj index of bottom block
     * @return the minimum
     */
    private int TopMin(int topi, int topj) {
    	int k = logs[topj-topi];
		int twotok = powers.get(k);
		int topMin = MinIndex(sparseTable[topi][k], sparseTable[topj-twotok+1][k]);
		return topMin;
    }
    
    /**
     * Evaluates RMQ(i, j) over the array stored by the constructor, returning
     * the index of the minimum value in that range.
     */
    @Override
    public int rmq(int i, int j) {
    	if (n == 1) return i;
		int b = (int)(Math.log(n) / Math.log(2));
        // Find min of bottom layer indices
    	int bottomMin = BottomMin(i, j, b);
    	// Find min over top layer sparse tree
    	int topi = (int)(i/b) + 1;
    	int topj = (int)(j/b) - 1;
    	if (topj < topi) return bottomMin;
    	int topMin = TopMin(topi, topj);
		return MinIndex(bottomMin, topMin);
    }
}
