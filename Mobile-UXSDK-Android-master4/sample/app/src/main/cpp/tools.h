//---------------------------------------------------------------------------
#ifndef ToolsH
#define ToolsH
//---------------------------------------------------------------------------
//---------------------------------------------------------------------------
//#ifdef _IPHONE <- your platform define here
#define stricmp strcasecmp
#define strnicmp strncasecmp
//#endif
//---------------------------------------------------------------------------
#include <vector>
//---------------------------------------------------------------------------
#define map_abs(x)  	((x) >= 0.0 ? (x):(-(x)))
#define map_sign(x)  	((x) >= 0.0 ? (+1.0):(-1.0))
#define map_max(a, b)  	(((a) > (b)) ? (a) : (b))
#define map_min(a, b)  	(((a) < (b)) ? (a) : (b))
#define round(x)		((x) - int(x)>=0.5?(int(x)+1):int(x))
#define _pi				((double)3.1415926535897932384626433832795)
//#define M_PI        			 3.14159265358979323846
//---------------------------------------------------------------------------
#define		_eps				1e-16
//---------------------------------------------------------------------------
#define     pi1               	(_pi)		// pi
#define     pi2               	(2.0*pi1)		// 2*pi
#define     pi_2             	(pi1/2.0)		// pi/2
#define     deg2rad_coff    	(pi1/180.0)
#define     rad2deg_coff    	(180.0/pi1)
#define     deg2mil_coff    	(6000.0/360.0)
#define     mil2deg_coff    	(360.0/6000.0)
//---------------------------------------------------------------------------
#define		dist2(p1,p2)	    ((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y))
#define		dist(p1,p2)	    	(sqrt(dist2(p1,p2)))
#define     deg2rad(x)      	((x)*deg2rad_coff)
//#define     rad2deg(x)      	((x)*rad2deg_coff)
#define     rad2deg(x)      	(db_deg((x)*rad2deg_coff))
#define     rad2deg2(x)      	((x)*rad2deg_coff)

#define     deg2mil(x)      	((x)*deg2mil_coff)
#define     mil2deg(x)      	((x)*mil2deg_coff)

#define 	randomf 			((float)rand() / (float)RAND_MAX)  // floating point random number generator ( 0 -> 1)
#define		randnf				(2.0*randomf - 1.0)	// floating point random number generator ( -1 -> 1)
#define     sqr(x)           	((x)*(x))
#define     aabs(x)           	((x) >= 0.0 ? (x):(-(x)))
#define ciel(x)			(int(x) + 1)
#define floor(x)	   	(int(x) - 1)
//---------------------------------------------------------------------------
#define EARTH_RADIUS	(6378137.0)
#define EARTH_DIAMETER	(EARTH_RADIUS * 2.0)
//---------------------------------------------------------------------------
typedef struct
{
    double lat,lon,alt,h0;
} ge_location;
//---------------------------------------------------------------------------
typedef std::vector<ge_location> ge_locations;
//---------------------------------------------------------------------------
typedef struct
{
    double x,y;
} xy_location;
//---------------------------------------------------------------------------
typedef std::vector<xy_location> xy_locations;
//---------------------------------------------------------------------------
typedef struct
{
    float t,h,c;
} ge_profile_point;
//---------------------------------------------------------------------------
typedef std::vector<ge_profile_point> ge_profile;
//---------------------------------------------------------------------------
#define ADD_LOG(strLog)
//---------------------------------------------------------------------------
#endif
