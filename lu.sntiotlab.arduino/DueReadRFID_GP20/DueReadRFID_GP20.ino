
#define bit9600Delay 104 
#define halfBit9600Delay 52
#define bit4800Delay 188 
#define halfBit4800Delay 94 

byte rx = 6;
String startB = "";
byte data[10];
String cr = "";
String lf = "";
String stopB = "";


void setup() {
 Serial.begin(14400);
 pinMode(rx,INPUT);
}


void readStart() {
  delayMicroseconds(halfBit9600Delay);
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      //val |= (!digitalRead(rx)) << offset;
      startB = (!digitalRead(rx)) + startB;
    }

for(int i = 0; i < 10; i++) {
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);

    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      data[i] |= (!digitalRead(rx)) << offset;
    }
}
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      //val |= (!digitalRead(rx)) << offset;
      cr = (!digitalRead(rx)) + cr;
    }
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      //val |= (!digitalRead(rx)) << offset;
      lf = (!digitalRead(rx)) + lf;
    }
    
    delayMicroseconds(bit9600Delay);
    while (!digitalRead(rx) == HIGH);   
    delayMicroseconds(halfBit9600Delay);
    
    for (int offset = 0; offset < 8; offset++) {
      delayMicroseconds(bit9600Delay);
      //val |= (!digitalRead(rx)) << offset;
      stopB = (!digitalRead(rx)) + stopB;
    }
}


void loop(){
  
  while (!digitalRead(rx) == HIGH);
  readStart();
    
    delayMicroseconds(bit9600Delay); 
    delayMicroseconds(bit9600Delay);
    Serial.println(startB);
for(int i = 0; i < 10; i++) {
    Serial.print((char)data[i]);  
}
    Serial.println();
    Serial.println(cr);
    Serial.println(lf);
    Serial.println(stopB);
}
