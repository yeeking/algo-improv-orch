package com.olliebown.beads.core;

/**
 * Bead is an abstract class which defines basic behaviour such as
 *         starting and stopping, deleting and handling messages. The main
 *         subclass of Bead is UGen, which is where most of the action happens
 *         in Beads.
 *         
 * @author ollie 
 * 
 */
public abstract class Bead {

	/**
	 * Determines whether this Bead is deleted. Certain classes, such as UGens,
	 * use this as an indicator of whether they should throw away references to
	 * the Bead.
	 */
	private boolean deleted = false;

	/**
	 * Determines whether or not the Bead sets its state to deleted when {@link
	 * stop()} is called.
	 */
	private boolean selfDelete = true;

	/**
	 * Sets self delete mode.
	 * 
	 * @param selfDelete
	 *            true if Bead is to delete itself, false otherwise.
	 */
	public void setSelfDelete(boolean selfDelete) {
		this.selfDelete = true;
	}

	/**
	 * Determines whether or not the Bead is self-deleting.
	 * 
	 * @return true if this Bead is self-deleteing, false otherwise.
	 */
	public boolean isSelfDeleting() {
		return selfDelete;
	}

	/**
	 * Returns the class of the Bead as a String.
	 * 
	 * @return String denoting the class of the Bead.
	 */
	@Override
	public String toString() {
		return getClass().toString();
	}

	/**
	 * Send this Bead a message.
	 * 
	 * @param message
	 *            the Bead is the message.
	 */
	public void message(Bead message) {
	}

	/**
	 * Starts this Bead. Means different things for different subclasses of
	 * Bead.
	 */
	public void start() {
		deleted = false;
	}

	/**
	 * Pauses this Bead. Means different things for different subclasses of
	 * Bead.
	 */
	public void pause(boolean paused) {
	}

	/**
	 * Stops this Bead, and sets its state to deleted if this Bead is
	 * self-deleting. Means different things for different subclasses of Bead.
	 */
	public void stop() {
		if (selfDelete) {
			delete();
		}
	}

	/**
	 * Sets this Bead's state to deleted.
	 */
	public void delete() {
		deleted = true;
	}

	/**
	 * Determines if this Bead is deleted.
	 * 
	 * @return true if this Bead's state is deleted, false otherwise.
	 */
	public boolean isDeleted() {
		return deleted;
	}

}
