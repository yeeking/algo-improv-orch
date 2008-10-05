package com.olliebown.beads.trash;

import java.util.ArrayList;
import java.util.Random;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.HanningWindow;
import com.olliebown.beads.data.StaticBuffer;
import com.olliebown.beads.trash.GrainSpace.Grain;

public class GrainSpacePlayer extends UGen {

	/**
	 * The Class GrainPlayer.
	 */
	private class GrainPlayer {

		/** The grain. */
		Grain grain;
		/** The position. */
		float position;
		float startPosition;
	}

	private GrainSpace grainSpace;
	/** The playing grains. */
	private ArrayList<GrainPlayer> playingGrains;
	/** The free grains. */
	private ArrayList<GrainPlayer> freeGrains;
	/** The dead grains. */
	private ArrayList<GrainPlayer> deadGrains;
	/** The current position. */
	private float[] currentPosition;
	/** The current grain interval in samples. */
	private float grainInterval;
	/** The grain size. */
	private float grainSize;
	/** The window. */
	private StaticBuffer window;
	/** The time since last grain. */
	private int timeSinceLastGrain;
	private float randomness;

	public GrainSpacePlayer(AudioContext context, int outs,
			GrainSpace grainSpace) {
		super(context, 0, outs);
		this.grainSpace = grainSpace;
		setGrainInterval(30.0f);
		setGrainSize(100.0f);
		randomness = 0.1f;
		timeSinceLastGrain = 0;
		playingGrains = new ArrayList<GrainPlayer>();
		freeGrains = new ArrayList<GrainPlayer>();
		deadGrains = new ArrayList<GrainPlayer>();
		window = new HanningWindow();
		currentPosition = null;
	}

	/**
	 * @see com.olliebown.beads.core.UGen#calculateBuffer()
	 */
	@Override
	public void calculateBuffer() {
		zeroOuts();
		for (int i = 0; i < bufferSize; i++) {
			if (timeSinceLastGrain > grainInterval && currentPosition != null) {
				startNewGrain();
			}
			int numGrains = playingGrains.size();
			//System.out.println(numGrains);
			for (int j = 0; j < numGrains; j++) {
				GrainPlayer gp = playingGrains.get(j);
				gp.position += 1.0f;
				float[] frame = gp.grain.getSample().getFrameLinear(
						(int) (gp.position + gp.startPosition), 0.0f);
				float windowScalar = window.getValueFraction(gp.position
						/ grainSize);
				for (int chan = 0; chan < outs; chan++) {
					bufOut[chan][i] += windowScalar * frame[chan % frame.length];
				}
				if (gp.position > grainSize) {
					deadGrains.add(gp);
				}
			}
			for (int j = 0; j < deadGrains.size(); j++) {
				freeGrains.add(deadGrains.get(j));
				playingGrains.remove(deadGrains.get(j));
			}
			deadGrains.clear();
			timeSinceLastGrain++;
		}
	}
	
	Random rng = new Random();

	/**
	 * Start a new grain.
	 */
	private void startNewGrain() {
		Grain grain = grainSpace.getGrain(currentPosition);
		if (grain != null) {
			//System.out.println("gotGrain");
			GrainPlayer newGrain;
			if (freeGrains.size() > 0) {
				newGrain = freeGrains.get(0);
				freeGrains.remove(newGrain);
				playingGrains.add(newGrain);
			} else {
				newGrain = new GrainPlayer();
				playingGrains.add(newGrain);
			}
			newGrain.grain = grain;
			newGrain.startPosition = (float) (grain.getStartPointSamples() + rng.nextGaussian() * randomness * grainSize);
			newGrain.position = 0.0f;
			timeSinceLastGrain = 0;
		} else {
			//System.out.println("nullGrain");
		}
	}

	/**
	 * Gets the grain interval.
	 * 
	 * @return the grain interval
	 */
	public float getGrainInterval() {
		return context.samplesToMs(grainInterval);
	}

	/**
	 * Sets the grain interval.
	 * 
	 * @param grainInterval
	 *            the new grain interval
	 */
	public void setGrainInterval(float grainInterval) {
		this.grainInterval = context.msToSamples(grainInterval);
	}

	/**
	 * Gets the grain size.
	 * 
	 * @return the grain size
	 */
	public float getGrainSize() {
		return context.samplesToMs(grainSize);
	}

	/**
	 * Sets the grain size.
	 * 
	 * @param grainSize
	 *            the new grain size
	 */
	public void setGrainSize(float grainSize) {
		this.grainSize = context.msToSamples(grainSize);
	}

	/**
	 * Gets the current position.
	 * 
	 * @return the current position
	 */
	public float[] getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * Sets the current position.
	 * 
	 * @param currentPosition
	 *            the new current position
	 */
	public void setCurrentPosition(float[] currentPosition) {
		this.currentPosition = currentPosition;
	}

}
