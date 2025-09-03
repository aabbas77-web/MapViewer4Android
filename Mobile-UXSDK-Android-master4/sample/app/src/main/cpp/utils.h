//
// Created by ASUS on 6/7/2024.
//

#ifndef SAMPLE_UTILS_H
#define SAMPLE_UTILS_H
//---------------------------------------------------------------------------
#include <android/log.h>

#define MAPVIEWER_TAG   "mapviewer"

#define MV_LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, MAPVIEWER_TAG, __VA_ARGS__))
#define MV_LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, MAPVIEWER_TAG, __VA_ARGS__))
#define MV_LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, MAPVIEWER_TAG, __VA_ARGS__))
#define MV_LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, MAPVIEWER_TAG, __VA_ARGS__))
#define MV_LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, MAPVIEWER_TAG, __VA_ARGS__))
//---------------------------------------------------------------------------
void mv_log(const char *filename, int line, const char *msg);
//---------------------------------------------------------------------------
#endif //SAMPLE_UTILS_H
