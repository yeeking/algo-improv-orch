import de.sciss.net.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketAddress;

// Represents an improvising agent. 
// Allows messages to be sent to improvising agents.
// Created when an agent sends a hello message to the server

public class Improviser{

  private Conductor conductor;
  private OSCClient client;
  private final Object sync;
  private OSCBundle bndl1;
  private OSCBundle bndl2;
  private Integer nodeID;

  private String address;
  private int port;
  private long signInTime;
  private boolean dead;

  // create a representation for a remote improviser sitting on the sent address and port
  // 
  public Improviser(String address, int port, Conductor conductor){
    System.out.println("Improviser:construct address:"+address+" port:"+port);
    this.conductor = conductor;
    this.address = address;
    this.port = port;
    
    sync = new Object();
    client = null;
    try {
        client = OSCClient.newUsing( OSCClient.UDP );    // create UDP client with any free port number
        client.setTarget( new InetSocketAddress( address, port ));  // talk to scsynth on the same machine
        client.start();  // open channel and (in the case of TCP) connect, then start listening for replies
    }
    catch( IOException e1 ) {
        e1.printStackTrace();
        return;
    }

    
    // register a listener for incoming osc messages
    client.addOSCListener( new OSCListener() {
        public void messageReceived( OSCMessage m, SocketAddress addr, long time )
        {
	  System.out.println("Improviser: message received: "+m.getName()+" time "+time);
	  // if we get the /n_end message, wake up the main thread 
	  // ; note: we should better also check for the node ID to make sure
	  // the message corresponds to our synth
	  if( m.getName().equals( "/n_end" )) {
	    synchronized( sync ) {
	      sync.notifyAll();
	    }
	  }
        }
      });
    dead = false;
    //testClient();
  }

  public int getPort(){
    return port;
  }
  public String getAddress(){
    return address;
  }

  public void setDead(){
    this.dead = true;
  }
  public boolean  isDead(){
    return this.dead;
  }
  
  public boolean sendPacket(OSCPacket packet){
    try {
      client.send(packet);
      return true;
    }
    catch( IOException e11 ) {
      e11.printStackTrace();
      return false;
    }
  }

  public void testClient(){
       // let's see what's going out and coming in
    client.dumpOSC( OSCChannel.kDumpBoth, System.err );

    try {
        // the /notify message tells scsynth to send info messages back to us
        client.send( new OSCMessage( "/notify", new Object[] { new Integer( 1 )}));
        // two bundles, one immediately (with 50ms delay), the other in 1.5 seconds
        bndl1   = new OSCBundle( System.currentTimeMillis() + 50 );
        bndl2   = new OSCBundle( System.currentTimeMillis() + 1550 );
        // this is going to be the node ID of our synth
        nodeID  = new Integer( 1001);
        // this next messages creates the synth
        bndl1.addPacket( new OSCMessage( "/s_new", new Object[] { "default", nodeID, new Integer( 1 ), new Integer( 0 )}));
        // this next messages starts to releases the synth in 1.5 seconds (release time is 2 seconds)
        bndl2.addPacket( new OSCMessage( "/n_set", new Object[] { nodeID, "gate", new Float( -(2f + 1f) )}));
        // send both bundles (scsynth handles their respective timetags)
        client.send( bndl1 );
        client.send( bndl2 );

        // now wait for the signal from our osc listener (or timeout in 10 seconds)
	try {
	  synchronized( sync ) {
            sync.wait( 10000 );
	  }
	} catch (InterruptedException e) {

	}
        //catch( InterruptedException e1 ) {}

        // ok, unsubscribe getting info messages
        client.send( new OSCMessage( "/notify", new Object[] { new Integer( 0 )}));

        // ok, stop the client
        // ; this isn't really necessary as we call dispose soon
        client.stop();
    }
    catch( IOException e11 ) {
        e11.printStackTrace();
    }

  }
  
  public String toString(){
    String status = "Improviser: address: "+address+" port: "+port;
    return status;
  }
    
}