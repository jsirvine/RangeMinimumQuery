package rmq;
/**
 * An &lt;O(n<sup>2</sup>), O(1)&gt; implementation of RMQ that precomputes the
 * value of RMQ_A(i, j) for all possible i and j.
 *
 * You will implement this class for problem 3.i of Problem Set One.
 */
public class PrecomputedRMQ implements RMQ {
    private int[][] precompTable;
	
    /**
     * Creates a new PrecomputedRMQ structure to answer queries about the
     * array given by elems.
     *
     * @elems The array over which RMQ should be computed.
     */
    public PrecomputedRMQ(float[] elems) {
      int numElems = elems.length;
      precompTable = new int[numElems][numElems];
      // Build graph by diagonals, starting with main diagonal
      // Initialize main diagonal
      for (int i = 0; i < numElems; i++) {
        precompTable[i][i] = i;
      }
      // Dynamically build rest of diagonals
      for (int intervalLen = 1; intervalLen < numElems; intervalLen++) {
        for (int i = 0; i < numElems - intervalLen; i++) {
          int j = i + intervalLen;
          if (elems[precompTable[i][j-1]] <= elems[j]) {
            precompTable[i][j] = precompTable[i][j-1];
          } else {
            precompTable[i][j] = j;
          }
        }
      }
    }

    /**
     * Evaluates RMQ(i, j) over the array stored by the constructor, returning
     * the index of the minimum value in that range.
     */
    @Override
    public int rmq(int i, int j) {
    	return precompTable[i][j];
    }
}
