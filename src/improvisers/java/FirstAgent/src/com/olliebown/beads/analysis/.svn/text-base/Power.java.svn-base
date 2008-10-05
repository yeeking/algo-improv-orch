package com.olliebown.beads.analysis;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.FrameFeatureExtractor;
import com.olliebown.beads.core.UGen;


public class Power extends UGen implements FrameFeatureExtractor {

	private boolean accumulate;
	private ArrayList<float[]> accumulatedFeatures;
	private float power;
	
	public Power(AudioContext context) {
		super(context, 1, 0);
		accumulate = false;
		accumulatedFeatures = new ArrayList<float[]>();
	}
	
	public void accumulate(boolean accumulate) {
		this.accumulate = accumulate;
	}

	public ArrayList<float[]> getAccumulatedFeatures() {
		return accumulatedFeatures;
	}

	public String[] getFeatureDescriptions() {
		return new String[] {"Power"};
	}

	public float[] getFeatures() {
		return new float[] {power};
	}

	public int getNumFeatures() {
		return 1;
	}

	public void setNumFeatures(int numFeatures) {
		//not allowed
	}

	@Override
	public void calculateBuffer() {
		power = 0.0f;
		for(int i = 0; i < bufferSize; i++) {
			power += bufIn[0][i] * bufIn[0][i];
		}
		power = (float)Math.sqrt(power / (float)bufferSize);
		if(accumulate) accumulatedFeatures.add(new float[] {power});
	}

	
	
}






