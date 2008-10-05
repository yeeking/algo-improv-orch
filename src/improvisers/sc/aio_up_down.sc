// slightly more complex example 
// - speed up and slow dow the time
// - play a synth on the clock

// create a simple synth
SynthDef("ping", {
  Out.ar(0, 
	WhiteNoise.ar(
	  mul:EnvGen.kr(Env.perc(0.001, 0.1), doneAction:2, levelScale:0.05)
	));
}).send(s);


var mode = 1;
// create a responder for clock messages from the conductor
~clock = OSCresponderNode(nil, '/clock', {arg time, responder, msg;
  var interval;
  interval = msg[1];
  if (mode == 1, {interval = interval - 50}, {interval = interval + 50});
  if (interval < 0, {mode = 0});
  if (interval > 1000, {mode = 1});
  interval.postln;
  ~conductor.sendMsg("/clock", interval);
  Synth("ping");
}).add;

// stop responding
~clock.remove;
