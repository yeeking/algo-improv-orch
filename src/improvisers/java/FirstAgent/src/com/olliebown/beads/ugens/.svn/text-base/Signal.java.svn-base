
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;

public class Signal extends UGen {

    private float value;
    
    public Signal(AudioContext player, float value) {
        super(player, 1);
        setValue(value);
    }
    
    public Signal(AudioContext player) {
        this(player, 0.0f);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
    
    @Override
    public void calculateBuffer() {
        for(int i = 0; i < bufferSize; i++) {
            bufOut[0][i] = value;
        }
    }
    
    public static void main(String[] args) {
    	Tools.init();
    	Gain g = new Gain(Tools.ac, 2, new Envelope(Tools.ac, 1f));
    	WavePlayer wp = new WavePlayer(Tools.ac, 500f, new SineBuffer());
    	g.addInput(wp);
    	Tools.ac.getRoot().addInput(g);
    	for(int i = 0; i < 2000; i++) {
    		Signal s = new Signal(Tools.ac);
    		g.addInput(s);
    	}
    	Tools.ac.start();
    }

}
