package rmq;
/**
 * An interface representing an object that can answer range minimum queries.
 * It is expected that any class implementing this interface will have a
 * constructor with the signature
 * <pre>
 *       public [ClassName](float[] elems)
 * </pre>
 * that will accept as input the array elems and preprocess it so that 
 * RMQ_elems(i, j) can be computed efficiently. We use floats here because
 * you cannot directly assign ints and floats in Java, which helps prevent
 * a lot of pesky type errors.
 */
public interface RMQ {
    /**
     * Given the values of i and j, returns the index of the smallest element
     * in the range A[i], A[i+1], ..., A[j]. If multiple elements in the range
     * are tied for the smallest value, this method may return any of them.
     * <p>
     * The implementation can assume that i &le; j and does not need to handle
     * the case where this isn't true.
     *
     * @param i The lower end of the range, inclusive.
     * @param j The upper end of the range, inclusive.
     * @return The value of RMQ_A(i, j).
     */
    public int rmq(int i, int j);
}
