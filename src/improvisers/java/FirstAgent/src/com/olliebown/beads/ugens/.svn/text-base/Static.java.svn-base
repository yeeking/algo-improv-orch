package com.olliebown.beads.ugens;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.core.UGen;


public class Static extends UGen {

	public float x;
	
	public Static(AudioContext ac, float x) {
		super(ac);
		this.x = x;
		pause(true); //might as well be muted
	}

	@Override
	public void calculateBuffer() {
		// Do nothing
	}
	
	public void setValue(float value) {
		x = value;
	}
	
	public float getValue(int a, int b) {
		return x;	//whatever happens return x
	}
	
	public float getValue() {
		return x;
	}
	
	
}
