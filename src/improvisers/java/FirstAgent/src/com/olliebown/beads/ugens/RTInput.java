package com.olliebown.beads.ugens;

import com.olliebown.beads.analysis.OnsetDetector;
import com.olliebown.beads.core.AudioUtils;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.UGen;
import com.olliebown.beads.data.SampleManager;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class RTInput extends UGen {

	AudioFormat audioFormat;
	TargetDataLine targetDataLine;

	public RTInput(AudioContext player) {
		this(player, new AudioFormat(44100, 16, 1, true, true));
	}

	public RTInput(AudioContext player, AudioFormat audioFormat) {
		super(player, audioFormat.getChannels());
		this.audioFormat = audioFormat;
		Mixer mixer = AudioContext.getMixer();
		if (mixer == null) {
			System.out.println("no mixer");
			return;
		}
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,
				audioFormat);
		try {
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info); // ignoring
																			// mixer
																			// !
			Line.Info[] infos = mixer.getTargetLineInfo();
			System.out.println(infos.length);
			for(int i = 0; i < infos.length; i++) {
				System.out.println(infos[i]);
			}
			targetDataLine.open(audioFormat, 4000);
			if(targetDataLine == null) System.out.println("no line");
			else System.out.println(targetDataLine.getLineInfo());
		} catch (LineUnavailableException ex) {
			System.out
					.println(getClass().getName() + " : Error getting line\n");
		}
		targetDataLine.start();
	}

	@Override
	public void calculateBuffer() {
		byte[] bbuf = new byte[bufferSize * audioFormat.getFrameSize()];
		targetDataLine.read(bbuf, 0, bbuf.length);
		float[] interleavedSamples = new float[bufferSize * audioFormat.getChannels()];
		AudioUtils.byteToFloat(interleavedSamples, bbuf, audioFormat.isBigEndian());
		AudioUtils.deinterleave(interleavedSamples, audioFormat.getChannels(), bufferSize, bufOut);
	}
	
	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 1500);
		ac.start();
		SamplePlayer sp = new SamplePlayer(ac, SampleManager.sample("audio/1234.aif"));
		RTInput input = new RTInput(ac, ac.getAudioFormat());
		ac.getRoot().addDependent(input);
		ac.getRoot().addInput(input);
		ac.getRoot().addInput(sp);
	}
	
}
