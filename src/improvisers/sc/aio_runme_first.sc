// This code enables SCLANG to receive messages from the conductor. 
// run this before trying to set up any OSCresponders

// need to quit the server fist to allow osc to be sent to us 
s.quit;

// connect to the conductor (change localhost to the IP address if
// conductor is in network mode)
~conductor = NetAddr("localhost", 8080); 
// request messages to be sent to us on port 57120 (the langauge port)
~conductor.sendMsg("/hello",  57120);

// start up the server
s = Server.local.boot;
