package driver;
import rmq.*;

/**
 * An interface representing an object that can create RMQ objects.
 */
public interface RMQFactory {
	/**
	 * Constructs a new RMQ object given the specified array of elements.
	 * 
	 * @param elems The elements for the array.
	 * @return An RMQ object for answering RMQ on that array.
	 */
	public RMQ create(float[] elems);
}
