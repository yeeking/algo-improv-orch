
package com.olliebown.beads.data;

import com.olliebown.beads.analysis.OnsetDetector;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.events.AudioContextStopTrigger;
import com.olliebown.beads.ugens.SamplePlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SegmentedSample extends Sample {
    
    ArrayList<int[]> timePoints;
    
    public SegmentedSample(String fn, float thresh) throws UnsupportedAudioFileException, IOException, ClassNotFoundException {
        super(fn);
        File onsetFile = new File(fn + ".onsets");
        if(onsetFile.exists()) {
            FileInputStream fis = new FileInputStream(onsetFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            timePoints = (ArrayList<int[]>)ois.readObject();
            fis.close();
            ois.close();
        } else {
            timePoints = new ArrayList<int[]>();
            AudioContext context = new AudioContext(512, -1, audioFormat);
            TimePointTracker tracker = new TimePointTracker(context);
            OnsetDetector od = new OnsetDetector(context, tracker);
            od.setThresholds(new float[] {thresh});
            SamplePlayer sp = new SamplePlayer(context, this);
            od.addInput(sp);
            context.getRoot().addDependent(od);
            sp.setEndListener(new AudioContextStopTrigger(context));
            context.runNonRealTime();
            for(int[] time : timePoints) {
            	System.out.println(time[0] + " " + time[1]);
            }
            context.kill();
        }
    }

    public ArrayList<int[]> getTimePoints() {
        return timePoints;
    }

    public void setTimePoints(ArrayList<int[]> timePoints) {
        this.timePoints = timePoints;
    }
    
    private class TimePointTracker extends Bead {
    	
    	AudioContext context;
    	
    	public TimePointTracker(AudioContext context) {
    		this.context = context;
    	}
    	
    	public void message(Bead message) {
    		int time = (int)context.samplesToMs(context.getTimeStep() * context.getBufferSize());
    		int strength = ((OnsetDetector)message).getBeatStrength();
    		timePoints.add(new int[] {time, strength});
    	}
    }
    
    public static void main(String[] args) throws Exception {
    	SegmentedSample ss = new SegmentedSample("audio/1234.aif", 0.1f);
    }
    
}
