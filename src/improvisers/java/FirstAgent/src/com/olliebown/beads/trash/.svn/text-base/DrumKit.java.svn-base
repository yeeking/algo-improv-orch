package com.olliebown.beads.trash;

import java.util.ArrayList;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.events.IntegerBead;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.GranularSamplePlayer;
import com.olliebown.beads.ugens.SamplePlayer;
import com.olliebown.beads.data.Sample;

public class DrumKit extends Gain {

	ArrayList<UGen> group;
	
	public DrumKit(AudioContext context, int inouts, ArrayList<UGen> group) {
		super(context, inouts);
		this.group = group;
	}
	
	public void play(int i) {
		UGen ugen = group.get(i);
		ugen.start();
		addInput(ugen);
	}
	
	public void message(Bead message) {
		play(((IntegerBead)message).getInt());
	}
	
}
