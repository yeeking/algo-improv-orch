
package com.olliebown.beads.analysis;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.FrameFeatureExtractor;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.ugens.Clicker;
import com.olliebown.beads.ugens.RTInput;
import com.olliebown.beads.ugens.SamplePlayer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.TreeSet;

public class OnsetDetector extends UGen {

    static private int BLOCK = 10;
    
    private int beatStrength;
    private float[] thresholds;
    private int count;
    private Bead listener;
    private int hop;
    private int cutout;
    private int cutoutCount;
    private boolean click;
    
    private int localCount;
    
    private boolean accumulate;
    private TreeMap<Float, Integer> timePoints;
    
    public OnsetDetector(AudioContext context, Bead listener) {
    	this(context);
    	setListener(listener);
    }
    
    public OnsetDetector(AudioContext context) {
        super(context, 1, 0);
        setThresholds(new float[] {0.1f, 0.2f, 0.4f});
        count = 0;
        cutout = (int) context.msToSamples(20f);
        cutoutCount = -1;
        setHop(5f);
        setClick(false);
        accumulate = false;
        timePoints = new TreeMap<Float, Integer>();
    }
    
    
    
    
	public float getCutout() {
		return context.samplesToMs(cutout);
	}

	
	public void setCutout(float cutout) {
		this.cutout = (int)context.msToSamples(cutout);
	}

	public void accumulate(boolean accumulate) {
    	this.accumulate = accumulate;
    }

    
	public boolean isClicking() {
		return click;
	}

	
	public void setClick(boolean click) {
		this.click = click;
	}

	public float getHop() {
        return context.samplesToMs(hop);
    }
    
    public void setHop(float msHop) {
        hop = (int)context.msToSamples(msHop);
        hop = hop - (hop  % BLOCK);
    }
    
	public float[] getThresholds() {
		return thresholds;
	}

	public void setThresholds(float[] thresholds) {
		this.thresholds = thresholds;
	}

	@Override
    public void calculateBuffer() {
    	if(count > cutoutCount) {
	        localCount = 0;
	        while(localCount < bufferSize - BLOCK) {
	            //grab average of next 4 samples
	            float average = 0.0f;
	            for(int i = 0; i < BLOCK; i++) {
	                average += bufIn[0][localCount++];
	            }
	            //System.out.println(localCount + " " + average);
	            average /= (float)BLOCK;
	            //System.out.println(average);
	            beatStrength = 0;
	            for(int i = 0; i < thresholds.length; i++) {
		            if(average > thresholds[i]) {
		            	beatStrength = i + 1;
		            }
	            }
	            if(beatStrength > 0) {
	                onset();
	                cutoutCount = count + localCount + cutout;
	                break;
	            } 
	            localCount += hop;
	        }   
    	}
        count += bufferSize;
    }
    	
    public int getBeatStrength() {
    	return beatStrength;
    }
	
    int btcnt = 0;
    
    private void onset() {
    	if(click) context.getRoot().addInput(new Clicker(context));
    	if(listener != null) listener.message(this);
    	if(accumulate) timePoints.put(timeInMS(), beatStrength);
    }

    private float timeInMS() {
    	//time in samples is count + localCount
    	return context.samplesToMs(count + localCount);
    }
	
	public void setListener(Bead listener) {
		this.listener = listener;
	}

	public TreeMap<Float, Integer> getTimePoints() {
		return timePoints;
	}
	
	public ArrayList<Float> getDistancesToOnsets(int sampleHop) {
		//sample hop is sample hop at current sample rate
		int index = 0;
		float currentTimeMS = 0;
		ArrayList<Float> distances = new ArrayList<Float>();
		for(float f : timePoints.keySet()) {
			float nextInterval = f - currentTimeMS;
			while(currentTimeMS < f) {
				float distance = (f - currentTimeMS) / nextInterval;
				distances.add(distance);
				index += sampleHop;
				currentTimeMS = context.samplesToMs(index);
			}
		}
		return distances;
	}
	
	public static void main(String[] args) {
		System.out.println("OnsetDetectorTest");
		AudioContext ac = new AudioContext(512, 1500);
		ac.start();
		//SamplePlayer sp = new SamplePlayer(ac, SampleManager.sample("audio/1234.aif"));
		RTInput input = new RTInput(ac, ac.getAudioFormat());
		OnsetDetector od = new OnsetDetector(ac);
		od.setClick(true);
		od.addInput(input);
		ac.getRoot().addDependent(od);
		//ac.getRoot().addInput(sp);
	}
    
}
