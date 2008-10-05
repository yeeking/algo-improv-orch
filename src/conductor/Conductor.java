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

  private  ArrayList<Improviser>  improvisers;
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
      // skip the first line..
      scan.nextLine();
      while (scan.hasNextLine()) {
	line = scan.nextLine();
	parts = line.split(",");
	message = parts[0];
	interval = new Integer(parts[1]).intValue();
	memLength = new Integer(parts[2]).intValue();
	System.out.println("Conductor - read message '"+message+"' from the config file.");
	if (memLength > 0) {
	  messengers.put("/"+message, new ConductorMessagerMemory(interval, "/"+message, new Object[]{new Integer(0)}, this, memLength));
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

    messageName = message.getName();
    // need to work out what the hostname is
    iSaddr = (InetSocketAddress)socketAddr;
    address = iSaddr.getHostName();
    System.out.println("Conductor - '"+message.getName()+"' message received from host "+address);
    // - is it a 'hello port' message?
    if (messageName.contains ("hello")) {
      if (message.getArgCount() > 0) {
	// they should send a number for argument 0
	port = ((Number) message.getArg( 0 )).intValue();
	addImproviser(address, port);
      }
    }
    
    // now try to look up the message in the messenger hashmap
    // - is it a melody message?
    messenger = messengers.get(messageName);
    if (messenger!=null) {
      messenger.setMessageData(oscArgsToObjects(message));
    }
  }

  private Object[] oscArgsToObjects(OSCMessage message){
    int argCount = message.getArgCount();
    Object[] args = new Object[argCount];
    
    for (int i=0;i<argCount;i++) {
      args[i] = message.getArg(i);
    }
    return args;
  }

  // called by the OSCServer thread to add a new improviser to the arraylist

  public synchronized void addImproviser(String address, int port){
    // first check if we already have this one...
    for (Improviser i : improvisers) {
      if (i.getPort() == port && i.getAddress().equals(address)) {
	return;
      }
    }
    // no? add it
    improvisers.add(new Improviser(address, port, this));    
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
