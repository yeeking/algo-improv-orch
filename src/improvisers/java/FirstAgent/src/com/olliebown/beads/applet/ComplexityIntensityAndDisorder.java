package com.olliebown.beads.applet;

import java.applet.Applet;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.events.IntegerBead;
import com.olliebown.beads.ugens.Clock;
import com.olliebown.beads.ugens.Gain;

/**
 * Complexity is the length of the pattern that is being used. Intensity is the
 * number of voices. Disorder is the amount of mashing up of the pattern.
 * 
 * @author ollie
 * 
 */
public class ComplexityIntensityAndDisorder extends Applet {

	public void init() {
		AudioContext ac = new AudioContext(512, 1500);
		PopUpFrame frame = new PopUpFrame(ac);
		BeadPanel panel = new BeadPanel("cid..", 0);
		Cidder cidder = new Cidder(ac);
		panel.addSlider("c", 100, cidder.c);
		panel.addSlider("i", 100, cidder.i);
		panel.addSlider("d", 100, cidder.d);
		
		frame.setContentPane(panel);
		frame.launch();
		ac.getRoot().addInput(cidder);
		ac.start();
	}

	public void draw() {

	}
	
	private static class Cidder extends Gain {
		
		Envelope c;
		Envelope i;
		Envelope d;
		
		
		public Cidder(AudioContext context) {
			super(context, 1);
			c = new Envelope(context);
			i = new Envelope(context);
			d = new Envelope(context);
		}

		@Override
		public void calculateBuffer() {
			
		}
		
		private static class CidTrack extends Gain {

			Envelope c;
			Envelope d;
			
			static int[][] pattern;
			
			static {
				pattern = new int[100][100];
				for(int i = 0; i < 100; i++) {
					for(int j = 0; j < 100; j++) {
						pattern[i][j] = (int)(Math.random() * 100.0f);
					}
				}
			}
			
			public CidTrack(AudioContext context, Envelope c, Envelope d, Clock clock) {
				super(context, 1);
				this.c = c;
				this.d = d;
				clock.addMessageListener(this);
			}
			
			public void message(Bead bead) {
				int time = ((IntegerBead)bead).getInt();
			}
			
		}
		
	}

}
