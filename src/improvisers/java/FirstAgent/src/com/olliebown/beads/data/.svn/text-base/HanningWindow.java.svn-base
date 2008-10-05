
package com.olliebown.beads.data;

/**
 * A StaticBuffer that gives you a Hanning Window. 
 * 
 * @author ollie
 * @see StaticBuffer
 */
public class HanningWindow implements StaticBuffer {

    private static final int DEFAULT_BUFFER_SIZE = 512;
    
    private static float[] buf = null;
    
    /**
     * Generates a static Hanning Window with the default buffer size (512).
     */
    public HanningWindow() {
        if(buf == null) generateWindow(DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Generates a static Hanning Window with the specified buffer size.
     * @param bufferSize
     */
    public HanningWindow(int bufferSize) {
    	generateWindow(bufferSize);
    }
    
    /**
     * @see com.olliebown.beads.data.StaticBuffer#getValueFraction(float)
     */
    public float getValueFraction(float fraction) { 
        return getValueIndex((int)(fraction * buf.length));
    }
    
    /**
     * @see com.olliebown.beads.data.StaticBuffer#getValueIndex(int)
     */
    public float getValueIndex(int index) {
        if(index < buf.length && index >= 0) return buf[index];
        else return 0.0f;
    }
    
    /**
     * @see com.olliebown.beads.data.StaticBuffer#getBufferSize()
     */
    public int getBufferSize() {
        return buf.length;
    }
    
    /**
     * @see com.olliebown.beads.data.StaticBuffer#generateWindow(int)
     */
    public void generateWindow(int bufferSize) {
        int lowerThresh = bufferSize / 4;
        int upperThresh = bufferSize - lowerThresh;
        buf = new float[bufferSize];
        for(int i = 0; i < bufferSize; i++) {
            if(i < lowerThresh || i > upperThresh) {
                buf[i] = 0.5f * (1.0f + (float)Math.cos((Math.PI + Math.PI * 4.0f * (float)i / (float)bufferSize)));
            } else {
                buf[i] = 1.0f;
            }
        }
    }
    
}
