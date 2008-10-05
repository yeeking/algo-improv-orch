package com.olliebown.beads.core;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A UGen is the main class for signal generation and processing. It inherits
 * start(), stop() and pause() methods and the self-deleting behaviour from
 * Bead, as well as the messaging system. UGens are constructed using an
 * AudioContext to determine the buffer size and have to be specified with a
 * given number of inputs and outputs. Buy connecting a UGen's output to another
 * UGen's input the UGen is automatically added to a call chain that propagates
 * through subsequent UGens from the root UGen of the AudioContext. UGens that
 * do not have outputs (such as Clocks and FrameFeatureExtractors) can be added
 * manually to the call chain using {@link #addDependent(UGen)} from any UGen
 * that is part of the call chain (such as the root of the AudioContext).</p>
 * 
 * When this call chain is propagated, each UGen checks to make sure that its
 * input UGens are not deleted. It deletes references to any that are. Since
 * Beads are set by default to self-delete, stopping a UGen will therefore cause
 * it to get dropped from the call chain.
 * 
 * @author ollie
 * 
 */
/**
 * @author ollie
 *
 */
public abstract class UGen extends Bead {

	/**
	 * The environment.
	 */
	protected AudioContext context;
	/**
	 * Number of inputs.
	 */
	protected int ins;
	/**
	 * Number of outputs.
	 */
	protected int outs;
	/**
	 * Buffer used internally to store input data.
	 */
	protected float[][] bufIn;
	/**
	 * The buffer that will be grabbed by others.
	 */
	protected float[][] bufOut;
	/**
	 * The buffer size.
	 */
	protected int bufferSize;
	/**
	 * Pointers to the input buffers.
	 */
	private ArrayList<BufferPointer>[] inputs;
	/**
	 * Other UGens that should be triggered by this one.
	 */
	private ArrayList<UGen> dependents;
	/**
	 * Used to avoid calling pullInputs unless required.
	 */
	private boolean noInputs;
	/**
	 * Used to determine if update should be called.
	 */
	protected boolean muted;
	/**
	 * Keep track of whether we've updated at this timeStep.
	 */
	private int lastTimeStep;

	/**
	 * Create a new UGen from the given AudioContext but with no inputs or
	 * outputs.
	 * 
	 * @param context
	 *            AudioContext to use.
	 */
	public UGen(AudioContext context) {
		super();
		this.context = context;
		if(context != null) {
			bufferSize = context.getBufferSize();
			dependents = new ArrayList<UGen>();
		}
		noInputs = true;
		muted = true;
		lastTimeStep = -1;
		start();
	}

	/**
	 * Create a new UGen from the given AudioContext with no inputs and the
	 * given number of outputs.
	 * 
	 * @param context
	 *            AudioContext to use.
	 * @param outs
	 *            number of outputs.
	 */
	public UGen(AudioContext context, int outs) {
		this(context);
		setOuts(outs);
	}

	/**
	 * Create a new UGen from the given AudioContext with the given number of
	 * inputs and outputs.
	 * 
	 * @param context
	 *            AudioContext to use.
	 * @param ins
	 *            number of inputs.
	 * @param outs
	 *            number of outputs.
	 */
	public UGen(AudioContext context, int ins, int outs) {
		this(context, outs);
		setIns(ins);
	}

	public AudioContext getContext() {
		return context;
	}

	/**
	 * Set the number of inputs.
	 * 
	 * @param ins
	 *            number of inputs.
	 */
	public void setIns(int ins) {
		this.ins = ins;
		bufIn = new float[ins][bufferSize];
		inputs = new ArrayList[ins];
		for (int i = 0; i < ins; i++) {
			inputs[i] = new ArrayList<BufferPointer>();
		}
		zeroIns();
	}

	/**
	 * Set the number of outputs.
	 * 
	 * @param outs
	 *            number of outputs.
	 */
	public void setOuts(int outs) {
		this.outs = outs;
		bufOut = new float[outs][bufferSize];
		zeroOuts();
	}

	/**
	 * Set the output buffers to zero.
	 */
	public void zeroOuts() {
		for(int i = 0; i < outs; i++) {
			for(int j = 0; j < bufferSize; j++) {
				bufOut[i][j] = 0f;
			}
		}
	}

	/**
	 * Set the input buffers to zero.
	 */
	public void zeroIns() {
		for(int i = 0; i < ins; i++) {
			for(int j = 0; j < bufferSize; j++) {
				bufIn[i][j] = 0f;
			}
		}
	}
	
//	private float[] buf;
	private int size;

	protected synchronized void pullInputs() {
		ArrayList<UGen> dependentsClone = (ArrayList<UGen>) dependents.clone();
		size = dependentsClone.size();
		for (int i = 0; i < size; i++) {
			UGen dependent = dependentsClone.get(i);
			if (dependent.isDeleted())
				removeDependent(dependent);
			else
				dependent.update();
		}
		if (!noInputs) {
			zeroIns();
			for (int i = 0; i < inputs.length; i++) {
				ArrayList<BufferPointer> inputsCopy = (ArrayList<BufferPointer>) inputs[i].clone();
				size = inputsCopy.size();
				for (int ip = 0; ip < size; ip++) {
					BufferPointer bp = inputsCopy.get(ip);
					if (bp.ugen.isDeleted())
						inputs[i].remove(bp);
					else {
						bp.ugen.update();
//						buf = bp.getBuffer();
						for (int j = 0; j < bufferSize; j++) {
//							bufIn[i][j] += buf[j];
							bufIn[i][j] += bp.get(j);
						}
					}
				}
			}
		}
	}

	/**
	 * Updates the UGen. If the UGen is muted or has already been updated at
	 * this time step then this method does nothing. If the UGen does update, it
	 * will also call the update() method on all UGens connected to its inputs.
	 */
	public synchronized void update() {
		if (!isUpdated() && !isMuted()) {
			// System.out.println("update:" + getClass());
			lastTimeStep = context.getTimeStep(); // do this first to break call
			// chain loops
			pullInputs();
			calculateBuffer();
		}
	}

	/**
	 * Prints the UGens connected to this UGens inputs to the Standard Output.
	 */
	public synchronized void printInputList() {
		for (int i = 0; i < inputs.length; i++) {
			for (BufferPointer bp : inputs[i]) {
				System.out.print(bp.ugen + " ");
			}
		}
		System.out.println();
	}

	/**
	 * Maximally connect another UGen to the inputs of this UGen. If the number
	 * of inputs does not match the number of outputs then the smaller of the
	 * two is cycled through until the large of the two is fully connected. If
	 * multiple UGens are connected to any one input then the outputs from those
	 * UGens are summed on their way into the input.
	 * 
	 * @param sourceUGen
	 *            the UGen to connect to this UGen.
	 */
	public synchronized void addInput(UGen sourceUGen) {
		if(ins != 0 && sourceUGen.outs != 0) {
			int max = Math.max(ins, sourceUGen.outs);
			for (int i = 0; i < max; i++) {
				addInput(i % ins, sourceUGen, i % sourceUGen.outs);
			}
		}
	}

	/**
	 * Connect a specific output from another UGen to a specific input of this
	 * UGen.
	 * 
	 * @param inputIndex
	 *            the input of this UGen to connect to.
	 * @param sourceUGen
	 *            the UGen to connect to this UGen.
	 * @param sourceOutputIndex
	 *            the output of the connecting UGen with which to make the
	 *            connection.
	 */
	public synchronized void addInput(int inputIndex, UGen sourceUGen,
			int sourceOutputIndex) {
		inputs[inputIndex]
				.add(new BufferPointer(sourceUGen, sourceOutputIndex));
		noInputs = false;
	}

	/**
	 * Adds a UGen to this UGen's dependency list, causing the dependent UGen to
	 * get updated when this one does.
	 * 
	 * @param dependent
	 *            the dependent UGen.
	 */
	public void addDependent(UGen dependent) {
		dependents.add(dependent);
	}

	/**
	 * Removes the specified UGen from this UGen's dependency list.
	 * 
	 * @param dependent
	 *            UGen to remove.
	 */
	public void removeDependent(UGen dependent) {
		dependents.remove(dependent);
	}

	/**
	 * Gets the number of UGen outputs connected at the specified input index of
	 * this UGen.
	 * 
	 * @param index
	 *            index of input to inspect.
	 * @return number of UGen outputs connected to that input.
	 */
	public int getNumberOfConnectedUGens(int index) {
		return inputs[index].size();
	}

	/**
	 * Disconnect the specified UGen from this UGen at all inputs.
	 * 
	 * @param sourceUGen
	 *            the UGen to disconnect.
	 */
	public void removeAllConnections(UGen sourceUGen) {
		if (!noInputs) {
			int inputCount = 0;
			for (int i = 0; i < inputs.length; i++) {
				ArrayList<BufferPointer> bplist = (ArrayList<BufferPointer>) inputs[i]
						.clone();
				for (BufferPointer bp : bplist) {
					if (sourceUGen.equals(bp.ugen)) {
						inputs[i].remove(bp);
					} else
						inputCount++;
				}
			}
			if (inputCount == 0) {
				noInputs = true;
				zeroIns();
			}
		}
	}

	/**
	 * Un-mutes and starts this UGen.
	 */
	public void start() {
		super.start();
		muted = false;
	}

	/**
	 * Stops and mutes this UGen, and deletes it if it is self-deleting.
	 */
	public void stop() {
		super.stop();
		muted = true;
	}
	
	public void stopGracefully() {
		stop();
	}

	/**
	 * Pauses and mutes this UGen.
	 */
	public void pause(boolean paused) {
		muted = paused;
	}

	/**
	 * Deletes this UGen, which means marking it as deleted. UGens receiving
	 * input from this UGen or upon which it is dependent will drop references
	 * to it.
	 */
	public void delete() {
		super.delete();
	}

	/**
	 * Prints the contents of the output buffers to the Standard Input. Could be
	 * a lot of data.
	 */
	public void printOutBuffers() {
		for (int i = 0; i < bufOut.length; i++) {
			for (int j = 0; j < bufOut[i].length; j++) {
				System.out.print(bufOut[i][j] + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Determines whether this UGen is muted.
	 * 
	 * @return true if the UGen is muted, false otherwise.
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * Determines whether this UGen has no UGens connected to its inputs.
	 * 
	 * @return true if this UGen has no UGens connected to its inputs, false
	 *         otherwise.
	 */
	public boolean noInputs() {
		return noInputs;
	}

	/**
	 * Subclassses of UGen should put the UGen's DSP perform routine in here. In
	 * general this involves grabbing data from {@link #bufIn} and putting data
	 * into {@link #bufOut} in some way. The length of the buffer is given by
	 * {@link #bufferSize}.
	 */
	public abstract void calculateBuffer(); // must be implemented by subclasses

	/**
	 * Gets a specific specified value from the output buffer, with indices i
	 * and j.
	 * 
	 * @param i
	 *            channel index.
	 * @param j
	 *            buffer frame index.
	 * @return value of specified sample.
	 */
	public float getValue(int i, int j) {
		return bufOut[i][j];
	}
	
	public float getValue() {
		return bufOut[0][0];
	}
	
	public void setValue(float value) {
	}

	private int getLastTimeStep() {
		return lastTimeStep;
	}

	private void setLastTimeStep(int lastTimeStep) {
		this.lastTimeStep = lastTimeStep;
	}

	private boolean isUpdated() {
		return lastTimeStep == context.getTimeStep();
	}

	private class BufferPointer {

		UGen ugen;
		int index;

		BufferPointer(UGen ugen, int index) {
			this.ugen = ugen;
			this.index = index;
		}

		float[] getBuffer() {
			return ugen.bufOut[index];
		}
		
		float get(int point) {
			return ugen.getValue(index, point);
		}
	}

}
