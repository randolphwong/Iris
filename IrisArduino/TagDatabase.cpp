#include "TagDatabase.h"

TagDatabase::TagDatabase() {}

void TagDatabase::load() {
    
}

void TagDatabase::store(Tag* tag) {

}

bool TagDatabase::contains(Tag* tag) {
    return false;
}

bool TagDatabase::contains(uint16_t id) {
    return false;
}

Tag* TagDatabase::get(uint16_t id) {
    Tag tag;
    Tag *tagpointer = &tag;
    return tagpointer;
}
