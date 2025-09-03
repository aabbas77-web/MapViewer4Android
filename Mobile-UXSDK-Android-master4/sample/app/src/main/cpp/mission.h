//---------------------------------------------------------------------------
#ifndef MissionH
#define MissionH
//---------------------------------------------------------------------------
//#include <string>
//#include <iostream>
//#include <fstream>
//using namespace std;
//---------------------------------------------------------------------------
#include "tools.h"
//---------------------------------------------------------------------------
//typedef struct
//{
//	double lat,lon,alt;
//} ge_location;
////---------------------------------------------------------------------------
//typedef vector<ge_location> ge_locations;
////---------------------------------------------------------------------------
//typedef struct
//{
//	double x,y;
//} xy_location;
////---------------------------------------------------------------------------
//typedef vector<xy_location> xy_locations;
//---------------------------------------------------------------------------
//void create_rect(ge_location p,double w,double h,ge_locations &path);
//void create_rect_utm(ge_location p,double w,double h,ge_locations &path);
//void add_polygon_as_kml(int iFile,ge_location p,ge_locations &path,UnicodeString name);
//void save_multi_polygon_as_kml(ge_locations &path,UnicodeString filename,UnicodeString name,double Lx,double Ly,UINT MaxIdx);
//void generate_rect(ge_location p,double Lx,double Ly,int level,ge_locations &path);
//---------------------------------------------------------------------------
void shift_path(xy_locations &path,double altitude_above_terrain,double ele_deg,double cx,double cy,double p1x,double p1y,double p2x,double p2y,xy_locations &new_path,double &azi_deg);
void generate_path(char *strKMLPath,ge_location &p0,int w,int h,double fov_deg,double alt,double Xpercent,double Ypercent,double ele_deg);
//void generate_path_ex(char *strKMLPath,ge_location &p0,ge_locations &boundary,int w,int h,double fov_deg,double alt,double Xpercent,double Ypercent,double ele_deg);
//---------------------------------------------------------------------------
void path_bbox(xy_locations &path,double &x_left,double &x_right,double &y_bottom,double &y_top);
void path_center(xy_locations &path,xy_location &center);
void location_rotate(xy_location &p,xy_location &c,double angle_deg,xy_location &r);
void path_rotate(xy_locations &path,xy_location &center,double angle_deg,xy_locations &new_path);
void path_min_rect(xy_locations &path,double &angle_deg,xy_locations &new_path);
void path_rect(xy_locations &path,xy_locations &new_path);
double path_Area(xy_locations &path);
//---------------------------------------------------------------------------
void ge2xy(ge_location &p,xy_location &q);
void ge2xy(ge_locations &path,xy_locations &new_path);
void xy2ge(xy_location &p,ge_location &q);
void xy2ge(xy_locations &path,ge_locations &new_path);
void divide_segment(xy_location &p1,xy_location &p2,int N,xy_locations &new_path,bool bAppend);
bool pointInPolygon(xy_location &p,xy_locations &path);
void stitch_rect(xy_location &p1,xy_location &p2,xy_location &p3,xy_location &p4,int w,int h,double fov_deg,double alt,double Xpercent,double Ypercent,double ele_deg,xy_locations &new_path);
void stitch_rect_ortho(xy_locations &boundary,xy_location &p1,xy_location &p2,xy_location &p3,xy_location &p4,int w,int h,double fov_deg,double alt_above_ground,double Xpercent,double Ypercent,xy_locations &new_path);
bool polygonPerimeter(xy_locations &path,xy_locations &new_path);
//void generate_oblique_paths(xy_locations &ortho_path,char *strMissionsPath,xy_location &c,xy_locations &bbox,int w,int h,double fov_deg,double alt_above_ground,double Xpercent,double Ypercent,double ele_deg);
void generate_oblique_paths(xy_locations &ortho_path,char *strMissionsPath,xy_location &c,xy_locations &bbox,int w,int h,double fov_deg,double alt_above_ground,double ele_deg,int path_size);
//---------------------------------------------------------------------------
void LL2UTM2(double Lat,double Lon,double &UTMNorthing, double &UTMEasting);
void UTM2LL2(double UTMNorthing,double UTMEasting,double& Lat,double& Lon);
//---------------------------------------------------------------------------
void solve_projectile(double &gun_lon,double &gun_lat,double target_lon,double target_lat,int iterations,double z0,double time_step,double velocity0,double angle0,double diameter0,double mass0,double wind0,double error,double dencity0,double cofficient0,double temp0,double gravity0,bool const_gravity0);
void save_projectile(const char *filename,double gun_lon,double gun_lat,double target_lon,double target_lat,double z0,double time_step,double velocity0,double angle0,double diameter0,double mass0,double wind0,double error,double dencity0,double cofficient0,double temp0,double gravity0,bool const_gravity0);
//---------------------------------------------------------------------------
#endif
