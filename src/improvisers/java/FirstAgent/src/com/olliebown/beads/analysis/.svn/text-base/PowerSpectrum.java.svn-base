package com.olliebown.beads.analysis;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.FrameFeatureExtractor;
import com.olliebown.beads.core.PowerSpectrumListener;

public class PowerSpectrum extends FFT implements FrameFeatureExtractor {

	float[] powerSpectrum;
	ArrayList<PowerSpectrumListener> listeners;

	private boolean accumulate;
	private ArrayList<float[]> accumulatedFeatures;
	private int numFeatures = 10;
	
	public PowerSpectrum(AudioContext context) {
		super(context);
		listeners = new ArrayList<PowerSpectrumListener>();
		accumulate = false;
		accumulatedFeatures = new ArrayList<float[]>();
	}
	
	public void addListener(PowerSpectrumListener psl) {
		listeners.add(psl);
	}
	
	public void removeListener(PowerSpectrumListener psl) {
		listeners.remove(psl);
	}
	
	public void calculateBuffer() {
		//use superclass, FFT, to get fftReal and fftImag
		super.calculateBuffer();
		//calculate the power spectrum
		calculatePower();
		for(int i = 0; i < listeners.size(); i++) {
			listeners.get(i).calculateFeatures(powerSpectrum);
		}
	}
	
	private void calculatePower() {
		powerSpectrum = new float[fftReal.length / 2];
		for(int i = 0; i < fftReal.length / 2; i++) {
			powerSpectrum[i] = (float)Math.sqrt(fftReal[i] * fftReal[i] + fftImag[i] * fftImag[i]);
		}
		if(accumulate) accumulatedFeatures.add(getFeatures());
	}
	
	public float[] getBinnedPower(int bins) {
		float[] binnedPower = new float[bins];
		for(int i = 0; i < binnedPower.length; i++) {
			binnedPower[i] = 0.0f;
		}
		float scale = (float)powerSpectrum.length / (float)binnedPower.length;
		for(int i = 0; i < powerSpectrum.length; i++) {
			binnedPower[(int)(i / scale)] += powerSpectrum[i];
		}
		for(int i = 0; i < binnedPower.length; i++) {
			binnedPower[i] /= scale;
			//System.out.print(binnedPower[i] + " ");
		}
		//System.out.println();
		return binnedPower;
	}

	public float[] getFeatures() {
		return getBinnedPower(numFeatures); 
		//return powerSpectrum;
	}

	public String[] getFeatureDescriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNumFeatures() {
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures) {
		this.numFeatures = numFeatures;
	}

	public void accumulate(boolean accumulate) {
		this.accumulate = accumulate;		
	}

	public ArrayList<float[]> getAccumulatedFeatures() {
		return accumulatedFeatures;
	}

}
