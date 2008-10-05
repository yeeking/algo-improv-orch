// slightly more complex example 
// - speed up and slow dow the time
// - play a synth on the clock

var mode = 1;

// create a simple synth
SynthDef("ping", {
  Out.ar(0, 
	WhiteNoise.ar(
	  mul:EnvGen.kr(Env.perc(0.001, 0.1), doneAction:2, levelScale:0.05)
	));
}).send(s);

// create a responder for clock messages from the conductor
~clock = OSCresponderNode(nil, '/clock', {arg time, responder, msg;
  var interval;
  interval = msg[1];
  if (mode == 1, {interval = interval - 50}, {interval = interval + 50});
  if (interval < 0, {mode = 0});
  if (interval > 1000, {mode = 1});
  interval.postln;
  // ... note how the first value we send it our port number. This
  // allows the conductor to keep track of who is sending what
  ~conductor.sendMsg("/clock", 57120, interval);
  Synth("ping");
}).add;

// stop responding
~clock.remove;
