//
// Created by AZUS on 10/25/2021.
//

#ifndef SAMPLE_TCP_LIB_H
#define SAMPLE_TCP_LIB_H

#include <string>

typedef struct Message
{
    int id;
    int message_length;
    char* message_str;
} message;

void rethrow_cpp_exception_as_java_exception();
//void addError(const char *msg);
void addError(std::string msg);

int SerializeMessage(const struct Message & msg, char * outBuf);
int DeserializeMessage(const char * inBuf, int numBytes, struct Message & msg);


#endif //SAMPLE_TCP_LIB_H
