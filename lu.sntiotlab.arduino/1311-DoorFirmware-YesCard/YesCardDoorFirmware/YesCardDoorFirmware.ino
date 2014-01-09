#define bit9600Delay 104 
#define halfBit9600Delay 52

//Mac of the Board
byte mac[] = {0x90, 0xA2, 0xDA, 0x0D, 0x94, 0x7A};

//Pin of the Card Reader
byte rx = 0;
//Pin of the door lock
int doorPin = A0;
//Card frame buffer
boolean frame[8*14];
int currentBit= 0;

void setup() {
  
 pinMode(rx,INPUT);
 pinMode(doorPin,OUTPUT);
 // Open serial communications and wait for port to open:
 //Serial.begin(14400);
  
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

void openDoor() {
  digitalWrite(doorPin, HIGH);
  delay(4000);
  digitalWrite(doorPin, LOW);
}


void loop() {
 
    if(readFrame()){
      openDoor();
    }
  
  
}






