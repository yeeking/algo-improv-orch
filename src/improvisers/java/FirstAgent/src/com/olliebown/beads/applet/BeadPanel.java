package com.olliebown.beads.applet;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.WavePlayer;


public class BeadPanel extends JPanel {

	public BeadPanel(String name, int orientation) {
		setLayout(new BoxLayout(this, orientation));
		setBorder(new TitledBorder(name));
	}
	
	public void addSlider(String name, int max, Envelope env) {
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		JSlider slider = new JSlider(0, max);
		slider.setOrientation(JSlider.VERTICAL);
		JLabel label = new JLabel(name);
		sliderPanel.add(slider);
		sliderPanel.add(label);
		EnvelopeChangeListener listener = new EnvelopeChangeListener(env);
		slider.addChangeListener(listener);
		slider.setValue(0);
		add(sliderPanel);
	}
	
	private class EnvelopeChangeListener implements ChangeListener {
		
		Envelope env;
		
		public EnvelopeChangeListener(Envelope env) {
			this.env = env;
		}
		
		public void stateChanged(ChangeEvent e) {
			env.clear();
			float value = (float)((JSlider)e.getSource()).getValue() / 100.0f;
			env.addSegment(value, 50.0f);
		}
	}
	
	public static void main(String[] args) {
		AudioContext ac = new AudioContext(512, 2000);
		PopUpFrame frame = new PopUpFrame(ac);
		
		for(int i = 0; i < 10; i++) {
			BeadPanel panel = new BeadPanel("osc" + (i + 1), BoxLayout.X_AXIS);
			WavePlayer wp = new WavePlayer(ac, 0.0f, new SineBuffer());
			Gain gain = new Gain(ac, 1, new Envelope(ac, 0.01f));
			gain.addInput(wp);
			ac.getRoot().addInput(gain);
			panel.addSlider("freq", 1000000, (Envelope)wp.getFrequencyEnvelope());
			frame.getContentPane().add(panel);
		}
		
		frame.launch();
		ac.start();
	}

}
