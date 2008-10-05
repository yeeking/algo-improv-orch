///////////////////////////////////////////////////////////////////////////////
// ConductorMessagerMemory.java							     //
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

// extended ConductorMessenger which adds support for memory, i.e. it
// remembers previously set messageData. Useful for melody messenging
// where you want to keep track of recent note events

public class ConductorMessagerMemory extends ConductorMessager{
  // how many values to store in total
  private int memLength;
  // how many values to send out each time?
  private int sendLength;
  // if true, send values randomly from the memory 
  // otherwiser, send the oldest 'sendLength' values
  private boolean sendRandom;

  public ConductorMessagerMemory(int interval, 
				 String messageType, 
				 int memLength, 
				 int sendLength, 
				 String sendMode, 
				 Object[] messageData, 
				 Conductor conductor){
    
    super (interval, messageType, messageData, conductor);
    this.memLength = memLength;
    this.sendLength = sendLength;
    if (sendMode.equals("random")) {
      sendRandom = true;
    }
    else {
      sendRandom = false;
    }


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
    oldData = super.getMessageData();

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

  @Override
  public Object[] getMessageData(){
    Object[] tempData = new Object[sendLength];
    Object[] data = super.getMessageData();
    if (sendRandom) {
      // read randomly from the array
      for (int i=0;i<sendLength;i++) {
	tempData[i] = data[getRandomInt(0, memLength)];
      }
    }
    else {
      // read from the beginning of the array (oldest values)
      for (int i=0;i<sendLength;i++) {
	tempData[i] = data[i];
      }
    }

//     // print it!
//     String dataS = "";
//     for (int i=0;i<tempData.length;i++) {
//       dataS+= tempData[i].toString()+"-";
//     }

//     System.out.println("ConductorMessagerMemory: that shit being... "+dataS);


    return tempData;
  }

  private int getRandomInt(int min, int max){
    return ((int)(Math.random() * max) + min);
  }

}
