package com.olliebown.beads.trash;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import com.olliebown.beads.analysis.MFCC;
import com.olliebown.beads.analysis.PowerSpectrum;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.events.AudioContextStopTrigger;
import com.olliebown.beads.trash.GrainSpace.Grain;
import com.olliebown.beads.trash.GrainSpace.GrainSpacePanel;
import com.olliebown.beads.ugens.MouseResponder;
import com.olliebown.beads.ugens.SamplePlayer;


public class Doozer {

	Random rng;
	private GrainSpace gs;
	private float[] position;
	private Grain grain;
	
	public Doozer(GrainSpace gs) {
		this.gs = gs;
		rng = new Random();
		position = new float[gs.dimensions];
		randomInit();
		grain = null;
	}
	
	public void randomInit() {
		for(int i = 0; i < position.length; i++) {
			position[i] = rng.nextFloat() * gs.width;
		}
	}
	
	public void update() {
		if(grain == null) {
			pickUpGrain();
		} else if(theresASimilarGrainAround()) {
			swapGrains();
		} 
		moveABit();
	}

	private void moveABit() {
		for(int i = 0; i < position.length; i++) {
			position[i] += rng.nextGaussian() * 1f;
			while(position[i] > gs.width) position[i] -= gs.width;
			while(position[i] < 0f) position[i] += gs.width;
		}
		
	}

	private void swapGrains() {
		grain.setLocation(position.clone());
		gs.addGrain(grain);
		pickUpGrain();
	}

	private boolean theresASimilarGrainAround() {
		boolean yesThereAre = false;
		for(int i = 0; i < 10; i++) {
			Grain candidateGrain = gs.getRandomGrainNearPoint(position);
			if(grain.isSimilarTo(candidateGrain)) {
				yesThereAre = true;
				break;
			}
		}
		
		return yesThereAre;
	}

	private void pickUpGrain() {
		grain = gs.getRandomGrainNearPoint(position);
		gs.removeGrain(grain);
	}
	
	
	public static void main(String[] args) {
		
		//set up the grain space
		GrainSpace gs = new GrainSpace(2, 500.0f);
		Random rng = new Random();
		Sample s = SampleManager.sample("audio/1234.aif");

		
		JFrame frame = new JFrame();
		final GrainSpacePanel panel = new GrainSpacePanel(gs);
		frame.setContentPane(panel);
		Graphics g = frame.getContentPane().getGraphics();
		frame.setSize(new Dimension(500,500));
		
		//we want to calculate features offline and add them here, hmmm.
		
		//prepare the audio data and features
		AudioContext temp = new AudioContext(512,1000);
		temp.setRealTime(false);
		SamplePlayer sp = new SamplePlayer(temp, s);
		PowerSpectrum ps = new PowerSpectrum(temp);
		MFCC mfcc = new MFCC(temp, 20);
		mfcc.accumulate(true);
		ps.addListener(mfcc);
		ps.addInput(sp);
		temp.getRoot().addDependent(ps);
		sp.setEndListener(new AudioContextStopTrigger(temp));
		temp.start();

		//put audio data into space
		for (int i = 0; i < 10000; i++) {
			float[] location = new float[] { rng.nextFloat() * gs.width, rng.nextFloat() * gs.width };
			float timeMS = i * 1;
			float[] featureData = mfcc.getAccumulatedFeatures().get((int)(temp.msToSamples(timeMS) / 512));
			gs.addGrain(s, timeMS, 100.0f, location, featureData);
		}
		frame.setVisible(true);
		
		ArrayList<Doozer> doozers = new ArrayList<Doozer>();
		//now for the Doozers
		for(int i = 0; i < 100; i++) {
			doozers.add(new Doozer(gs));
		}
		
		//now run the world
		for(int i = 0; i < 200; i++) {
			for(Doozer d : doozers) d.update();
			System.out.println(i);
		}
		frame.repaint();
		
		

		AudioContext ac = new AudioContext(512, 1500);
		
		
		final GrainSpacePlayer gsp = new GrainSpacePlayer(ac, 2, gs);
		ac.getRoot().addInput(gsp);
		
		MouseResponder mr = new MouseResponder(ac);
		ac.getRoot().addDependent(mr);
		mr.setListener(new Bead() {
			public void message(Bead message) {
				float[] newPos = new float[] { ((MouseResponder)message).getPoint().x - panel.getX(), 
											   ((MouseResponder)message).getPoint().y - panel.getY()};
				gsp.setCurrentPosition(newPos);
				panel.repaint();
				//frame.repaint();
			}
		});
		
		ac.start();
	}
	
	
}













