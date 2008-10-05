package com.olliebown.beads.data;

import java.io.Serializable;

/**
 * A StaticBuffer stores a one-dimensional buffer of floats statically so that
 * it can be accessed by multiple classes without need for a reference to an
 * object. Users can request the buffer to be regenerated with different buffer
 * lengths. The assumption is that one buffer length will suffice across a whole
 * program.
 * 
 * @author ollie
 * 
 */
public interface StaticBuffer extends Serializable {
	
	/**
	 * Returns the value of the buffer at the given fraction along its length (0
	 * = start, 1 = end).
	 * 
	 * @param fraction
	 *            the point along the buffer to inspect.
	 * @return the value at that point.
	 */
	public float getValueFraction(float fraction);

	/**
	 * Returns the value of the buffer at a specific index.
	 * 
	 * @param index
	 *            the index to inspect.
	 * @return the value at that point.
	 */
	public float getValueIndex(int index);

	/**
	 * Returns the size of the buffer.
	 * 
	 * @return size of buffer.
	 */
	public int getBufferSize();

	/**
	 * Regenerates the static buffer with the specified length.
	 * 
	 * @param bufferSize
	 *            the required length of the buffer.
	 */
	public void generateWindow(int bufferSize);

}
