//
// Created by toshiba on 02/04/2017.
//
//---------------------------------------------------------------------------
#ifndef AUGMENTED_MONITORING_PROTECTION_H
#define AUGMENTED_MONITORING_PROTECTION_H
//---------------------------------------------------------------------------
#include <sys/system_properties.h>
#include <string>
#include <jni.h>
//---------------------------------------------------------------------------
// AliSoft don't forget to enable it
#define 	_PROTECTED_
//---------------------------------------------------------------------------
//#define		_SILENT_
//#define 	_TEST_
//---------------------------------------------------------------------------
//typedef	unsigned long int   __int64;
//typedef jlong   __int64;
//---------------------------------------------------------------------------
void leave(int variable);
void update_info(char *info);
long mv_gsi();
//---------------------------------------------------------------------------
// Invalid Version (DEMO)!
const std::string c_strInvalidVersion = "\"*PNFE)!opjtsfW!ejmbwoJ";
// Demo Version
const std::string c_strDemoVersion = "opjtsfW!pnfE";
// Unprotected Version
const std::string c_strUnprotectedVersion = "opjtsfW!efudfupsqoV";
// Please run MapViewer as Administrator!
const std::string c_strRunAsAdministrator = "\"spubsutjojneB!tb!sfxfjWqbN!ovs!ftbfmQ";

// Software\\AA
const std::string c_strSN_Key = "BB]]fsbxugpT";

// [MapViewer]
const long c_nMapViewer_Key = 55555555;// MapViewer Key
// SN
const std::string c_strSN_MapViewer = "OT";

// [GEManager]
const long c_nGEManager_Key = 55005500;// GEManager Key
// GE_SN
const std::string c_strSN_GEManager = "OT`FH";

// [GEViewer]
const long c_nGEViewer_Key = 55445544;// GEViewer Key
// GEV_SN
const std::string c_strSN_GEViewer = "OT`WFH";

// [MapViewer Augmented Monitoring]
const long c_nMapViewer_AM_Key = 53435534;// MapViewer Augmented Monitoring Key
// MVAM_SN
const std::string c_strSN_MapViewer_AM = "";

//extern long g_SerialNumber1;
//extern long g_SerialNumber2;
//extern long g_SerialNumber3;

long mv_gsn();// GetSerialNumber
void mv_l_sn(char *filename);// LoadSerialNumber
void mv_s_sn(char *filename, long sn);// SaveSerialNumber
bool mv_idv();// mv_idv
//---------------------------------------------------------------------------
// Don't try to crack MapViewer!
const std::string c_strDebuggerPresent = "\"sfxfjWqbN!ldbsd!pu!zsu!u(opE";
// لا تحاول كسر البرنامج
//const UnicodeString c_strDebuggerPresent = "حنبهزةمب!زشل!مىبخث!بم";
//---------------------------------------------------------------------------
long mv_esi(long nSerialNumber, long nKey);// EncodeSerialNumber
std::string mv_em(std::string strText);// EncodeMessage
std::string mv_dm(std::string strText);// DecodeMessage
//---------------------------------------------------------------------------
#endif //AUGMENTED_MONITORING_PROTECTION_H
