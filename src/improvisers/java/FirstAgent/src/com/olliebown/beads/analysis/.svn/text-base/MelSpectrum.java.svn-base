//Much code taken from MEAP

package com.olliebown.beads.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.PowerSpectrumListener;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.SamplePlayer;
import com.olliebown.beads.ugens.WavePlayer;
import com.olliebown.beads.data.Sample;

public class MelSpectrum implements PowerSpectrumListener {

	private final AudioContext ac;
	// for each mel bin...
	protected double[] melCenter; // actual targe mel value at center of this
	// bin
	protected double[] melWidth; // mel width divisor for this bin (constant,
	// except broadens in low bins)
	// for each fft bin
	protected double[] melOfLin;

	protected float[] features;
	String[] featureDescription;
	
	
	protected boolean accumulate;
	protected ArrayList<float[]> accumulatedFeatures;

	public MelSpectrum(AudioContext ac, int numCoeffs) {
		this.ac = ac;
		setNumFeatures(numCoeffs);
		accumulate = false;
		accumulatedFeatures = new ArrayList<float[]>();
	}

	public void setNumFeatures(int numFeatures) {
		int twiceBufferSize = ac.getBufferSize() * 2;
		features = new float[numFeatures];
		// Calculate the locations of the bin centers on the mel scale and
		// as indices into the input vector
		melCenter = new double[numFeatures + 2];
		melWidth = new double[numFeatures + 2];
		double melMin = lin2mel(0);
		double melMax = lin2mel((8000.0 < ac.getSampleRate() / 2) ? 8000.0 : ac
				.getSampleRate() / 2); // dpwe 2006-12-11 - hard maximum
		double hzPerBin = ac.getSampleRate() / 2 / twiceBufferSize;
		for (int i = 0; i < numFeatures + 2; i++) {
			melCenter[i] = melMin + i * (melMax - melMin) / (numFeatures + 1);
		}
		for (int i = 0; i < numFeatures + 1; i++) {
			melWidth[i] = melCenter[i + 1] - melCenter[i];
			double linbinwidth = (mel2lin(melCenter[i + 1]) - mel2lin(melCenter[i]))
					/ hzPerBin;
			if (linbinwidth < 1) {
				melWidth[i] = lin2mel(mel2lin(melCenter[i]) + hzPerBin)
						- melCenter[i];
			}
		}
		// precalculate mel translations of fft bin frequencies
		melOfLin = new double[twiceBufferSize];
		for (int i = 0; i < twiceBufferSize; i++) {
			melOfLin[i] = lin2mel(i * ac.getSampleRate()
					/ (2 * twiceBufferSize));
			// System.out.println("linbin2Mel["+i+"]="+linbin2mel[i]);
		}
		featureDescription = new String[numFeatures];
		for (int i = 0; i < numFeatures; i++) {
			if(i < 9) featureDescription[i] = "mel0" + (i + 1);
			else featureDescription[i] = "mel" + (i + 1);
		}
	}

	public void calculateFeatures(float[] powerSpectrum) {
		float[] linSpec = new float[powerSpectrum.length];
		// convert log magnitude to linear magnitude for binning
		for (int band = 0; band < linSpec.length; band++)
			linSpec[band] = (float) Math.pow(10, powerSpectrum[band] / 10);
		// convert to mel scale
		for (int bin = 0; bin < features.length; bin++) {
			// initialize
			features[bin] = 0;
			for (int i = 0; i < linSpec.length; ++i) {
				//System.out.println(i + " " + linSpec.length);
				double weight = 1.0 - (Math.abs(melOfLin[i] - melCenter[bin]) / melWidth[bin]);
				if (weight > 0) {
					features[bin] += weight * linSpec[i];
				}
			}
			// Take log
			//features[bin] = (float)(10 * Math.log(features[bin]) / Math.log(10)) / (bin + 1); //mucking about by Ollie
			features[bin] = Math.max(0f, (float)(10 * Math.log(features[bin]) / Math.log(10)));
		}

		if(accumulate) accumulatedFeatures.add(features.clone());
		//printFeatures();
	}

	public String[] getFeatureDescriptions() {
		return featureDescription;
	}

	public float[] getFeatures() {
		return features;
	}

	public int getNumFeatures() {
		return features.length;
	}

	private void printFeatures() {
		for (int i = 0; i < features.length; i++) {
			System.out.print(features[i] + " ");
		}
		System.out.println();
	}

	public double lin2mel(double fq) {
		return 1127.0 * Math.log(1.0 + fq / 700.0);
	}

	public double mel2lin(double mel) {
		return 700.0 * (Math.exp(mel / 1127.0) - 1.0);
	}

	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 2000);

//		Sample s = SampleManager.sample("audio/1234.aif");
//		SamplePlayer sp = new SamplePlayer(ac, s);
//		ac.getRoot().addInput(sp);

		WavePlayer wp = new WavePlayer(ac, 600.0f, new SineBuffer());
		Gain g = new Gain(ac, 1, new Envelope(ac, 0.1f));
		g.addInput(wp);
		ac.getRoot().addInput(g);

		PowerSpectrum p = new PowerSpectrum(ac);
		p.addListener(new MelSpectrum(ac, 20));

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
