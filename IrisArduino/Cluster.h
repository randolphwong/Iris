#ifndef CLUSTER_H
#define CLUSTER_H

#if defined(ARDUINO) && ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#define MAX_SIZE 20
#define MAX_CLUSTER_COUNT 10

class Cluster {

public:
    Cluster();
    bool classify();
    uint8_t getSize();
    void add(uint16_t);
    void print();

private:
    void buildCluster();
    int8_t _classify(uint16_t);

    uint16_t overallArray[MAX_SIZE];
    uint16_t clusteredArray[MAX_CLUSTER_COUNT][MAX_SIZE];
    uint8_t cluster_size[MAX_CLUSTER_COUNT];
    int8_t cluster_count;
    uint8_t size;
    uint8_t pos;
};

#endif
