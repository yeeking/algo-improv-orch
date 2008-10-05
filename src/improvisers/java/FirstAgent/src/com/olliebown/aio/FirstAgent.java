package com.olliebown.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import com.olliebown.beads.core.AudioContext;
import com.olliebown.beads.core.Tools;
import com.olliebown.beads.data.SineBuffer;
import com.olliebown.beads.envelopes.Envelope;
import com.olliebown.beads.events.StopTrigger;
import com.olliebown.beads.ugens.Gain;
import com.olliebown.beads.ugens.WavePlayer;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCServer;


public class FirstAgent extends Gain implements OSCListener {

	OSCServer server;
	int port;
	InetSocketAddress conductorAddress;
	
	public FirstAgent(AudioContext context, int myPort, String conductorHost, int conductorPort) {
		super(context, 2);
		this.port = myPort;
		conductorAddress = new InetSocketAddress(conductorHost, conductorPort);
		establishConnection();
	}

	int messageCount = 0;
	
	public void messageReceived(OSCMessage msg, SocketAddress sad, long port) {
		if(msg.getName().equals("/clock")) {
			WavePlayer wp = new WavePlayer(context, (float)Math.random() * 5000f + 1000f, new SineBuffer());
			Envelope gainEnv = new Envelope(context, 1f);
			Gain g = new Gain(context, 2, gainEnv);
			gainEnv.addSegment(0f, 500f, new StopTrigger(g));
			g.addInput(wp);
			addInput(g);
			System.out.println(messageCount++);
		}
	}
	
	public void establishConnection() {
		try {
			server = OSCServer.newUsing(OSCServer.UDP, port);
			server.start();
			server.send(new OSCMessage("/hello", new Integer[] {port}), conductorAddress);
			server.addOSCListener(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Tools.init();
		try {
			FirstAgent fa = new FirstAgent(Tools.ac, Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
			Tools.ac.getRoot().addInput(fa);
		} catch(NumberFormatException nfe) {
			System.out.println("Usage: FirstAgent <agentPort> <conductorHostName> <conductorPort>");
		}
		Tools.ac.start();
	}
	
}
