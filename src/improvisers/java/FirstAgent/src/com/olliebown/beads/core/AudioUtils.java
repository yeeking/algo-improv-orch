/**
 * This class is taken from JASS TODO: Proper credits and license
 * 
 */
package com.olliebown.beads.core;

/**
 * AudioUtils is used for basic conversion of buffers of audio
 *         data between different formats.
 *
 * @author ollie 
 * 
 */
public final class AudioUtils {

	/**
	 * Converts a buffer of shorts to a buffer of floats.
	 * 
	 * @param out
	 *            buffer of floats.
	 * @param in
	 *            buffer of shorts.
	 */
	static final public void shortToFloat(float[] out, short[] in) {
		for (int i = 0; i < in.length; i++) {
			out[i] = (float) (in[i] / 32768.);
		}
	}

	/**
	 * Converts a buffer of floats to a buffer of shorts.
	 * 
	 * @param out
	 *            buffer of shorts.
	 * @param in
	 *            buffer of floats.
	 */
	static final public void floatToShort(short[] out, float[] in) {
		for (int i = 0; i < in.length; i++) {
			out[i] = (short) (32767. * in[i]);
		}
	}

	/**
	 * Converts a buffer of floats to a buffer of bytes with a given byte order.
	 * 
	 * @param out
	 *            buffer of bytes.
	 * @param in
	 *            buffer of floats.
	 * @param bigEndian
	 *            true for big endian byte order, false otherwise.
	 */
	static final public void floatToByte(byte[] out, float[] in,
			boolean bigEndian) {
		int bufsz = in.length;
		int ib = 0;
		if (bigEndian) {
			for (int i = 0; i < bufsz; ++i) {
				short y = (short) (32767. * Math.min(Math.max(in[i], -1.0f),
						1.0f));
				out[ib++] = (byte) ((y >> 8) & 0xFF);
				out[ib++] = (byte) (y & 0xFF);
			}
		} else {
			for (int i = 0; i < bufsz; ++i) {
				short y = (short) (32767. * in[i]);
				out[ib++] = (byte) (y & 0xFF);
				out[ib++] = (byte) ((y >> 8) & 0xFF);
			}
		}
	}

	/**
	 * Converts a buffer of bytes to a buffer of floats with a given byte order.
	 * 
	 * @param out
	 *            buffer of floats.
	 * @param in
	 *            buffer of bytes.
	 * @param bigEndian
	 *            true for big endian byte order, false otherwise.
	 */
	static final public void byteToFloat(float[] out, byte[] in,
			boolean bigEndian) {
		if (bigEndian) {
			int ib = 0;
			for (int i = 0; i < out.length; ++i) {
				float sample = ((in[ib + 0] << 8) | (in[ib + 1] & 0xFF)) / 32768.0F;
				ib += 2;
				out[i] = sample;
			}
		} else {
			int ib = 0;
			for (int i = 0; i < out.length; ++i) {
				float sample = ((in[ib] & 0xFF) | (in[ib + 1] << 8)) / 32768.0F;
				ib += 2;
				out[i] = sample;
			}
		}
	}

	/**
	 * De-interleave an interleaved buffer of floats to form a 2D array of
	 * floats of size nChannels x nFrames.
	 * 
	 * @param source
	 *            interleaved buffer of floats.
	 * @param nChannels
	 *            first dimension of resulting 2D array.
	 * @param nFrames
	 *            second dimension of resulting 2D array.
	 * @return 2D array with dimensions nChannels x nFrames.
	 */
	static final public void deinterleave(float[] source, int nChannels,
			int nFrames, float[][] result) {
		for (int i = 0, count = 0; i < nFrames; ++i) {
			for (int j = 0; j < nChannels; ++j) {
				result[j][i] = source[count++];
			}
		}
	}

	/**
	 * Interleave a 2D array of floats of size nChannels x nFrames to form a
	 * single interleaved buffer of floats.
	 * 
	 * @param source
	 *            2D array of floats.
	 * @param nChannels
	 *            first dimension of input 2D array.
	 * @param nFrames
	 *            second dimension of input 2D array.
	 * @return interleaved buffer of floats of length nChannels x nFrames.
	 */
	static final public void interleave(float[][] source, int nChannels,
			int nFrames, float[] result) {
		for (int i = 0, counter = 0; i < nFrames; ++i) {
			for (int j = 0; j < nChannels; ++j) {
				result[counter++] = source[j][i];
			}
		}
	}

}
