// melody example
// - writes notes from c major to the server
// - reads notes back from the server and plays chords with them

// initialise the frequency
~freqs = [48.midicps];

// create a simple synth
SynthDef("sine", {arg freq;
  Out.ar(0, 
	SinOsc.ar(
	  freq:freq, 
	  mul:EnvGen.kr(Env.perc(0.2, 2.0), doneAction:2, levelScale:0.1)
	));
}).send(s);

// create a responder for clock messages from the conductor
~clock2 = OSCresponderNode(nil, '/clock', {arg time, responder, msg;
  // play up to 3 notes using the freqs read from the conductor
  (3.rand + 1).do{
	Synth("sine", [\freq, ~freqs.choose]);
  };
}).add;


// send notes into the conductor using a routine
// c major ... note how the first value we send it our port
// number. This allows the conductor to keep track of who is sending
// what
~send_notes = {inf.do{
  ~conductor.sendMsg("/melody", 57120, ([48, 50, 52, 53, 55, 57].choose.midicps)*([1, 2, 4].choose));
  0.5.wait;
}}.fork;

// read notes back out from the conductor (/melody) and store them to ~freqs
~melody_rec = OSCresponderNode(nil, '/melody', {arg time, responder, msg;
  // skip the fist element of the msg, which is the command /melody
  ~freqs = msg.copyRange(1, msg.size-1);
}).add;

// stop responding
~clock2.remove;
~melody_rec.remove;
~send_notes.stop;