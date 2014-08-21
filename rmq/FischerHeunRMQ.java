package rmq;

import java.util.ArrayList;
import java.util.Stack;

/**
 * An &lt;O(n), O(1)&gt; implementation of the Fischer-Heun RMQ data structure.
 *
 * You will implement this class for problem 3.iv of Problem Set One.
 */
public class FischerHeunRMQ implements RMQ {
	private int[] top;
	private int[] bottom;
	private float[] elements;
	private int[] logs;
	private ArrayList<Integer> powers;
	private int[][] sparseTable;
	private int n;
	private int b;
	private int blocks;
	private RMQStructure[] cartesianRMQs;
	private int[] cartesians;

	private class RMQStructure {
		private int[][] rmqs;

		public RMQStructure(int i, int j) {
			int numElems = j - i + 1;
			rmqs = new int[numElems][numElems];
			// Build graph by diagonals, starting with main diagonal
			// Initialize main diagonal
			for (int k = 0; k < numElems; k++) {
				rmqs[k][k] = k;
			}
			// Dynamically build rest of table
			for (int k = 0; k < numElems; k++) {
				for (int l = k+1; l < numElems; l++) {
					if (elements[rmqs[k][l-1] + i] <= elements[i+l]) {
						rmqs[k][l] = rmqs[k][l-1];
					} else {
						rmqs[k][l] = l;
					}
				}
			}
		}
		
		public int RMQ(int k, int l) {
			return rmqs[k][l];
		}
	};
	
	private int add0right(int x) {
		return 2*x;
	}
	
	private int add1right(int x) {
		return 2*x + 1;
	}
	
	private int CartesianNumber(int i, int j) {
		int cartesian = 0;
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(i);
		cartesian = add1right(cartesian);
		for (int k = i+1; k <= j; k++) {
			while (!stack.isEmpty() && elements[k] < elements[stack.peek()]) {
				stack.pop();
				cartesian = add0right(cartesian);
			}
			stack.push(k);
			cartesian = add1right(cartesian);
		}
		while (!stack.isEmpty()) {
			stack.pop();
			cartesian = add0right(cartesian);
		}
		return cartesian;
	}
	
	private void InitializeCartesians() {
		cartesians = new int[blocks];
		cartesianRMQs = new RMQStructure[powers.get(2*b)];
		for (int block = 0; block < blocks; block++) {
			int i = block*b;
			int j = Math.min(n-1, (block+1)*b - 1);
			int c = CartesianNumber(i, j);
			cartesians[block] = c;
			if (cartesianRMQs[c] == null) {
				cartesianRMQs[c] = new RMQStructure(i, j);
			}
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

	private void InitializeTop() {
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

	private void InitializeBottom() {
		bottom = new int[n];
		for (int i=0; i < n; i++) {
			bottom[i] = i;
		}
	}

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
	 * Creates a new FischerHeunRMQ structure to answer queries about the
	 * array given by elems.
	 *
	 * @elems The array over which RMQ should be computed.
	 */
	public FischerHeunRMQ(float[] elems) {
		n = elems.length;
		if (n == 0) return;
		// Copy elems to permanent storage for MinIndex function in rmq
		elements = new float[n];
		System.arraycopy(elems, 0, elements, 0, n);
		b = (int)(Math.log(n) / (4*Math.log(2)));
		// If b = 0, just linear pass through it
		if (b < 1) return;
		blocks = (int) Math.ceil((double)(n)/b);
		// initialize arrays and fill in bottom one
		InitializeTopAndBottom();
		CalculateLogs();
		CalculatePowers();
		BuildSparseTable();
		InitializeCartesians();
	}


	private int BottomMin(int i, int j) {
		int iBlock = (int)(i/b);
		int jBlock = (int)(j/b);
		// First block (i)
		int end;
		if (iBlock == jBlock) {  
			end = j;
		} else {
			end = (iBlock + 1)*b - 1;
		}
		int firstMin = cartesianRMQs[cartesians[iBlock]].RMQ(i%b, end%b) + iBlock*b;
		// Second block (j)
		int start;
		if (iBlock == jBlock) {
			start = i;
		} else {
			start = jBlock*b;
		}
		int secondMin = cartesianRMQs[cartesians[jBlock]].RMQ(start%b, j%b) + jBlock*b;
		return MinIndex(firstMin, secondMin);
	}

	private int TopMin(int topi, int topj) {
		int k = logs[topj-topi];
		int twotok = powers.get(k);
		int topMin = MinIndex(sparseTable[topi][k], sparseTable[topj-twotok+1][k]);
		return topMin;
	}
	
	private int linearPass(int i, int j) {
		int minIndex = i;
		for (int k = i+1; k <= j; k++) {
			minIndex = MinIndex(minIndex, k);
		}
		return minIndex;
	}
	
	/**
	 * Evaluates RMQ(i, j) over the array stored by the constructor, returning
	 * the index of the minimum value in that range.
	 */
	@Override
	public int rmq(int i, int j) {
		if (b < 1) return linearPass(i, j);
		// Find min of bottom layer indices
		int bottomMin = BottomMin(i, j);
		// Find min over top layer sparse tree
		int topi = (int)(i/b) + 1;
		int topj = (int)(j/b) - 1;
		if (topj < topi) return bottomMin;
		int topMin = TopMin(topi, topj);
		return MinIndex(bottomMin, topMin);
	}
}
