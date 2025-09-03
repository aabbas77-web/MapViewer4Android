//
// Created by mapviewer on 02/04/2017.
//

#include "Protection.h"
#include <sys/system_properties.h>
#include <exception>

using namespace std;

//-------------------------------------------------
// Without Boost LIB usage
//-------------------------------------------------
//#include <sys/statvfs.h>
#include <sys/sysinfo.h>
#include <cstdio>
#include <string>
//-------------------------------------------------
#include <sys/types.h>
#include <signal.h>
#include <unistd.h>
#include <stdlib.h>
#include <iostream>
#include <string.h>
//-------------------------------------------------
//
// Public codes are defined in http://developer.android.com/reference/java/lang/System.html#getProperty(java.lang.String).
// Codes below are defined in https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/os/Build.java.
// Items with * are intended for display to the end user.
//

#define ANDROID_OS_BUILD_VERSION_RELEASE     "ro.build.version.release"          // * The user-visible version string. E.g., "1.0" or "3.4b5".
#define ANDROID_OS_BUILD_VERSION_INCREMENTAL "ro.build.version.incremental"      // The internal value used by the underlying source control to represent this build.
#define ANDROID_OS_BUILD_VERSION_CODENAME    "ro.build.version.codename"         // The current development codename, or the string "REL" if this is a release build.
#define ANDROID_OS_BUILD_VERSION_SDK         "ro.build.version.sdk"              // The user-visible SDK version of the framework.

#define ANDROID_OS_BUILD_MODEL               "ro.product.model"                  // * The end-user-visible name for the end product..
#define ANDROID_OS_BUILD_MANUFACTURER        "ro.product.manufacturer"           // The manufacturer of the product/hardware.
#define ANDROID_OS_BUILD_BOARD               "ro.product.board"                  // The name of the underlying board, like "goldfish".
#define ANDROID_OS_BUILD_BRAND               "ro.product.brand"                  // The brand (e.g., carrier) the software is customized for, if any.
#define ANDROID_OS_BUILD_DEVICE              "ro.product.device"                 // The name of the industrial design.
#define ANDROID_OS_BUILD_PRODUCT             "ro.product.name"                   // The name of the overall product.
#define ANDROID_OS_BUILD_HARDWARE            "ro.hardware"                       // The name of the hardware (from the kernel command line or /proc).
#define ANDROID_OS_BUILD_CPU_ABI             "ro.product.cpu.abi"                // The name of the instruction set (CPU type + ABI convention) of native code.
#define ANDROID_OS_BUILD_CPU_ABI2            "ro.product.cpu.abi2"               // The name of the second instruction set (CPU type + ABI convention) of native code.

#define ANDROID_OS_BUILD_DISPLAY             "ro.build.display.id"               // * A build ID string meant for displaying to the user.
#define ANDROID_OS_BUILD_HOST                "ro.build.host"
#define ANDROID_OS_BUILD_USER                "ro.build.user"
#define ANDROID_OS_BUILD_ID                  "ro.build.id"                       // Either a changelist number, or a label like "M4-rc20".
#define ANDROID_OS_BUILD_TYPE                "ro.build.type"                     // The type of build, like "user" or "eng".
#define ANDROID_OS_BUILD_TAGS                "ro.build.tags"                     // Comma-separated tags describing the build, like "unsigned,debug".

#define ANDROID_OS_BUILD_FINGERPRINT         "ro.build.fingerprint"              // A string that uniquely identifies this build. 'BRAND/PRODUCT/DEVICE:RELEASE/ID/VERSION.INCREMENTAL:TYPE/TAGS'.
//---------------------------------------------------------------------------
// Return pseudo unique ID
//public static String getUniquePsuedoID() {
//    // If all else fails, if the user does have lower than API 9 (lower
//    // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
//    // returns 'null', then simply the ID returned will be solely based
//    // off their Android device information. This is where the collisions
//    // can happen.
//    // Thanks http://www.pocketmagic.net/?p=1662!
//    // Try not to use DISPLAY, HOST or ID - these items could change.
//    // If there are collisions, there will be overlapping data
//    String m_szDevIDShort = "35" +
//            (Build.BOARD.length() % 10) +
//            (Build.BRAND.length() % 10) +
//            (Build.CPU_ABI.length() % 10) +
//            (Build.DEVICE.length() % 10) +
//            (Build.MANUFACTURER.length() % 10) +
//            (Build.MODEL.length() % 10) +
//            (Build.PRODUCT.length() % 10);
//
//    // Thanks to @Roman SL!
//    // https://stackoverflow.com/a/4789483/950427
//    // Only devices with API >= 9 have android.os.Build.SERIAL
//    // http://developer.android.com/reference/android/os/Build.html#SERIAL
//    // If a user upgrades software or roots their device, there will be a duplicate entry
//    String serial = null;
//    try {
//        serial = android.os.Build.class.getField("SERIAL").get(null).toString();
//
//        // Go ahead and return the serial for api => 9
//        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//    } catch (Exception exception) {
//        // String needs to be initialized
//        serial = "serial"; // some value
//    }
//
//    // Thanks @Joe!
//    // https://stackoverflow.com/a/2853253/950427
//    // Finally, combine the values we have found by using the UUID class to create a unique identifier
//    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//}
//---------------------------------------------------------------------------
char prop[1024];
char value[1024];
//---------------------------------------------------------------------------
void update_info(char *info)
{
    try
    {
        std::string strText = "MapViewer [Augmented Monitoring]:\n";

        // MODEL
        __system_property_get(ANDROID_OS_BUILD_MODEL, prop);
        sprintf(value,"MODEL: %s\n",prop);
        strText += value;

        // MANUFACTURER
        __system_property_get(ANDROID_OS_BUILD_MANUFACTURER, prop);
        sprintf(value,"MANUFACTURER: %s\n",prop);
        strText += value;

        // BOARD
        __system_property_get(ANDROID_OS_BUILD_BOARD, prop);
        sprintf(value,"BOARD: %s\n",prop);
        strText += value;

        // BRAND
        __system_property_get(ANDROID_OS_BUILD_BRAND, prop);
        sprintf(value,"BRAND: %s\n",prop);
        strText += value;

        // DEVICE
        __system_property_get(ANDROID_OS_BUILD_DEVICE, prop);
        sprintf(value,"DEVICE: %s\n",prop);
        strText += value;

        // PRODUCT
        __system_property_get(ANDROID_OS_BUILD_PRODUCT, prop);
        sprintf(value,"PRODUCT: %s\n",prop);
        strText += value;

        // HARDWARE
//        __system_property_get(ANDROID_OS_BUILD_HARDWARE, prop);
//        sprintf(value,"HARDWARE: %s\n",prop);
//        strText += value;

        // CPU_ABI
        __system_property_get(ANDROID_OS_BUILD_CPU_ABI, prop);
        sprintf(value,"CPU_ABI: %s\n",prop);
        strText += value;

        // CPU_ABI2
        __system_property_get(ANDROID_OS_BUILD_CPU_ABI2, prop);
        sprintf(value,"CPU_ABI2: %s\n",prop);
        strText += value;

        // DISPLAY
//        __system_property_get(ANDROID_OS_BUILD_DISPLAY, prop);
//        sprintf(value,"DISPLAY: %s\n",prop);
//        strText += value;

        // HOST
//        __system_property_get(ANDROID_OS_BUILD_HOST, prop);
//        sprintf(value,"HOST: %s\n",prop);
//        strText += value;

        // USER
//        __system_property_get(ANDROID_OS_BUILD_USER, prop);
//        sprintf(value,"USER: %s\n",prop);
//        strText += value;

        // ID
//        __system_property_get(ANDROID_OS_BUILD_ID, prop);
//        sprintf(value,"ID: %s\n",prop);
//        strText += value;

        // TYPE
//        __system_property_get(ANDROID_OS_BUILD_TYPE, prop);
//        sprintf(value,"TYPE: %s\n",prop);
//        strText += value;

        // TAGS
//        __system_property_get(ANDROID_OS_BUILD_TAGS, prop);
//        sprintf(value,"TAGS: %s\n",prop);
//        strText += value;

        // FINGERPRINT
//        __system_property_get(ANDROID_OS_BUILD_FINGERPRINT, prop);
//        sprintf(value,"FINGERPRINT: %s\n",prop);
//        strText += value;

        // VERSION_RELEASE
//        __system_property_get(ANDROID_OS_BUILD_VERSION_RELEASE, prop);
//        sprintf(value,"VERSION_RELEASE: %s\n",prop);
//        strText += value;

        // VERSION_INCREMENTAL
//        __system_property_get(ANDROID_OS_BUILD_VERSION_INCREMENTAL, prop);
//        sprintf(value,"VERSION_INCREMENTAL: %s\n",prop);
//        strText += value;

        // VERSION_CODENAME
//        __system_property_get(ANDROID_OS_BUILD_VERSION_CODENAME, prop);
//        sprintf(value,"VERSION_CODENAME: %s\n",prop);
//        strText += value;

        // VERSION_SDK
//        __system_property_get(ANDROID_OS_BUILD_VERSION_SDK, prop);
//        sprintf(value,"VERSION_SDK: %s\n",prop);
//        strText += value;

//    strText += mv_dm(c_strDebuggerPresent);

        strcpy(info,strText.c_str());
    }
    catch (exception &ex)
    {

    }
}
//---------------------------------------------------------------------------
void update_info0(char *info)
{
    try
    {
        std::string strText = "MapViewer [Augmented Monitoring]:\n";

        // MODEL
        __system_property_get(ANDROID_OS_BUILD_MODEL, prop);
        sprintf(value,"MODEL: %s\n",prop);
        strText += value;

        // MANUFACTURER
        __system_property_get(ANDROID_OS_BUILD_MANUFACTURER, prop);
        sprintf(value,"MANUFACTURER: %s\n",prop);
        strText += value;

        // BOARD
        __system_property_get(ANDROID_OS_BUILD_BOARD, prop);
        sprintf(value,"BOARD: %s\n",prop);
        strText += value;

        // BRAND
        __system_property_get(ANDROID_OS_BUILD_BRAND, prop);
        sprintf(value,"BRAND: %s\n",prop);
        strText += value;

        // DEVICE
        __system_property_get(ANDROID_OS_BUILD_DEVICE, prop);
        sprintf(value,"DEVICE: %s\n",prop);
        strText += value;

        // PRODUCT
        __system_property_get(ANDROID_OS_BUILD_PRODUCT, prop);
        sprintf(value,"PRODUCT: %s\n",prop);
        strText += value;

        // HARDWARE
        __system_property_get(ANDROID_OS_BUILD_HARDWARE, prop);
        sprintf(value,"HARDWARE: %s\n",prop);
        strText += value;

        // CPU_ABI
        __system_property_get(ANDROID_OS_BUILD_CPU_ABI, prop);
        sprintf(value,"CPU_ABI: %s\n",prop);
        strText += value;

        // CPU_ABI2
        __system_property_get(ANDROID_OS_BUILD_CPU_ABI2, prop);
        sprintf(value,"CPU_ABI2: %s\n",prop);
        strText += value;

        // DISPLAY
        __system_property_get(ANDROID_OS_BUILD_DISPLAY, prop);
        sprintf(value,"DISPLAY: %s\n",prop);
        strText += value;

        // HOST
        __system_property_get(ANDROID_OS_BUILD_HOST, prop);
        sprintf(value,"HOST: %s\n",prop);
        strText += value;

        // USER
        __system_property_get(ANDROID_OS_BUILD_USER, prop);
        sprintf(value,"USER: %s\n",prop);
        strText += value;

        // ID
        __system_property_get(ANDROID_OS_BUILD_ID, prop);
        sprintf(value,"ID: %s\n",prop);
        strText += value;

        // TYPE
        __system_property_get(ANDROID_OS_BUILD_TYPE, prop);
        sprintf(value,"TYPE: %s\n",prop);
        strText += value;

        // TAGS
        __system_property_get(ANDROID_OS_BUILD_TAGS, prop);
        sprintf(value,"TAGS: %s\n",prop);
        strText += value;

        // FINGERPRINT
        __system_property_get(ANDROID_OS_BUILD_FINGERPRINT, prop);
        sprintf(value,"FINGERPRINT: %s\n",prop);
        strText += value;

        // VERSION_RELEASE
        __system_property_get(ANDROID_OS_BUILD_VERSION_RELEASE, prop);
        sprintf(value,"VERSION_RELEASE: %s\n",prop);
        strText += value;

        // VERSION_INCREMENTAL
        __system_property_get(ANDROID_OS_BUILD_VERSION_INCREMENTAL, prop);
        sprintf(value,"VERSION_INCREMENTAL: %s\n",prop);
        strText += value;

        // VERSION_CODENAME
        __system_property_get(ANDROID_OS_BUILD_VERSION_CODENAME, prop);
        sprintf(value,"VERSION_CODENAME: %s\n",prop);
        strText += value;

        // VERSION_SDK
        __system_property_get(ANDROID_OS_BUILD_VERSION_SDK, prop);
        sprintf(value,"VERSION_SDK: %s\n",prop);
        strText += value;

//    strText += mv_dm(c_strDebuggerPresent);

        strcpy(info,strText.c_str());
    }
    catch (exception &ex)
    {

    }
}
//---------------------------------------------------------------------------
void leave(int variable) {
    try
    {
        if(variable == 0) {
            exit(0);
        } else if(variable == 1) {
            //log stuff
            exit(0);
        } else {
            std::cerr << "Some data could not be processed. Plz save us.";
            //take input if needed, etc
            exit(0);
        }
    }
    catch (exception &ex)
    {

    }
}
//---------------------------------------------------------------------------
long mv_gsi()
{
    long id = 0;
    try
    {
        try
        {
            char info[10240];
            int m = 1;
            update_info(info);
            for(int i=0;i<strlen(info);i++)
            {
                id += m * info[i];
                if(i % 10 == 0) m++;
            }
        }
        catch(...)
        {
//            leave(0);
        }

        if(id <= 0)
        {
//            leave(0);
        }
    }
    catch (exception &ex)
    {
//        leave(0);
    }

    return id;
}
//---------------------------------------------------------------------------
long mv_esi(long nSerialNumber, long nKey)
{
//    return 1002134 + nSerialNumber ^ nKey + 200 * nSerialNumber | nKey + 10 * nSerialNumber & nKey;
//    return 1002134 + nSerialNumber ^ nKey + 200 * nSerialNumber | nKey + 10 * nSerialNumber & nKey;
    return ((1002134 + nSerialNumber) ^ (nKey + (200 * nSerialNumber))) | ((nKey + (10 * nSerialNumber)) & nKey);
}
//---------------------------------------------------------------------------
static long g_SerialNumber1 = 0;
static long g_SerialNumber2 = 0;
static long g_SerialNumber3 = 0;
//---------------------------------------------------------------------------
long mv_gsn()
{
    return g_SerialNumber1;
}
//---------------------------------------------------------------------------
void mv_l_sn(char *filename)
{
    g_SerialNumber1 = 0;
    try
    {
        FILE *file = fopen(filename,"rb");
        if(file != nullptr)
        {
            fread(&g_SerialNumber1,1,sizeof(long),file);
            fclose(file);
        }
    }
    catch(...)
    {
    }
}
//---------------------------------------------------------------------------
void mv_s_sn(char *filename, long sn)
{
    try
    {
        g_SerialNumber1 = sn;

        FILE *file = fopen(filename,"wb");
        if(file != nullptr)
        {
            fwrite(&g_SerialNumber1,1,sizeof(long),file);
            fclose(file);
        }
    }
    catch(...)
    {
    }
}
//---------------------------------------------------------------------------
bool mv_idv()
{
    try
    {
        long id = mv_gsi();
        if(id <= 0)
            return true;
        else
            return mv_esi(id, c_nMapViewer_AM_Key) != g_SerialNumber1;
    }
    catch (exception &ex)
    {
        return true;
    }
}
//---------------------------------------------------------------------------
// EncodeMessage
std::string mv_em(std::string strText)
{
    std::string strResult = "";
    try
    {
        int nLength = static_cast<int>(strText.length());
        if(nLength <= 0)    return "";
        for(int i=0;i<nLength;i++)
        {
            strResult += wchar_t(int(strText.at(static_cast<unsigned long>(nLength - 1 - i))) + 1);
        }
    }
    catch (exception &ex)
    {

    }
    return strResult;
}
//---------------------------------------------------------------------------
// DecodeMessage
std::string mv_dm(std::string strText)
{
    std::string strResult = "";
    try
    {
        long nLength = strText.length();
        if(nLength <= 0)    return "";
        for(int i=0;i<nLength;i++)
        {
            strResult += wchar_t(int(strText.at(static_cast<unsigned long>(nLength - 1 - i))) - 1);
        }
    }
    catch (exception &ex)
    {

    }
    return strResult;
}
//---------------------------------------------------------------------------
/*
stringstream   strStream;
unsigned long  hdd_size;
unsigned long  hdd_free;
ostringstream  strConvert;
//---
struct sysinfo info;
sysinfo( &info );
//---
struct statvfs fsinfo;
statvfs("/", &fsinfo);
//---
//---
unsigned num_cpu = std::thread::hardware_concurrency();
//---
ifstream cpu_freq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
strStream << cpu_freq.rdbuf();
std::string  cpufrequency = strStream.str();
//---
strStream.str("");
ifstream cpu_temp("/sys/class/thermal/thermal_zone0/temp");
strStream << cpu_temp.rdbuf();
strConvert<< fixed << setprecision(2) << std::stof(strStream.str());
std::string cputemp = strConvert.str();
//---
std::string   mem_size = to_string( (size_t)info.totalram *     (size_t)info.mem_unit );
//---
hdd_size = fsinfo.f_frsize * fsinfo.f_blocks;
hdd_free = fsinfo.f_bsize * fsinfo.f_bfree;
//---
std::cout << "CPU core number           ==" << num_cpu       << endl;
std::cout << "CPU core speed            ==" << cpufrequency  << endl;
std::cout << "CPU temperature (C)       ==" << cputemp       << endl;
//---
std::cout << "Memory size               ==" << mem_size      << endl;
//---
std::cout << "Disk, filesystem size     ==" << hdd_size      << endl;
std::cout << "Disk free space           ==" << hdd_free      << endl;
//---
*/

/*
struct sysinfo info;
sysinfo( &info );
//---
space_info si = space(".");
//---
unsigned num_cpu = std::thread::hardware_concurrency();
//---
ifstream  cpu_freq("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
ifstream cpu_temp("/sys/class/thermal/thermal_zone0/temp");
//---
std::string cpunumber = to_string(num_cpu);
std::string cpufrequency = cpu_freq.str();
std::string cputemp = cpu_temp.str();
std::string mem_size = to_string( (size_t)info.totalram *     (size_t)info.mem_unit );
std::string disk_available = to_string(si.available);
std::string fslevel = to_string( (si.available/si.capacity)*100 );
//---
*/
/*
//        pid_t pid = fork();
//        if(pid == 0) {
//            setpgid(0, 0);
//            execl("/bin/sh", "sh", "-c", exePath);
//            exit(127);
//        } else if(pid == -1) {
//            // timeout code
//            if(timeout) {
//                kill(-pid, SIGTERM);
//                sleep(2);
//                kill(-pid, SIGKILL);
//            }
//        }
//        MessageDlg(mv_dm(c_strRunAsAdministrator),mtError,TMsgDlgButtons() << mbOK,0);
 */
