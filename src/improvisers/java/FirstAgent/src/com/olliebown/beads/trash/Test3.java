
package com.olliebown.beads.trash;


import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.MonoToStereo;
import com.olliebown.beads.ugens.RTInput;
import com.olliebown.beads.ugens.WavePlayer;



public class Test3 {

    
    public static void main(String[] args) {
        
        AudioContext player = new AudioContext(512);
        MonoToStereo mts = new MonoToStereo(player);
        WavePlayer wp = WavePlayer.newSinePlayer(player, 440f);
        mts.addInput(wp);
        mts.printInputList();
        mts.printInputList();
        
        
    }
    
}
