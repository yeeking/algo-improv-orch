
package com.olliebown.beads.trash;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.events.Pattern;
import com.olliebown.beads.ugens.Clock;
import com.olliebown.beads.ugens.Gain;

public class TestFMSynth implements MouseMotionListener {

    public static void main(String[] args) {
    	new TestFMSynth();	
    }
    
    Clock clock;
    
    public TestFMSynth() {
        AudioContext ac = new AudioContext(128);

        JFrame window = new JFrame("Sounds");
        window.setVisible(true);
        window.addMouseMotionListener(this);
        
        Gain gain = new Gain(ac, 2);
        gain.setGainEnvelope(new Envelope(ac, 0.1f));
        
        FMPattern fmp = new FMPattern(ac);
        clock = new Clock(ac);
        clock.setIntervalEnvelope(new Envelope(ac, 200.0f));
        Pattern p = new Pattern();
        //p.addEvent(new Integer(0), fmp);
        p.setMod(2);
        clock.addMessageListener(p);

        gain.addInput(0, fmp, 0);
        gain.addInput(1, fmp, 0);
        
        Pattern p2 = new Pattern();
        fmp = new FMPattern(ac);
        fmp.setFreqs(new float[]{4000.0f, 5000.0f, 6000.0f, 10000.0f});
        //p2.addEvent(new Integer(0), fmp);
        p2.setMod(3);

        gain.addInput(0, fmp, 0);
        gain.addInput(1, fmp, 0);
        
        clock.addMessageListener(p2);
        
        ((Envelope)clock.getIntervalEnvelope()).addSegment(1000.0f, 20000.0f);
        
        ac.setRoot(gain);
        ac.start();
    }


	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		clock.setIntervalEnvelope(new Envelope(clock.getContext(), arg0.getX() + 1.0f));
		
	}
    
    
}
