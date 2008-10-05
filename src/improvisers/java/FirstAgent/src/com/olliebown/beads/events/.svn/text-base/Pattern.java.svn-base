
package com.olliebown.beads.events;

import com.olliebown.beads.core.Bead;
import java.util.Hashtable;

public class Pattern extends Bead implements IntegerBead {
	
    private Hashtable<Integer, Integer> events;
    private Bead listener;
    int mult;
    int mod;
    int currentValue;
    
    public Pattern() {
        events = new Hashtable<Integer, Integer>();
        setMod(Integer.MAX_VALUE);
        setMult(1);
    }
    
    public void setListener(Bead listener) {
    	this.listener = listener;
    }
    
    public void addEvent(int key, int value) {
        events.put(key, value);
    }
    
    public void removeEvent(int key) {
    	for(int index : events.keySet()) {
            if(index == key) {
                events.remove(index);
                break;
            }
        }
    }

    public void message(Bead message) {
        int time = ((IntegerBead)message).getInt();
        if(mod != Integer.MAX_VALUE) time = time % (mod * mult);            
        for(int index : events.keySet()) {
            if(index * mult == time) {
            	currentValue = events.get(index);
            	if(listener != null) {
	            	if(listener.isDeleted()) {
	            		stop();
	            	} else {
	            		listener.message(this);
	            	}
            	}
                break;
            }
        }
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

	public int getMult() {
		return mult;
	}

	public void setMult(int mult) {
		this.mult = mult;
	}

	public int getInt() {
		return currentValue;
	}
    
}
