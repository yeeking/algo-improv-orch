
package com.olliebown.beads.trash;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.SegmentedSample;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Test6 {

    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, ClassNotFoundException {
    
        //AudioContext ac = new AudioContext();
        
        SegmentedSample sample = new SegmentedSample("audio/1234.aif", 0.1f);
        for(int i = 0; i < sample.getTimePoints().size(); i++) {
            System.out.println("time point: " + sample.getTimePoints().get(i));
        }
        System.out.println("total time points: " + sample.getTimePoints().size());
    }
}
