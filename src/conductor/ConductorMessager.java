import de.sciss.net.OSCMessage;
// This class runs in its own thread and is responsible for telling
// the Conductor to broadcast messages.  There will be one of these
// objects for each message type, which allows them to run with
// independent timing.
public class ConductorMessager implements Runnable{

  private Conductor conductor;
  // the nane of the message to place before the data
  private String messageType;
  // the data to send - could be an array of notes etc
  private Object[] messageData;
  // how to wait between sending of messages
  private int interval;

  public ConductorMessager(int interval, String messageType, Object[] messageData, Conductor conductor){
    this.interval = interval;
    this.messageType = messageType;
    this.messageData = messageData;
    this.conductor = conductor;
    // start in a new thread
    new Thread(this).start();
  }

  public void setMessageData(Object[] messageData){
//     String data = "";
//     for (int i=0;i<messageData.length;i++) {
//       data+= messageData[i].toString()+"-";
//     }

//     System.out.println("ConductorMessager: "+data);
    this.messageData = messageData;
    // simple interval based message data
    if (messageData.length == 1) {
      int newInterval = ((Number) messageData[0]).intValue();
      // don't let it go crazy...
      if (newInterval > 10) {
	interval = newInterval;
      }
    }
  }

  public Object[] getMessageData(){
    return messageData;
  }


  // gets called when this thread is created and .started
  public void run(){
    try {
      do{
	//System.out.println("ConductorMessager: sending '"+messageType+"'");
	//messageData = new Object[] {new Integer((int)System.currentTimeMillis())};
	conductor.broadcastMessage(new OSCMessage(messageType, getMessageData()));
	Thread.sleep(interval);
      }while (true);

    } catch (Exception e) {
      System.out.println("ConductorMessager responsible for message '"+messageType+"' fell over");
    }
  }
}