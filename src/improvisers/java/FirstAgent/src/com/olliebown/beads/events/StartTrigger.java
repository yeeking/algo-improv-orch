package com.olliebown.beads.events;

import com.olliebown.beads.core.Bead;

public class StartTrigger extends Bead {

	Bead receiver;
	
	public StartTrigger(Bead receiver) {
		this.receiver = receiver;
	}
	
	public void message(Bead message) {
		receiver.start();
		
	}
}