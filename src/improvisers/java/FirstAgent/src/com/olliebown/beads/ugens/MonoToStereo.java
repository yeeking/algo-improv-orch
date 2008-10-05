
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;

public class MonoToStereo extends UGen {
    
    public MonoToStereo(AudioContext player) {
        super(player, 1, 2);
    }
    
    @Override
    public void calculateBuffer() {
        for(int i = 0; i < bufferSize; i++) {
            for(int j = 0; j < outs; j++) {
                bufOut[j][i] = bufIn[0][i];
            }
        }
    }

}
