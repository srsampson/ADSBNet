##### Raspberry PI Version
Unfortunately Java 8 is the latest version available for the ARM 32-bit CPU.

So I added this version for running it on a Raspberry Pi and doing the multicast from there.

Unfortunately also, the PI is totally gimped as far as X11 forwarding is concerned, but if you can figure it out
then you can forward the GUI display to your workstation.

The GUI shows the status of your Unicast shares, and the Basestation Port data. I guess I should make this optional.

![My image](https://raw.githubusercontent.com/srsampson/ADSNet/master/sample.png)
