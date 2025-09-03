#include <vector>
//using namespace std;
//---------------------------------------------------------------------------
#include <jni.h>
#include <string>
#include <cstdio>
#include <libgen.h>
#include <sys/stat.h>
#include <cstdlib>
#include <iostream>
#include <fstream>
#include "GeoLocationLib.h"
#include "LatLong-UTMconversion.h"
#include <string>
#include <climits>
#include <unistd.h>
#include <iostream>
#include <climits>
#include <cstring>
#include <cmath>
#include "SimpleIni.h"
#include "Protection.h"
#include "mission.h"
#include "tools.h"
#include "utils.h"
#include "tcp_io_handler.h"
//#include "iniFile.h"
//---------------------------------------------------------------------------
#define MAX_PATHNAME_LEN    1024
//---------------------------------------------------------------------------
static char strAppDir[1024];
static char strIniPath[1024];
static char strDefaultPath[1024];
static char strOutputPath[1024];
static char strTestPath[1024];
static char strDemPath[1024];
static char strMapsPath[1024];
static char strMapsUri[1024];
static char strCachePath[1024];
static char strKMLPath[1024];
static char strFavoritesPath[1024];
static char strDBPath[1024];
static char strSNPath[1024];
static char strEncryptionKeyPath[1024];
static char strEmulatorPath[1024];
static char strDevelopmentPath[1024];
static char strDarkModePath[1024];
static char strLogPath[1024];
static char strProjectilePath[1024];
static char strMissionPath[1024];
static char strMissionsPath[1024];
static char strText[1024];
static char UTMZone[10];
std::string strOutput;
//---------------------------------------------------------------------------
char _dir[1024];
char _fname[1024];
char _ext[1024];
//---------------------------------------------------------------------------
static int w = 256,h = 256;
static int target_x = 0, target_y = 0;
static double fov_h = 72.0, fov_v = 72.0, uav_lon = 0.0, uav_lat = 0.0, uav_alt = 0.0, uav_yaw = 0.0;
static double uav_pitch = 0.0, uav_roll = 0.0, gimb_azi = 0.0, gimb_ele = 0.0, max_dist = 0.0, step = 10.0;
static double laser_dist = 0.0, target_lon = 0.0, target_lat = 0.0, target_alt = 0.0;
static float dYaw = 0.0f, dPitch = 0.0f, dRoll = 0.0f;
static float dDeviceYaw = 0.0f, dDevicePitch = 0.0f, dDeviceRoll = 0.0f;
static int bDebug = 0;
static double home_lon = 0.0, home_lat = 0.0;
static double map_lon = 0.0, map_lat = 0.0, map_zoom = 1.0, map_rot = 0.0;
//---------------------------------------------------------------------------
void GetFileParts(char *path, char *path_, char *base_, char *ext_);
char *get_filename_ext(char *filename);
static void printFileInfo(char *path);
//---------------------------------------------------------------------------
//bool mv_idv();
void localization_proc();
//---------------------------------------------------------------------------
static ge_profile s_profile;
//---------------------------------------------------------------------------
void LoadSettings(char *filename)
{
    try
    {
        CSimpleIniA ini;
        ini.SetUnicode();

        SI_Error rc = ini.LoadFile(filename);
//        if (rc < 0) return;

        // Calibration
        dYaw = (float)ini.GetDoubleValue("Calibration", "dYaw", dYaw);
        dPitch = (float)ini.GetDoubleValue("Calibration", "dPitch", dPitch);
        dRoll = (float)ini.GetDoubleValue("Calibration", "dRoll", dRoll);

        dDeviceYaw = (float)ini.GetDoubleValue("Calibration", "dDeviceYaw", dDeviceYaw);
        dDevicePitch = (float)ini.GetDoubleValue("Calibration", "dDevicePitch", dDevicePitch);
        dDeviceRoll = (float)ini.GetDoubleValue("Calibration", "dDeviceRoll", dDeviceRoll);

        // Localization
        w = (int)ini.GetDoubleValue("Localization", "w", w);
        h = (int)ini.GetDoubleValue("Localization", "h", h);
        target_x = (int)ini.GetDoubleValue("Localization", "target_x", target_x);
        target_y = (int)ini.GetDoubleValue("Localization", "target_y", target_y);
        fov_h = ini.GetDoubleValue("Localization", "fov_h", fov_h);
        fov_v = ini.GetDoubleValue("Localization", "fov_v", fov_v);
        uav_lon = ini.GetDoubleValue("Localization", "uav_lon", uav_lon);
        uav_lat = ini.GetDoubleValue("Localization", "uav_lat", uav_lat);
        uav_alt = ini.GetDoubleValue("Localization", "uav_alt", uav_alt);
        uav_yaw = ini.GetDoubleValue("Localization", "uav_yaw", uav_yaw);
        uav_pitch = ini.GetDoubleValue("Localization", "uav_pitch", uav_pitch);
        uav_roll = ini.GetDoubleValue("Localization", "uav_roll", uav_roll);
        gimb_azi = ini.GetDoubleValue("Localization", "gimb_azi", gimb_azi);
        gimb_ele = ini.GetDoubleValue("Localization", "gimb_ele", gimb_ele);
        max_dist = ini.GetDoubleValue("Localization", "max_dist", max_dist);
        step = ini.GetDoubleValue("Localization", "step", step);
        laser_dist = ini.GetDoubleValue("Localization", "laser_dist", laser_dist);
        target_lon = ini.GetDoubleValue("Localization", "target_lon", target_lon);
        target_lat = ini.GetDoubleValue("Localization", "target_lat", target_lat);
        target_alt = ini.GetDoubleValue("Localization", "target_alt", target_alt);
        home_lon = ini.GetDoubleValue("Localization", "home_lon", home_lon);
        home_lat = ini.GetDoubleValue("Localization", "home_lat", home_lat);
        map_lon = ini.GetDoubleValue("Localization", "map_lon", map_lon);
        map_lat = ini.GetDoubleValue("Localization", "map_lat", map_lat);
        map_zoom = ini.GetDoubleValue("Localization", "map_zoom", map_zoom);
        map_rot = ini.GetDoubleValue("Localization", "map_rot", map_rot);

        strcpy(strMapsUri,ini.GetValue("Settings", "strMapsUri", strMapsUri));
        bDebug = ini.GetLongValue("Settings", "bDebug", bDebug);

        ini.SaveFile(filename);

        mv_log(__FILE__, __LINE__,"MapViewer Start Point");

//        const char* pv;
//        pv = ini.GetValue("section", "AHhha", "default");
//        ini.SetValue("section", "AHhha", "newvalue");
//        pv = ini.GetValue("section", "AHhha", "default");



//        CIniFile iniFile(filename);
//        iniFile.ReadFile();
//
//        // Calibration
//        dYaw = (float)iniFile.GetValueF("Calibration", "dYaw", dYaw);
//        dPitch = (float)iniFile.GetValueF("Calibration", "dPitch", dPitch);
//        dRoll = (float)iniFile.GetValueF("Calibration", "dRoll", dRoll);
//
//        // Localization
//        w = (int)iniFile.GetValueF("Localization", "w", w);
//        h = (int)iniFile.GetValueF("Localization", "h", h);
//        target_x = (int)iniFile.GetValueF("Localization", "target_x", target_x);
//        target_y = (int)iniFile.GetValueF("Localization", "target_y", target_y);
//        fov_h = iniFile.GetValueF("Localization", "fov_h", fov_h);
//        fov_v = iniFile.GetValueF("Localization", "fov_v", fov_v);
//        uav_lon = iniFile.GetValueF("Localization", "uav_lon", uav_lon);
//        uav_lat = iniFile.GetValueF("Localization", "uav_lat", uav_lat);
//        uav_alt = iniFile.GetValueF("Localization", "uav_alt", uav_alt);
//        uav_yaw = iniFile.GetValueF("Localization", "uav_yaw", uav_yaw);
//        uav_pitch = iniFile.GetValueF("Localization", "uav_pitch", uav_pitch);
//        uav_roll = iniFile.GetValueF("Localization", "uav_roll", uav_roll);
//        gimb_azi = iniFile.GetValueF("Localization", "gimb_azi", gimb_azi);
//        gimb_ele = iniFile.GetValueF("Localization", "gimb_ele", gimb_ele);
//        max_dist = iniFile.GetValueF("Localization", "max_dist", max_dist);
//        step = iniFile.GetValueF("Localization", "step", step);
//        laser_dist = iniFile.GetValueF("Localization", "laser_dist", laser_dist);
//        target_lon = iniFile.GetValueF("Localization", "target_lon", target_lon);
//        target_lat = iniFile.GetValueF("Localization", "target_lat", target_lat);
//        target_alt = iniFile.GetValueF("Localization", "target_alt", target_alt);
//        home_lon = iniFile.GetValueF("Localization", "home_lon", home_lon);
//        home_lat = iniFile.GetValueF("Localization", "home_lat", home_lat);
//        map_lon = iniFile.GetValueF("Localization", "map_lon", map_lon);
//        map_lat = iniFile.GetValueF("Localization", "map_lat", map_lat);
//
//        bDebug = iniFile.GetValueI("Settings", "bDebug", bDebug);
//
//        iniFile.WriteFile();
    }
    catch(...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
void SaveSettings()
{
    try
    {
        CSimpleIniA ini;
        ini.SetUnicode();

        SI_Error rc = ini.LoadFile(strIniPath);
//        if (rc < 0) return;

        // Calibration
        ini.SetDoubleValue("Calibration", "dYaw", dYaw);
        ini.SetDoubleValue("Calibration", "dPitch", dPitch);
        ini.SetDoubleValue("Calibration", "dRoll", dRoll);

        ini.SetDoubleValue("Calibration", "dDeviceYaw", dDeviceYaw);
        ini.SetDoubleValue("Calibration", "dDevicePitch", dDevicePitch);
        ini.SetDoubleValue("Calibration", "dDeviceRoll", dDeviceRoll);

        // Localization
        ini.SetDoubleValue("Localization", "w", w);
        ini.SetDoubleValue("Localization", "h", h);
        ini.SetDoubleValue("Localization", "target_x", target_x);
        ini.SetDoubleValue("Localization", "target_y", target_y);
        ini.SetDoubleValue("Localization", "fov_h", fov_h);
        ini.SetDoubleValue("Localization", "fov_v", fov_v);
        ini.SetDoubleValue("Localization", "uav_lon", uav_lon);
        ini.SetDoubleValue("Localization", "uav_lat", uav_lat);
        ini.SetDoubleValue("Localization", "uav_alt", uav_alt);
        ini.SetDoubleValue("Localization", "uav_yaw", uav_yaw);
        ini.SetDoubleValue("Localization", "uav_pitch", uav_pitch);
        ini.SetDoubleValue("Localization", "uav_roll", uav_roll);
        ini.SetDoubleValue("Localization", "gimb_azi", gimb_azi);
        ini.SetDoubleValue("Localization", "gimb_ele", gimb_ele);
        ini.SetDoubleValue("Localization", "max_dist", max_dist);
        ini.SetDoubleValue("Localization", "step", step);
        ini.SetDoubleValue("Localization", "laser_dist", laser_dist);
        ini.SetDoubleValue("Localization", "target_lon", target_lon);
        ini.SetDoubleValue("Localization", "target_lat", target_lat);
        ini.SetDoubleValue("Localization", "target_alt", target_alt);
        ini.SetDoubleValue("Localization", "home_lon", home_lon);
        ini.SetDoubleValue("Localization", "home_lat", home_lat);
        ini.SetDoubleValue("Localization", "map_lon", map_lon);
        ini.SetDoubleValue("Localization", "map_lat", map_lat);
        ini.SetDoubleValue("Localization", "map_zoom", map_zoom);
        ini.SetDoubleValue("Localization", "map_rot", map_rot);

        // Settings
        ini.SetValue("Settings", "strMapsUri", strMapsUri);
        ini.SetLongValue("Settings", "bDebug", bDebug);

        ini.SaveFile(strIniPath);

//        CIniFile iniFile(strIniPath);
//        iniFile.ReadFile();
//
//        // Calibration
//        iniFile.SetValueF("Calibration", "dYaw", dYaw);
//        iniFile.SetValueF("Calibration", "dPitch", dPitch);
//        iniFile.SetValueF("Calibration", "dRoll", dRoll);
//
//        // Localization
//        iniFile.SetValueF("Localization", "w", w);
//        iniFile.SetValueF("Localization", "h", h);
//        iniFile.SetValueF("Localization", "target_x", target_x);
//        iniFile.SetValueF("Localization", "target_y", target_y);
//        iniFile.SetValueF("Localization", "fov_h", fov_h);
//        iniFile.SetValueF("Localization", "fov_v", fov_v);
//        iniFile.SetValueF("Localization", "uav_lon", uav_lon);
//        iniFile.SetValueF("Localization", "uav_lat", uav_lat);
//        iniFile.SetValueF("Localization", "uav_alt", uav_alt);
//        iniFile.SetValueF("Localization", "uav_yaw", uav_yaw);
//        iniFile.SetValueF("Localization", "uav_pitch", uav_pitch);
//        iniFile.SetValueF("Localization", "uav_roll", uav_roll);
//        iniFile.SetValueF("Localization", "gimb_azi", gimb_azi);
//        iniFile.SetValueF("Localization", "gimb_ele", gimb_ele);
//        iniFile.SetValueF("Localization", "max_dist", max_dist);
//        iniFile.SetValueF("Localization", "step", step);
//        iniFile.SetValueF("Localization", "laser_dist", laser_dist);
//        iniFile.SetValueF("Localization", "target_lon", target_lon);
//        iniFile.SetValueF("Localization", "target_lat", target_lat);
//        iniFile.SetValueF("Localization", "target_alt", target_alt);
//        iniFile.SetValueF("Localization", "home_lon", home_lon);
//        iniFile.SetValueF("Localization", "home_lat", home_lat);
//        iniFile.SetValueF("Localization", "map_lon", map_lon);
//        iniFile.SetValueF("Localization", "map_lat", map_lat);
//
//        iniFile.SetValueI("Settings", "bDebug", bDebug);
//
//        iniFile.WriteFile();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
char *get_filename_ext(char *filename)
{
    try
    {
        char *dot = strrchr(filename, '.');
        if(!dot || dot == filename) return nullptr;
        return dot + 1;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
static void printFileInfo(char *path)
{
    try
    {
        char *bname;
        char *path2 = strdup(path);
        bname = basename(path2);
        printf("%s.%s\n",bname, get_filename_ext(bname));
        free(path2);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
int parse(const char* perms) {
    try{
        int bits = 0;
        for(int i=0; i<9; i++){
            if (perms[i] != '-') {
                bits |= 1<<(8-i);
            }
        }
        return bits;
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return 0;
    }
}
//---------------------------------------------------------------------------
/////////////////////////////////////////////////////////
//
// Example:
// Given path == "C:\\dir1\\dir2\\dir3\\file.exe"
// will return path_ as   "C:\\dir1\\dir2\\dir3"
// Will return base_ as   "file"
// Will return ext_ as    "exe"
//
/////////////////////////////////////////////////////////
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_LoadSettingsJNI(JNIEnv *env,jclass self0,jstring dir_internal,jstring dir_data)
{
    try
    {
        strOutput = "MapViewer [Augmented Monitoring]:\n";

        const char* temp_internal = env->GetStringUTFChars(dir_internal, NULL);
        const char* temp_data = env->GetStringUTFChars(dir_data, NULL);

        // internal paths (application)
        sprintf(strAppDir,"%s/MapViewer",temp_internal);
        strOutput += "strAppDir: "+std::string(strAppDir)+"\n";
        sprintf(strIniPath,"%s/MapViewer/Settings.txt",temp_internal);
        strOutput += "strIniPath: "+std::string(strIniPath)+"\n";
        sprintf(strDefaultPath,"%s/MapViewer/default.txt",temp_internal);
        strOutput += "strDefaultPath: "+std::string(strDefaultPath)+"\n";
        sprintf(strTestPath,"%s/MapViewer/Test.txt",temp_internal);
        sprintf(strOutputPath,"%s/MapViewer/Output.txt",temp_internal);
        strOutput += "strOutputPath: "+std::string(strOutputPath)+"\n";
        sprintf(strKMLPath,"%s/MapViewer/KML",temp_internal);
        strOutput += "strKMLPath: "+std::string(strKMLPath)+"\n";
        sprintf(strFavoritesPath,"%s/MapViewer/favorites.kml",temp_internal);
        strOutput += "strFavoritesPath: "+std::string(strFavoritesPath)+"\n";
        sprintf(strSNPath,"%s/MapViewer/MapViewer.sn",temp_internal);
        strOutput += "strSNPath: "+std::string(strSNPath)+"\n";
        sprintf(strEncryptionKeyPath,"%s/MapViewer/key.enc",temp_internal);
        strOutput += "strEncryptionKeyPath: "+std::string(strEncryptionKeyPath)+"\n";
        sprintf(strEmulatorPath,"%s/MapViewer/MapViewer.emu",temp_internal);
        strOutput += "strEmulatorPath: "+std::string(strEmulatorPath)+"\n";
        sprintf(strDevelopmentPath,"%s/MapViewer/MapViewer.dev",temp_internal);
        strOutput += "strDevelopmentPath: "+std::string(strDevelopmentPath)+"\n";
        sprintf(strDarkModePath,"%s/MapViewer/MapViewer.dark",temp_internal);
        strOutput += "strDarkModePath: "+std::string(strDarkModePath)+"\n";
        sprintf(strMissionPath,"%s/MapViewer/mission.kml",temp_internal);
        strOutput += "strMissionPath: "+std::string(strMissionPath)+"\n";
        sprintf(strMissionsPath,"%s/MapViewer/Missions",temp_internal);
        strOutput += "strMissionsPath: "+std::string(strMissionsPath)+"\n";
        sprintf(strLogPath,"%s/MapViewer/MapViewerLog.txt",temp_internal);
        strOutput += "strLogPath: "+std::string(strLogPath)+"\n";
        sprintf(strProjectilePath,"%s/MapViewer/projectile.csv",temp_internal);
        strOutput += "strProjectilePath: "+std::string(strProjectilePath)+"\n";

        // external paths (data)
//        sprintf(strDemPath,"%s/MapViewer/DEM/Syria.bin",temp_data);
//        sprintf(strDemPath,"%s/MapViewer/DEM/Syria.bin",temp_internal);
//        sprintf(strDemPath,"%s/MapViewer/DEM/Syria.wrd",temp_internal);
        sprintf(strDemPath,"%s/MapViewer/DEM/Syria.byt",temp_internal);
        strOutput += "strDemPath: "+std::string(strDemPath)+"\n";
        sprintf(strMapsPath,"%s/MapViewer/Maps/",temp_data);
        strOutput += "strMapsPath: "+std::string(strMapsPath)+"\n";
        strOutput += "strMapsUri: "+std::string(strMapsUri)+"\n";
//        sprintf(strDBPath,"%s/MapViewer/DB/syria.db",temp_data);
        sprintf(strDBPath,"%s/MapViewer/DB/syria.db",temp_internal);
        strOutput += "strDBPath: "+std::string(strDBPath)+"\n";
//        sprintf(strCachePath,"%s/MapViewer/Cache/",temp_internal);
        sprintf(strCachePath,"%s/MapViewer/Cache/",temp_data);
        strOutput += "strCachePath: "+std::string(strCachePath)+"\n";

        try {
            mkdir(strCachePath, 0770);
            mkdir(strKMLPath, 0770);
            mkdir(strMissionsPath, 0770);

//            mkdir(strMapsPath, 0770);

//            chmod(strMapsPath, 0770);
//            char perms[]="rwx-w-r--";
//            chmod(strMapsPath, parse(perms));

            env->ReleaseStringUTFChars(dir_internal, temp_internal);  // release resources
            env->ReleaseStringUTFChars(dir_data, temp_data);  // release resources
        }
        catch(...)
        {
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
        }

        mv_l_sn(strSNPath);
        LoadSettings(strIniPath);

        // end localization
        mv_localization_end();

//        mv_load_dem_from_flash(strDemPath);
//        mv_load_dem_from_flash_wrd(strDemPath);
        mv_load_dem_from_flash_byt(strDemPath);
//        mv_set_flat(0, 700.0);

        // start localization
        double lon0, lat0;
        lon0 = s_header.xll + (float)s_header.w * s_header.cellsizeX / 2.0;
        lat0 = s_header.yll + (float)s_header.h * s_header.cellsizeY / 2.0;
        mv_localization_start(lon0, lat0);

        strOutput += "strDemPath: "+std::string(strDemPath)+"\n";
        strOutput += "s_header.xll: "+std::to_string(s_header.xll)+"\n";
        strOutput += "s_header.yll: "+std::to_string(s_header.yll)+"\n";
        strOutput += "s_header.cellsizeX: "+std::to_string(s_header.cellsizeX)+"\n";
        strOutput += "s_header.cellsizeY: "+std::to_string(s_header.cellsizeY)+"\n";

        // save output
        std::ofstream myfile;
        myfile.open(strOutputPath);
        myfile << strOutput;
        myfile.close();

        // encryptions
        try{
            mv_l_key(strEncryptionKeyPath);
        }
        catch (...){
            mv_log(__FILE__, __LINE__,"MapViewer LOG");
            return;
        }
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_SetDevelopmentJNI(JNIEnv *env, jclass clazz, jboolean value) {
    try
    {
        if(value){
            std::ofstream myfile;
            myfile.open(strDevelopmentPath);
            myfile << "Development";
            myfile.close();
            return true;
        }else{
            return (remove(strDevelopmentPath) == 0);
        }
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_SetEmulatorJNI(JNIEnv *env, jclass clazz, jboolean value) {
    try
    {
        if(value){
            std::ofstream myfile;
            myfile.open(strEmulatorPath);
            myfile << "Emulator";
            myfile.close();
            return true;
        }else{
            return (remove(strEmulatorPath) == 0);
        }
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_LoadDefaultJNI(JNIEnv *env, jclass self0)
{
    try
    {
        LoadSettings(strDefaultPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_oghab_mapviewer_MainActivity_EncodeMessageJNI(JNIEnv *env,jclass self0,jstring message)
//{
//    std::string strText = "";
//    try
//    {
//        const char* temp = env->GetStringUTFChars(message, NULL);
//        strText = mv_em(temp);
//        env->ReleaseStringUTFChars(message, temp);  // release resources
//    }
//    catch (...)
//    {
//
//    }
//    return env->NewStringUTF(strText.c_str());
//}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_DecodeMessageJNI(JNIEnv *env,jclass self0,jstring message)
{
    std::string strText0;
    try
    {
        const char* temp = env->GetStringUTFChars(message, nullptr);
        strText0 = mv_dm(temp);
        env->ReleaseStringUTFChars(message, temp);  // release resources
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
    return env->NewStringUTF(strText0.c_str());
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetAppPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strAppDir);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetMapsPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strMapsPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SetMapsUriJNI(JNIEnv *env,jclass self0,jstring uri)
{
    try
    {
        const char* temp = env->GetStringUTFChars(uri, NULL);
        strcpy(strMapsUri,temp);
        env->ReleaseStringUTFChars(uri, temp);  // release resources
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetMapsUriJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strMapsUri);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetEmulatorPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strEmulatorPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetDevelopmentPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strDevelopmentPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetDarkModePathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strDarkModePath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetLogPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strLogPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetProjectilePathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strProjectilePath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetKMLPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strKMLPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetFavoritesPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strFavoritesPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetDBPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strDBPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetCachePathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strCachePath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
        Java_com_oghab_mapviewer_MainActivity_GetMissionPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strMissionPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
        Java_com_oghab_mapviewer_MainActivity_GetMissionsPathJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strMissionsPath);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SetFlatModelJNI(JNIEnv *env,jclass self0,jboolean bFlat,jfloat fAlt)
{
    try
    {
        mv_set_flat(bFlat,fAlt);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_LocalizeJNI(JNIEnv *env,jclass self0,
                                                                     jint target_x0,
                                                                     jint target_y0,jdouble fov_h0,
                                                                     jdouble fov_v0,jint w0,jint h0,
                                                                     jdouble uav_lon0,jdouble uav_lat0,
                                                                     jdouble uav_alt0,jdouble uav_yaw0,
                                                                     jdouble uav_pitch0,jdouble uav_roll0,
                                                                     jdouble gimb_azi0,jdouble gimb_ele0,
                                                                     jdouble max_dist0,jdouble step0,
                                                                     jdouble laser_dist0)
{
    try
    {
        w = w0;
        h = h0;
        target_x = target_x0;
        target_y = target_y0;
        fov_h = fov_h0;
        fov_v = fov_v0;
        uav_lon = uav_lon0;
        uav_lat = uav_lat0;
        uav_alt = uav_alt0;
        uav_yaw = uav_yaw0;
        uav_pitch = uav_pitch0;
        uav_roll = uav_roll0;
        gimb_azi = gimb_azi0;
        gimb_ele = gimb_ele0;
        max_dist = max_dist0;
        step = step0;
        laser_dist = laser_dist0;

        strOutput = "MapViewer [Augmented Monitoring]:\n";

        if(!mv_idv())
        {
            localization_proc();
            SaveSettings();

            jdouble outCArray[] = {target_lon, target_lat, target_alt, laser_dist};

            // Step 3: Convert the C's Native jdouble[] to JNI jdoublearray, and return
            jdoubleArray outJNIArray = env->NewDoubleArray(4);  // allocate
            if (nullptr == outJNIArray) return nullptr;
            env->SetDoubleArrayRegion(outJNIArray, 0 , 4, outCArray);  // copy
            return outJNIArray;
        }
        else
        {
            return nullptr;
        }
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SaveSettingsJNI(JNIEnv *env,jclass self0,
                                                      jint target_x0,
                                                      jint target_y0,jdouble fov_h0,
                                                      jdouble fov_v0,jint w0,jint h0,
                                                      jdouble uav_lon0,jdouble uav_lat0,
                                                      jdouble uav_alt0,jdouble uav_yaw0,
                                                      jdouble uav_pitch0,jdouble uav_roll0,
                                                      jdouble gimb_azi0,jdouble gimb_ele0,
                                                      jdouble max_dist0,jdouble step0,
                                                      jdouble home_lon0,jdouble home_lat0,
                                                      jdouble map_lon0,jdouble map_lat0,jdouble map_zoom0,jdouble map_rot0,
                                                      jdouble target_lon0,jdouble target_lat0,jdouble target_alt0,
                                                      jdouble laser_dist0)
{
    try
    {
        w = w0;
        h = h0;
        target_x = target_x0;
        target_y = target_y0;
        fov_h = fov_h0;
        fov_v = fov_v0;
        uav_lon = uav_lon0;
        uav_lat = uav_lat0;
        uav_alt = uav_alt0;
        uav_yaw = uav_yaw0;
        uav_pitch = uav_pitch0;
        uav_roll = uav_roll0;
        gimb_azi = gimb_azi0;
        gimb_ele = gimb_ele0;
        max_dist = max_dist0;
        step = step0;
        home_lon = home_lon0;
        home_lat = home_lat0;
        map_lon = map_lon0;
        map_lat = map_lat0;
        map_zoom = map_zoom0;
        map_rot = map_rot0;
        target_lon = target_lon0;
        target_lat = target_lat0;
        target_alt = target_alt0;
        laser_dist = laser_dist0;

        SaveSettings();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SaveCurrSettingsJNI(JNIEnv *env,jclass self0)
{
    try
    {
        SaveSettings();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloat JNICALL
Java_com_oghab_mapviewer_MainActivity_GetHeightJNI(JNIEnv *env,jclass self0,jdouble lon,jdouble lat)
{
    float alt = 0.0;
    try
    {
        alt = mv_get_height(lon,lat);
//        alt = mv_get_height(36.276518,33.513805);
    }
    catch(...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
    return alt;
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetOutputJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(strOutput.c_str());
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_oghab_mapviewer_MainActivity_GetSysInfoJNI(JNIEnv *env,jclass self0)
//{
//    try
//    {
//        char info[1024000];
//        update_info(info);
//        return env->NewStringUTF(("info:\n" + std::string(info)).c_str());
//    }
//    catch (...)
//    {
//mv_log(__FILE__, __LINE__,"MapViewer LOG");
//        return env->NewStringUTF("");
//    }
//}
//---------------------------------------------------------------------------
//bool mv_idv()
//{
//    try
//    {
//        long id = mv_gsi();
//
//        // save output
//        ofstream myfile;
//        myfile.open(strOutputPath);
//        myfile << strOutput << endl;
//        myfile << "get_sys_id: " << mv_gsi() << endl;
//        myfile << "g_SerialNumber1: " << g_SerialNumber1 << endl;
//        myfile << "encode_system_id: " << mv_esi(id, c_nMapViewer_AM_Key) << endl;
//        myfile.close();
//
//        if(id <= 0)
//        {
//            return true;
//        }
//        else {
//            return mv_esi(id, c_nMapViewer_AM_Key) != g_SerialNumber1;
//        }
//    }
//    catch (...)
//    {
//mv_log(__FILE__, __LINE__,"MapViewer LOG");
//        return true;
//    }
//}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_LeaveJNI(JNIEnv *env,jclass self0)
{
    try
    {
        leave(0);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_UpdateEncryptionKeyJNI(JNIEnv *env,jclass self0,jstring key)
{
    try{
        const char* temp_key = env->GetStringUTFChars(key, nullptr);
        update_encryption_key(temp_key);
        env->ReleaseStringUTFChars(key, temp_key);  // release resources
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetEncryptionKeyJNI(JNIEnv *env, jclass self0) {
//    jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
//    env->ThrowNew(exception_cls, "Error in sendFileNative");

    try{
        return env->NewStringUTF(get_encryption_key());
    }catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        jclass exception_cls = (env)->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(exception_cls, "Error in sendFileNative");
//        throw std::runtime_error("Error in sendFileNative");
//        addError("Error in sendFileNative");
        return nullptr;
    }
    return nullptr;
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SaveEncryptionKeyJNI(JNIEnv *env,jclass self0)
{
    try{
        mv_s_key(strEncryptionKeyPath);
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_LoadEncryptionKeyJNI(JNIEnv *env,jclass self0)
{
    try{
        mv_l_key(strEncryptionKeyPath);
    }
    catch (...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_RegisterJNI(JNIEnv *env,jclass self0,jlong sn)
{
    try
    {
//        g_SerialNumber1 = sn;
        mv_s_sn(strSNPath, sn);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jlong JNICALL
Java_com_oghab_mapviewer_MainActivity_GetSystemIdJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return mv_gsi();// GetSystemID
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return 0;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jlong JNICALL
Java_com_oghab_mapviewer_MainActivity_GetSystemSNJNI(JNIEnv *env,jclass self0)
{
    try
    {
        return mv_gsn();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return 0;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL Java_com_oghab_mapviewer_MainActivity_sumAndAverage
        (JNIEnv *env, jclass thisObj, jintArray inJNIArray) {
    try
    {
        // Step 1: Convert the incoming JNI jintarray to C's jint[]
        jint *inCArray = env->GetIntArrayElements(inJNIArray, nullptr);
        if (nullptr == inCArray) return nullptr;
        jsize length = env->GetArrayLength(inJNIArray);

        // Step 2: Perform its intended operations
        jdouble sum = 0;
        int i;
        for (i = 0; i < length; i++) {
            sum += inCArray[i];
        }
        jdouble average = (jdouble)sum / length;
        env->ReleaseIntArrayElements(inJNIArray, inCArray, 0); // release resources

        jdouble outCArray[] = {sum, average};

        // Step 3: Convert the C's Native jdouble[] to JNI jdoublearray, and return
        jdoubleArray outJNIArray = env->NewDoubleArray(2);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 2, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
static jstring getpkg(JNIEnv* env, jclass thiz, jclass activity)
{
    try
    {
        jclass android_content_Context =env->GetObjectClass(activity);
        jmethodID midGetPackageName = env->GetMethodID(android_content_Context,"getPackageName", "()Ljava/lang/String;");
        jstring packageName= (jstring)env->CallObjectMethod(activity, midGetPackageName);
        return packageName;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
void localization_proc()
{
    try
    {
        mv_localize_target(
                target_x,
                target_y,
                fov_h,
                fov_v,
                w,
                h,
                uav_lon,
                uav_lat,
                uav_alt,
                uav_yaw,
                uav_pitch,
                uav_roll,
                gimb_azi,
                gimb_ele,
                max_dist,
                step,
                &laser_dist,
                &target_lon,
                &target_lat,
                &target_alt);

        sprintf(strText,"%s","----------------------------------------\n");
        strOutput += strText;
        sprintf(strText,"%s","Localization\n");
        strOutput += strText;
        sprintf(strText,"UAV: lon[%0.06f]\tlat[%0.06f]\talt[%0.03f]\n", uav_lon, uav_lat, uav_alt);
        strOutput += strText;
        sprintf(strText,"UAV: yaw[%0.02f]\tpitch[%0.02f]\troll[%0.02f]\n", uav_yaw, uav_pitch, uav_roll);
        strOutput += strText;
        sprintf(strText,"Platform: azi[%0.02f]\tele[%0.02f]\tx[%d]\ty[%d]\n", gimb_azi, gimb_ele, target_x, target_y);
        strOutput += strText;
        sprintf(strText,"Platform: FOVx[%0.02f]\tFOVy[%0.02f]\tw[%d]\th[%d]\n", fov_h, fov_v, w, h);
        strOutput += strText;
        sprintf(strText,"Platform: max_dist[%0.03f]\tstep[%0.03f]\n", max_dist, step);
        strOutput += strText;
        sprintf(strText,"%s","----------------------------------------\n");
        strOutput += strText;
        sprintf(strText,"Result: %0.03f\t%0.06f\t%0.06f\t%0.03f\n", laser_dist, target_lon, target_lat, target_alt);
        strOutput += strText;
    }
    catch(...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_oghab_mapviewer_MainActivity_CalculateAnglesJNI(JNIEnv *env, jclass self0,
                                                      jdouble lon1, jdouble lat1,
                                                      jdouble alt1, jdouble lon2,
                                                      jdouble lat2, jdouble alt2)
{
    double azi = 0.0,ele = 0.0;
    try
    {
        calculate_angles(lon1,lat1,alt1,lon2,lat2,alt2,&azi,&ele);

        jfloat outCArray[] = {(float)azi, (float)ele};

        jfloatArray outJNIArray = env->NewFloatArray(2);
        if (nullptr == outJNIArray) return nullptr;
        env->SetFloatArrayRegion(outJNIArray, 0 , 2, outCArray);
        return outJNIArray;
    }
    catch(...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_oghab_mapviewer_MainActivity_GetCalibrationDataJNI(JNIEnv *env,jclass self0)
{
    try
    {
        jfloat outCArray[] = {dYaw, dPitch, dRoll};

        jfloatArray outJNIArray = env->NewFloatArray(3);
        if (nullptr == outJNIArray) return nullptr;
        env->SetFloatArrayRegion(outJNIArray, 0 , 3, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_oghab_mapviewer_MainActivity_GetDeviceCalibrationDataJNI(JNIEnv *env,jclass self0)
{
    try
    {
        jfloat outCArray[] = {dDeviceYaw, dDevicePitch, dDeviceRoll};

        jfloatArray outJNIArray = env->NewFloatArray(3);
        if (nullptr == outJNIArray) return nullptr;
        env->SetFloatArrayRegion(outJNIArray, 0 , 3, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_IsDemoVersionJNI(JNIEnv *env,jclass type)
{
    try
    {
//        return static_cast<jboolean>(mv_idv());
        return false;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return static_cast<jboolean>(true);
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SaveCalibrationData(JNIEnv *env,
                                                                             jclass type,
                                                                             jfloat yaw,
                                                                             jfloat pitch,
                                                                             jfloat roll) {
    try
    {
        dYaw = yaw;
        dPitch = pitch;
        dRoll = roll;
        SaveSettings();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_GetSettingsJNI(JNIEnv *env,jclass self0)
{
    try
    {
        jdouble outCArray[] = {(double)w, (double)h, (double)target_x, (double)target_y, fov_h, fov_v, uav_lon, uav_lat, uav_alt,
                               uav_yaw, uav_pitch, uav_roll, gimb_azi, gimb_ele, max_dist, step,
                               laser_dist, target_lon, target_lat, target_alt, home_lon, home_lat, map_lon, map_lat, map_zoom, map_rot};

        jdoubleArray outJNIArray = env->NewDoubleArray(26);
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 26, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_IsDebugJNI(JNIEnv *env,jclass type)
{
    try
    {
        return static_cast<jboolean>(bDebug);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return static_cast<jboolean>(true);
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_SetDebugJNI(JNIEnv *env,jclass type, jboolean debug)
{
    try
    {
        bDebug = debug;
        SaveSettings();
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
bool is_strait_line(bool bProfile,int count,double lon1,double lat1,double alt1,double lon2,double lat2,double alt2,double &azi,double &ele,double &len,ge_profile &profile)
{
    try{
        double e1,n1,e2,n2;
        azi = 0.0;
        ele = 0.0;
        len = 0.0;
        LL2UTM2(lat1,lon1,n1,e1);
        LL2UTM2(lat2,lon2,n2,e2);
//	if(fabs(n2-n1) <= 1e-16)	return false;

        // calculate azimuth
        if(n2-n1 != 0.0)
            azi = atan2(e2-e1,n2-n1);
        else
        {
            if(e2-e1 > 0.0)
                azi = +pi_2;
            else
            if(e2-e1 < 0.0)
                azi = -pi_2;
            else
                return false;
        }
        azi = db_change_deg(azi,0.0);

        // calculate elevation
//	len = sqrt((e2-e1)*(e2-e1)+(n2-n1)*(n2-n1));// Planimetric distance
//	if(len > 0.0)	ele = atan2(alt2 - alt1,len);

        len = sqrt((e2-e1)*(e2-e1)+(n2-n1)*(n2-n1)+(alt2-alt1)*(alt2-alt1));// Planimetric distance
        if(len > 0.0)	ele = asin((alt2 - alt1)/len);

        // draw heights profile
        bool is_ok = true;
        double R_refer = 0.13;
        if(bProfile && (len > 0.0))
        {
            double kx,ky,dt,x,y,Dist2,Z_surface,Z_curvature,Diam_earth;
            ge_profile_point p;
            kx = (e2-e1)/len;
            ky = (n2-n1)/len;
            dt = len/count;
            Diam_earth = EARTH_DIAMETER;
            profile.clear();
            for(double t=0.0;t<=len;t+=dt)
            {
                x = e1 + t * kx;
                y = n1 + t * ky;
                Dist2 = (x - e1)*(x - e1) + (y - n1)*(y - n1);
                Z_surface = mv_get_alt_asl(x,y);
                Z_curvature = (R_refer - 1.0) * Dist2 / Diam_earth;
                p.t = static_cast<float>(t);
                p.h = static_cast<float>(Z_surface);
                p.c = static_cast<float>(Z_curvature);
                profile.push_back(p);
//            FormHeightsProfile->Series1->AddXY(t,Z_surface);
//            FormHeightsProfile->Series2->AddXY(t,Z_curvature);
//            FormHeightsProfile->Series3->AddXY(t,Z_actual);
                if(Z_surface >= alt1)	is_ok = false;
            }
        }
        return is_ok;
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
}
//---------------------------------------------------------------------------
// E:\Geoscan\SDK\4.4.1\Mobile-SDK-Android-master\Sample Code\Java and C_C++  JNI Guide – Developers Area.html
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_processData(JNIEnv *env, jclass type,
                                                  jobject buf,
                                                                     jint len) {
    try{
        char *data = (char *)env->GetDirectBufferAddress(buf);
//    long len = env->GetDirectBufferCapacity(buf);
        for(int i=0;i<len;i++)
        {
            if(data[i] != 0)    data[i] = 'X';
        }
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_is_1strait_1line(JNIEnv *env, jclass type,
                                                                          jboolean bProfile,
                                                                          jdouble lon1,
                                                                          jdouble lat1,
                                                                          jdouble alt1,
                                                                          jdouble lon2,
                                                                          jdouble lat2,
                                                                          jdouble alt2,
                                                       jobject buf,
                                                                          jint count) {
    try{
        jdouble azi,ele,len;
        bool res;
        res = is_strait_line(bProfile,count,lon1,lat1,alt1,lon2,lat2,alt2,azi,ele,len,s_profile);

//    double *data = (double *)env->GetDirectBufferAddress(buf);
//    unsigned char *pData = (unsigned char *)data;
////    count = env->GetDirectBufferCapacity(buf);
//    for(int i=0;i<profile.size();i++)
//    {
//        ge_profile_point &p = profile[i];
//        ge_profile_point *q = (ge_profile_point *)pData;
//        q->t = p.t;
//        q->h = p.h;
//        q->c = p.c;
//        pData += sizeof(ge_profile_point);
//    }
        return (jboolean)res;
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_oghab_mapviewer_MainActivity_is_1strait_1line2(JNIEnv *env, jclass type,
                                                                          jboolean bProfile,
                                                                          jdouble lon1,
                                                                          jdouble lat1,
                                                                          jdouble alt1,
                                                                          jdouble lon2,
                                                                          jdouble lat2,
                                                                          jdouble alt2,
                                                                          jint count) {
    try{
        jdouble azi,ele,len;
        bool res;
        res = is_strait_line(bProfile,count,lon1,lat1,alt1,lon2,lat2,alt2,azi,ele,len,s_profile);
        return (jboolean)res;
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return false;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_oghab_mapviewer_MainActivity_GetTs(JNIEnv *env,jclass self0)
{
    try
    {
        jsize count = (jsize)s_profile.size();
        jfloat *outCArray = new jfloat[count];
        for(int i=0;i<count;i++)
        {
            outCArray[i] = s_profile[i].t;
        }

        jfloatArray outJNIArray = env->NewFloatArray(count);
        if (nullptr == outJNIArray) return nullptr;
        env->SetFloatArrayRegion(outJNIArray, 0 , count, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_oghab_mapviewer_MainActivity_GetHs(JNIEnv *env,jclass self0)
{
    try
    {
        jsize count = (jsize)s_profile.size();
        jfloat *outCArray = new jfloat[count];
        for(int i=0;i<count;i++)
        {
            outCArray[i] = s_profile[i].h;
        }

        jfloatArray outJNIArray = env->NewFloatArray(count);
        if (nullptr == outJNIArray) return nullptr;
        env->SetFloatArrayRegion(outJNIArray, 0 , count, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL
Java_com_oghab_mapviewer_MainActivity_FinalizeDem(JNIEnv *env,
                                                                     jclass type) {
    try{
        // end localization
        mv_localization_end();
    }
    catch(...){
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_LL2STM(JNIEnv *env,jclass self0,
                                                                jdouble lon,jdouble lat)
{
    try
    {
        double X,Y;
        int zone;
        Convert_Geo_To_XY(lon,lat,zone,X,Y);

        jdouble outCArray[] = {X, Y, (double)zone};
        jdoubleArray outJNIArray = env->NewDoubleArray(3);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 3, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_STM2LL(JNIEnv *env,jclass self0,
                                                                jdouble X,jdouble Y,jint zone)
{
    try
    {
        double lon,lat;
        Convert_XY_To_Geo(X,Y,zone,lon,lat);

        jdouble outCArray[] = {lon, lat};
        jdoubleArray outJNIArray = env->NewDoubleArray(2);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 2, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_LL2UTM(JNIEnv *env,jclass self0,
                                                                jdouble lon,jdouble lat)
{
    try
    {
        double X,Y;
        int ZoneNumber;
        char UTMChar;
        LLtoUTM(23,lat,lon,Y,X,UTMZone,ZoneNumber,UTMChar);

        jdouble outCArray[] = {X, Y,(double)ZoneNumber,(double)UTMChar};
        jdoubleArray outJNIArray = env->NewDoubleArray(4);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 4, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_GetUTMZone(JNIEnv *env,jclass self0)
{
    try
    {
        return env->NewStringUTF(UTMZone);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_UTM2LL(JNIEnv *env,jclass self0,
                                                                jdouble UTMEasting,jdouble UTMNorthing, jstring Zone)
{
    try
    {
        double lon,lat;
        const char* zone = env->GetStringUTFChars(Zone, nullptr);
        UTMtoLL(23, UTMNorthing, UTMEasting, zone, lat,  lon);
        env->ReleaseStringUTFChars(Zone, zone);  // release resources

        jdouble outCArray[] = {lon, lat};
        jdoubleArray outJNIArray = env->NewDoubleArray(2);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 2, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_oghab_mapviewer_MainActivity_Decimal2DMS(JNIEnv *env,jclass self0,
                                                                         jdouble coord)
{
    try
    {
        int deg,minutes;
        double secs;
        CoordinateToDMS(coord,deg,minutes,secs);

        jdouble outCArray[] = {(double)deg,(double)minutes,secs};
        jdoubleArray outJNIArray = env->NewDoubleArray(3);  // allocate
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 3, outCArray);  // copy
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdouble JNICALL
Java_com_oghab_mapviewer_MainActivity_DMS2Decimal(JNIEnv *env, jclass type,
                                                                           jdouble d,
                                                                           jdouble m,
                                                                           jdouble s) {
    return DMS2Decimal(d,m,s);
}
//---------------------------------------------------------------------------
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_oghab_mapviewer_MainActivity_CoordinateToDMS(JNIEnv *env,jclass self0,
//                                                       jdouble ll)
//{
//    try
//    {
//        int deg,minutes;
//        double secs;
//        char text[1024];
//
//        // latitude or longitude
//        CoordinateToDMS(ll,deg,minutes,secs);
////        sprintf(text,"%d°%d′%02.02f″",deg,minutes,secs);
//        sprintf(text,"%d°%d′%0.02f″",deg,minutes,(float)secs);
////        std::string strText0 = std::string(text);
//
////        return env->NewStringUTF(strText0.c_str());
//        return env->NewStringUTF(text);
////        return env->NewStringUTF("1°1′1.11″");
//    }
//    catch (...)
//    {
//mv_log(__FILE__, __LINE__,"MapViewer LOG");
//        return env->NewStringUTF("");
////        return env->NewStringUTF(ex.what());
////        return env->NewStringUTF("0°0′0.00″");
//    }
//}
//---------------------------------------------------------------------------
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_oghab_mapviewer_MainActivity_CoordinatesToDMS(JNIEnv *env,jclass self0,
//                                                       jdouble lon,jdouble lat)
//{
//    try
//    {
//        int deg,minutes;
//        double secs;
//        char text[1024];
//        std::string strText0;
//
//        // latitude
//        CoordinateToDMS(lat,deg,minutes,secs);
////        sprintf(text,"%d:%d:%02.02f",deg,minutes,secs);
//        sprintf(text,"%d°%d′%02.02f″",deg,minutes,secs);
//        strText0 += std::string(text)+(lat>=0?" N":" S");
//
//        strText0 += ", ";
//
//        // longitude
//        CoordinateToDMS(lon,deg,minutes,secs);
////        sprintf(text,"%d:%d:%02.02f",deg,minutes,secs);
//        sprintf(text,"%d°%d′%02.02f″",deg,minutes,secs);
//        strText0 += std::string(text)+(lon>=0?" E":" W");
//
//        return env->NewStringUTF(strText0.c_str());
//    }
//    catch (...)
//    {
//mv_log(__FILE__, __LINE__,"MapViewer LOG");
//        return env->NewStringUTF("");
//    }
//}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_oghab_mapviewer_MainActivity_CoordinatesToDMSText(JNIEnv *env,jclass self0,
                                                                          jdouble coordinate)
{
    try
    {
        int deg,minutes;
        double secs;
        char text[1024];
        std::string strText = "";

        CoordinateToDMS(coordinate,deg,minutes,secs);
        sprintf(text,"%d:%d:%02.02f",deg,minutes,secs);
        strText += std::string(text);

        return env->NewStringUTF(strText.c_str());
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return env->NewStringUTF("");
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL Java_com_oghab_mapviewer_MainActivity_StitchPolygon
        (JNIEnv *env, jclass thisObj, jdoubleArray polygon, jdouble fov_deg, jint w0, jint h0, jdouble alt_above_ground, jdouble Xpercent, jdouble Ypercent, jdouble ele_deg, jint path_size) {
    try
    {
        // Step 1: Convert the incoming JNI jintarray to C's jint[]
        jdouble *polygonArray = env->GetDoubleArrayElements(polygon, NULL);
        if (NULL == polygonArray) return static_cast<jboolean>(false);
        jsize length = env->GetArrayLength(polygon);

        // Step 2: Perform its intended operations
        ge_locations boundary;
        ge_location b;
        int I;
        for (int i = 0; i < length/2; i++) {
            I = 2*i;
            b.lon = polygonArray[I];
            b.lat = polygonArray[I+1];
            boundary.push_back(b);
        }
        env->ReleaseDoubleArrayElements(polygon, polygonArray, 0); // release resources

        // stitching
        xy_locations bbox_xy,boundary_xy;
        ge2xy(boundary,boundary_xy);
        path_rect(boundary_xy,bbox_xy);

        xy_location p1,p2,p3,p4;
        p1 = bbox_xy[0];
        p2 = bbox_xy[1];
        p3 = bbox_xy[2];
        p4 = bbox_xy[3];
        xy_locations perimeter_xy;
        xy_locations ortho_path_xy;
        if(polygonPerimeter(boundary_xy,perimeter_xy))
            stitch_rect_ortho(perimeter_xy, p1, p2, p3, p4, w0, h0, fov_deg, alt_above_ground, Xpercent, Ypercent, ortho_path_xy);
        else
            stitch_rect_ortho(boundary_xy, p1, p2, p3, p4, w0, h0, fov_deg, alt_above_ground, Xpercent, Ypercent, ortho_path_xy);

        xy_location center;
        path_center(bbox_xy,center);
        generate_oblique_paths(ortho_path_xy, strMissionsPath, center, bbox_xy, w0, h0, fov_deg, alt_above_ground, ele_deg, path_size);

        // save output
//        char strText0[1024];
//        std::string strOutput0 = "MapViewer\n";
//
//        sprintf(strText0, "%d", length);
//        strOutput0 += "length: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", w0);
//        strOutput0 += "w0: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", h0);
//        strOutput0 += "h0: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", path_size);
//        strOutput0 += "part_size: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%f", alt_above_ground);
//        strOutput0 += "alt_above_ground: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", boundary.size());
//        strOutput0 += "boundary size: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", bbox_xy.size());
//        strOutput0 += "bbox_xy size: " + std::string(strText0) + "\n";
//
//        sprintf(strText0, "%d", ortho_path_xy.size());
//        strOutput0 += "Path size: " + std::string(strText0) + "\n";
//
//        ofstream myfile;
//        myfile.open(strTestPath);
//        myfile << strOutput0;
//        myfile.close();

        return static_cast<jboolean>(true);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return static_cast<jboolean>(false);
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT jdoubleArray JNICALL Java_com_oghab_mapviewer_MainActivity_Projectile
        (JNIEnv *env, jclass thisObj,jdouble gun_lon0,jdouble gun_lat0,jdouble target_lon0,jdouble target_lat0,jint iterations,jdouble z0,jdouble time_step,jdouble velocity0,jdouble angle0,jdouble diameter0,jdouble mass0,jdouble wind0,jdouble error,jdouble dencity0,jdouble cofficient0,jdouble temp0,jdouble gravity0,jboolean const_gravity0) {
    try
    {
        solve_projectile(gun_lon0,gun_lat0,target_lon0,target_lat0,iterations,z0,time_step,velocity0,angle0,diameter0,mass0,wind0,error,dencity0,cofficient0,temp0,gravity0,const_gravity0);
        jdouble outCArray[] = {gun_lon0,gun_lat0};

        jdoubleArray outJNIArray = env->NewDoubleArray(2);
        if (nullptr == outJNIArray) return nullptr;
        env->SetDoubleArrayRegion(outJNIArray, 0 , 2, outCArray);
        return outJNIArray;
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
        return nullptr;
    }
}
//---------------------------------------------------------------------------
extern "C"
JNIEXPORT void JNICALL Java_com_oghab_mapviewer_MainActivity_SaveProjectile
        (JNIEnv *env, jclass thisObj,jstring filename0,jdouble gun_lon0,jdouble gun_lat0,jdouble target_lon0,jdouble target_lat0,jdouble z0,jdouble time_step,jdouble velocity0,jdouble angle0,jdouble diameter0,jdouble mass0,jdouble wind0,jdouble error,jdouble dencity0,jdouble cofficient0,jdouble temp0,jdouble gravity0,jboolean const_gravity0) {
    try
    {
        const char* filename = env->GetStringUTFChars(filename0, nullptr);
        save_projectile(filename,gun_lon0,gun_lat0,target_lon,target_lat,z0,time_step,velocity0,angle0,diameter0,mass0,wind0,error,dencity0,cofficient0,temp0,gravity0,const_gravity0);
    }
    catch (...)
    {
        mv_log(__FILE__, __LINE__,"MapViewer LOG");
    }
}
//---------------------------------------------------------------------------
