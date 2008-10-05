package com.olliebown.beads.ugens;

import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;

public class SamplePlayer extends UGen {

    public static enum InterpolationType {
        LINEAR, CUBIC
    };
       
    public static enum LoopType {
        NO_LOOP_FORWARDS, NO_LOOP_BACKWARDS, LOOP_FORWARDS, LOOP_BACKWARDS, LOOP_ALTERNATING
    };
    
    protected Sample buffer = null;                //UNITS
    protected float sampleRate;                    //samples per second
    protected double position;                      //ms
    protected float lastChangedPosition;
    protected UGen positionEnvelope;
    protected UGen rateEnvelope;                   //scalar multiple
    protected float positionIncrement;             //ms
    protected boolean forwards;                    //are we going forwards? (if not we're going backwards)
    protected InterpolationType interpolationType;
    protected UGen loopStartEnvelope;              //ms
    protected UGen loopEndEnvelope;                //ms
    protected LoopType loopType;
    protected float loopCrossFade;                 //ms
    protected boolean startLoop;
    
    protected Bead endListener;

    public SamplePlayer(AudioContext context) {
        super(context);
        rateEnvelope = new Static(context, 1.0f);
        positionEnvelope = new Static(context, 0.0f);
        interpolationType = InterpolationType.LINEAR;
        loopType = LoopType.NO_LOOP_FORWARDS;
        loopStartEnvelope = new Static(context, 0.0f);
        loopEndEnvelope = new Static(context, 0.0f);
        forwards = true;
    }

    public SamplePlayer(AudioContext context, Sample buffer) {
        this(context);
        setBuffer(buffer);
    }

    public void setBuffer(Sample buffer) {
        this.buffer = buffer;
        sampleRate = buffer.audioFormat.getSampleRate();
        setOuts(buffer.nChannels);
        updatePosInc();
    }
    
    public Sample getBuffer() {
    	return buffer;
    }

    public void setToEnd() {
        position = buffer.nFrames;
    }

    public void stop() {
        super.stop();
        if(endListener != null) endListener.message(this);
    }
    
    public void start(float msPosition) {
        position = msPosition;
        start();
    }
    
    public void reset() {
        position = 0f;
    }
    
    public float getPosition() {
    	return context.samplesToMs((float)position);
    }
    
	public UGen getPositionEnvelope() {
		return positionEnvelope;
	}

	public void setPositionEnvelope(UGen positionEnvelope) {
		this.positionEnvelope = positionEnvelope;
	}

	public UGen getRateEnvelope() {
        return rateEnvelope;
    }

    public void setRateEnvelope(UGen rateEnvelope) {
        this.rateEnvelope = rateEnvelope;
    }

    private void updatePosInc() {
        positionIncrement = context.samplesToMs(sampleRate / context.getSampleRate());
    }

    public InterpolationType getInterpolationType() {
        return interpolationType;
    }

    public void setInterpolationType(InterpolationType interpolationType) {
        this.interpolationType = interpolationType;
    }

    public float getLoopCrossFade() {
        return buffer.samplesToMs(loopCrossFade);
    }

    public void setLoopCrossFade(float loopCrossFade) {
        this.loopCrossFade = buffer.msToSamples(loopCrossFade);
    }

    public UGen getLoopEndEnvelope() {
        //return buffer.samplesToMs(loopEnd);
    	return loopEndEnvelope;
    }

    public void setLoopEndEnvelope(UGen loopEndEnvelope) {
        //this.loopEnd = buffer.msToSamples(loopEnd);
    	this.loopEndEnvelope = loopEndEnvelope;
    }

    public UGen getLoopStartEnvelope() {
        //return buffer.samplesToMs(loopStart);
    	return loopStartEnvelope;
    }

    public void setLoopStartEnvelope(UGen loopStartEnvelope) {
        //this.loopStart = buffer.msToSamples(loopStart);
    	this.loopStartEnvelope = loopStartEnvelope;
    }
    
    public void setLoopPointsFraction(float start, float end) {
        loopStartEnvelope = new Static(context, start * (float)buffer.length);
        loopEndEnvelope = new Static(context, end * (float)buffer.length);
    }

    public LoopType getLoopType() {
        return loopType;
    }

    public void setLoopType(LoopType loopType) {
        this.loopType = loopType;
        if(loopType != LoopType.LOOP_ALTERNATING) {
            if(loopType == LoopType.LOOP_FORWARDS || loopType == LoopType.NO_LOOP_FORWARDS) {
                forwards = true;
            } else {
                forwards = false;
            }
        }
    }

    public float getSampleRate() {
        return sampleRate;
    }
    
    public void setEndListener(Bead endListener) {
    	this.endListener = endListener;
    }
    
    public Bead getEndListener() {
    	return endListener;
    }

    @Override
    public void calculateBuffer() {
//    	System.out.println("samplePos " + position + " rate " + positionIncrement * rateEnvelope.getValue(0, 0) + " buf " + bufferSize);
    	rateEnvelope.update();
    	positionEnvelope.update();
    	loopStartEnvelope.update();
    	loopEndEnvelope.update();
        for (int i = 0; i < bufferSize; i++) {
            //calculate the samples
        	float posInSamples = context.msToSamples((float)position);
            int currentSample = (int) posInSamples;
            float fractionOffset = posInSamples - currentSample;
            float[] frame = null;
            switch (interpolationType) {
                case LINEAR:
                    frame = buffer.getFrameLinear(currentSample, fractionOffset);
                    break;
                case CUBIC:
                    frame = buffer.getFrameCubic(currentSample, fractionOffset);
                    break;
            }
            for (int j = 0; j < outs; j++) {
                bufOut[j][i] = frame[j];
            }
            //update the position, loop state, direction
            calculateNextPosition(i);
            if(muted) {
                //make sure to zero the remaining outs
                while(i < bufferSize) {
                    for (int j = 0; j < outs; j++) {
                        bufOut[j][i] = 0.0f;
                    }
                    i++;
                }
                break;
            }
        }
    }
    
    protected float rate;
    protected float loopStart;
    protected float loopEnd;
    
    public void calculateNextPosition(int i) {
    	rate = rateEnvelope.getValue(0, i);
    	loopStart = loopStartEnvelope.getValue(0, i);
    	loopEnd = loopEndEnvelope.getValue(0, i);
    	if(lastChangedPosition != positionEnvelope.getValue(0, i)) {
    		position = positionEnvelope.getValue(0, i);
    		lastChangedPosition = (float)position;
    	}
        switch(loopType) {
            case NO_LOOP_FORWARDS:
                position += positionIncrement * rate;
                if(position > buffer.length || position < 0) stop();
                break;
            case NO_LOOP_BACKWARDS:
                position -= positionIncrement * rate;
                if(position > buffer.length || position < 0) stop();
                break;
            case LOOP_FORWARDS:
                position += positionIncrement * rate;
                if(rate > 0 && position > Math.max(loopStart, loopEnd)) {
                    position = Math.min(loopStart, loopEnd);
                } else if(rate < 0 && position < Math.min(loopStart, loopEnd)) {
                    position = Math.max(loopStart, loopEnd);
                }
                break;
            case LOOP_BACKWARDS:
                position -= positionIncrement * rate;
                if(rate > 0 && position < Math.min(loopStart, loopEnd)) {
                    position = Math.max(loopStart, loopEnd);
                } else if(rate < 0 && position > Math.max(loopStart, loopEnd)) {
                    position = Math.min(loopStart, loopEnd);
                }
                break;
            case LOOP_ALTERNATING:
                position += forwards ? positionIncrement * rate : -positionIncrement * rate;
                if((forwards || rate < 0) && position > Math.max(loopStart, loopEnd)) {
                    forwards = (rate < 0);
                    position = 2 * Math.max(loopStart, loopEnd) - position;
                } else if((!forwards || rate < 0) && position < Math.min(loopStart, loopEnd)) {
                    forwards = (rate > 0);
                    position = 2 * Math.min(loopStart, loopEnd) - position;
                }
                break;
        }
    }
    
    public static void main(String[] args) throws Exception {
    	AudioContext ac = new AudioContext(512);
//    	Sample s1 = new Sample("/Users/ollie/Music/Audio/output5.aif");	
    	Sample s1 = new Sample("/Users/ollie/Music/iTunes/iTunes Music/Unknown Artist/Unknown Album/Jakob_Ollie_Copenhagen.wav");	
//    	Sample s1 = SampleManager.sample("audio/1234.aif");	
    	System.out.println(s1.length);
    	SamplePlayer sp = new SamplePlayer(ac, s1); 	
//    	sp.getRateEnvelope().setValue(1f);
//    	sp.setLoopType(SamplePlayer.LoopType.LOOP_ALTERNATING); 	
////    	sp.getRateEnvelope().setValue(-1f);
//    	sp.getLoopEndEnvelope().setValue(1000f);
//    	sp.getLoopStartEnvelope().setValue(500f);  	
    	ac.getRoot().addInput(sp);
    	ac.start();
//    	ac.runNonRealTime();
    }
    
}
