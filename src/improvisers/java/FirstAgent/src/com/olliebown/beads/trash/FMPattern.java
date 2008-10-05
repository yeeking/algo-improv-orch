
package com.olliebown.beads.trash;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.events.IntegerBead;
import com.olliebown.beads.ugens.Gain;

public class FMPattern extends Gain {
    
    AudioContext ac;
    float[] freqs = {800.0f, 400.0f, 1000.0f};
    
    public FMPattern(AudioContext ac) {
        super(ac, 1);
        this.ac = ac;
    }
    
    public void setFreqs(float[] freqs) {
    	this.freqs = freqs;
    }
    
    public void message(Bead message) {
        //just look at the clock count to see what time it is
        addInput(FM.squidge(ac, freqs[((IntegerBead)message).getInt() % freqs.length])); 
    }
}
