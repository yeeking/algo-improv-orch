
package com.olliebown.beads.trash;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.instruments.FMSynth;
import com.olliebown.beads.instruments.FMSynth.FMOperator;

public abstract class FM {

    public static FMSynth squidge(AudioContext ac, float frequency) {
    	
    	//currently broken because haven't worked out a way to make envelopes suitably adaptable
    	
        FMSynth fm = new FMSynth(ac);
        fm.setFrequencyEnvelope(new Envelope(ac, frequency));
        FMOperator op = fm.newOperator(300.0f);
        ((Envelope)op.getHarmonicityEnvelope()).addSegment(2.0f, 10000.0f);
        fm.addOperator(0, op);
        op = fm.newOperator(200.0f);
        ((Envelope)op.getHarmonicityEnvelope()).addSegment(2.0f, 10000.0f);
        fm.addOperator(1, op);
        ((Envelope)fm.getGainEnvelope()).addSegment(0.0f, 1000.0f, fm.getStopTrigger());
        return fm;
    }
    
        
}
