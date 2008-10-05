package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;

public class DelayTrigger extends UGen {

	private long sampleDelay;
	private long count;
	private Bead receiver;
	
	public DelayTrigger(AudioContext context, float delay, Bead receiver) {
		super(context, 0, 0);
		sampleDelay = (long)context.msToSamples(delay);
		reset();
		this.receiver = receiver;
	}
	
	public void reset() {
		count = 0;
	}

	@Override
	public void calculateBuffer() {
		if(sampleDelay - count > bufferSize) {
			count += bufferSize;
		} else {
			if(receiver != null) receiver.message(this);
		}
	}

	public float getSampleDelay() {
		return context.samplesToMs(sampleDelay);
	}

	public void setSampleDelay(float sampleDelay) {
		this.sampleDelay = (long)context.msToSamples(sampleDelay);
	}

	public Bead getReceiver() {
		return receiver;
	}

	public void setReceiver(Bead receiver) {
		this.receiver = receiver;
	}

	public float getCount() {
		return context.samplesToMs(count);
	}
	
}
