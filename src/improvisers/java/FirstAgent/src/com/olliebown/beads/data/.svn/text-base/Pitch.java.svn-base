
package com.olliebown.beads.data;

/**
 * A set of static methods for manipulations associated with pitch.
 * @author ollie
 *
 */
public abstract class Pitch {
	
	public final static float LOG2 = (float)Math.log(2);
    
	/**
	 * Convert frequency to MIDI note number.
	 * @param frequency the required frequency.
	 * @return the resulting MIDI note number.
	 */
	public static final float ftom(float frequency) {
        return (float)Math.log(frequency / 440.0f) / LOG2 * 12f + 69f;
    }
    
	/**
	 * Convert MIDI note number to frequency. 
	 * @param midi the required MIDI note number.
	 * @return the resuling frequency.
	 */
	public static final float mtof(float midi) {
        return 440.0f * (float)Math.pow(2.0f, (midi - 69f) / 12.0f);
    }
	
	public static int[] dorian = {0, 2, 3, 5, 7, 9, 10};

	/**
	 * The major key.
	 */
	public static int[] major = {0, 2, 4, 5, 7, 9, 11};
	
	/**
	 * The minor key.
	 */
	public static int[] minor = {0, 2, 3, 5, 7, 8, 10};
    
    /**
     * The circle of fifths.
     */
    public static final int[] circleOfFifths = {0, 5, 10, 3, 8, 1, 6, 11, 4, 9, 2, 7};

}
