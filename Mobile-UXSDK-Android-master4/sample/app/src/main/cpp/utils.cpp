//
// Created by ASUS on 6/7/2024.
//

#include "utils.h"
//---------------------------------------------------------------------------
void mv_log(const char *filename, int line, const char *msg){
    MV_LOGE("[%s#%d] %s", filename, line, msg);
}
//---------------------------------------------------------------------------
