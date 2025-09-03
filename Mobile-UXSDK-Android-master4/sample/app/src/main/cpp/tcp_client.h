//
// Created by AZUS on 10/25/2021.
//

#ifndef SAMPLE_TCP_CLIENT_H
#define SAMPLE_TCP_CLIENT_H

class tcp_client {
private:

public:
    tcp_client();
    ~tcp_client();

    int connect(const char *ip, int port);
    int stop();
};


#endif //SAMPLE_TCP_CLIENT_H
