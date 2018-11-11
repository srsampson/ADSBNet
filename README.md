#### ADSNet
ADS-B Java Server for LAN Multicast and WAN Unicast

This server listens on a TCP port for Basestation compatible data (127.0.0.1 Port 30003 by default). I've
been working with modesdeco2 with an RTL SDR and modesmixer2 with beast-splitter and a Mode-S Beast receiver.

It then decodes the data into target reports and outputs them to the settable Multicast UDP address and port.
Then you can use a display program to plot the data.

Also, you can specify a Unicast UDP address and port, and send the data anywhere in the world, and also
specify the data update rate.

This was originally designed for Java in 2010, and I am updating it for Java 11.

If you find any bugs, or would like to suggest a better way, feel free to add an issue.
