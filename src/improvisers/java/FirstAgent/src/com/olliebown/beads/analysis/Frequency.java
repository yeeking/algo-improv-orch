//Much code borrowed from MEAP

package com.olliebown.beads.analysis;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.PowerSpectrumListener;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.WavePlayer;


public class Frequency implements PowerSpectrumListener {

	static final int FIRSTBAND = 3;
	
	private float bin2hz;
	private float[] features;
	
	private boolean accumulate;
	private ArrayList<float[]> accumulatedFeatures;
	
	public Frequency(AudioContext context) {
		bin2hz = context.getSampleRate() / (2 * (context.getBufferSize() - 1));
		accumulate = false;
		accumulatedFeatures = new ArrayList<float[]>();
	}
	
	public synchronized void calculateFeatures(float[] powerSpectrum) {
		features = new float[1];
		// collect average linear spectrum
		double[] linSpec = new double[powerSpectrum.length];
		for (int band = 0; band < linSpec.length; band++) {
			linSpec[band] = Math.pow(10, powerSpectrum[band] / 10);
		}
		// now pick best peak from linspec
		double pmax = -1;
		int maxbin = 0;
		for (int band = FIRSTBAND; band < powerSpectrum.length; band++) {
			// double pwr = pitchWt[band]*linSpec[band];
			double pwr = linSpec[band];
			if (pwr > pmax) {
				pmax = pwr;
				maxbin = band;
			}
		}
		// cubic interpolation
		double yz = linSpec[maxbin];
		double ym = maxbin <= 0? linSpec[maxbin] : linSpec[maxbin - 1];
		double yp = maxbin < linSpec.length - 1 ? linSpec[maxbin + 1] : linSpec[maxbin];
		double k = (yp + ym) / 2 - yz;
		double x0 = (ym - yp) / (4 * k);
		//double c = yz - k * Math.pow(x0, 2);
		features[0] = (float)(bin2hz * (maxbin + x0));
		if(accumulate) accumulatedFeatures.add(features.clone());
		//printFeatures();
	}

	public void printFeatures() {
		for(int i = 0; i < features.length; i++) {
			System.out.print(features[i]);
		}
		System.out.println();
	}

	public String[] getFeatureDescriptions() {
		return new String[]{"frequency"};
	}

	public float[] getFeatures() {
		return features;
	}

	public int getNumFeatures() {
		return features.length;
	}

	public void setNumFeatures(int numFeatures) {
		//Not allowed
	}
	
	public void accumulate(boolean accumulate) {
		this.accumulate = accumulate;		
	}

	public ArrayList<float[]> getAccumulatedFeatures() {
		return accumulatedFeatures;
	}
	
	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 2000);
		Envelope e = new Envelope(ac);
		WavePlayer wp = new WavePlayer(ac, e, new SineBuffer());
		e.setValue(100f);
		e.addSegment(10000f, 10000f);
		Gain g = new Gain(ac, 1, new Envelope(ac, 0.1f));
		g.addInput(wp);
		ac.getRoot().addInput(g);
		PowerSpectrum p = new PowerSpectrum(ac);
		p.addListener(new Frequency(ac));
		ac.getRoot().addDependent(p);
		p.addInput(ac.getRoot());
		ac.start();
	}

}
