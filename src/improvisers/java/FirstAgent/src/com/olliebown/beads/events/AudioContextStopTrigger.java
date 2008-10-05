package com.olliebown.beads.events;


import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;

public class AudioContextStopTrigger extends Bead {

	AudioContext ac;
	
	public AudioContextStopTrigger(AudioContext ac) {
		this.ac = ac;
	}
	
	public void message(Bead message) {
		stop();
		//System.out.println("AudioContext killed by " + message.toString());
	}
	
	public void stop() {
		ac.kill();
    }
	
}
