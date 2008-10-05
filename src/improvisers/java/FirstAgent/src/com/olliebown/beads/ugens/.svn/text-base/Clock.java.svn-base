
package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.BeadArray;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.events.IntegerBead;

import java.util.ArrayList;

public class Clock extends UGen implements IntegerBead {

    private UGen intervalEnvelope;
    private float point;
    private int count;
    private BeadArray listeners;
    private boolean click;
    
    public Clock(AudioContext context) {
        this(context, 1000.0f);
    }
    
    public Clock(AudioContext context, float interval) {
        this(context, new Static(context, interval));
    }
    
    public Clock(AudioContext context, UGen env) {
        super(context, 0, 1);
        intervalEnvelope = env;
        listeners = new BeadArray();
        resetImmediately();
    }
    
	public boolean isClicking() {
		return click;
	}
	
	public void setClick(boolean click) {
		this.click = click;
	}

	public void addMessageListener(Bead newListener) {
        listeners.add(newListener);
    }
    
    public void removeMessageListener(Bead newListener) {
        listeners.remove(newListener);
    }
    
    public void pause() {
    }
    
    public void resetImmediately() {
        point = 0.0f;
        count = 0;
        tick();
    }
    
    public void resetOnNextClock(int i) {
        count = i - 1;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setIntervalEnvelope(UGen intervalEnvelope) {
        this.intervalEnvelope = intervalEnvelope;
    }
    
    public UGen getIntervalEnvelope() {
    	return intervalEnvelope;    	
    }
    
    @Override
    public void calculateBuffer() {
    	intervalEnvelope.update();
    	for(int i = 0; i < bufferSize; i++) {   
    		float value = Math.max(1.0f, intervalEnvelope.getValue(0, i));
    		point += 1.0f / context.msToSamples(value);
    		if(point >= 1.0f) {
    			tick();
    			count++;
    			while(point >= 1.0f) point -= 1.0f;
    		}
    		bufOut[0][i] = point;
    	}
    }
    
    private void tick() {
    	if(click) context.getRoot().addInput(new Clicker(context));
    	listeners.message(this);
    }

	public int getInt() {
		return getCount();
	}
	
	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512);
		Clock clock = new Clock(ac, new Static(ac, 500f));
		ac.getRoot().addDependent(clock);
		clock.setClick(true);
		clock.addMessageListener(new ClockListener());
		ac.start();
	}
	
	public static class ClockListener extends Bead {
//		new Bead() {
			public void message(Bead message) {
				Clock clock = (Clock)message;
				System.out.println(clock.getCount());
			}
//		}
	}
}





