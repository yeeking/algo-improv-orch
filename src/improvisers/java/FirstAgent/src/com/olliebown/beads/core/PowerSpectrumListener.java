package com.olliebown.beads.core;


public interface PowerSpectrumListener extends FrameFeatureExtractor {

	public void calculateFeatures(float[] powerSpectrum);
	
}
