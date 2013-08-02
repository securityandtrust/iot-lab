#include <SPI.h>
#include <Ethernet.h>
#include <EthernetUdp.h>
#include <EEPROM.h>

#define bit9600Delay 104 
#define halfBit9600Delay 52

//Mac of the Board
byte mac[] = {0x90, 0xA2, 0xDA, 0x0D, 0x94, 0x7A};
//Local UDP port
unsigned int localPort = 2020;

//Pin of the Card Reader
byte rx = 6;
//Pin of the door lock
int doorPin = 2;
//Card frame buffer
boolean frame[8*14];
int currentBit= 0;


EthernetClient client;
EthernetUDP Udp;
IPAddress remoteIp;
int remotePort;

void setup() {
  
 pinMode(rx,INPUT);
  pinMode(doorPin,OUTPUT);
 // Open serial communications and wait for port to open:
  Serial.begin(14400);
 
  // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // no point in carrying on, so do nothing forevermore:
    for(;;)
      ;
  }
  
  Udp.begin(localPort);
  
  // print your local IP address:
  Serial.print("My IP address: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++) {
    // print the value of each byte of the IP address:
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print("."); 
  }
  Serial.println();
  
  for (int i =0; i < 4; i++) {
    remoteIp[i] = EEPROM.read(i);
  }
    EEPROM.read(4);
  
}

//tries to read a frame from the card reader
boolean readFrame(){
  //If start bit
  if(!digitalRead(rx) == HIGH){
  return false;
  }

  //Read start STX:0x02
  delayMicroseconds(halfBit9600Delay);
  for (int offset = 0; offset < 8; offset++) {
    delayMicroseconds(bit9600Delay);
    frame[currentBit++] = !digitalRead(rx);
  }
  
  //Read ID: 10 times 0x##
  for(int i = 0; i < 10; i++) { //For each byte
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    for (int offset = 0; offset < 8; offset++) { //for each Bit
      delayMicroseconds(bit9600Delay);
       frame[currentBit++] = !digitalRead(rx);
    }
  }
    
    //Read CR
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
    //Read LF
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
    //Read ETX: 0x03
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
    return true;
    
}

//parses the ID
String parseId() {
  String id = "";
  for(int i = 1; i < 11; i++) { // data byte
    char c = 0;
    int offset = 0;
    for (int dec = 8*i; dec < (8*i)+8; dec++) { // data bit
      c |= frame[dec] << offset++;
    }
    id += c;
  }
  return id;
}

//Checks if any UDP message arrived.
void checkUdp() {
  int packetSize = Udp.parsePacket();
  if(packetSize) {        
    Serial.print("Received packet of size ");
    Serial.println(packetSize);

      // read the packet into packetBufffer
    String content = "";
    for(int i = 0; i < packetSize; i++) {
      content += (char) Udp.read();
    }

      Serial.println("Contents:");
      Serial.println(content);
      if(content == "open") {
        Serial.println("Opening");
        digitalWrite(doorPin, HIGH);
        delay(3000);
        digitalWrite(doorPin, LOW);
      } else if(content == "renew") {
        Serial.print("From ");
        remoteIp = Udp.remoteIP();
        remotePort = Udp.remotePort();
        for (int i =0; i < 4; i++) {
          EEPROM.write(i,remoteIp[i]);
          Serial.print(remoteIp[i], DEC);
          if (i < 3) {
            Serial.print(".");
          }
        }
        EEPROM.write(4,remotePort);
        Serial.print(", port ");
        Serial.println(remotePort);
        // send a reply, to the IP address and port that sent us the packet we received
        Udp.beginPacket(remoteIp, remotePort);
        char ack[] = "acknowledged";
        Udp.write(ack);
        Udp.endPacket();
      } else {
         Serial.println("Unknown command");
      }
      //incomingPacketBuffer[UDP_TX_PACKET_MAX_SIZE];
    }
}


void loop() {
  //Serial.println("Start loop Remote:" + remoteIp);
  if(remoteIp) {    
    if(readFrame()){
      String id = parseId();
      Serial.println("Card:"+id);
      char idBuffer[10];
      id.toCharArray(idBuffer, 10);
      Udp.beginPacket(remoteIp, remotePort);
      Udp.write(idBuffer);
      Udp.endPacket();
    
      currentBit = 0;
    }
   
  } 
    checkUdp();
   
  
}






