package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;


public class Add extends UGen {

	public Add(AudioContext context, UGen a, UGen b) {
		super(context, 2, 1);
		addInput(0, a, 0);
		addInput(1, b, 0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calculateBuffer() {
		for(int i = 0; i < bufferSize; i++) {
			bufOut[0][i] = bufIn[0][i] + bufIn[1][i];
		}
	}

}
