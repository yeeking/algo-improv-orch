///////////////////////////////////////////////////////////////////////////////
// Conductor.java							     //
// 									     //
// Copyright (c) 2008 Strange Loop. All rights reserved.		     //
//  									     //
//  									     //
//   This program is free software: you can redistribute it and/or modify    //
//   it under the terms of the GNU General Public License as published by    //
//   the Free Software Foundation, either version 3 of the License, or	     //
//   (at your option) any later version.				     //
//     									     //
//     This program is distributed in the hope that it will be useful,	     //
//     but WITHOUT ANY WARRANTY; without even the implied warranty of	     //
//     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the	     //
//     GNU General Public License for more details.			     //
//     									     //
//     You should have received a copy of the GNU General Public License     //
//     along with this program.  If not, see <http://www.gnu.org/licenses/>. //
///////////////////////////////////////////////////////////////////////////////
				
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.io.File;

import de.sciss.net.*;

public class Conductor {

  // receives messages from clients
  //public Runnable clientReceiver;
  // sends message to clients
  //public Runnable clientSender;

  private ArrayList<Improviser>  improvisers;
  private String[] messages;
  private boolean debug = true;

  private ConductorServer server;
  private Thread serverThread;
  private HashMap<String,ConductorMessager> messengers;

  public Conductor(int port, boolean loopbackMode){
    improvisers = new ArrayList<Improviser>();
    // fire up the server for receiving messages
    server = new ConductorServer(port, this, loopbackMode);
    serverThread = new Thread(server);
    serverThread.start();
    // fire up the conductor messagers, which will send messages to improvisers
    messengers = new HashMap<String,ConductorMessager>();

    // read the config file
    try {
      Scanner scan = new Scanner(new File("mcp.config"));
      String line;
      String[] parts;
      String message;
      int interval;
      int memLength;
      int sendLength;
      String sendMode;
      // skip the first line..
      scan.nextLine();
      while (scan.hasNextLine()) {
	line = scan.nextLine();
	parts = line.split(",");
	message = parts[0];
	interval = new Integer(parts[1]).intValue();
	memLength = new Integer(parts[2]).intValue();
	sendLength = new Integer(parts[3]).intValue();
	sendMode = parts[4];

	System.out.println("Conductor - read message '"+message+"' from the config file.");
	if (memLength > 0) {
	  messengers.put("/"+message, new ConductorMessagerMemory(interval, "/"+message, memLength, sendLength, sendMode, new Object[]{new Integer(0)}, this));
	}
	else {
	  messengers.put("/"+message, new ConductorMessager(interval, "/"+message, new Object[]{new Integer(interval)}, this));
	}
      }

    } catch (Exception e) {
      System.out.println("Conductor - an error occurred reading the config file mcp.config - does it exist?");
      e.printStackTrace();
    }

  }
  
  // this gets called by the server from a seperate thread
  public  void messageReceived(OSCMessage message, SocketAddress socketAddr, long time){
    // this gets called when a client sends us a message

    int port;
    String address;
    InetSocketAddress iSaddr;
    ConductorMessager messenger;
    String messageName;
    Improviser improviser; 

    try {
      messageName = message.getName();
      // need to work out what the hostname is
      iSaddr = (InetSocketAddress)socketAddr;
      address = iSaddr.getHostName();
      System.out.println("Conductor - '"+message.getName()+"' message received from host "+address);
      // we need at least one arg as the first arg is always the port
      // number on which the client is listening, which allows us to
      // identify which client it is
      if (message.getArgCount() > 0) {
	port = ((Number) message.getArg( 0 )).intValue();
	// - is it a 'hello port' message?
	if (messageName.contains ("hello")) {
	  addImproviser(address, port);
	}
	else {
	  // see if we know about this message
	  messenger = messengers.get(messageName);
	  if (messenger!=null) {
	    // we know this message
	    improviser = getImproviser(address, port);
	    if (improviser!=null && improviser.canSendMessages()) {
	      // we have an improviser registered for this message
	      // and they have not been blocked (e.g. by sending too many messages...)
	      improviser.addToMessageCounts(message.getName());
	      messenger.setMessageData(oscArgsToObjects(message));
	    }
	  }  
	}
      }
    } catch (Exception e) {
      // this probably means there was something wrong with the
      // message, so we just avoid the puke and do nothing
    }
  }

  private Object[] oscArgsToObjects(OSCMessage message){
    int argCount = message.getArgCount();
    // note that we skip the first arg, which is the port of the sending improviser
    Object[] args = new Object[argCount-1];
    
    for (int i=0;i<argCount-1;i++) {
      args[i] = message.getArg(i+1);
    }
    return args;
  }

  private Improviser getImproviser(String address, int port){
    for (Improviser i : improvisers) {
      if (i.getPort() == port && i.getAddress().equals(address)) {
	return i;
      }
    }
    return null;
  }

  // called by the OSCServer thread to add a new improviser to the arraylist

  public synchronized void addImproviser(String address, int port){
    // only add if we don't have it already
    if (getImproviser(address, port) == null) {
      improvisers.add(new Improviser(address, port, this));    
    }

  }

  // sends a message to all clients

  public void broadcastMessage(OSCMessage message){
    boolean test;
    // for each improviser, create a new threa to send the message to
    // that improviser.  the thread shoould be passed a reference to
    // the improviser. If the improviser is dead then the thread should set it to alive = false;
    for (Improviser i : improvisers) {
      if (!i.isDead()) {
	System.out.println("Conductor: Sending message "+message.getName()+" to "+i.toString());
	test = i.sendPacket(message);
	// special behaviour for 'change' - resets the counters
	if (message.getName().equals("/change")) {
	  i.resetMessageCounts();
	}

	if (test) {
	  System.out.println("Conductor: message sending succeeded");
	}
	else {
	  System.out.println("Conductor: message sending failed - removing client! ");
	  // flag it for removal
	  i.setDead();
	}
      }
    }
    // killing them kills the messagesending thread for some reason ....
    // commenting this out for now.
//     for (Improviser i : improvisers) {
//       if (i.isDead()) {
// 	improvisers.remove(i);
//       }
//     }
    // now 
  }


  public static void usage(){
    System.out.println("Usage:\n java Conductor <port> <loopback|network> \n\n port is the port you want it to listen on. \n loopback will only respond to localhost and network will only respond to the network \n (you'll need to connect using a proper IP address as well)");
    System.exit(0);
  }
  
  public static void main(String[] args){
    int port;
    boolean loopbackMode = true;
    
    if (args.length == 0) {
      System.out.println("You did not specify a port");
      usage();
    }
    if (args.length == 1) {
      loopbackMode = true;
    }
    if (args.length == 2) {
      if (args[1].equals("network")) {

	loopbackMode = false;
      }
      else {
	loopbackMode = true;
      }
    }

    try {
      if (!loopbackMode) {
	System.out.println("Conductor: you selected network mode. I am reading your hostname/IP address as: "+InetAddress.getLocalHost()+"\nYou'll need to connect to that IP from remote machines.  ");
      }
      port = new Integer(args[0]).intValue();
      Conductor c = new Conductor(port,loopbackMode);
      do {
	try {
	  Thread.sleep(1000);
	} 
	catch (Exception e) {
	}
	
      }while (true);


      //c.broadcastMessage(new OSCMessage( "/s_new", new Object[] { "default", -1}));
      
    } catch (Exception e) {
      
      e.printStackTrace();
      System.out.println("An error occurred starting up the Conductor...");
      usage();
    }


  }

}
