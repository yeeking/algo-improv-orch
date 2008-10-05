package com.olliebown.beads.core;

import java.io.IOException;
import java.util.Random;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.events.AudioContextStopTrigger;
import com.olliebown.beads.ugens.Clock;
import com.olliebown.beads.ugens.MouseResponder;
import com.olliebown.beads.ugens.RTInput;
import com.olliebown.beads.ugens.Recorder;


public class Tools {
	
	public static AudioContext ac;
	public static Clock clock;
	public static MouseResponder mouse;
	public static RTInput in;
	public static Random rng;
	
	public static void init() {
		//create
		rng = new Random();
		ac = new AudioContext(512, 2000);
		clock = new Clock(ac, new Envelope(ac, 500f));
		mouse = new MouseResponder(ac);
		in = new RTInput(ac);
		//connect
		ac.getRoot().addDependent(clock);
		ac.getRoot().addDependent(mouse);
	}
	
	public static void record(float timeMS, String filename) throws IOException {
		Sample s = new Sample(ac.getAudioFormat(), (int)ac.msToSamples(timeMS));
		Recorder r = new Recorder(ac, s);
		r.addInput(ac.getRoot());
		ac.getRoot().addDependent(r);
		r.start();
		r.setEndListener(new AudioContextStopTrigger(ac));
		while(ac.isAlive()) {}
		s.write(filename);
	}
}
