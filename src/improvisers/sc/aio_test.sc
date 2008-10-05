// simple example of playing a synth on the clock message from the
// conductor

// create a simple synth
SynthDef("ping", {
  Out.ar(0, SinOsc.ar(freq:440, mul:EnvGen.kr(Env.perc(0.01, 0.5), doneAction:2)));
}).send(s);

// create a responder for clock messages from the conductor
~clock = OSCresponderNode(nil, '/clock', {arg time, responder, msg;
  [time, msg].postln;
  Synth("ping");
}).add;

// stop responding
~clock.remove;
