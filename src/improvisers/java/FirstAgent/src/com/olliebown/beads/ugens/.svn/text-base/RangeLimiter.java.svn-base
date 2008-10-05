package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;

public class RangeLimiter extends UGen {

	public RangeLimiter(AudioContext context, int inouts) {
		super(context, inouts, inouts);
	}

	@Override
	public void calculateBuffer() {
		for(int i = 0; i < bufferSize; i++) {
			for(int j = 0; j < ins; j++) {
				bufOut[j][i] = bufIn[j][i];
				if(bufOut[j][i] > 1.0f) bufOut[j][i] = 1.0f;
				else if(bufOut[j][i] < -1.0f) bufOut[j][i] = -1.0f;
			}
			//System.out.println(bufOut[0][i]);
		}
	}

	
}
