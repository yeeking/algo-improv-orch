
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;

public class Gain extends UGen {

    private UGen gainEnvelope;
    
    public Gain(AudioContext context, int inouts, UGen gainEnvelope) {
       this(context, inouts);
        setGainEnvelope(gainEnvelope);
    }
    
    public Gain(AudioContext context, int inouts) {
        super(context, inouts, inouts);
        gainEnvelope = new Static(context, 1.0f);
    }
    
    public UGen getGainEnvelope() {
        return gainEnvelope;
    }
    
    public void setGainEnvelope(UGen gainEnvelope) {
        this.gainEnvelope = gainEnvelope;
    }
    
    @Override
    public void calculateBuffer() {
    	gainEnvelope.update();
        for(int i = 0; i < bufferSize; ++i) {
            float gain = gainEnvelope.getValue(0, i);
            for(int j = 0; j < ins; ++j) {
                bufOut[j][i] = gain * bufIn[j][i];
            }
        }
    }

}
