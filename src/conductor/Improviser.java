///////////////////////////////////////////////////////////////////////////////
// Improviser.java							     //
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


import de.sciss.net.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

/**
 * Represents an improvising agent. 
 * Allows messages to be sent to improvising agents.
 * Created when an agent sends a hello message to the server
 *
 * @author <a href="mailto:matthew@hyde">Matthew Yee-King</a>
 * @version 1.0
 */
public class Improviser{

  private Conductor conductor;
  private OSCClient client;

  private String address;
  private int port;
  private long signInTime;
  private boolean dead;

  private HashMap<String,Integer> messageCounts;

  /**
   * create a representation for a remote improviser sitting on the sent address and port
   *
   * @param address a <code>String</code> value
   * @param port an <code>int</code> value
   * @param conductor a <code>Conductor</code> value
   */
  public Improviser(String address, int port, Conductor conductor){
    System.out.println("Improviser:construct address:"+address+" port:"+port);
    this.conductor = conductor;
    this.address = address;
    this.port = port;
    
    this.resetMessageCounts();

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
    dead = false;
  }

  /**
   * This method is used to decide if messages from this improviser
   * should be processed by the conductor. 
   *
   */
  public boolean canSendMessages(){
    // algorithm for this TBC....
    // for now, look for a count over 100..
    for (Integer i : messageCounts.values()) {
      if (i.intValue() > 100) {
	System.out.println("Improviser: not allowing message as count > 100");
	return false;
      }
    }
    return true;
  }

  /**
   * Tests if this improviser has sent too many of the sent type of
   * message in this cycle according to the thresholds defined in the
   * config file.
   *
   * @param message a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public boolean canSendMessage(String message){
    int count, allowed;
    Integer countI;
    countI = messageCounts.get(message);
    if (countI == null) {
      return true;
    }
    else {
      count = countI.intValue();
      allowed = (conductor.getThresholds().get(message)).intValue();
      //System.out.println("Improviser: "+message+" count is "+count+" of allowed "+allowed);
      if (count > allowed) {
	return false;
      }
    }
    return true;
  }


  /**
   * When a message is received by the conductor from this improviser,
   * this method is called to keep a count of the messages being sent
   * by this improviser, with an aim to ignoring really 'message
   * noisy' improvisers.
   *
   * @param message a <code>String</code> which is the name of the message, e.g. /clock
   */
  public synchronized void addToMessageCounts(String message, int length){
    Integer count, c;
    
    count = messageCounts.get(message);
    if (count == null) {
      count = new Integer(length);
    }
    else {
      c = count.intValue();
      count = new Integer(c+length);
    }
    //System.out.println("Improviser: message "+message+" has count "+count.intValue());
    messageCounts.put(message, count);
  }

  public synchronized void resetMessageCounts(){
    messageCounts = new HashMap<String,Integer>();
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
  
  public String toString(){
    String status = "Improviser: address: "+address+" port: "+port;
    return status;
  }
   
 
}