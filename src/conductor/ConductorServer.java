import de.sciss.net.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketAddress;

// simple threaded osc server implementation

public class ConductorServer implements Runnable{
  
  private final Conductor conductor;
  private final Object sync;
  private OSCServer server;
  private boolean keepRunning;

  public ConductorServer(int port, Conductor conductorIn, boolean loopbackMode){
    this.conductor = conductorIn;
    sync = new Object();
    //System.out.println("ConductorServer - getting ready to listen on port "+port);
    // create a responder server in a new thread
    try {
      // create a UDP server
      server = OSCServer.newUsing( OSCServer.UDP, port, loopbackMode);
      // now add a listener for incoming messages from
      // any of the active connections
      server.addOSCListener( new OSCListener() {
	  public void messageReceived( OSCMessage m, SocketAddress addr, long time )
	  {
	    System.out.println("ConductorServer - received a message!"+m.getName());
	    // pass it on up...
	    conductor.messageReceived(m, addr, time);
	  }
	});
    }
    catch( IOException e1 ) {

      e1.printStackTrace();
      return;
    }
  }

  public void run(){
    //System.out.println("ConductorServer - run called by thread. Listening in the background");
    try {
      // now make the server start to listen
      server.start();
    }
    catch( IOException e1 ) {
      System.out.println("ConductorServer - server failed to start - exiting.");
      System.exit(0);
      e1.printStackTrace();
      return;
    }
  }

  public void isActive(){
    if (server.isActive()) {
      System.out.println("ConductorServer - server seems to be active");
    }
    else {
      System.out.println("ConductorServer - server is NOT active");
    }

  }
  
  public void dispose(){
    // kill the server, free its resources
    server.dispose();
  }

}