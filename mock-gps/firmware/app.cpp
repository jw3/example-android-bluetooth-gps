#include <Particle.h>
#include "coords.h"
#include <Serial4/Serial4.h>

SYSTEM_MODE(MANUAL);

const String Id("MockGPS");
const String MovedEvent = String("M/") + Id;

using tick = system_tick_t;
tick lastEvent = 0;
tick interval = 5000;

void setup() {
  Serial4.begin(9600);
}

void loop() {
  for(auto& coord: coords) {
    if(millis() - lastEvent > interval) {
      Serial4.printlnf(String::format("%s:%f:%f", MovedEvent.c_str(), std::get<0>(coord), std::get<1>(coord)));
      lastEvent = millis();
    }
  }
}