
package com.olliebown.beads.trash;


import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.Sample;
import com.olliebown.beads.core.Bead;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.SamplePlayer;
import com.olliebown.beads.ugens.Clock;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.GranularSamplePlayer;
import com.olliebown.beads.ugens.MonoToStereo;
import com.olliebown.beads.ugens.OscillatorBank;
import com.olliebown.beads.ugens.ScalingMixer;
import com.olliebown.beads.ugens.WavePlayer;
import java.io.IOException;
import java.net.SocketException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Test1 {

    public static void main(String[] args) throws Exception {
        
        //complexFile();
        complexFileGranular2();
        //simpleFile();
        //simpleSine();
        
        //sineBank();
        
        //AudioContext player = new AudioContext();
        //problem();
        //test();
        
    }
    

    public static void simpleFile() throws UnsupportedAudioFileException, IOException {
        AudioContext player = new AudioContext(512);
        Sample buf = new Sample("audio/1234.aif");
        SamplePlayer p = new SamplePlayer(player, buf);
        p.start();
        
        player.setRoot(p);
        player.start();
    }
    
    public static void complexFileGranular() throws UnsupportedAudioFileException, IOException {
        AudioContext player = new AudioContext(1024);
        Sample buf = new Sample("audio/1234.aif");
        Gain gain = new Gain(player, 2);
        gain.start();
        gain.setGainEnvelope(new Envelope(player, 0.1f));
        for(int i = 0; i < 20; i++) {
            GranularSamplePlayer p = new GranularSamplePlayer(player, buf);
            p.getGrainSizeEnvelope().setValue(100);
            p.getGrainIntervalEnvelope().setValue(50);
            p.start();
            p.setRateEnvelope(new Envelope(player, 1f + (float)Math.random() * 0.2f));
            gain.addInput(0, p, 0);
            gain.addInput(1, p, 1);
        }
        player.setRoot(gain);
        player.start();
    }
    
    
        
    public static void complexFileGranular2() throws UnsupportedAudioFileException, IOException {
        AudioContext player = new AudioContext(1024);
        Sample buf = new Sample("audio/1234.aif");
        
        Gain gain = new Gain(player, 2);
        gain.start();
        gain.setGainEnvelope(new Envelope(player, 0.1f));
        
            GranularSamplePlayer p = new GranularSamplePlayer(player, buf);
            p.getGrainSizeEnvelope().setValue(100);
            p.getGrainIntervalEnvelope().setValue(50);
            
            p.setRateEnvelope(new Envelope(player, 0.5f));
            
            p.start();
            
            gain.addInput(0, p, 0);
            gain.addInput(1, p, 1);
    
        
        player.setRoot(gain);
        player.start();
    }
        
    public static void complexFile() throws UnsupportedAudioFileException, IOException {
        AudioContext player = new AudioContext(512);
        Sample buf = new Sample("audio/1234.aif");
        Gain gain = new Gain(player, 2);
        gain.start();
        gain.setGainEnvelope(new Envelope(player, 0.1f));
        for(int i = 0; i < 100; i++) {
            SamplePlayer p = new SamplePlayer(player, buf);
            p.start();
            p.setRateEnvelope(new Envelope(player, 1f + (float)Math.random() * 0.2f));
            gain.addInput(0, p, 0);
            gain.addInput(1, p, 1);
        }
        player.setRoot(gain);
        player.start();
    }
    
    public static void test() throws InterruptedException {
        
        Thread t = new Thread(){
            
        public void run() {
            int count = 0;
        while(true) {
            System.out.println(count++);
            try{ sleep((long)500); }catch(Exception e) {e.printStackTrace();}
        }
        }
        };
        t.start();
    }
    
    public static class SimpleSoundTrigger extends Bead {

        Clock clock;
        WavePlayer wp;
        
        public SimpleSoundTrigger(ScalingMixer sm, Clock clock) {
            wp = WavePlayer.newSinePlayer(sm.getContext(), 440f);
            wp.start();
            sm.addInput(0, wp, 0);
            this.clock = clock;
        }
        

        public boolean isDead() {
            return false;
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void delete() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isDeleted() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        
    }
    
    public static void gui() {
        
    }
    
    public static void simpleSine() {
        AudioContext player = new AudioContext(512);
        
        MonoToStereo mts = new MonoToStereo(player);
        Gain gain = new Gain(player, 1);
        
        for(int i = 0; i < 200; i++) {
            WavePlayer wp = WavePlayer.newSinePlayer(player, 440.0f + (float)Math.random() * 300.0f);
            wp.start();
            gain.addInput(wp);
        }
        
        ((Envelope)gain.getGainEnvelope()).addSegment(0.005f, 0.0f, 1.0f, null);
        
        gain.start();
        mts.addInput(gain);
        mts.start();
        player.setRoot(mts);
        player.start();
    }
    
    public static void sineBank() {
        
        AudioContext player = new AudioContext(1024);
        
        MonoToStereo mts = new MonoToStereo(player);
        
        OscillatorBank oscBank = new OscillatorBank(player, new SineBuffer(), 200);
               
        oscBank.start();
        
        mts.addInput(oscBank);
        mts.start();
        player.setRoot(mts);
        player.start();
    }
    
    public static void problem() {
        AudioContext player = new AudioContext();
        ScalingMixer sm = new ScalingMixer(player, 1);
        sm.start();
        MonoToStereo mts = new MonoToStereo(player);
        mts.addInput(sm);
        mts.start();

        for(int i = 0; i < 1; i++) {
            Clock clock = new Clock(player);
            clock.setIntervalEnvelope(new Envelope(player, 400.0f));
            SimpleSoundTrigger sst = new SimpleSoundTrigger(sm, clock);
            clock.addMessageListener(sst);
            clock.start();
        }
        
        player.setRoot(mts);
        player.start();
    }
    
}


