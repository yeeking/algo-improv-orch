package com.olliebown.beads.trash;

import java.applet.Applet;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.ugens.WavePlayer;
import com.olliebown.beads.ugens.Gain;

public class TestApplet extends Applet implements ChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JFrame frame;
	JSlider volumeSlider;
	JSlider freqSlider;
	WavePlayer wp;
	Gain gain;

	public void init() {
		AudioContext ac = new AudioContext(512, 4000);
		gain = new Gain(ac, 1, new Envelope(ac, 0.0f));
		wp = new WavePlayer(ac, new Envelope(ac, 0.0f), new SineBuffer());
		gain.addInput(wp);
		ac.getRoot().addInput(gain);
		ac.start();
		frame = new JFrame();
		JPanel volumeSliderPanel = new JPanel();
		volumeSliderPanel.setLayout(new BoxLayout(volumeSliderPanel, BoxLayout.Y_AXIS));
		volumeSlider = new JSlider(new DefaultBoundedRangeModel());
		volumeSlider.addChangeListener(this);
		volumeSlider.setOrientation(JSlider.VERTICAL);
		volumeSliderPanel.add(volumeSlider);
		JLabel volumeLabel = new JLabel("Gain");
		volumeLabel.setAlignmentX(CENTER_ALIGNMENT);
		volumeSliderPanel.add(volumeLabel);
		JPanel freqSliderPanel = new JPanel();
		freqSliderPanel.setLayout(new BoxLayout(freqSliderPanel, BoxLayout.Y_AXIS));
		freqSlider = new JSlider(new DefaultBoundedRangeModel());
		freqSlider.addChangeListener(this);
		freqSlider.setOrientation(JSlider.VERTICAL);
		freqSliderPanel.add(freqSlider);
		JLabel freqLabel = new JLabel("Freq");
		freqLabel.setAlignmentX(CENTER_ALIGNMENT);
		freqSliderPanel.add(freqLabel);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createRigidArea(new Dimension(20,10)));
		panel.add(volumeSliderPanel);
		panel.add(Box.createRigidArea(new Dimension(10,10)));
		panel.add(freqSliderPanel);
		panel.add(Box.createRigidArea(new Dimension(20,10)));
		panel.setBorder(new TitledBorder("One oscillator"));
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if(source == volumeSlider) {
			float value = (float)volumeSlider.getValue() / 100.0f;
			((Envelope)gain.getGainEnvelope()).clear();
			((Envelope)gain.getGainEnvelope()).addSegment(value, 50.0f);
		} else if(source == freqSlider) {
			float value = (float)freqSlider.getValue() * 100.0f;
			((Envelope)wp.getFrequencyEnvelope()).clear();
			((Envelope)wp.getFrequencyEnvelope()).addSegment(value, 50.0f);
			
		} 
		
	}
	
	
}
