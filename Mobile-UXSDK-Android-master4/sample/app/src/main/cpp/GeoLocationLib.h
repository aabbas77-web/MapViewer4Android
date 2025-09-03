//---------------------------------------------------------------------------
#ifndef GeoLocationLibH
#define GeoLocationLibH
//---------------------------------------------------------------------------
#define MV_EMBEDED	1
//---------------------------------------------------------------------------
#include <time.h>
//---------------------------------------------------------------------------
#ifndef MV_EMBEDED
#include "DIG_Map.h"
#endif // MV_EMBEDED
//---------------------------------------------------------------------------
#define     mv_deg2rad(x)      	((x)*0.01745329251994329576923690768489)
//---------------------------------------------------------------------------
struct tag_dem_header
{
  int w;
  int h;
  float xll;
  float yll;
  float cellsizeX;
  float cellsizeY;
  float minHeight;
  float maxHeight;
  int bIsFlipped;
};
typedef struct tag_dem_header t_dem_header;
//---------------------------------------------------------------------------
//static float s_map[738304];
//static float s_map[12967201];
extern t_dem_header s_header;
extern float *s_map;
//---------------------------------------------------------------------------
typedef struct
{
	double FOVx,FOVy;
	int w,h;
	double uav_lon,uav_lat,uav_alt;
	double uav_yaw,uav_pitch,uav_roll;
	double gimb_azi,gimb_ele;
	double max_dist,step,laser_dist;

	double Hparometer,EMA;
	time_t time;

	int target_x,target_y;
	double target_lon,target_lat,target_alt;
} mv_GEO_Localization_Packet;
//---------------------------------------------------------------------------
extern int mv_load_dem_from_flash(char *address);
extern void mv_set_flat(int bFlat,float fValue);
extern void mv_get_flat(int &bFlat,float &fValue);
extern float mv_get_alt_asl(double x,double y);
extern float mv_get_height(double lon,double lat);
extern double mv_utm_distance2(double lon1,double lat1,double lon2,double lat2);
extern double mv_utm_distance3(double lon1,double lat1,double alt1,double lon2,double lat2,double alt2);
extern void calculate_angles(double lon1,double lat1,double alt1,double lon2,double lat2,double alt2,double *azi,double *ele);
//---------------------------------------------------------------------------
// All angles in degrees
int mv_localize_target(int target_x,int target_y,double fov_h,double fov_v,int w,int h,double uav_lon,double uav_lat,double uav_alt,double uav_yaw,double uav_pitch,double uav_roll,double gimb_azi,double gimb_ele,double max_dist,double step,double *laser_dist,double *target_lon,double *target_lat,double *target_alt);
//---------------------------------------------------------------------------
void mv_localization_start(double lon0,double lat0);
void mv_localization_end();
//---------------------------------------------------------------------------
extern void header2str(char *str);
int mv_save_dem_to_flash(char *address);
extern float get_sample_val(int x, int y);
extern float get_sample_val_flipped(int x, int y,bool bFlipped);
//---------------------------------------------------------------------------
int mv_save_dem_to_flash_wrd(char *address);
int mv_load_dem_from_flash_wrd(char *address);
//---------------------------------------------------------------------------
int mv_save_dem_to_flash_byt(char *address);
int mv_load_dem_from_flash_byt(char *address);
//---------------------------------------------------------------------------
#ifndef MV_EMBEDED
void Bin_SaveAs_GrayScale_Height_Map(UnicodeString filename);
extern int mv_load_dem_from_dig(DIG_Map *pDIG_Map);
#endif // MV_EMBEDED
//---------------------------------------------------------------------------
double pixel_size(double L,double w,double fov_deg,double t_deg);
//---------------------------------------------------------------------------
void LL2XY(double Lat,double Lon,double *UTMNorthing, double *UTMEasting);
void XY2LL(double UTMNorthing,double UTMEasting,double *Lat,double *Lon);
//---------------------------------------------------------------------------
// STM
void Convert_Geo_To_XY(double Lon,double Lat,int &zone,double &X,double &Y);
void Convert_XY_To_Geo(double X,double Y,int zone,double &lon,double &lat);
//---------------------------------------------------------------------------
// UTM
void LatLonToUTMXY(double lat,double lon,double zone,double &x,double &y);
void UTMXYToLatLon(double x,double y,double zone,bool southhemi,double &lat,double &lon);
//---------------------------------------------------------------------------
void CoordinateToDMS(double coord,int &deg,int &minutes,double &secs);
double DMS2Decimal(double d,double m,double s);
//---------------------------------------------------------------------------
double db_mod(double x,double y);
double db_deg(double x);
double db_change_deg(double x,double y);
double db_change_deg_ex(double x,double y,double z);
//---------------------------------------------------------------------------
#endif
