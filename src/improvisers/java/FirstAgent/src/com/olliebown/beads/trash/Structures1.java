
package com.olliebown.beads.trash;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.core.BeadArray;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Clock;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.WavePlayer;

public class Structures1 {

    public static class CrazySoundReactor extends Gain {
        
        Clock clock;
        float scale = 500.0f;
        
        public CrazySoundReactor(AudioContext context) {
            super(context, 2);
            clock = new Clock(context);
            clock.setIntervalEnvelope(new Envelope(context, 500.0f));
            clock.start();
            clock.addMessageListener(this);
        }
        
        public void message(Bead message) {
            //muck around with the clock
            clock.setIntervalEnvelope(new Envelope(context, Math.random() < 0.5 ? 500.0f : 1000.0f));
            
            BeadArray stopTrigger = new BeadArray();
            WavePlayer sinePlayer = WavePlayer.newSinePlayer(context, (float)Math.random() * scale + 100.0f);
            sinePlayer.start();
            Gain gain = new Gain(context, 1);
            
            stopTrigger.add(sinePlayer);
            ((Envelope)gain.getGainEnvelope()).addSegment(0.1f, 200, 1, null);
            ((Envelope)gain.getGainEnvelope()).addSegment(0.01f, 500, 1, null);
            ((Envelope)gain.getGainEnvelope()).addSegment(0.1f, 500, 1, null);
            ((Envelope)gain.getGainEnvelope()).addSegment(0, 3000, 1, stopTrigger);
            gain.addInput(sinePlayer);
            gain.start();
            stopTrigger.add(gain);
            addInput(gain);
            
        }

    }
    
    public static void main(String[] args) {
        AudioContext context = new AudioContext(1024);
        Gain gain = new Gain(context, 2);
        
        CrazySoundReactor csr = new CrazySoundReactor(context);
        gain.addInput(csr);
        gain.start();
        context.setRoot(gain);
        csr.start();
        context.start();
        
    }
    
}
