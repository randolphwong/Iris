#ifndef TAG_H
#define TAG_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

class Tag {

public:
    Tag();
    Tag(Tag*);
    Tag(uint16_t);
    void setID(uint16_t);
    void enable();
    void disable();
    void setStolen(bool stolen);
    uint16_t getID();
    bool isEnabled();
    bool isStolen();

private:
    uint16_t id;
    bool enabled;
    bool stolen;
};

#endif
