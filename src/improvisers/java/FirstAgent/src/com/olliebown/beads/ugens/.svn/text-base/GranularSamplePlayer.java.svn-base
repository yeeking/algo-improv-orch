package com.olliebown.beads.ugens;

import com.olliebown.beads.data.Sample;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.HanningWindow;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.data.StaticBuffer;
import com.olliebown.beads.envelopes.Envelope;
import java.util.ArrayList;

public class GranularSamplePlayer extends SamplePlayer {

    private UGen pitchEnvelope;
    private UGen grainIntervalEnvelope;
    private UGen grainSizeEnvelope;
    private UGen randomnessEnvelope;
    private float timeSinceLastGrain;
    private float msPerSample;
    protected float pitch;
    private ArrayList<Grain> grains;
    private ArrayList<Grain> freeGrains;
    private ArrayList<Grain> deadGrains;
    private StaticBuffer window;
    
    private float[] frame;

    private static class Grain {
        boolean free;
        float startTime;
        float position;
        float age;
    }

    public GranularSamplePlayer(AudioContext context) {
        super(context);
        grains = new ArrayList<Grain>();
        freeGrains = new ArrayList<Grain>();
        deadGrains = new ArrayList<Grain>();
        pitchEnvelope = new Envelope(context, 1f);
        setGrainIntervalEnvelope(new Static(context, 70.0f));
        setGrainSizeEnvelope(new Static(context, 100.0f));
        setRandomnessEnvelope(new Static(context, 0.0f));
        window = new HanningWindow();
        msPerSample = context.samplesToMs(1f);
    }

    public GranularSamplePlayer(AudioContext context, Sample buffer) {
        this(context);
        setBuffer(buffer);
    }

    public UGen getPitchEnvelope() {
        return pitchEnvelope;
    }

    public void setPitchEnvelope(UGen pitchEnvelope) {
        this.pitchEnvelope = pitchEnvelope;
    }

    public UGen getGrainIntervalEnvelope() {
        return grainIntervalEnvelope;
    }

    public void setGrainIntervalEnvelope(UGen grainIntervalEnvelope) {
        this.grainIntervalEnvelope = grainIntervalEnvelope;
    }

    public UGen getGrainSizeEnvelope() {
        return grainSizeEnvelope;
    }

    public void setGrainSizeEnvelope(UGen grainSizeEnvelope) {
        this.grainSizeEnvelope = grainSizeEnvelope;
    }

    public UGen getRandomnessEnvelope() {
		return randomnessEnvelope;
	}

	public void setRandomnessEnvelope(UGen randomnessEnvelope) {
		this.randomnessEnvelope = randomnessEnvelope;
	}

	@Override
    public void start() {
        super.start();
        timeSinceLastGrain = 0;
    }
	
	public void stop() {
		super.stop();
	}
    
    private void resetGrain(Grain g, int time) {
        g.startTime = (float)position + (grainSizeEnvelope.getValue(0, time) * randomnessEnvelope.getValue(0, time) * (float)(Math.random() * 2.0f - 1.0f));
        g.position = g.startTime;
        g.age = 0f;
    }   
    

    @Override
    public void calculateBuffer() {
    	//update the various envelopes
    	rateEnvelope.update();
    	positionEnvelope.update();
    	loopStartEnvelope.update();
    	loopEndEnvelope.update();
    	pitchEnvelope.update();
    	grainIntervalEnvelope.update();
    	grainSizeEnvelope.update();
    	randomnessEnvelope.update();
    	//now loop through the buffer
        for (int i = 0; i < bufferSize; i++) {
            //determine if we need a new grain
            if (timeSinceLastGrain > grainIntervalEnvelope.getValue(0, i)) {
                if(freeGrains.size() > 0) {
                    Grain g = freeGrains.get(0);
                    freeGrains.remove(0);
                    resetGrain(g, i);
                    grains.add(g);
                } else {
                    Grain g = new Grain();
                    resetGrain(g, i);
                    grains.add(g);
                }
                timeSinceLastGrain = 0f;
            }
            //for each channel, start by resetting current output frame
            for (int j = 0; j < outs; j++) {
                bufOut[j][i] = 0.0f;
            }
            //gather the output from each grain
            for(int gi = 0; gi < grains.size(); gi++) {
                Grain g = grains.get(gi);
                //calculate value of grain window
                float windowScale = window.getValueFraction(g.age / grainSizeEnvelope.getValue(0, i));
                //get position in sample for this grain
                float samplePosition = context.msToSamples(g.position);
                int currentSample = (int)samplePosition;
                float fractionOffset = samplePosition - currentSample;
                //get the frame for this grain
                switch (interpolationType) {
                    case LINEAR:
                        frame = buffer.getFrameLinear(currentSample, fractionOffset);
                        break;
                    case CUBIC:
                        frame = buffer.getFrameCubic(currentSample, fractionOffset);
                        break;
                }
                //add it to the current output frame
                for (int j = 0; j < outs; j++) {
                    bufOut[j][i] += windowScale * frame[j];
                }
            }
            //increment time and stuff
            calculateNextPosition(i);
            //for(Grain g : grains) {
            pitch = Math.abs(pitchEnvelope.getValue(0, i));
            for(int gi = 0; gi < grains.size(); gi++) {
                Grain g = grains.get(gi);
                calculateNextGrainPosition(g);
            }
            if (muted) {
                //make sure to zero the remaining outs
                while(i < bufferSize) {
                    for (int j = 0; j < outs; j++) {
                        bufOut[j][i] = 0.0f;
                    }
                    i++;
                }
                break;
            }
            //increment timeSinceLastGrain
            timeSinceLastGrain += msPerSample;
            //finally, see if any grains are dead
            for(int gi = 0; gi < grains.size(); gi++) {
                Grain g = grains.get(gi);
                if(g.age > grainSizeEnvelope.getValue(0, i)) {
                    freeGrains.add(g);
                    deadGrains.add(g);
                }
            }
            for(int gi = 0; gi < deadGrains.size(); gi++) {
                Grain g = deadGrains.get(gi);
                grains.remove(g);
            }
            deadGrains.clear();
        }
    }
    
    private void calculateNextGrainPosition(Grain g) {
    	int direction = rate > 0 ? 1 : -1;
    	g.age += msPerSample;
        switch(loopType) {
            case NO_LOOP_FORWARDS:
                g.position += direction * positionIncrement * pitch;
                break;
            case NO_LOOP_BACKWARDS:
                g.position -= direction * positionIncrement * pitch;
                break;
            case LOOP_FORWARDS:
                g.position += direction * positionIncrement * pitch;
                if(rate > 0 && g.position > Math.max(loopStart, loopEnd)) {
                    g.position = Math.min(loopStart, loopEnd);
                } else if(rate < 0 && g.position < Math.min(loopStart, loopEnd)) {
                    g.position = Math.max(loopStart, loopEnd);
                }
                break;
            case LOOP_BACKWARDS:
                g.position -= direction * positionIncrement * pitch;
                if(rate > 0 && g.position < Math.min(loopStart, loopEnd)) {
                    g.position = Math.max(loopStart, loopEnd);
                } else if(rate < 0 && g.position > Math.max(loopStart, loopEnd)) {
                    g.position = Math.min(loopStart, loopEnd);
                }
                break;
            case LOOP_ALTERNATING:
                g.position += direction * (forwards ? positionIncrement * pitch : -positionIncrement * pitch);
                if(((forwards && rate > 0) || (!forwards && rate < 0)) && g.position > Math.max(loopStart, loopEnd)) {
                    g.position = 2 * Math.max(loopStart, loopEnd) - g.position;
                } else if(((!forwards && rate > 0) || (forwards && rate < 0)) && g.position < Math.min(loopStart, loopEnd)) {
                    g.position = 2 * Math.min(loopStart, loopEnd) - g.position;
                }
                break;
        }   
    }
    
    public static void main(String[] args) {
    	AudioContext ac = new AudioContext(512);
    	GranularSamplePlayer sp = new GranularSamplePlayer(ac, SampleManager.sample("audio/1234.aif"));
    	sp.getRateEnvelope().setValue(1f);
    	sp.setLoopType(SamplePlayer.LoopType.LOOP_ALTERNATING);
//    	sp.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
//    	sp.getRateEnvelope().setValue(-1f);
    	sp.getLoopEndEnvelope().setValue(1000f);
    	sp.getLoopStartEnvelope().setValue(500f);
    	ac.getRoot().addInput(sp);
    	ac.start();
    }
  
    
}
