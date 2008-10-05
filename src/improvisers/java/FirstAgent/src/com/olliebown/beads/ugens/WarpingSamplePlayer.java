package com.olliebown.beads.ugens;

import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SegmentedSample;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.events.AudioContextStopTrigger;

public class WarpingSamplePlayer extends GranularSamplePlayer {

	private Iterator<int[]> timePoints;
	private float nextTimePoint = 0;
	private float timeGap;
	private boolean rateDecay;

	public WarpingSamplePlayer(AudioContext context, 
			SegmentedSample buffer) {
		super(context);
		setBuffer(buffer);
		timePoints = buffer.getTimePoints().iterator();
		rateDecay = false;
		message(this);
	}
	
	public boolean isRateDecay() {
		return rateDecay;
	}

	public void setRateDecay(boolean rateDecay) {
		this.rateDecay = rateDecay;
	}

	public void calculateBuffer() {
		super.calculateBuffer();
		if(rateDecay) {
			rate = (nextTimePoint - (float)position) / timeGap;
			System.out.println(rate);
		}
	}

	public void message(Bead message) {
		//TODO check position, because it may be that we've looped
		
		
		
		if (timePoints.hasNext()) {
			//work out how long until the next event
			timeGap = nextTimePoint;			//position is in ms now
			position = nextTimePoint; //and move to this point
			nextTimePoint = timePoints.next()[0];
			timeGap = Math.max(1, nextTimePoint - timeGap);
			if(!rateDecay && message instanceof Clock) {
				Clock clock = (Clock)message;
				float untilNextTick = clock.getIntervalEnvelope().getValue(); 
				float timeLeft = Math.max(0, nextTimePoint - (float)position);
				rateEnvelope.setValue(timeLeft / untilNextTick);
			}  
		}
	}
	
	//TODO test the rate decay mode.
	//TODO how is this going to work whilst looping.

	public static void main(String[] args) {
		try {
			AudioContext context = new AudioContext(512, 1500);
			Clock clock = new Clock(context, 500f);
//			clock.setClick(true);
//			SegmentedSample ss = new SegmentedSample("audio/1234.aif");
			SegmentedSample ss1 = new SegmentedSample("/Library/Audio/Apple Loops/Apple/Apple Loops for GarageBand/80s Pop Beat 09.aif", 0.5f);
			SegmentedSample ss2 = new SegmentedSample("/Library/Audio/Apple Loops/Apple/Apple Loops for GarageBand/Acoustic Picking 01.aif", 0.1f);
			WarpingSamplePlayer w1 = new WarpingSamplePlayer(context, ss1);
			w1.getGrainSizeEnvelope().setValue(30f);
			w1.setGrainIntervalEnvelope(new Add(context, new Static(context, 10f), 
										new Mult(context, new Static(context, 5f), 
												new WavePlayer(context, 1f, new SineBuffer() ) )));
			w1.getRandomnessEnvelope().setValue(0.001f);
			
			WarpingSamplePlayer w2 = new WarpingSamplePlayer(context, ss2);
			w2.getGrainSizeEnvelope().setValue(100f);
			w2.setGrainIntervalEnvelope(new Add(context, new Static(context, 40f), 
										new Mult(context, new Static(context, 25f), 
												new WavePlayer(context, 0.25f, new SineBuffer() ) )));
			w2.getRandomnessEnvelope().setValue(0.001f);
			
			clock.addMessageListener(w1);
			clock.addMessageListener(w2);
			context.getRoot().addInput(w1);
			context.getRoot().addInput(w2);
			context.getRoot().addDependent(clock);
			Sample s = new Sample(context.getAudioFormat(), (int)context.msToSamples(20000f));
			Recorder r = new Recorder(context, s);
			r.start();
			r.addInput(context.getRoot());
			w1.addDependent(r);	
			w2.setEndListener(new AudioContextStopTrigger(context));
			context.start();
			while(context.isAlive()) {}
			s.write("/Users/ollie/Desktop/wsp.aif");		
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
