package com.olliebown.beads.core;

import java.util.ArrayList;

/**
 * An interface identifying classes (likely to be UGens) that
 *         extract features from individual frames of audio.
 *         
 * @author ollie 
 * 
 */
public interface FrameFeatureExtractor {

	/**
	 * Gets the current feature set for the current frame.
	 * 
	 * @return an array of floats.
	 */
	public float[] getFeatures();

	/**
	 * Gets the number of features produced by this FrameFeatureExtractor.
	 * 
	 * @return number of features.
	 */
	public int getNumFeatures();

	/**
	 * Sets the number of features produced by this FrameFeatureExtractor.
	 * 
	 * @param numFeatures
	 *            number of features.
	 */
	public void setNumFeatures(int numFeatures);

	/**
	 * Gets an array of Strings describing the features returned by this
	 * FrameFeatureExtractor.
	 * 
	 * @return array of Strings describing features.
	 */
	public String[] getFeatureDescriptions();
	
	public void accumulate(boolean accumulate);
	public ArrayList<float[]> getAccumulatedFeatures();

}
