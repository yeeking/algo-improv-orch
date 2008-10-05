package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;

public class Clicker extends UGen {

	public Clicker(AudioContext context) {
		super(context, 0, 1);
	}

	@Override
	public void calculateBuffer() {
		bufOut[0][0] = 1.0f;
		for (int i = 1; i < bufferSize; i++) {
			bufOut[0][i] = 0.0f;
		}
		stop();
	}

}