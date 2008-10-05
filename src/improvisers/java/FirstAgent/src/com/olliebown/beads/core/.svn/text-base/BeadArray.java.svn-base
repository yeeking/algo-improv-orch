package com.olliebown.beads.core;

import java.util.ArrayList;

/**
 * A BeadArray is an array of Beads (wow!), but also a subclass of
 *         Bead. Its purpose is to forward commands to its array members.
 *         
 * @author ollie 
 * 
 */
public class BeadArray extends Bead {

	private ArrayList<Bead> beads;

	/**
	 * Default constructor.
	 */
	public BeadArray() {
		beads = new ArrayList<Bead>();
	}

	/**
	 * Adds a new Bead to the list of receivers.
	 * 
	 * @param bead
	 *            Bead to add.
	 */
	public void add(Bead bead) {
		beads.add(bead);
	}

	/**
	 * Removes a Bead from the list of receivers.
	 * 
	 * @param bead
	 *            Bead to remove.
	 */
	public void remove(Bead bead) {
		beads.remove(bead);
	}

	/**
	 * Gets the ith Bead from the list of receivers.
	 * 
	 * @param i
	 *            index of Bead to retrieve.
	 * @return the Bead at the ith index.
	 */
	public Bead get(int i) {
		return beads.get(i);
	}

	/**
	 * Clears the list of receivers.
	 */
	public void clear() {
		beads.clear();
	}

	/**
	 * Gets the size of the list of receivers.
	 * 
	 * @return size of list.
	 */
	public int size() {
		return beads.size();
	}

	/**
	 * Forwards incoming message to all receivers.
	 * 
	 * @param message
	 *            incoming message.
	 */
	public void message(Bead message) {
		BeadArray clone = clone();
		for (int i = 0; i < clone.size(); i++) {
			clone.get(i).message(message);
		}
	}

	/**
	 * Starts all receivers.
	 */
	public void start() {
		BeadArray clone = clone();
		for (int i = 0; i < clone.size(); i++) {
			Bead bead = clone.get(i);
			bead.start();
		}
	}

	/**
	 * Pauses all receivers.
	 */
	public void pause(boolean paused) {
		BeadArray clone = clone();
		for (int i = 0; i < clone.size(); i++) {
			Bead bead = clone.get(i);
			bead.pause(paused);
		}
	}

	/**
	 * Stops all receivers, and if they self-delete, removes them from the list
	 * of receivers.
	 */
	public void stop() {
		super.stop();
		BeadArray clone = clone();
		for (int i = 0; i < clone.size(); i++) {
			Bead bead = clone.get(i);
			bead.stop();
			if (bead.isDeleted())
				remove(bead);
		}
	}

	/**
	 * Creates a shallow copy of itself.
	 * 
	 * @return shallow copy of this Bead.
	 */
	public BeadArray clone() {
		BeadArray clone = new BeadArray();
		for (int i = 0; i < beads.size(); i++) {
			clone.add(beads.get(i));
		}
		return clone;
	}

}
