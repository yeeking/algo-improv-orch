
package com.olliebown.beads.data;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import com.olliebown.beads.analysis.Frequency;
import com.olliebown.beads.analysis.MelSpectrum;
import com.olliebown.beads.analysis.OnsetDetector;
import com.olliebown.beads.analysis.Power;
import com.olliebown.beads.analysis.PowerSpectrum;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.AudioUtils;
import com.olliebown.beads.events.AudioContextStopTrigger;
import com.olliebown.beads.ugens.SamplePlayer;

/**
 * A Sample is a buffer for audio data which can be loaded from a file or recorded into the buffer.
 * 
 * @see SampleManager
 */
public class Sample {
    
	private String name;
	
    /** The audio format. */
    public final AudioFormat audioFormat;
    
    /** The number of channels. */
    public final int nChannels;
    
    /** The number of frames. */
    public final int nFrames;
    
    public final float length;
    
    /** The audio data. */
    public final float[][] buf;
    
    /** The result. */
    private float[] result;
    
    private int featureHop;
    private Hashtable<String, float[][]> features; //time point hopCount, name, value
    
    /**
	 * Instantiates a new empty buffer with the specified audio format and number of frames.
	 * 
	 * @param audioFormat
	 *            the audio format
	 * @param nFrames
	 *            the number frames
	 */
    public Sample(AudioFormat audioFormat, int nFrames) {
        this.audioFormat = audioFormat;
        nChannels = audioFormat.getChannels();
        this.nFrames = nFrames;
        buf = new float[nChannels][nFrames];
        for(int i = 0; i < nChannels; i++) {
            for(int j = 0; j < nFrames; j++) {
                buf[i][j] = 0.0f;
            }
        }
        result = new float[nChannels];
        features = new Hashtable<String, float[][]>();
        length = nFrames / audioFormat.getSampleRate() * 1000f;
    }

    /**
	 * Creates a new Sample from the specified file.
	 * 
	 * @param fn
	 *            the file name
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
    public Sample(String fn) throws UnsupportedAudioFileException, IOException {
    	this.name = fn;
        File fileIn = new File(fn);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
        audioFormat = audioInputStream.getFormat();
        nChannels = audioFormat.getChannels();
        nFrames = (int)audioInputStream.getFrameLength();
        if(nFrames == -1) throw new IOException();
        int frameHop = 256;
        byte[] audioBytes = new byte[frameHop * audioFormat.getFrameSize()];
        buf = new float[nChannels][nFrames];
        int framesRead = 0;
        int bytesRead;
		while ((bytesRead = audioInputStream.read(audioBytes)) != -1) {
			frameHop = bytesRead / audioFormat.getFrameSize();
            float[] bufTemp = new float[frameHop * nChannels];
            AudioUtils.byteToFloat(bufTemp, audioBytes, audioFormat.isBigEndian());     
            float[][] bufSegment = deinterleave(bufTemp);
            for (int i = 0; i < bufSegment.length; i++) {
				for (int j = 0; j < bufSegment[i].length; j++) {
					buf[i][j + framesRead] = bufSegment[i][j];	
				}
			}
            framesRead += frameHop;
        }
        audioInputStream.close();
        result = new float[nChannels];
        features = new Hashtable<String, float[][]>();
        length = nFrames / audioFormat.getSampleRate() * 1000f;
    }
    
    public void loadFeatures() {
        //try to get the features
    	try {
			readFeatures();
		} catch (Exception e) {
			// do nothing
		}
		if(features == null || features.size() == 0) {
			extractFeatures();
		}
    }
    
    public float[] getFeatures(String name, float time) {
    	int featureIndex = msTimeToFeatureIndex(time);
    	return features.get(name)[featureIndex];
    }
    
    public void addFeatures(String name, float time, float values[]) {
    	int timeIndex = msTimeToFeatureIndex(time);
    	features.get(name)[timeIndex] = values;
    }
    
    public Integer msTimeToFeatureIndex(float time) {
    	return new Integer((int)(msToSamples(time)) / featureHop);
    }
    
    /**
	 * Deinterleaves an interleaved array.
	 * 
	 * @param source
	 *            the interleaved array
	 * 
	 * @return the de-interleaved array (channels x frames)
	 */
    private float[][] deinterleave(float[] source) {
    	int nFrames = source.length / nChannels;
        float[][] result = new float[nChannels][nFrames];   
        for(int i = 0, count = 0; i < nFrames; i++) {
            for(int j = 0; j < nChannels; j++) {
                result[j][i] = source[count++];
            }
        }
        return result;
    }
    
    /**
	 * Interleaves a de-interleaved (channels x frames) array.
	 * 
	 * @param source
	 *            the source array
	 * 
	 * @return the interleaved array (frame by frame, alternating channels)
	 */
    private float[] interleave(float[][] source) {
        float[] result = new float[nChannels * nFrames];
        for(int i = 0, counter = 0; i < nFrames; i++) {
            for(int j = 0; j < nChannels; j++) {
                result[counter++] = source[j][i];
            }   
        }
        return result;
    }
    
    /**
	 * Converts from millieconds to samples.
	 * 
	 * @param msTime
	 *            the time in milliseconds
	 * 
	 * @return the time in samples
	 */
    public float msToSamples(float msTime) {
        return msTime * audioFormat.getSampleRate() / 1000.0f;
    }

    /**
	 * Converts from samples to milliseconds.
	 * 
	 * @param sampleTime
	 *            the time in samples
	 * 
	 * @return the time in milliseconds
	 */
    public float samplesToMs(float sampleTime) {
        return sampleTime / audioFormat.getSampleRate() * 1000.0f;
    }
    
    /**
	 * Retrieves a frame of audio using linear interpolation.
	 * 
	 * @param currentSample
	 *            the current sample
	 * @param fractionOffset
	 *            the offset from the current sample as a fraction of the time to the next sample
	 * 
	 * @return the interpolated frame
	 */
    public float[] getFrameLinear(int currentSample, float fractionOffset) {
        if(currentSample >= 0 && currentSample < nFrames) {
            for (int i = 0; i < nChannels; i++) {
                if(currentSample < (nFrames - 1)) {
                    result[i] = (1f - fractionOffset) * buf[i][currentSample] +
                            fractionOffset * buf[i][currentSample + 1];
                } else {
                    result[i] = buf[i][currentSample];
                }   
            }
        } else {
             for(int i = 0; i < nChannels; i++) {
                 result[i] = 0.0f;
             }
        }
        return result;
    }
    
    /**
	 * Retrieves a frame of audio using cubic interpolation.
	 * 
	 * @param currentSample
	 *            the current sample
	 * @param fractionOffset
	 *            the offset from the current sample as a fraction of the time to the next sample
	 * 
	 * @return the interpolated frame
	 */
    public float[] getFrameCubic(int currentSample, float fractionOffset) {
        float[] result = new float[nChannels];
        float a0,a1,a2,a3,mu2;
        float ym1,y0,y1,y2;
        for (int i = 0; i < nChannels; i++) {
            int realCurrentSample = currentSample;
            if(realCurrentSample >= 0 && realCurrentSample < (nFrames - 1)) {
                realCurrentSample--;
                if (realCurrentSample < 0) {
                    ym1 = buf[i][0];
                    realCurrentSample = 0;
                } else {
                    ym1 = buf[i][realCurrentSample++];
                }
                y0 = buf[i][realCurrentSample++];
                if (realCurrentSample >= nFrames) {
                    y1 = buf[i][nFrames - 1];
                } else {
                    y1 = buf[i][realCurrentSample++];
                }
                if (realCurrentSample >= nFrames) {
                    y2 = buf[i][nFrames - 1];
                } else {
                    y2 = buf[i][realCurrentSample];
                }
                mu2 = fractionOffset * fractionOffset;
                a0 = y2 - y1 - ym1 + y0;
                a1 = ym1 - y0 - a0;
                a2 = y1 - ym1;
                a3 = y0;
                result[i] = a0 * fractionOffset * mu2 + a1 * mu2 + a2 * fractionOffset + a3;
            } else {
                result[i] = 0.0f;
            }
        }
        return result;
    }

    /**
	 * Post audio format info.
	 */
    public void postAudioFormatInfo() {
        System.out.println("Sample Rate: " + audioFormat.getSampleRate());
        System.out.println("Channels: " + nChannels);
        System.out.println("Frame size in Bytes: " + audioFormat.getFrameSize());
        System.out.println("Encoding: " + audioFormat.getEncoding());
        System.out.println("Big Endian: " + audioFormat.isBigEndian());
    }   
    
    /**
	 * Write to a file.
	 * 
	 * @param fn
	 *            the file name
	 * 
	 * @throws IOException
	 */
    public void write(String fn) throws IOException {
        byte[] bytes = new byte[nFrames * audioFormat.getFrameSize()];
        AudioUtils.floatToByte(bytes, interleave(buf), audioFormat.isBigEndian());
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        AudioInputStream aos = new AudioInputStream(bais, audioFormat, nFrames);
        AudioSystem.write(aos, AudioFileFormat.Type.AIFF, new File(fn));
    }
    
    public void extractFeatures() {
        featureHop = 1024;
    	//set up the basic network
    	AudioContext ac = new AudioContext(featureHop, -1, audioFormat);
    	ac.setRealTime(false);
    	SamplePlayer sp = new SamplePlayer(ac, this);
    	sp.setEndListener(new AudioContextStopTrigger(ac));
       	PowerSpectrum ps = new PowerSpectrum(ac);
    	ps.addInput(sp);
    	sp.addDependent(ps);
    	ps.setNumFeatures(10);
    	ac.getRoot().addInput(sp);
    	ps.accumulate(true);
    	//run and extract
    	ac.logTime(true);
    	ac.runNonRealTime();
    	System.out.println("finished analysis");
        float[][] allFeatures = new float[ps.getAccumulatedFeatures().size()][];
    	int totalFeatures = 10;
    	for(int i = 0; i < ps.getAccumulatedFeatures().size(); i++) {
    		float[] newFeatureFrame = new float[totalFeatures]; 
    		int featureIndex = 0;
    		float[] powerBin = ps.getAccumulatedFeatures().get(i);
    		for(int j = 0; j < powerBin.length; j++) {
    			newFeatureFrame[featureIndex++] = powerBin[j];
    		}
    		allFeatures[i] = newFeatureFrame;
    	}
    	features.put("all", allFeatures);
    	if(name != null) {
    		try {
    			writeFeatures();
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public void writeFeatures(String filename) throws IOException {
    	FileOutputStream fos = new FileOutputStream(filename);
    	ObjectOutputStream oos = new ObjectOutputStream(fos);
    	oos.writeObject(new Integer(featureHop));
    	oos.writeObject(features);
    	oos.close();
    	fos.close();
    }
    
    public void readFeatures(String filename) throws IOException, ClassNotFoundException {
    	FileInputStream fis = new FileInputStream(filename);
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	featureHop = (Integer)ois.readObject();
    	features = (Hashtable<String, float[][]>)ois.readObject();
    	ois.close();
    	fis.close();
    }
    
    public void readFeatures() throws IOException, ClassNotFoundException {
    	readFeatures(name + ".features");
    }
    
    public void writeFeatures() throws IOException {
    	writeFeatures(name + ".features");
    }
    
    public void printFeatures(String name, String filename) throws IOException {
		File outFile = new File(filename);
		FileOutputStream fos = new FileOutputStream(outFile);
		PrintStream ps = new PrintStream(fos);
		float[][] theseFeatures = features.get(name);
		for(int i = 0; i < theseFeatures.length; i++) {
			for(int j = 0; j < theseFeatures[i].length; j++) {
				ps.println(i + " " + j + " " + theseFeatures[i][j]);
			}
			ps.println();
		}
		ps.close();
		fos.close();
    }
    
    public String toString() {
    	return name;
    }
    
    public static void main(String[] args) throws Exception {
    	Sample s1 = new Sample("/Users/ollie/Music/Audio/output5.aif");	
//    	Sample s1 = new Sample("audio/1234.aif");	
    	s1.loadFeatures();
     	try {
    		s1.printFeatures("all", "/Users/ollie/Desktop/audioFeatures");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
