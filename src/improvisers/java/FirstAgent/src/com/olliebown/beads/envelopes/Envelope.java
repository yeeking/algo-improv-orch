 
package com.olliebown.beads.envelopes;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;

import java.util.ArrayList;

public class Envelope extends UGen {

    private ArrayList<Segment> segments;
    private float currentStartValue;
    private float currentValue;
    private int currentTime;
    private Segment currentSegment;
    private boolean change;
    
    public class Segment {
        
        float endValue;     //no units
        int duration;       //samples
        float curvature;    //no units, defines a power
        Bead trigger;
        
        public Segment(float endValue, float duration, float curvature, Bead trigger) {
            this.endValue = endValue;
            this.duration = (int)context.msToSamples(duration);
            this.curvature = Math.abs(curvature);
            this.trigger = trigger;
        }
        
    }
    
    public Envelope(AudioContext context) {
        super(context, 0, 1);
        segments = new ArrayList<Segment>();
        currentStartValue = 0;
        currentValue = 0;
        currentSegment = null;
        change = false;
    }
    
    public Envelope(AudioContext context, float value) {
    	this(context);
    	setValue(value);
    }
    
    public synchronized void addSegment(float endValue, float duration, float curvature, Bead trigger) {
        segments.add(new Segment(endValue, duration, curvature, trigger));
        change = true;
        //if(currentSegment == null) getNextSegment();
    }
    
    public void addSegment(float endValue, float duration) {
        addSegment(endValue, duration, 1.0f, null);        
    }
    
    public void addSegment(float endValue, float duration, Bead trigger) {
        addSegment(endValue, duration, 1.0f, trigger);        
    }
    
    public void setValue(float value) {
        clear();
        addSegment(value, 0.0f);
    }
    
    public void clear() {
        segments = new ArrayList<Segment>();
        currentSegment = null;
    }
    
    private synchronized void getNextSegment() {
        if(currentSegment != null) {
            currentStartValue = currentSegment.endValue;
            currentValue = currentStartValue;
            segments.remove(currentSegment);
            if(currentSegment.trigger != null) {
            	currentSegment.trigger.message(this);
            	//System.out.println("envelope:stopTrigger");
            }
        } else {
        	currentStartValue = currentValue;
        }
        if(segments.size() > 0) {
            currentSegment = segments.get(0);
        } else {
            currentSegment = null;
            change = false;
        }
        currentTime = 0;
    }
    
    public float getCurrentValue() {
        return currentValue;
    }

	@Override
	public synchronized void calculateBuffer() {
//		if(change) {
			for(int i = 0; i < bufferSize; ++i) {
				try {
					if(currentSegment == null) {
			            //currentValue = currentStartValue;
						getNextSegment();
			        } else if(currentSegment.duration == 0) {
			            getNextSegment();
			        } else {
			        	float ratio;
			        	if(currentSegment.curvature != 1.0f) ratio = (float)Math.pow((float)currentTime / (float)currentSegment.duration, currentSegment.curvature);
			        	else ratio = (float)currentTime / (float)currentSegment.duration;
			            currentValue = (1f - ratio) * currentStartValue + ratio * currentSegment.endValue;
			            currentTime++;
			            if(currentTime > currentSegment.duration) getNextSegment();
			        }
					bufOut[0][i] = currentValue;
				} catch(Exception e) {
					System.out.println("error when i=" + i);
				}
			}
//		}
		
	}
    
}
