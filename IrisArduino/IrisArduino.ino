int led = 13;

void setup() {
    Serial.begin(9600);
    pinMode(led, OUTPUT);
}

void loop() {
    if (Serial.available()) {
        char readVal = Serial.read();
        if (readVal == 'H') {
            digitalWrite(led, HIGH);
            Serial.println("Hello, my LED is switched on!");
        }
        else if (readVal == 'L') {
            digitalWrite(led, LOW);
            Serial.println("Hello, my LED is switched off!");
        }
    }
}
