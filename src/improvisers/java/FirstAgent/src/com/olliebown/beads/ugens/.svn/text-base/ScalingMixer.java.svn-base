
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;

public class ScalingMixer extends UGen {

	public ScalingMixer(AudioContext player) {
        this(player, 1);
    }
	
    public ScalingMixer(AudioContext player, int inouts) {
        super(player, inouts, inouts);
    }
    
    @Override
    public void calculateBuffer() {
        for(int i = 0; i < ins; i++) {
            int numInputs = getNumberOfConnectedUGens(i);
	        for(int j = 0; j < bufferSize; j++) {
	            bufOut[i][j] = bufIn[i][j] / (float)numInputs;
	            //System.out.println(bufIn[0][i] + " " + bufOut[0][i] + " " + numInputs);
	        }
        }
    }

}
