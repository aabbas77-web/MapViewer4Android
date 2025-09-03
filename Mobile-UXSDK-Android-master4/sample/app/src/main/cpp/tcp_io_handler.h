//
// Created by AZUS on 10/26/2021.
//

#ifndef SAMPLE_TCP_IO_HANDLER_H
#define SAMPLE_TCP_IO_HANDLER_H

class tcp_io_handler {

};

const char *get_encryption_key();
void update_encryption_key(const char *new_key);
//bool load_encryption_key(const char *filename);
//void save_encryption_key(const char *filename);
void mv_l_key(char *filename);
void mv_s_key(char *filename);

#endif //SAMPLE_TCP_IO_HANDLER_H
