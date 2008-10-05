/**
 * This class is taken from JASS TODO: Proper credits and license
 * 
 */
package com.olliebown.beads.core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import com.olliebown.beads.ugens.RangeLimiter;

/**
 * AudioContext is the main class required for running a Beads
 *         project. The AudioContext determines the JavaSound AudioFormat used,
 *         the IO device, the audio buffer size and the system IO buffer size.
 *         An AudioContext also has a root {@link UGen} which is the entry point for
 *         networks of UGens in a Beads project.
 *         
 * @author ollie 
 * 
 */
public class AudioContext extends Thread {

	// audio format info
	private AudioFormat audioFormat;

	// stuff for writing audio
	private SourceDataLine sourceDataLine;
	private int bufferSizeInBytes;
	private byte[] bbuf;

	// management
	private boolean stop;
	private UGen root;
	private boolean realTime;					//could be redundant
	private boolean checkForDroppedFrames;
	private int timeStep;
	private boolean logTime;

	// the buffer size, measured in frames
	private int bufferSizeInFrames;
	private int systemBufferSizeInFrames;

	/**
	 * Creates a new AudioContext with default settings. The default buffer size
	 * is 1024 and the default system buffer size is determined by the JVM. The
	 * default audio format is 44.1Khz, 16 bit, stereo, signed, bigEndian.
	 */
	public AudioContext() {
		// use entirely default settings
		this(1024);
	}

	/**
	 * Creates a new AudioContext with default settings and the specified buffer
	 * size. The default system buffer size is determined by the JVM. The
	 * default audio format is 44.1Khz, 16 bit, stereo, signed, bigEndian.
	 * 
	 * @param bufferSizeInFrames
	 *            the buffer size in samples.
	 */
	public AudioContext(int bufferSizeInFrames) {
		this(bufferSizeInFrames, -1);
	}

	/**
	 * Creates a new AudioContext with default audio format and the specified
	 * buffer size and system buffer size. The default audio format is 44.1Khz,
	 * 16 bit, stereo, signed, bigEndian.
	 * 
	 * @param bufferSizeInFrames
	 *            the buffer size in samples.
	 * @param systemBufferSizeInFrames
	 *            the system buffer size in samples.
	 */
	public AudioContext(int bufferSizeInFrames, int systemBufferSizeInFrames) {
		// use almost entirely default settings
		this(bufferSizeInFrames, systemBufferSizeInFrames, new AudioFormat(
				44100, 16, 2, true, true));
	}

	/**
	 * Creates a new AudioContext with the specified buffer size, system buffer
	 * size and audio format.
	 * 
	 * @param bufferSizeInFrames
	 *            the buffer size in samples.
	 * @param systemBufferSizeInFrames
	 *            the system buffer size in samples.
	 * @param audioFormat
	 *            the audio format, which specifies sample rate, bit depth,
	 *            number of channels, signedness and byte order.
	 */
	public AudioContext(int bufferSizeInFrames, int systemBufferSizeInFrames,
			AudioFormat audioFormat) {
		setPriority((int)(MAX_PRIORITY * 0.5));
		// set up other basic stuff
		stop = false;
		realTime = true;
		checkForDroppedFrames = false;
		logTime = false;
		// set audio format
		this.audioFormat = audioFormat;
		// set buffer size
		setBufferSize(bufferSizeInFrames);
		this.systemBufferSizeInFrames = systemBufferSizeInFrames;
		// set up the default root
		setRoot(new RangeLimiter(this, audioFormat.getChannels()));
	}

	private void initJavaSound() {
		// and away
		Mixer mixer = getMixer();
		System.out.print("CHOSEN MIXER: ");
		System.out.println(mixer.getMixerInfo().getName());
		if (mixer == null)
			return;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try {
			sourceDataLine = (SourceDataLine) mixer.getLine(info);
			if (systemBufferSizeInFrames < 0)
				sourceDataLine.open(audioFormat);
			else
				sourceDataLine.open(audioFormat, systemBufferSizeInFrames
						* audioFormat.getFrameSize());
		} catch (LineUnavailableException ex) {
			System.out
					.println(getClass().getName() + " : Error getting line\n");
		}
	}

	/**
	 * Gets the JavaSound mixer being used by this AudioContext.
	 * 
	 * @return the requested mixer.
	 */
	public static Mixer getMixer() {
		Mixer mixer = null;
		Mixer.Info[] mixerinfo = AudioSystem.getMixerInfo();
		if (mixerinfo.length > 0) {
			mixer = AudioSystem.getMixer(mixerinfo[0]);
		} else {
			System.out.println("No audio mixer found, therfore no sound.");
			mixer = null;
		}
		return mixer;
	}

	/**
	 * Prints information about the current Mixer to the Standard Output.
	 */
	public static void printMixerInfo() {
		Mixer.Info[] mixerinfo = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixerinfo.length; i++) {
			String name = mixerinfo[i].getName();
			if (name.equals(""))
				name = "No name";
			System.out.println(name + " --- " + mixerinfo[i].getDescription());
			Mixer m = AudioSystem.getMixer(mixerinfo[i]);
			Line.Info[] lineinfo = m.getSourceLineInfo();
			for (int j = 0; j < lineinfo.length; j++) {
				System.out.println("  - " + lineinfo[j].toString());
			}
		}
	}

	/**
	 * Used by this AudioContext's Thread, use {@link #start()} instead of this.
	 */
	public void run() {
		initJavaSound();
		sourceDataLine.start();
		// calibration test stuff
		long nanoStart = System.nanoTime();
		long nanoLeap = (long) (1000000000 / (audioFormat.getSampleRate() / (float) bufferSizeInFrames));
		boolean skipFrame = false;
		timeStep = 0;
		float[] interleavedOutput = new float[audioFormat.getChannels() * bufferSizeInFrames];
		while (root != null && !stop) {
			if (!skipFrame) {
				synchronized (this) {
					if (!root.isMuted())
						root.update(); // this will propagate most of the
										// updates
				}
				if (realTime) {
					interleave(root.bufOut, interleavedOutput);
					AudioUtils.floatToByte(bbuf, interleavedOutput,
							audioFormat.isBigEndian());
					sourceDataLine.write(bbuf, 0, bbuf.length);
				}
			}
			if (checkForDroppedFrames) {
				long expectedNanoTime = nanoLeap * (timeStep + 1);
				long realNanoTime = System.nanoTime() - nanoStart;
				float frameDifference = (float) (expectedNanoTime - realNanoTime)
						/ (float) nanoLeap;
				if (frameDifference < -1) {
					skipFrame = true;
					System.out.println("Audio dropped frame.");
				} else
					skipFrame = false;
			}
			timeStep++;
			if(logTime && timeStep % 100 == 0) {
				System.out.println(samplesToMs(timeStep * bufferSizeInFrames) / 1000f);
			}
		}
		sourceDataLine.stop();
		sourceDataLine.close();
	}

	public void runNonRealTime() {
		while (root != null && !stop) {
			if (!root.isMuted())
				root.update();
			timeStep++;
			if(logTime && timeStep % 100 == 0) {
				System.out.println(samplesToMs(timeStep * bufferSizeInFrames) / 1000f);
			}
		}
	}

	private void interleave(float[][] source, float[] result) {
//		float[] result = new float[audioFormat.getChannels()
//				* bufferSizeInFrames];
		for (int i = 0, counter = 0; i < bufferSizeInFrames; ++i) {
			for (int j = 0; j < audioFormat.getChannels(); ++j) {
				result[counter++] = source[j][i];
			}
		}
	}

	private void setBufferSize(int bufferSize) {
		bufferSizeInFrames = bufferSize;
		bufferSizeInBytes = bufferSizeInFrames * audioFormat.getFrameSize();
		bbuf = new byte[bufferSizeInBytes];
	}

	/**
	 * Choose whether the AudioContext runs in real time when started (on by
	 * default). If you run the AudioContext in non-real time then no output
	 * will be heard, but exactly the same signal processing will occur, and it
	 * is possible to record audio to a buffer and write it to a file.
	 * 
	 * @param realTime
	 *            true if this AudioContext is to be run in real time. False
	 *            otherwise.
	 */
	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}

	/**
	 * Determines if this AudioContext is set to run in real time.
	 * 
	 * @return true if set to run in real time. False otherwise.
	 */
	public boolean isRealTime() {
		return realTime;
	}

	/**
	 * Gets the buffer size for this AudioContext.
	 * 
	 * @return Buffer size in samples.
	 */
	public int getBufferSize() {
		return bufferSizeInFrames;
	}

	/**
	 * Gets the sample rate for this AudioContext.
	 * 
	 * @return sample rate in samples per second.
	 */
	public float getSampleRate() {
		return audioFormat.getSampleRate();
	}

	/**
	 * Gets the AudioFormat for this AudioContext.
	 * 
	 * @return AudioFormat used by this AudioContext.
	 */
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * Determines the UGen that this AudioContext treats as its root. The root
	 * UGen sends its outputs straight to the JavaSound output, and is called
	 * first in the call chain. This is set by default to a {@link RangeLimiter}
	 * .
	 * 
	 * @param uGen
	 *            the UGen used as root by this AudioContext.
	 */
	public void setRoot(UGen uGen) {
		if (uGen.outs != audioFormat.getChannels()) {
			System.out
					.println("RTPlay: Error setting root UGen. Wrong number of channels");
		} else {
			root = uGen;
		}
	}

	/**
	 * Gets the current root UGen.
	 * 
	 * @return the root UGen used by this AudioContext.
	 */
	public UGen getRoot() {
		return root;
	}

	/**
	 * Stops the current AudioContext and throws it away.
	 */
	public void kill() {
		stop = true;
	}

//	/**
//	 * Starts the current AudioContext.
//	 */
//	public void start() {
//		if (realTime)
//			super.start();
//		else
//			runNonRealTime();
//	}

	/**
	 * Prints AudioFormat information to the Standard Output.
	 */
	public void postAudioFormatInfo() {
		System.out.println("Sample Rate: " + audioFormat.getSampleRate());
		System.out.println("Channels: " + audioFormat.getChannels());
		System.out
				.println("Frame size in Bytes: " + audioFormat.getFrameSize());
		System.out.println("Encoding: " + audioFormat.getEncoding());
		System.out.println("Big Endian: " + audioFormat.isBigEndian());
	}

	/**
	 * Prints SourceDataLine info to the Standard Output.
	 */
	public void postSourceDataLineInfo() {
		System.out.println("----------------");
		System.out.println("buffer: " + (sourceDataLine.getBufferSize()));
		System.out
				.println("spare: "
						+ (sourceDataLine.getBufferSize() - sourceDataLine
								.available()));
		System.out.println("available: " + sourceDataLine.available());
	}

	/**
	 * Returns number of samples for given duration in milliseconds.
	 * 
	 * @param msTime
	 *            duration in milliseconds.
	 * @return number of samples.
	 */
	public float msToSamples(float msTime) {
		return msTime * audioFormat.getSampleRate() / 1000.0f;
	}

	/**
	 * Returns duration in milliseconds for given number of samples.
	 * 
	 * @param sampleTime
	 *            number of samples.
	 * @return duration in milliseconds.
	 */
	public float samplesToMs(float sampleTime) {
		return sampleTime / audioFormat.getSampleRate() * 1000.0f;
	}

	/**
	 * Gets the current time step of this AudioContext. The time step begins at
	 * zero and is incremented for each update of the audio buffer.
	 * 
	 * @return current time step.
	 */
	public int getTimeStep() {
		return timeStep;
	}

	public void logTime(boolean logTime) {
		this.logTime = logTime;
	}
}
