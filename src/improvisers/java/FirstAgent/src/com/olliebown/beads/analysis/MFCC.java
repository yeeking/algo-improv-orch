//Much code taken from MEAP

package com.olliebown.beads.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.ugens.SamplePlayer;


public class MFCC extends MelSpectrum {

	private float[] mfccs;
	
	private ArrayList<float[]> accumulatedFeaturesMFCC;
	
	public MFCC(AudioContext ac, int numCoeffs) {
		super(ac, numCoeffs);
		mfccs = new float[numCoeffs];
		accumulatedFeaturesMFCC = new ArrayList<float[]>();
		featureDescription = new String[numCoeffs];
		for (int i = 0; i < numCoeffs; i++) {
			if(i < 9) featureDescription[i] = "mfcc0" + (i + 1);
			else featureDescription[i] = "mfcc" + (i + 1);
		}
	}
	
	public void calculateFeatures(float[] powerSpectrum) {
        // precompute DCT matrix
        int nmel = features.length;  
        double m = Math.sqrt(2.0/nmel);
        double[][] DCTcoeffs = new double[nmel][features.length];
        for(int i = 0; i < nmel; i++) {
            for(int j = 0; j < features.length; j++) {
                DCTcoeffs[i][j] = m*Math.cos(Math.PI*(j+1)*(i+.5)/(double)nmel);
            }
        }
        super.calculateFeatures(powerSpectrum);
        // convert to cepstrum:
        for(int x = 0; x < features.length; x++) {
            // convert from dB to plain old log magnitude
            features[x] = features[x]/10;  
            // take DCT
            for(int y = 0; y < features.length; y++) {
                mfccs[y] = (float)(DCTcoeffs[x][y]*features[x]);
            }
        }
        if(accumulate) accumulatedFeaturesMFCC.add(mfccs.clone());
        //printFeatures();
	}
	
	public void setNumFeatures(int numFeatures) {
		super.setNumFeatures(numFeatures);
		featureDescription = new String[numFeatures];
		for (int i = 0; i < numFeatures; i++) {
			if(i < 9) featureDescription[i] = "mfcc0" + (i + 1);
			else featureDescription[i] = "mfcc" + (i + 1);
		}
	}

	public String[] getFeatureDescriptions() {
		return featureDescription;
	}

	public float[] getFeatures() {
		return mfccs;
	}

	public int getNumFeatures() {
		return mfccs.length;
	}

	private void printFeatures() {
		for (int i = 0; i < mfccs.length; i++) {
			System.out.print(mfccs[i] + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 2000);
		
		Sample s = SampleManager.sample("audio/1234.aif");
		SamplePlayer sp = new SamplePlayer(ac, s);
		ac.getRoot().addInput(sp);

		//WavePlayer wp = new WavePlayer(ac, 600.0f, new SineBuffer());
		//Gain g = new Gain(ac, 1, new Envelope(ac, 0.1f));
		//g.addInput(wp);
		//ac.getRoot().addInput(g);

		PowerSpectrum p = new PowerSpectrum(ac);
		p.addListener(new MFCC(ac, 20));

		ac.getRoot().addDependent(p);
		p.addInput(ac.getRoot());

		ac.start();
	}

	public ArrayList<float[]> getAccumulatedFeatures() {
		return accumulatedFeaturesMFCC;
	}
}
