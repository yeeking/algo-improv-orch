package com.olliebown.beads.ugens;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;


public class DelayLine extends UGen {

	//INCOMPLETE
	
	private ArrayList<Float>[] buffs;
	private int index = 0;
	private int max = 100;
	
	public DelayLine(AudioContext context, int inouts, float msLength) {
		super(context, inouts, inouts);
		buffs = new ArrayList[inouts];
		for(int i = 0; i < inouts; i++) {
			buffs[i] = new ArrayList<Float>();
		}
	}

	@Override
	public void calculateBuffer() {
		for(int i = 0; i < bufferSize; i++) {
			for(int j = 0; j < ins; j++) {
				buffs[j].set(index, bufIn[j][i]);
			}
			index = (index + 1) % max;
		}
	}

}
