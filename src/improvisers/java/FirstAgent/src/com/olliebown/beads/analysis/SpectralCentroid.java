//Much code taken from MEAP

package com.olliebown.beads.analysis;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.PowerSpectrumListener;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.SamplePlayer;
import com.olliebown.beads.ugens.WavePlayer;

public class SpectralCentroid implements PowerSpectrumListener {

	private float[] features;
	private AudioContext ac;
	private boolean accumulate;
	private ArrayList<float[]> accumulatedFeatures;

	public SpectralCentroid(AudioContext ac) {
		features = new float[1];
		this.ac = ac;
		accumulate = false;
		accumulatedFeatures = new ArrayList<float[]>();
	}

	public void calculateFeatures(float[] powerSpectrum) {
		double[] avgSpecCentroid = new double[1];
		double num = 0;
		double den = 0;
		num = 0;
		den = 0;
		for (int band = 0; band < powerSpectrum.length; band++) {
			double freqCenter = band * (ac.getSampleRate() / 2)
					/ (powerSpectrum.length - 1);
			// convert back to linear power
			double p = Math.pow(10, powerSpectrum[band] / 10);

			num += freqCenter * p;
			den += p;
		}
		features[0] = (float) (num / den);
		if(accumulate) accumulatedFeatures.add(features.clone());
		//System.out.println(features[0]);
	}

	public String[] getFeatureDescriptions() {
		// TODO Auto-generated method stub
		return null;
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

	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 2000);
		
		Sample s = SampleManager.sample("audio/1234.aif");
		SamplePlayer sp = new SamplePlayer(ac, s);
		ac.getRoot().addInput(sp);

//		WavePlayer wp = new WavePlayer(ac, 600.0f, new SineBuffer());
//		Gain g = new Gain(ac, 1, new Envelope(ac, 0.1f));
//		g.addInput(wp);
//		ac.getRoot().addInput(g);

		PowerSpectrum p = new PowerSpectrum(ac);
		p.addListener(new SpectralCentroid(ac));

		ac.getRoot().addDependent(p);
		p.addInput(ac.getRoot());

		ac.start();
	}
	
	public void accumulate(boolean accumulate) {
		this.accumulate = accumulate;		
	}

	public ArrayList<float[]> getAccumulatedFeatures() {
		return accumulatedFeatures;
	}
}
