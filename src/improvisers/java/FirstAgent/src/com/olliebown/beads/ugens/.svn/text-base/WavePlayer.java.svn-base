
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SawBuffer;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.data.StaticBuffer;
import com.olliebown.beads.envelopes.Envelope;

//This should be rebuilt without the input, which is superfluous now that 
//frequencyEnvelope is a UGen -- i.e., frequency modulation can be done via that alone.

public class WavePlayer extends UGen {

    float point;
    UGen frequencyEnvelope;
    StaticBuffer buffer;
    
    public WavePlayer(AudioContext context, UGen frequencyEnvelope, StaticBuffer buffer) {
        super(context, 1, 1);
        this.frequencyEnvelope = frequencyEnvelope;
        this.buffer = buffer;
    }
    
    public WavePlayer(AudioContext context, float frequency, StaticBuffer buffer) {
        super(context, 1, 1);
        frequencyEnvelope = new Envelope(context);
        ((Envelope)frequencyEnvelope).setValue(frequency);
        this.buffer = buffer;
    }
    
    //factory methods
    public static WavePlayer newSinePlayer(AudioContext context, float frequency) {
        return new WavePlayer(context, frequency, new SineBuffer());
    }
    
    public static WavePlayer newSawPlayer(AudioContext context, float frequency) {
        return new WavePlayer(context, frequency, new SawBuffer());
    }
    
    public void start() {
        super.start();
        point = 0;
    }
    
    @Override
    public void calculateBuffer() {
    	frequencyEnvelope.update();
        float sr = context.getSampleRate();
        for(int i = 0; i < bufferSize; i++) {
            float frequency = frequencyEnvelope.getValue(0, i);
            float realFrequency;
            if(noInputs()) realFrequency = (frequency / sr);
            else realFrequency = bufIn[0][i] * (frequency / sr); //here's the frequency modulation
            point += realFrequency;
            point = point % 1.0f;
            bufOut[0][i] = buffer.getValueFraction(point);
         }
        //System.out.println(bufOut[0][0]);
    }

	public UGen getFrequencyEnvelope() {
		return frequencyEnvelope;
	}

	public void setFrequencyEnvelope(UGen frequencyEnvelope) {
		this.frequencyEnvelope = frequencyEnvelope;
	}
	
	public static void main(String[] args) {
		Tools.ac = new AudioContext(512);
		ScalingMixer sm = new ScalingMixer(Tools.ac, 2);
		Tools.ac.getRoot().addInput(sm);
		for(int i = 0; i < 200; i++) {
			WavePlayer wp = new WavePlayer(Tools.ac, 400f + (float)Math.random() * 1000f, new SineBuffer());
			sm.addInput(wp);
		}
		Tools.ac.start();
	}


}
