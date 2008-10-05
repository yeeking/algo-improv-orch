
package com.olliebown.beads.data;

/**
 * The Class SawBuffer.
 */
public class SawBuffer implements StaticBuffer {

	private static final long serialVersionUID = 1;
	
    /** The Constant DEFAULT_BUFFER_SIZE. */
    public static final int DEFAULT_BUFFER_SIZE = 512;
    
    /** The buf. */
    private static float[] buf = null;
    
    static {
    	new SawBuffer();
    }
    
    /**
	 * Instantiates a new saw buffer.
	 */
    public SawBuffer() {
     if(buf == null) generateWindow(DEFAULT_BUFFER_SIZE);
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
            buf[i] = (float)i / (float)bufferSize * 2.0f - 1.0f;
        }
    }
    
}