
package com.olliebown.beads.data;

// TODO: Auto-generated Javadoc
/**
 * The Class SineBuffer.
 */
public class SineBuffer implements StaticBuffer {

	private static final long serialVersionUID = 1;
	
    /** The default buffer size (512). */
    public static final int DEFAULT_BUFFER_SIZE = 512;
    
    /** The buffer. */
    private static float[] buf = null;
    
    static {
    	new SineBuffer();
    }
    
    /**
	 * Instantiates a new sine buffer.
	 */
    public SineBuffer() {
     if(buf == null) generateWindow(DEFAULT_BUFFER_SIZE);
    }
    
    /**
	 * Instantiates a new sine buffer with the specified buffer size.
	 * 
	 * @param bufferSize
	 *            the buffer size
	 */
    public SineBuffer(int bufferSize) {
    	generateWindow(bufferSize);
    }
    
    /* (non-Javadoc)
     * @see com.olliebown.beads.data.StaticBuffer#getValueFraction(float)
     */
    public float getValueFraction(float fraction) {
        return getValueIndex((int)(fraction * buf.length));
    }
    
    /* (non-Javadoc)
     * @see com.olliebown.beads.data.StaticBuffer#getValueIndex(int)
     */
    public float getValueIndex(int index) {
        if(index < buf.length && index >= 0) return buf[index];
        else return 0.0f;
    }
    
    /* (non-Javadoc)
     * @see com.olliebown.beads.data.StaticBuffer#getBufferSize()
     */
    public int getBufferSize() {
        return buf.length;
    }

    /* (non-Javadoc)
     * @see com.olliebown.beads.data.StaticBuffer#generateWindow(int)
     */
    public void generateWindow(int bufferSize) {
        buf = new float[bufferSize];
        for(int i = 0; i < bufferSize; i++) {
            buf[i] = (float)Math.sin(2.0 * Math.PI * (double)i / (double)bufferSize);
        }
    }
    
}
