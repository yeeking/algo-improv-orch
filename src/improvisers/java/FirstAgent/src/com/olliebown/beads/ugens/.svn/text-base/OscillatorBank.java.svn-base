
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.data.StaticBuffer;

public class OscillatorBank extends UGen {

    float[] frequency;
    float[] gains;
    float[] point;
    float[] increment;
    StaticBuffer buffer;
    int numOscillators;
    float sr, gain;
    
    public OscillatorBank(AudioContext player, StaticBuffer buffer, int numOscillators) {
        super(player, 1, 1);
        this.buffer = buffer;
        this.numOscillators = numOscillators;
        frequency = new float[numOscillators];
        gains = new float[numOscillators];
        point = new float[numOscillators];
        increment = new float[numOscillators];
        for(int i = 0; i < numOscillators; i++) {
            point[i] = 0f;
            gains[i] = 1f;
            frequency[i] = 0f;
            increment[i] = frequency[i] / context.getSampleRate();
        }
        sr = context.getSampleRate();
        gain = 1f / (float)numOscillators;
    }
    
    public void setFrequencies(float[] frequencies) {
        for(int i = 0; i < numOscillators; i++) {
        	if(i < frequencies.length) {
        		frequency[i] = frequencies[i];
        	} else {
        		frequency[i] = 0f;
        	}
            increment[i] = frequency[i] / context.getSampleRate();
        }
    }
    
    public void setGains(float[] gains) {
    	for(int i = 0; i < numOscillators; i++) {
        	if(i < gains.length) {
        		this.gains[i] = gains[i];
        	} else {
        		this.gains[i] = 0f;
        	}
        }
    }

    @Override
    public void calculateBuffer() {
        zeroOuts();
        for(int i = 0; i < bufferSize; i++) {
            for(int j = 0; j < numOscillators; j++) {
                point[j] += increment[j];
                while(point[j] > 1.0f) point[j] -= 1.0f;
                bufOut[0][i] += gains[j] * buffer.getValueFraction(point[j]);
            }
            bufOut[0][i] *= gain;
        }
    }   
    
    public static void main(String[] args) {
    	AudioContext ac = new AudioContext(1024, 5000);
    	float[] frequencies = new float[30];
    	for(int i = 0; i < frequencies.length; i++) {
    		frequencies[i] = (float)Math.random() * 5000f + 100f;
    	}
    	OscillatorBank ob = new OscillatorBank(ac, new SineBuffer(), frequencies.length);
    	ob.setFrequencies(frequencies);
    	ac.getRoot().addInput(ob);
    	ac.start();    	
    }
    
}




