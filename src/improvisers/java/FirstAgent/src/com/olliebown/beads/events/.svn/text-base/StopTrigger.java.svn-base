package com.olliebown.beads.events;

import com.olliebown.beads.core.Bead;

public class StopTrigger extends Bead {

	Bead receiver;
	
	public StopTrigger(Bead receiver) {
		this.receiver = receiver;
	}
	
	public void message(Bead message) {
		if(receiver != null) receiver.stop();
	}
}
