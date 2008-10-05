package com.olliebown.beads.trash;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.WavePlayer;

public class Test8 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test1();
	}
	
	public static void test1() {
		AudioContext ac = new AudioContext(512, 2000);
		
		
		
		Gain gain = new Gain(ac, 1);
		
		
		
		Envelope wpenv = new Envelope(ac, 500.0f);
		wpenv.addSegment(0.0f, 5000.0f);
		
		
		WavePlayer wp = new WavePlayer(ac, wpenv, new SineBuffer());
		
		
		Envelope gainenv = new Envelope(ac, 1.0f);
		gainenv.addSegment(0.0f, 500.0f);
		
		gain.setGainEnvelope(gainenv);
		
		gain.addInput(wp);
		
		ac.getRoot().addInput(gain);
		
		ac.start();
	}

}
