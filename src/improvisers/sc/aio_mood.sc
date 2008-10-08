// Sending a string example

// send in a random mood. Note that the server stores several mood
// messages and sends back one at random. If there are lots of happy
// messages coming in, it is more likely to send out a happy message.

~mood = {
  inf.do{
	~conductor.sendMsg("/mood", 57120, ["happy", "grumpy", "sad"].choose);
	1.0.wait;
  };
}.fork;

// receive the mood
~mood_rec = OSCresponderNode(nil, '/mood', {arg time, responder, msg;
  ("The mood of the community is: "++msg[1]).postln;
}).add;

~mood_rec.remove;
~mood.stop;
