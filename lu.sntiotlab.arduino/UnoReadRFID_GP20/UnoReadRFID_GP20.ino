
#define bit9600Delay 104 
#define halfBit9600Delay 52

byte rx = 6;
boolean frame[8*14];
int currentBit= 0;

void setup() {
 Serial.begin(14400);
 pinMode(rx,INPUT);
}


void readFrame(){
  while (!digitalRead(rx) == HIGH);
  delayMicroseconds(halfBit9600Delay);
  for (int offset = 0; offset < 8; offset++) { //START
    delayMicroseconds(bit9600Delay);
    frame[currentBit++] = !digitalRead(rx);
  }

  for(int i = 0; i < 10; i++) {  //DATA
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
       frame[currentBit++] = !digitalRead(rx);
    }
  }
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      frame[currentBit++] = !digitalRead(rx);
    }
    
}

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
  


void loop(){
  readFrame();
  Serial.println(parseId());
  currentBit = 0;
  
}
