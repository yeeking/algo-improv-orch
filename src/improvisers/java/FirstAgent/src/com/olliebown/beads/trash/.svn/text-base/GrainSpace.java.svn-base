package com.olliebown.beads.trash;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.olliebown.beads.analysis.MFCC;
import com.olliebown.beads.analysis.PowerSpectrum;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.data.SampleManager;
import com.olliebown.beads.events.AudioContextStopTrigger;
import com.olliebown.beads.ugens.MouseResponder;
import com.olliebown.beads.ugens.SamplePlayer;

/**
 * A GrainSpace is a continuous multi-dimensional space containing a set of
 * grains that occupy positions in the space. The GrainSpace can be played by
 * moving around in the space, for which it has a set of typical granular
 * synthesis parameters.
 * 
 * @author ollie
 * 
 */
public class GrainSpace {

	private static class Position {

		final private float[] pos;

		public Position(float[] pos) {
			this.pos = pos;
		}

		public float[] getValue() {
			return pos;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(pos);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			if (!Arrays.equals(pos, other.pos))
				return false;
			return true;
		}

		
		
	}

	/**
	 * The Class Grain.
	 */
	public class Grain {

		/** The sample. */
		private Sample sample;
		/** The start point. */
		private float startPoint;
		/** The radius. */
		private float radius;
		/** The location. */
		private float[] location;

		private float[] features;
		
		public Sample getSample() {
			return sample;
		}

		public float getStartPoint() {
			return startPoint;
		}
		

		public float getStartPointSamples() {
			return sample.msToSamples(startPoint);
		}

		public float getRadius() {
			return radius;
		}

		public float[] getLocation() {
			return location;
		}
		
		public void setLocation(float[] location) {
			this.location = location;
		}
		
		public String toString() {
			return "Grain:" + sample.toString() + "(" + startPoint + "ms)";
		}
		
		public void setFeatures(float[] features) {
			this.features = features;
		}

		public boolean isSimilarTo(Grain candidateGrain) {
			float distance = 0.0f;
			for(int i = 0; i < features.length; i++) {
				distance += (features[i] - candidateGrain.features[i]) * (features[i] - candidateGrain.features[i]);
			}
			distance = (float)Math.sqrt(distance);
			//System.out.println(distance);
			boolean yes = (distance < 0.0001f);
			if(yes) System.out.println("yes");
			return yes;
		}
		
	}

	/** The dimensions. */
	public final int dimensions;
	public final float width;
	/** The zone res. */
	private float zoneRes;
	/** The grains. */
	private ArrayList<Grain> grains;
	/** The zones. */
	private Hashtable<Position, ArrayList<Grain>> zones;
	private Grain currentGrain;

	/**
	 * Instantiates a new grain surface.
	 * 
	 * @param context
	 *            the AudioContext
	 * @param outs
	 *            the number of outputs
	 * @param dimensions
	 *            the number of dimensions
	 */
	public GrainSpace(int dimensions, float width) {
		this.dimensions = dimensions;
		this.width = width;
		setZoneRes(50.0f);
		grains = new ArrayList<Grain>();
		zones = new Hashtable<Position, ArrayList<Grain>>();
	}

	/**
	 * Adds a grain at the given position.
	 * 
	 * @param sample
	 *            the sample
	 * @param startPoint
	 *            the start point
	 * @param radius
	 *            the radius
	 * @param location
	 *            the location
	 */
	public synchronized void addGrain(Sample sample, float startPoint, float radius,
			float[] location, float[] features) {
		Grain newGrain = new Grain();
		newGrain.sample = sample;
		newGrain.startPoint = startPoint;
		newGrain.radius = radius;
		newGrain.location = location.clone();
		newGrain.features = features;
		addGrain(newGrain);
	}
	
	public synchronized void addGrain(Grain newGrain) {
		grains.add(newGrain);
		addGrainToZoneMap(newGrain);
	}

	/**
	 * Adds the grain to zone map.
	 * 
	 * @param newGrain
	 *            the new grain
	 */
	private void addGrainToZoneMap(Grain newGrain) {
		Position zone = new Position(getZone(newGrain.location));
		//System.out.println(zone[0] + " " + zone[1]);
		
			
		if (zones.containsKey(zone)) {
			zones.get(zone).add(newGrain);
		} else {
			ArrayList<Grain> zoneList = new ArrayList<Grain>();
			zoneList.add(newGrain);
			zones.put(zone, zoneList);
		}
	}

	/**
	 * Gets the zone.
	 * 
	 * @param location
	 *            the location
	 * 
	 * @return the zone
	 */
	private float[] getZone(float[] location) {
		float[] zone = new float[dimensions];
		for (int i = 0; i < dimensions; i++) {
			zone[i] = (int) (location[i] / zoneRes) * zoneRes;
			 //System.out.print(zone[i] + " ");
		}
		 //System.out.println();
		return zone;
	}

	/**
	 * Gets the grain at the given location.
	 * 
	 * @param location
	 *            the location
	 * 
	 * @return the grain
	 */
	public Grain getGrain(float[] location) {
		float[] loc = getZone(location);
		// System.out.println(location[0] + " " + location[1] + " -- " + loc[0]
		// + " " + loc[1]);
		ArrayList<Grain> zoneList = zones.get(new Position(loc));
		if (zoneList != null) {
			Grain grain = getNearestGrain(location, zoneList);
			currentGrain = grain;
			return grain;
		} else {
			//System.out.println("x");
			//Grain grain = getNearestGrain(location, grains);
			//currentGrain = grain;
			return null;
		}
	}
	

	public Grain getRandomGrainNearPoint(float[] location) {
		float[] loc = getZone(location);
		// System.out.println(location[0] + " " + location[1] + " -- " + loc[0]
		// + " " + loc[1]);
		ArrayList<Grain> zoneList = zones.get(new Position(loc));
		if (zoneList != null) {
			Grain grain = zoneList.get((int)(Math.random() * zoneList.size()));
			return grain;
		} else {
			//System.out.println("x");
			//Grain grain = getNearestGrain(location, grains);
			//currentGrain = grain;
			return null;
		}
	}
	
	

	/**
	 * Gets the nearest grain.
	 * 
	 * @param location
	 *            the location
	 * @param zoneList
	 *            the zone list
	 * 
	 * @return the nearest grain
	 */
	private static Grain getNearestGrain(float[] location,
			ArrayList<Grain> zoneList) {
		Grain winner = null;
		float bestDistance = Float.MAX_VALUE;
		int listSize = zoneList.size();
		for (int i = 0; i < listSize; i++) {
			Grain grain = zoneList.get(i);
			float distance = euclidianDistance(location, grain.location);
			if (distance < grain.radius) {
				if (winner == null) {
					winner = grain;
				} else {
					if (distance < bestDistance) {
						winner = grain;
						bestDistance = distance;
					}
				}
			}
		}
		//System.out.println(winner);
		return winner;
	}

	/**
	 * Euclidian distance.
	 * 
	 * @param x
	 *            one array
	 * @param y
	 *            another array
	 * 
	 * @return the float
	 */
	private static float euclidianDistance(float[] x, float[] y) {
		float distance = 0.0f;
		for (int i = 0; i < x.length; i++) {
			distance += (x[i] - y[i]) * (x[i] - y[i]);
		}
		return (float) Math.sqrt(distance);
	}

	/**
	 * Gets the zone resolution.
	 * 
	 * @return the zone resolution
	 */
	public float getZoneRes() {
		return zoneRes;
	}

	/**
	 * Sets the zone resolution.
	 * 
	 * @param zoneRes
	 *            the new zone res
	 */
	public void setZoneRes(float zoneRes) {
		this.zoneRes = zoneRes;
	}

	/**
	 * Gets the grain list.
	 * 
	 * @return the grains
	 */
	public ArrayList<Grain> getGrains() {
		return grains;
	}
	
	public void removeGrain(Grain grain) {
		grains.remove(grain);
		for(ArrayList<Grain> zoneList : zones.values()) {	// TODO efficiency
			zoneList.remove(grain);
		}
	}
	
	public static class GrainSpacePanel extends JPanel {
		
		GrainSpace gs;
		
		public GrainSpacePanel(GrainSpace gs) {
			this.gs = gs;
		}
		
		public synchronized void paint(Graphics g) {
			
//			Graphics2D g2 = (Graphics2D) g.create();
//			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//			RenderingHints.VALUE_ANTIALIAS_ON);
			
			g.setColor(Color.white);
			g.fillRect(0, 0, 100000, 100000);
			for(Grain gr : gs.grains) {
				if(gr == gs.currentGrain) g.setColor(Color.red);
				else g.setColor(Color.blue);
				g.fillOval((int)gr.location[0], (int)gr.location[1], 15, 15);
			}
		}
		
	}

	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 1500);
		GrainSpace gs = new GrainSpace(2, 1000f);
		Random rng = new Random();
		Sample s = SampleManager.sample("audio/1234.aif");

		
		float[] location = new float[] { 250f, 250f };
		JFrame frame = new JFrame();
		final GrainSpacePanel panel = new GrainSpacePanel(gs);
		frame.setContentPane(panel);
		Graphics g = frame.getContentPane().getGraphics();
		frame.setSize(new Dimension(500,500));
		
		//we want to calculate features offline and add them here, hmmm.
		
		
		AudioContext temp = new AudioContext(512,1000);
		temp.setRealTime(false);
		SamplePlayer sp = new SamplePlayer(temp, SampleManager.sample("audio/1234.aif"));
		PowerSpectrum ps = new PowerSpectrum(temp);
		MFCC mfcc = new MFCC(temp, 20);
		mfcc.accumulate(true);
		ps.addListener(mfcc);
		ps.addInput(sp);
		temp.getRoot().addDependent(ps);
		sp.setEndListener(new AudioContextStopTrigger(temp));
		temp.start();
	
//		for(float[] results : mfcc.getAccumulatedFeatures()) {
//			System.out.println(results[0] + " " + results[1]);
//		}
		
		for (int i = 0; i < 10000; i++) {
			float timeMS = i * 1;
			float[] featureData = mfcc.getAccumulatedFeatures().get((int)(temp.msToSamples(timeMS) / 512));
			gs.addGrain(s, timeMS, 100.0f, location, featureData);
			location[0] += rng.nextGaussian() * 5f;
			location[1] += rng.nextGaussian() * 5f;
		}
		final GrainSpacePlayer gsp = new GrainSpacePlayer(ac, 2, gs);
		ac.getRoot().addInput(gsp);
		frame.setVisible(true);
		
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
