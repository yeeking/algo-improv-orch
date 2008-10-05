
package com.olliebown.beads.instruments;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.BeadArray;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.Mult;
import com.olliebown.beads.ugens.WavePlayer;
import java.util.ArrayList;

public class FMSynth extends Gain {

    private ArrayList<FMOperator> operators;
    BeadArray stopTrigger;
    UGen frequencyEnvelope;
    
    public class FMOperator extends Gain {
        
        private WavePlayer wp;
        
        public FMOperator(AudioContext context) {
        	super(context, 1);
        }
        
        public FMOperator(AudioContext context, UGen harmonicityEnvelope) {
            this(context);
            wp = new WavePlayer(context, harmonicityEnvelope, new SineBuffer());
            addInput(wp);
        }
        
        @Override
        public void stop() {
            wp.stop();
            super.stop();
        }
        
        public WavePlayer getWavePlayer() {
            return wp;
        }
        
        public UGen getHarmonicityEnvelope() {
        	return wp.getFrequencyEnvelope();
        }
        
        public void setHarmonicityEnvelope(UGen harmonicityEnvelope) {
        	wp.setFrequencyEnvelope(harmonicityEnvelope);
        }
       
    }   
    
    public FMSynth(AudioContext context) {
        //test code only
        super(context, 1);
        frequencyEnvelope = new Envelope(context, 441.0f);
        operators = new ArrayList<FMOperator>();
        FMOperator op1 = new FMOperator(context, frequencyEnvelope);
        stopTrigger = new BeadArray();
        stopTrigger.add(this);
        operators.add(op1);
        addInput(op1);
    }
    
    public FMOperator getRoot() {
        return operators.get(0);
    }
    
    public void setFrequencyEnvelope(UGen frequencyEnvelope) {
    	this.frequencyEnvelope = frequencyEnvelope;
    	operators.get(0).setHarmonicityEnvelope(frequencyEnvelope);
    }
    
    public UGen getFrequencyEnvelope() {
    	return frequencyEnvelope;
    }
    
    @Override
    public void stop() {
        for(FMOperator op : operators) op.stop();
        super.stop();
    }
    
    public void addOperator(int attachNode, FMOperator op) {
        operators.get(attachNode).addInput(op);
        operators.add(op);
    }
    
    public FMOperator newOperator(float harmonicity) {
    	Mult mult = new Mult(context, frequencyEnvelope, new Envelope(context, harmonicity));
    	FMOperator op = new FMOperator(getContext(), mult);
    	return op;
    }
    
    
    public FMOperator newOperator(UGen frequencyEnvelope) {
    	FMOperator op = new FMOperator(getContext(), frequencyEnvelope);
    	return op;
    }

    public BeadArray getStopTrigger() {
        return stopTrigger;
    }
    
}
