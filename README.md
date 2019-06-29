#### ADSNet

This Java track server listens on a TCP port 30003 for Basestation compatible data, and sends target reports over UDP Multicast and Unicast ports to local and remote listeners.

![My image](https://raw.githubusercontent.com/srsampson/ADSNet/master/sample.png)

I've been working with modesdeco2 with an RTL SDR and modesmixer2 with beast-splitter and a Mode-S Beast receiver.

I have a Raspberry PI in the rafters with Power Over Ethernet (POE), and it has a Mode-S Beast receiver plugged in. I'm using the ```beast-splitter``` program to read the USB serial port data. Then I use ```modesmixer2``` to convert that to Basestation Port 30003.
```
nohup sudo beast-splitter --serial /dev/beast --listen 30005:R &
nohup ./modesmixer2 --location 34.382901:-98.423287 --outServer msg:30003 --inConnect 127.0.0.1:30005&
```
If you don't want to use beast-splitter you can just use the serial option to modesmixer2:
```
nohup ./modesmixer2 --location 34.382:-98.423 --outServer msg:30003 --inSerial /dev/ttyUSB0:3000000 &
```
If I use an RTL SDR receiver (which doesn't work half as well as the Beast), you can use something like this:
```
nohup sudo ./modesdeco2 --location 34.382901:-98.423287 --msg 30003&
```
In this case, you don't need the ```beast-splitter``` or ```modesmixer2```.

The ADSNet then decodes the noisy data into target reports, and outputs them to the configurable Multicast address and UDP port. Also, you can optionally specify a Unicast address and UDP port, and send the data anywhere in the world, and also specify the data update rate. This was a main feature of the program, in which to share your data with others.

This program was originally designed for Java in 2010, and I have recently updated it for Oracle Java 11. If you find any bugs, or would like to suggest a better way, feel free to add an issue.

Included is ```export-adsnet.zip``` which is a project export from Netbeans. Just import it in and away you go...

##### Theory 
The Kinetic Basestation TCP port has a lot of redundant data that is not efficient across the Internet, or even across a local radio based LAN. It requires a lot of bandwidth. Back in the early days there was no good way to share data, so I came up with this port 30003 Basestation sharing application. Obviously things are different now, but there are some people still using this to share data privately, and not have to install huge amounts of software.

ADSNet is for lowering the data rate, and filtering the data so that it is more efficient over a radio network, or across the Internet. It also allows configured multiple hosts to receive the data. This reduced data flow can be used by multiple applications.

ADSNet sends ASCII data using UDP protocol in both Unicast mode, and Multicast mode. Unicast mode is normally sent to Internet or radio modems using port 30339. Multicast mode uses port 31090 on multicast address 239.192.10.90 only on your LAN. These addresses were pretty much picked at random.

##### ADSNet Overview
The Basestation TCP port has ASCII text output representing target detections in real time. The TCP port data is quite redundant, and would require a lot of bandwidth to transmit over the internet.

What is needed is a shim that groups this data into tracks, and then transmits tracks at a lower rate, with only changes to the tracks being sent. Also a heartbeat packet to say the transmitting site is still on the network, in the event no ADS-B targets are being processed (late at night). This heartbeat is transmitted every 30 seconds.

A local user may have multiple workstations or laptops on their network. It would be inconvenient and a waste of time and bandwidth to transmit all the tracks one by one to each workstation. Thus, a multicast port is used to broadcast the data once to all computers simultaneously.

The multicast data is sent as quickly as the tracks are updated.

The remote internet hosts of the data have to be specified in the configuration file. You can also specify the cycle time. For example, you can set the cycle time to 0 seconds (default), and the track data that has changed in the last second is transmitted over this unicast network. To simulate a 6 RPM antenna rotation, you might specify a 10 second cycle time. Then the remote users will get track updates every 10 seconds, thus lowering the data rate even further.

ADSNet only uses UDP protocol data format. This is a fire and forget broadcast data mode. The remote users don’t have to connect, nor do they have to acknowledge receipt. If they don’t get the data, or parts are corrupt, then the data is dropped until the next cycle. I haven't needed an error correction or CRC data in my testing with European remote users.
```
                      +----------+
(TCP Port 30003)----->| ADS Net  |<------->(UDP Ucast Port 30339 WAN)
                      |          |
                      |          |-------->(UDP Mcast Port 31090 Local or ZeroTier LAN)
 RAW ADS-B Data       |          |
                      |          |           Processed ADS-B Tracks
                      +----------+
```
The ```multicast.nicaddress``` line in the config file is needed only on computers that have multiple network interface cards (NIC), or multiple virtual networks. For example, you might have a wireless and a gigabit interface.

If more than one NIC is enabled, the software has no way of knowing which one you want to use. By specifying the IP address of the card you want to use, then it configures the multicast packets to go out via that NIC.

For example, if you have a Wireless card that you want to send multicast packets out, and it has IP address ```192.168.0.195```, and a gigabit network card on the same computer at IP address ```192.168.1.200```, then the configuration file should have the following entry:
```
multicast.nicaddress = 192.168.0.195
```
Which specifies that the multicast packets will go out the wireless network. Note, if you don’t specify the multicast port or the address, then it will default to IP address 239.192.10.90 and UDP Port 31090 (which both have 1090 in there, to help jog your memory).

If you don’t specify a ```multicast.nicaddress``` it will default to the first network in your stack. There is no way to shut it off. When in doubt, or problems develop, always set this parameter. It can’t hurt.

The unicast network lets you specify a list of comma separated hostnames or IP addresses to send the data to. It repeats the data to each host, one after the other. It also lets you specify the port and the cycle time.

The default UDP port is 30339, and the default cycle time is 0 seconds. That means the track table will collect data changes for 1 second, and then transmit all the tracks that had any item change. If nothing changed, the track data is not sent. (note, these hostnames and ip addresses are imaginary).
```
#unicast.address = site21.faa.gov:n,island33.faa.gov:y,44.78.32.118:n
#unicast.port = 30339
#unicast.seconds = 0
```
If you don’t specify a unicast.address then the unicast transmit port is not used. The receiver will still remain enabled however. Thus you can be receive only.

The “:y” and “:n” in the host names signify that you want to send all tracks (“:y”), or just local tracks (“:n”). This is useful if you have a friend who wants to see all your tracks that you have merged from other sites. This user will no doubt be receive only, because otherwise he would also receive their tracks in return.

If you don't want the GUI to display, you can switch it off in the ```adsnet.config``` file by setting ```gui.enable = false```. It is default true.

##### ADSNet Data output Format
Currently, the ADSNet program outputs two types of data: TRK and STA. The TRK is Track Data, and the STA is Station Data, or the Heartbeat. Each line is terminated with a CR, LF.

The TRK line has the following fields:
```
HomeID          A 64-bit random number to identify the transmit host
Timestamp       A SQL Timestamp containing UTC date and time
ICAO ID         The Mode-S ICAO ID
Callsign        The target callsign if entered by flight crew
Squawk          The Mode-3A squawk
VerticalRate    The vertical rate in 64 foot increments, - means descend
GroundTrack     The angle the target is heading relative to true north
GroundSpeed     The groundspeed in knots
Altitude        The altitude in feet (29.92 QNH) either 100 or 25 foot increments
Latitude        The degrees and fraction of degrees (+ is North)
Longitude       The degrees and fraction of degrees (+ is East)
Alert           The Alert Boolean bit (means Mode-3A change or other alert) -1 = true
Emergency       The Emergency Boolean, means the crew has declared an emergency -1 = true
SPI             The SPI Boolean, means the crew has pressed the Ident button -1 = true
OnGround        The OnGround Boolean, means the aircraft is on the ground -1 = true
TrackQuality    The Track quality 0 to 9, with 9 being best.
```
The STA heartbeat has the following fields sent every 30 seconds:
```
HomeID          A 64-bit random number to identify the transmit host
Timestamp       A SQL Timestamp containing UTC date and time
HomeName        A text string identifying the transmit site
HomeLatitude    The site latitude in degrees and fractions of degree (+ is north)
HomeLongitude   The site longitude in degrees and fractions of degree (+ is east)
```
##### Router Config
There are many routers on the market, and it would be hard to give out procedures, but most have a feature called port forwarding, or virtual server. You must adjust your router to open up the firewall port for Unicast UDP Port 30339.

Then you must configure the IP address where your ADSNet program is running. This will allow UDP packets to arrive on the WAN side of your router and pass unmolested to the LAN side and a particular machine. Also check if your router has a switch to enable multicast. If you are using ZeroTier Multicast, you don't need any port forwarding. The world is your LAN.

If you are using a Public WiFi or a subsidized cost network in your home, then it might have UDP networking restricted. Many free or low cost networks drop this network data to prevent users from running servers.
