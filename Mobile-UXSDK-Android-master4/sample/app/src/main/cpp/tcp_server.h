//
// Created by AZUS on 10/25/2021.
//

#ifndef SAMPLE_TCP_SERVER_H
#define SAMPLE_TCP_SERVER_H

class tcp_server {
private:
    int port;

public:
    tcp_server();
    ~tcp_server();

    int listen(int port);
    int stop();
};

#endif //SAMPLE_TCP_SERVER_H
