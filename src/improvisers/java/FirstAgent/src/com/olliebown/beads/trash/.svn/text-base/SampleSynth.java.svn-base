package com.olliebown.beads.trash;

import java.util.ArrayList;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.events.IntegerBead;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.GranularSamplePlayer;

public class SampleSynth extends Gain {
	
	ArrayList<Sample> group;
	
	public SampleSynth(AudioContext context, int inouts) {
		super(context, inouts);
	}
	
	public void setGroup(ArrayList<Sample> group) {
		this.group = group;
	}
	
	public void play(Sample sample) {
		GranularSamplePlayer sp = new GranularSamplePlayer(context, sample);
		addInput(sp);
	}
	
	public void play(String file) {
		play(SampleManager.sample(file));
	}
	
	public void play(int i) {
		play(group.get(i));
	}
	
	public void message(Bead message) {
		play(((IntegerBead)message).getInt());
		
		System.out.println(((IntegerBead)message).getInt());
	}

}
