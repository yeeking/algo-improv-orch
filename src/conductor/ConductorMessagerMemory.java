// extended ConductorMessenger which adds support for memory, i.e. it
// remembers previously set messageData. Useful for melody messenging
// where you want to keep track of recent note events

public class ConductorMessagerMemory extends ConductorMessager{

  private int memLength;
  
  public ConductorMessagerMemory(int interval, 
				 String messageType, 
				 Object[] messageData, 
				 Conductor conductor, 
				 int memLength){
    
    super (interval, messageType, messageData, conductor);
    this.memLength = memLength;
    // create a blank array for the memory to avoid complex resizing code later
    Object[] data = new Object[memLength];
    for (int i=0;i<memLength;i++) {
      data[i] = new Integer(0);
    }
    super.setMessageData(data);
    //System.out.println("ConductorMessagerMemory - construct - data length is "+getMessageData().length);
  }

  @Override
  public void setMessageData(Object[] newData){
    int offset, addFromNew;
    Object[] data, oldData;
    offset = memLength - newData.length;
    if (offset == 0) {
      // we're adding all of the data from the new array
      super.setMessageData(newData);
      return;
    }
    if (offset < 0) {
      // the new array is bigger than our old one
      data = new Object[memLength];
      for (int i=0;i<memLength;i++) {
	data[i] = newData[i];
      }
      super.setMessageData(data);
      return;
    }
    
    // if we get here, need to keep some of the old data
    data = new Object[memLength];
    oldData = getMessageData();

    // FIRST - shunt along the old data
    // copy from length - offset -> length
    // to 0 -> offset
    
    System.arraycopy(oldData, memLength - offset, data, 0, offset);
    
    // SECOND - copy the new data in at the end
    System.arraycopy(newData, 0, data, offset, memLength-offset);
      
//     // print it!
//     String dataS = "";
//     for (int i=0;i<data.length;i++) {
//       dataS+= data[i].toString()+"-";
//     }

//     System.out.println("ConductorMessagerMemory: "+dataS);

    super.setMessageData(data);
  }

}