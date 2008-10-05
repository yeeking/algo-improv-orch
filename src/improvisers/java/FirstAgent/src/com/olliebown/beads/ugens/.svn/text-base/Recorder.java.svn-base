
package com.olliebown.beads.ugens;

import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.events.AudioContextStopTrigger;

public class Recorder extends UGen {

    private Sample sample;
    private int position;
    private boolean loopRecord;
    private Bead endListener;
    
    public Recorder(AudioContext player, Sample sample) {
        super(player, sample.nChannels, 0);
        this.sample = sample;
        setLoopRecord(false);
//        setSelfDelete(false);
        pause(true);
    }
    
	public Bead getEndListener() {
		return endListener;
	}

	public void setEndListener(Bead endListener) {
		this.endListener = endListener;
	}

	public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
    
    public void reset() {
        position = 0;
    }
    
    public void stop() {
    	super.stop();
    	if(endListener != null) endListener.message(this);
    }
    
    public void setPosition(float position) {
        position = sample.msToSamples(position);
    }
    
    @Override
    public void calculateBuffer() {
        for(int i = 0; i < bufferSize; i++) {
            for(int j = 0; j < ins; j++) {
                sample.buf[j][position] = bufIn[j][i];
            }
//            System.out.println(bufIn[0][i]);
            position++;
            if(position >= sample.buf[0].length) {
            	if(loopRecord) position = 0;
            	else {
            		stop();
            		break;
            	}
            }
        }
    }

	public boolean isLoopRecord() {
		return loopRecord;
	}

	public void setLoopRecord(boolean loopRecord) {
		this.loopRecord = loopRecord;
	}
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		Tools.init();
		
		Sample s = new Sample(Tools.ac.getAudioFormat(), (int)Tools.ac.msToSamples(5000f));
		Recorder r = new Recorder(Tools.ac, s);
		r.setEndListener(new AudioContextStopTrigger(Tools.ac));
		r.addInput(Tools.in);
		r.start();
		Tools.ac.getRoot().addDependent(r);
		Tools.ac.start();
		while(Tools.ac.isAlive()) {}
		s.write("audio/record_test.aif");
	}
	
}
