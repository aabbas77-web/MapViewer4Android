//---------------------------------------------------------------------------
#include <string>
#include <iostream>
#include <fstream>
#include <cmath>
using namespace std;
//---------------------------------------------------------------------------
#include "mission.h"
#include "GeoLocationLib.h"
//#include "utilities.h"
//#include "UTM.h"
//---------------------------------------------------------------------------
void LL2UTM2(double Lat,double Lon,double &UTMNorthing, double &UTMEasting)
{
	try
	{
		LL2XY(Lat,Lon,&UTMNorthing, &UTMEasting);
	}
	catch (exception ex)
	{

	}
}
//---------------------------------------------------------------------------
void UTM2LL2(double UTMNorthing,double UTMEasting,double& Lat,double& Lon)
{
	try
	{
		XY2LL(UTMNorthing,UTMEasting,&Lat,&Lon);
	}
	catch (exception ex)
	{

	}
}
//---------------------------------------------------------------------------
void create_rect(ge_location p,double w,double h,ge_locations &path)
{
	ge_location q;
	double w2,h2;
	q.alt = p.alt;
	path.clear();

	w2 = w/2.0;
	h2 = h/2.0;

	q.lon = p.lon-w2;
	q.lat = p.lat-h2;
	path.push_back(q);

	q.lon = p.lon+w2;
	q.lat = p.lat-h2;
	path.push_back(q);

	q.lon = p.lon+w2;
	q.lat = p.lat+h2;
	path.push_back(q);

	q.lon = p.lon-w2;
	q.lat = p.lat+h2;
	path.push_back(q);

	q.lon = p.lon-w2;
	q.lat = p.lat-h2;
	path.push_back(q);
}
//---------------------------------------------------------------------
void create_rect_utm(ge_location p,double w,double h,ge_locations &path)
{
	ge_location q;
	double x,y,w2,h2;
	q.alt = p.alt;
	path.clear();

	w2 = w/2.0;
	h2 = h/2.0;
	LL2UTM2(p.lat,p.lon,y,x);

	UTM2LL2(y-h2,x-w2,q.lat,q.lon);
	path.push_back(q);

	UTM2LL2(y-h2,x+w2,q.lat,q.lon);
	path.push_back(q);

	UTM2LL2(y+h2,x+w2,q.lat,q.lon);
	path.push_back(q);

	UTM2LL2(y+h2,x-w2,q.lat,q.lon);
	path.push_back(q);

	UTM2LL2(y-h2,x-w2,q.lat,q.lon);
	path.push_back(q);
}
//---------------------------------------------------------------------
void create_rect_utm_xy(xy_location p,double w,double h,xy_locations &path)
{
	xy_location q;
	double w2,h2;
	path.clear();

	w2 = w/2.0;
	h2 = h/2.0;

	q.x = p.x - w2;
	q.y = p.y - h2;
	path.push_back(q);

	q.x = p.x + w2;
	q.y = p.y - h2;
	path.push_back(q);

	q.x = p.x + w2;
	q.y = p.y + h2;
	path.push_back(q);

	q.x = p.x - w2;
	q.y = p.y + h2;
	path.push_back(q);

	q.x = p.x - w2;
	q.y = p.y - h2;
	path.push_back(q);
}
//---------------------------------------------------------------------
//string to_string(double f)
//{
//	char text[256];
//	sprintf(text,"%f",f);
//	return string(text);
//}
//---------------------------------------------------------------------
void add_polygon_as_kml(ofstream &ofile,ge_location p,ge_locations &path,string name,double azi_deg,double ele_deg)
{
	string strLine;

	strLine = "\t<Placemark>\n";
	ofile << strLine;
	strLine = "\t\t<name>"+name+"</name>\n";
	ofile << strLine;
	strLine = "\t\t<styleUrl>#trajRed</styleUrl>\n";
	ofile << strLine;

//	strLine = "<LookAt>";
//	ofile << strLine;
//	strLine = "	<longitude>"+to_string(p.lon)+"</longitude>";
//	ofile << strLine;
//	strLine = "	<latitude>"+to_string(p.lat)+"</latitude>";
//	ofile << strLine;
//	strLine = "	<altitude>"+to_string(p.alt)+"</altitude>";
//	ofile << strLine;
//	strLine = "	<heading>0.0</heading>";
//	ofile << strLine;
//	strLine = "	<tilt>0.0</tilt>";
//	ofile << strLine;
//	strLine = "	<range>500.0</range>";
//	ofile << strLine;
//	strLine = "	<gx:altitudeMode>relativeToGround</gx:altitudeMode>";
//	ofile << strLine;
//	strLine = "</LookAt>";
//	ofile << strLine;

	strLine = "<Polygon id=\"ID\">";
	ofile << strLine;
	strLine = "  <extrude>0</extrude>";
	ofile << strLine;
	strLine = "  <tessellate>0</tessellate>";
	ofile << strLine;
	strLine = "  <altitudeMode>clampToGround</altitudeMode>";
	ofile << strLine;
//	strLine = "  <altitudeMode>relativeToGround</altitudeMode>";
//	ofile << strLine;
	strLine = "  <outerBoundaryIs>";
	ofile << strLine;

	strLine = "\t\t<LinearRing>\n";
	ofile << strLine;
	strLine = "\t\t<altitudeMode>relativeToGround</altitudeMode>\n";
	ofile << strLine;
	strLine = "\t\t\t<coordinates>\n";
	ofile << strLine;

	// write path
	for(int i=0;i<path.size();i++)
	{
		ge_location &T = path[i];
		strLine = "\t\t\t"+to_string(T.lon)+","+to_string(T.lat)+","+to_string(T.alt)+"\n";
		ofile << strLine;
	}

	strLine = "\t\t\t</coordinates>\n";
	ofile << strLine;
	strLine = "\t\t</LinearRing>\n";
	ofile << strLine;
	strLine = "  </outerBoundaryIs>";
	ofile << strLine;
	strLine = "</Polygon>";
	ofile << strLine;

	strLine = "\t<ExtendedData>\n";
	ofile << strLine;

	strLine = "\t<Data name=\"LON\"><value>"+to_string(p.lon)+"</value></Data>\n";
	ofile << strLine;
	strLine = "\t<Data name=\"LAT\"><value>"+to_string(p.lat)+"</value></Data>\n";
	ofile << strLine;
	strLine = "\t<Data name=\"ALT\"><value>"+to_string(p.alt)+"</value></Data>\n";
	ofile << strLine;

//	strLine = "\t<Data name=\"STAY\"><value>0</value></Data>\n";
//	ofile << strLine;
	strLine = "\t<Data name=\"START_TAKE_PHOTO\"><value>1</value></Data>\n";
	ofile << strLine;
//	strLine = "\t<Data name=\"START_RECORD\"><value>0</value></Data>\n";
//	ofile << strLine;
//	strLine = "\t<Data name=\"STOP_RECORD\"><value>0</value></Data>\n";
//	ofile << strLine;
	strLine = "\t<Data name=\"ROTATE_AIRCRAFT\"><value>"+to_string(azi_deg)+"</value></Data>\n";
	ofile << strLine;
	strLine = "\t<Data name=\"GIMBAL_PITCH\"><value>"+to_string(ele_deg)+"</value></Data>\n";
	ofile << strLine;
//	strLine = "\t<Data name=\"CAMERA_ZOOM\"><value>0</value></Data>\n";
//	ofile << strLine;
//	strLine = "\t<Data name=\"CAMERA_FOCUS\"><value>0</value></Data>\n";
//	ofile << strLine;
	strLine = "\t</ExtendedData>\n";
	ofile << strLine;

	strLine = "\t</Placemark>\n";
	ofile << strLine;
}
//---------------------------------------------------------------------
//void save_multi_polygon_as_kml(ge_locations &path,string filename,string name,double Lx,double Ly,int MaxIdx,double azi_deg,double ele_deg)
//{
//	if(path.empty())	return;
//	string strLine;
//	ofstream ofile;
//	ofile.open(filename.c_str());
//	if(ofile.is_open())
//	{
//		strLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
//		ofile << strLine;
//		strLine = "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n";
//		ofile << strLine;
//		strLine = "<Document>\n";
//		ofile << strLine;
//		strLine = "\t<name>"+name+"</name>\n";
//		ofile << strLine;
//
//		strLine = "\t<Style id=\"trajRed\">\n";
//		ofile << strLine;
//
//		strLine = "\t\t<LineStyle>\n";
//		ofile << strLine;
//		strLine = "\t\t\t<color>7f0000ff</color>\n";
//		ofile << strLine;
//		strLine = "\t\t\t<width>6</width>\n";
//		ofile << strLine;
//		strLine = "\t\t</LineStyle>\n";
//		ofile << strLine;
//
//		strLine = "<PolyStyle>";
//		ofile << strLine;
//		strLine = "	<color>407fff00</color>";
//		ofile << strLine;
//		strLine = "</PolyStyle>";
//		ofile << strLine;
//
//		strLine = "\t</Style>\n";
//		ofile << strLine;
//
//		// write path
//		ge_locations rect_ge;
//		xy_locations rect_xy;
//		xy_locations rot_xy;
//        xy_location q;
//		for(int i=0;i<map_min(MaxIdx,path.size());i++)
//		{
//			ge_location &p = path[i];
////			create_rect_utm(p,Lx,Ly,rect_ge);
//			ge2xy(p,q);
//			create_rect_utm_xy(q,Lx,Ly,rect_xy);
//			path_rotate(rect_xy,q,azi_deg,rot_xy);
//			xy2ge(rot_xy,rect_ge);
//			add_polygon_as_kml(ofile,p,rect_ge,"rect",azi_deg,ele_deg);
//		}
//
//		strLine = "</Document>\n";
//		ofile << strLine;
//		strLine = "</kml>\n";
//		ofile << strLine;
//
//		ofile.close();
//	}
//}
//---------------------------------------------------------------------
void save_multi_polygon_as_kml(ge_locations &path,string filename,string name,double Lx,double Ly,int MinIdx,int MaxIdx,double azi_deg,double ele_deg)
{
    if(path.empty())	return;
    string strLine;
    ofstream ofile;
    ofile.open(filename.c_str());
    if(ofile.is_open())
    {
        strLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        ofile << strLine;
        strLine = "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n";
        ofile << strLine;
        strLine = "<Document>\n";
        ofile << strLine;
        strLine = "\t<name>"+name+"</name>\n";
        ofile << strLine;

        strLine = "\t<Style id=\"trajRed\">\n";
        ofile << strLine;

        strLine = "\t\t<LineStyle>\n";
        ofile << strLine;
        strLine = "\t\t\t<color>7f0000ff</color>\n";
        ofile << strLine;
        strLine = "\t\t\t<width>6</width>\n";
        ofile << strLine;
        strLine = "\t\t</LineStyle>\n";
        ofile << strLine;

        strLine = "<PolyStyle>";
        ofile << strLine;
        strLine = "	<color>407fff00</color>";
        ofile << strLine;
        strLine = "</PolyStyle>";
        ofile << strLine;

        strLine = "\t</Style>\n";
        ofile << strLine;

        // write path
        ge_locations rect_ge;
        xy_locations rect_xy;
        xy_locations rot_xy;
        xy_location q;
        for(int i=map_max(0,MinIdx);i<map_min(MaxIdx,path.size());i++)
        {
            ge_location &p = path[i];
//			create_rect_utm(p,Lx,Ly,rect_ge);
            ge2xy(p,q);
            create_rect_utm_xy(q,Lx,Ly,rect_xy);
            path_rotate(rect_xy,q,azi_deg,rot_xy);
            xy2ge(rot_xy,rect_ge);
            add_polygon_as_kml(ofile,p,rect_ge,"rect",azi_deg,ele_deg);
        }

        strLine = "</Document>\n";
        ofile << strLine;
        strLine = "</kml>\n";
        ofile << strLine;

        ofile.close();
    }
}
//---------------------------------------------------------------------
void save_multi_polygon_as_kml_ex(ge_locations &path,string dir,string name,double Lx,double Ly,int part_size,double azi_deg,double ele_deg)
{
    if(path.empty())	return;
    int count = static_cast<int>(path.size());
    if(part_size <= 0)  part_size = count;
    int N = ciel((double)count / (double)part_size);
    int MinIdx,MaxIdx;
    char filename[256];
    for(int i=0;i<N;i++)
    {
        MinIdx = i*part_size;
        MaxIdx = (i+1)*part_size;
        sprintf(filename,"%spath_%d.kml",dir.c_str(),i+1);
        save_multi_polygon_as_kml(path,filename,name,Lx,Ly,MinIdx,MaxIdx,azi_deg,ele_deg);
    }
}
//---------------------------------------------------------------------
//void add_polygon_as_kml(ofstream &ofile,ge_location p,ge_locations &path,string name,double azi_deg,double ele_deg)
//{
//	string strLine;
//
//	strLine = "\t<Placemark>\n";
//	ofile << strLine;
//	strLine = "\t\t<name>"+name+"</name>\n";
//	ofile << strLine;
//	strLine = "\t\t<styleUrl>#trajRed</styleUrl>\n";
//	ofile << strLine;
//
////    strLine = "<LookAt>";
////    ofile << strLine;
////    strLine = "	<longitude>"+to_string(p.lon)+"</longitude>";
////    ofile << strLine;
////    strLine = "	<latitude>"+to_string(p.lat)+"</latitude>";
////    ofile << strLine;
////    strLine = "	<altitude>"+to_string(p.alt)+"</altitude>";
////    ofile << strLine;
////    strLine = "	<heading>0.0</heading>";
////    ofile << strLine;
////    strLine = "	<tilt>0.0</tilt>";
////    ofile << strLine;
////    strLine = "	<range>500.0</range>";
////    ofile << strLine;
////    strLine = "	<gx:altitudeMode>relativeToGround</gx:altitudeMode>";
////    ofile << strLine;
////    strLine = "</LookAt>";
////    ofile << strLine;
//
//	strLine = "<Polygon id=\"ID\">";
//	ofile << strLine;
//	strLine = "  <extrude>0</extrude>";
//	ofile << strLine;
//	strLine = "  <tessellate>0</tessellate>";
//	ofile << strLine;
//	strLine = "  <altitudeMode>clampToGround</altitudeMode>";
//	ofile << strLine;
////	strLine = "  <altitudeMode>relativeToGround</altitudeMode>";
////	ofile << strLine;
//	strLine = "  <outerBoundaryIs>";
//	ofile << strLine;
//
//	strLine = "\t\t<LinearRing>\n";
//	ofile << strLine;
//	strLine = "\t\t<altitudeMode>relativeToGround</altitudeMode>\n";
//	ofile << strLine;
//	strLine = "\t\t\t<coordinates>\n";
//	ofile << strLine;
//
//	// write path
//	double lon,lat;
//	for(int i=0;i<path.size();i++)
//	{
//		ge_location &T = path[i];
//		strLine = "\t\t\t"+to_string(T.lon)+","+to_string(T.lat)+","+to_string(T.alt)+"\n";
//		ofile << strLine;
//	}
//
//	strLine = "\t\t\t</coordinates>\n";
//	ofile << strLine;
//	strLine = "\t\t</LinearRing>\n";
//	ofile << strLine;
//	strLine = "  </outerBoundaryIs>";
//	ofile << strLine;
//	strLine = "</Polygon>";
//	ofile << strLine;
//
//	strLine = "\t<ExtendedData>\n";
//	ofile << strLine;
//
//	strLine = "\t<Data name=\"LON\"><value>"+to_string(p.lon)+"</value></Data>\n";
//	ofile << strLine;
//	strLine = "\t<Data name=\"LAT\"><value>"+to_string(p.lat)+"</value></Data>\n";
//	ofile << strLine;
//	strLine = "\t<Data name=\"ALT\"><value>"+to_string(p.alt)+"</value></Data>\n";
//	ofile << strLine;
//
////	strLine = "\t<Data name=\"STAY\"><value>0</value></Data>\n";
////	ofile << strLine;
//	strLine = "\t<Data name=\"START_TAKE_PHOTO\"><value>1</value></Data>\n";
//	ofile << strLine;
////	strLine = "\t<Data name=\"START_RECORD\"><value>0</value></Data>\n";
////	ofile << strLine;
////	strLine = "\t<Data name=\"STOP_RECORD\"><value>0</value></Data>\n";
////	ofile << strLine;
//	strLine = "\t<Data name=\"ROTATE_AIRCRAFT\"><value>"+to_string(azi_deg)+"</value></Data>\n";
//	ofile << strLine;
//	strLine = "\t<Data name=\"GIMBAL_PITCH\"><value>"+to_string(ele_deg)+"</value></Data>\n";
//	ofile << strLine;
////	strLine = "\t<Data name=\"CAMERA_ZOOM\"><value>0</value></Data>\n";
////	ofile << strLine;
////	strLine = "\t<Data name=\"CAMERA_FOCUS\"><value>0</value></Data>\n";
////	ofile << strLine;
//	strLine = "\t</ExtendedData>\n";
//	ofile << strLine;
//
//	strLine = "\t</Placemark>\n";
//	ofile << strLine;
//}
//---------------------------------------------------------------------
//void save_multi_polygon_as_kml(ge_locations &path,string filename,string name,double Lx,double Ly,int MaxIdx,double azi_deg,double ele_deg)
//{
//    if(path.empty())	return;
//    string strLine;
//    ofstream ofile;
//    ofile.open(filename.c_str());
//    if(ofile.is_open())
//    {
//        strLine = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
//        ofile << strLine;
//        strLine = "<kml xmlns=\"http://earth.google.com/kml/2.1\">\n";
//        ofile << strLine;
//        strLine = "<Document>\n";
//        ofile << strLine;
//        strLine = "\t<name>"+name+"</name>\n";
//        ofile << strLine;
//
//        strLine = "\t<Style id=\"trajRed\">\n";
//        ofile << strLine;
//
//        strLine = "\t\t<LineStyle>\n";
//        ofile << strLine;
//        strLine = "\t\t\t<color>7f0000ff</color>\n";
//        ofile << strLine;
//        strLine = "\t\t\t<width>6</width>\n";
//        ofile << strLine;
//        strLine = "\t\t</LineStyle>\n";
//        ofile << strLine;
//
//        strLine = "<PolyStyle>";
//        ofile << strLine;
//        strLine = "	<color>407fff00</color>";
//        ofile << strLine;
//        strLine = "</PolyStyle>";
//        ofile << strLine;
//
//        strLine = "\t</Style>\n";
//        ofile << strLine;
//
//        // write path
//        ge_locations rect;
//        for(int i=0;i<map_min(MaxIdx,path.size());i++)
//        {
//            ge_location &p = path[i];
//            create_rect_utm(p,Lx,Ly,rect);
//            add_polygon_as_kml(ofile,p,rect,"rect",azi_deg,ele_deg);
//        }
//
//        strLine = "</Document>\n";
//        ofile << strLine;
//        strLine = "</kml>\n";
//        ofile << strLine;
//
//        ofile.close();
//    }
//}
//---------------------------------------------------------------------
void generate_rect(ge_location p,double Lx,double Ly,int level,ge_locations &path)
{
	double y0,x0;
	ge_location q;
	int x,y;

	LL2UTM2(p.lat,p.lon,y0,x0);
	q.alt = p.alt;

	y = -level;
	for(x=-level;x<=+level;x++)
	{
		UTM2LL2(y0+y*Ly,x0+x*Lx,q.lat,q.lon);
        path.push_back(q);
    }

	x = +level;
	for(y=-level+1;y<=+level;y++)
	{
		UTM2LL2(y0+y*Ly,x0+x*Lx,q.lat,q.lon);
        path.push_back(q);
    }

	y = +level;
	for(x=+level-1;x>=-level;x--)
	{
		UTM2LL2(y0+y*Ly,x0+x*Lx,q.lat,q.lon);
        path.push_back(q);
    }

	x = -level;
	for(y=+level-1;y>=-level+1;y--)
	{
		UTM2LL2(y0+y*Ly,x0+x*Lx,q.lat,q.lon);
        path.push_back(q);
    }
}
//---------------------------------------------------------------------------
void reflect_point(double p0x,double p0y,double p1x,double p1y,double p2x,double p2y,double u,double &rx,double &ry)
{
	double Dx,Dy,CD;
	Dx = (p1x+p2x)/2.0;
	Dy = (p1y+p2y)/2.0;
	CD = sqrt((p0x-Dx)*(p0x-Dx)+(p0y-Dy)*(p0y-Dy));
	rx = p0x+u*(Dx - p0x)/CD;
	ry = p0y+u*(Dy - p0y)/CD;
}
//---------------------------------------------------------------------------
void shift_path(xy_locations &path,double altitude_above_terrain,double ele_deg,double cx,double cy,double p1x,double p1y,double p2x,double p2y,xy_locations &new_path,double &azi_deg)
{
	azi_deg = 0.0;
    new_path.clear();
	if(path.empty())	return;
	xy_location q;

	double L;
	double rx,ry,dx,dy;
	L = altitude_above_terrain * tan(deg2rad(ele_deg));
	reflect_point(cx,cy,p1x,p1y,p2x,p2y,L,rx,ry);
	dx = rx - cx;
    dy = ry - cy;

//	double azi = 0.0;
//	if(ry-cy != 0.0)
//		azi = atan2(rx-cx,ry-cy);
//	else
//	{
//		if(rx-cx > 0.0)
//			azi = +pi_2;
//		else
//		if(rx-cx < 0.0)
//			azi = -pi_2;
//		else
//			azi = 0.0;
//	}
//	azi_deg = rad2deg(azi);

	double azi;
	if(cy-ry != 0.0)
		azi = atan2(cx-rx,cy-ry);
	else
	{
		if(cx-rx > 0.0)
			azi = +pi_2;
		else
		if(cx-rx < 0.0)
			azi = -pi_2;
		else
			azi = 0.0;
	}
	azi_deg = rad2deg(azi);

    for(int i=0;i<path.size();i++)
	{
		xy_location &p = path[i];
		q.x = p.x + dx;
		q.y = p.y + dy;
		new_path.push_back(q);
	}
}
//---------------------------------------------------------------------
void path_bbox(xy_locations &path,double &x_left,double &x_right,double &y_bottom,double &y_top)
{
	x_left = 1e16;
	x_right = -1e16;
	y_bottom = 1e16;
	y_top = -1e16;
	if(path.empty())	return;
	for(int i=0;i<path.size();i++)
	{
		xy_location &p = path[i];
		if(p.x < x_left)	x_left = p.x;
		if(p.x > x_right)	x_right = p.x;
		if(p.y < y_bottom)	y_bottom = p.y;
		if(p.y > y_top)		y_top = p.y;
	}
}
//---------------------------------------------------------------------
void path_center(xy_locations &path,xy_location &center)
{
	center.x = 0;
	center.y = 0;
	if(path.empty())	return;
	int count = static_cast<int>(path.size());
	for(int i=0;i<count;i++)
	{
		xy_location &p = path[i];
		center.x += p.x;
		center.y += p.y;
	}
	center.x /= count;
	center.y /= count;
}
//---------------------------------------------------------------------
void location_rotate(xy_location &p,xy_location &c,double angle_deg,xy_location &r)
{
	double t,ct,st;
	t = deg2rad(angle_deg);

	ct = cos(t);
	st = sin(t);
	r.x = c.x + (p.x-c.x)*ct-(p.y-c.y)*st;
	r.y = c.y + (p.x-c.x)*st+(p.y-c.y)*ct;
}
//---------------------------------------------------------------------
void path_rotate(xy_locations &path,xy_location &center,double angle_deg,xy_locations &new_path)
{
	new_path.clear();
	if(path.empty())	return;
	xy_location q;
	for(int i=0;i<path.size();i++)
	{
		xy_location &p = path[i];
		location_rotate(p,center,angle_deg,q);
		new_path.push_back(q);
	}
}
//---------------------------------------------------------------------
//  Public-domain function by Darel Rex Finley, 2006.
double path_Area(xy_locations &path)
{
	double area = 0.0;
    int points = static_cast<int>(path.size());
	int j = points - 1;
	for(int i=0;i<points;i++)
	{
		xy_location &pi = path[i];
		xy_location &pj = path[j];
		area += (pj.x + pi.x)*(pj.y - pi.y);
		j = i;
	}
	return area * 0.5;
}
//---------------------------------------------------------------------
void path_min_rect(xy_locations &path,double &angle_deg,xy_locations &new_path)
{
	angle_deg = 0;
    new_path.clear();
	if(path.empty())	return;
	double x_left,x_right,y_bottom,y_top,area,min_area,W,H;
	xy_location center;
	path_center(path,center);
	min_area = 1e16;
	for(double t=0;t<180;t+=1.0)
	{
		path_rotate(path,center,t,new_path);

		path_bbox(new_path,x_left,x_right,y_bottom,y_top);
		W = x_right - x_left;
		H = y_top - y_bottom;
		area = W*H;

//		area = path_Area(new_path);

		if(area < min_area)
		{
			min_area = area;
			angle_deg = t;
		}
	}

	xy_location p,q;
	path_rotate(path,center,-angle_deg,new_path);
	path_bbox(new_path,x_left,x_right,y_bottom,y_top);

	new_path.clear();
	p.x = x_left;
	p.y = y_bottom;
	location_rotate(p,center,angle_deg,q);
	new_path.push_back(q);

	p.x = x_left;
	p.y = y_top;
	location_rotate(p,center,angle_deg,q);
	new_path.push_back(q);

	p.x = x_right;
	p.y = y_top;
	location_rotate(p,center,angle_deg,q);
	new_path.push_back(q);

	p.x = x_right;
	p.y = y_bottom;
	location_rotate(p,center,angle_deg,q);
	new_path.push_back(q);
}
//---------------------------------------------------------------------
void path_rect(xy_locations &path,xy_locations &new_path)
{
    new_path.clear();
    if(path.empty())	return;
    double x_left,x_right,y_bottom,y_top;

    path_bbox(path,x_left,x_right,y_bottom,y_top);

    xy_location p;

    new_path.clear();
    p.x = x_left;
    p.y = y_bottom;
    new_path.push_back(p);

    p.x = x_left;
    p.y = y_top;
    new_path.push_back(p);

    p.x = x_right;
    p.y = y_top;
    new_path.push_back(p);

    p.x = x_right;
    p.y = y_bottom;
    new_path.push_back(p);
}
//---------------------------------------------------------------------
void ge2xy(ge_location &p,xy_location &q)
{
	LL2UTM2(p.lat,p.lon,q.y,q.x);
}
//---------------------------------------------------------------------
void ge2xy(ge_locations &path,xy_locations &new_path)
{
	new_path.clear();
	if(path.empty())	return;
	xy_location q;
	for(int i=0;i<path.size();i++)
	{
		ge_location &p = path[i];
		LL2UTM2(p.lat,p.lon,q.y,q.x);
		new_path.push_back(q);
	}
}
//---------------------------------------------------------------------
void xy2ge(xy_location &p,ge_location &q)
{
	UTM2LL2(p.y,p.x,q.lat,q.lon);
}
//---------------------------------------------------------------------
void xy2ge(xy_locations &path,ge_locations &new_path)
{
	new_path.clear();
	if(path.empty())	return;
	ge_location q;
	q.alt = 0.0;
	for(int i=0;i<path.size();i++)
	{
		xy_location &p = path[i];
		UTM2LL2(p.y,p.x,q.lat,q.lon);
		new_path.push_back(q);
	}
}
//---------------------------------------------------------------------
void divide_segment(xy_location &p1,xy_location &p2,int N,xy_locations &new_path,bool bAppend)
{
	if(!bAppend)	new_path.clear();
	if(N <= 1)  return;
	xy_location p,k;
	double L;
	L = sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
	if(L <= 0)  return;
	double dt;
	dt = L/(N-1);
	k.x = (p2.x-p1.x)/L;
	k.y = (p2.y-p1.y)/L;
	for(double t=0;t<L;t+=dt)
	{
		p.x = p1.x + t * k.x;
		p.y = p1.y + t * k.y;
		new_path.push_back(p);
	}
	new_path.push_back(p2);
}
//---------------------------------------------------------------------
//  Globals which should be set before calling this function:
//
//  int    polyCorners  =  how many corners the polygon has
//  float  polyX[]      =  horizontal coordinates of corners
//  float  polyY[]      =  vertical coordinates of corners
//  float  x, y         =  point to be tested
//
//  (Globals are used in this example for purposes of speed.  Change as
//  desired.)
//
//  The function will return YES if the point x,y is inside the polygon, or
//  NO if it is not.  If the point is exactly on the edge of the polygon,
//  then the function may return YES or NO.
//
//  Note that division by zero is avoided because the division is protected
//  by the "if" clause which surrounds it.
//---------------------------------------------------------------------
bool pointInPolygon(xy_location &p,xy_locations &path)
{
    if(path.empty())    return true;
    int polyCorners = static_cast<int>(path.size());
	int j=polyCorners-1;
	bool oddNodes = false;
	for(int i=0;i<polyCorners;i++)
	{
		xy_location &pi = path[i];
		xy_location &pj = path[j];
		if((((pi.y < p.y) && (pj.y >= p.y)) || ((pj.y < p.y) && (pi.y >= p.y))) && ((pi.x <= p.x) || (pj.x <= p.x)))
		{
			oddNodes ^= (pi.x + (p.y-pi.y)/(pj.y-pi.y)*(pj.x-pi.x) < p.x);
		}
		j = i;
	}
	return oddNodes;
}
//---------------------------------------------------------------------
void stitch_rect_ortho(xy_locations &boundary,xy_location &p1,xy_location &p2,xy_location &p3,xy_location &p4,int w,int h,double fov_deg,double alt_above_ground,double Xpercent,double Ypercent,xy_locations &new_path)
{
	new_path.clear();

	double fov_h,fov_v;
	double Lx,Ly,LX,LY;

	double f;
	f = sqrt((double)(w*w+h*h))/(2.0*tan(deg2rad(fov_deg/2.0)));
	fov_h = (float)(2.0*(atan(w/(2.0*f))));
	fov_v = (float)(2.0*(atan(h/(2.0*f))));

	Lx = 2.0 * alt_above_ground * tan(fov_h/2.0);
	Ly = 2.0 * alt_above_ground * tan(fov_v/2.0);
	LX = (1.0 - Xpercent)*Lx;
	LY = (1.0 - Ypercent)*Ly;

	xy_locations path1,path2,tmp;
	int N,M;
	double L1,L2;
	L1 = sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y));
	L2 = sqrt((p4.x-p1.x)*(p4.x-p1.x)+(p4.y-p1.y)*(p4.y-p1.y));
	N = ciel(L1/LX);
	M = ciel(L2/LY);
	divide_segment(p1,p4,M,path1,false);
	divide_segment(p2,p3,M,path2,false);
	for(int i=0;i<path1.size();i++)
	{
		xy_location &q1 = path1[i];
		xy_location &q2 = path2[i];
		if(i % 2 == 0)
			divide_segment(q1,q2,N,tmp,false);
		else
			divide_segment(q2,q1,N,tmp,false);
		for(int j=0;j<tmp.size();j++)
		{
			xy_location &p = tmp[j];
			if(pointInPolygon(p,boundary))
			{
				new_path.push_back(p);
			}
		}
	}
}
//---------------------------------------------------------------------
//void generate_oblique_paths(xy_locations &ortho_path,char *strMissionsPath,xy_location &c,xy_locations &bbox,int w,int h,double fov_deg,double alt_above_ground,double Xpercent,double Ypercent,double ele_deg)
//{
//	if(bbox.empty())	return;
//	char text[256];
//	double f,fov_h,fov_v;
//	f = sqrt((double)(w*w+h*h))/(2.0*tan(deg2rad(fov_deg/2.0)));
//	fov_h = (float)(2.0*(atan(w/(2.0*f))));
//	fov_v = (float)(2.0*(atan(h/(2.0*f))));
//	double Lx = 2.0 * alt_above_ground * tan(fov_h/2.0);
//	double Ly = 2.0 * alt_above_ground * tan(fov_v/2.0);
//
//	// Oblique
//	double azi_deg = 0.0;
//	double azi_deg0 = 0.0;
//	xy_locations new_path;
//	ge_locations new_path_ge;
//
//	int count = (int)bbox.size();
//	for(int i=0;i<count;i++)
//	{
//		xy_location &a = bbox[i % count];
//		xy_location &b = bbox[(i+1) % count];
//		sprintf(text,"%s/mission_%d.kml",strMissionsPath,i+1);
//		shift_path(ortho_path,alt_above_ground,ele_deg,c.x,c.y,a.x,a.y,b.x,b.y,new_path,azi_deg);
//        if(i == 0)  azi_deg0 = azi_deg;
//		xy2ge(new_path,new_path_ge);
//		if(i % 2 == 0)
//			save_multi_polygon_as_kml(new_path_ge,text,"MapViewer",Lx,Ly,new_path.size(),azi_deg,ele_deg);
//		else
//			save_multi_polygon_as_kml(new_path_ge,text,"MapViewer",Ly,Lx,new_path.size(),azi_deg,ele_deg);
//	}
//
//	// Ortho
//	sprintf(text,"%s/mission_%d.kml",strMissionsPath,0);
//	ge_locations ortho_path_ge;
//	xy2ge(ortho_path,ortho_path_ge);
//	save_multi_polygon_as_kml(ortho_path_ge,text,"MapViewer",Lx,Ly,ortho_path_ge.size(),azi_deg0,-90.0);
//}
//---------------------------------------------------------------------------
void generate_oblique_paths(xy_locations &ortho_path,char *strMissionsPath,xy_location &c,xy_locations &bbox,int w,int h,double fov_deg,double alt_above_ground,double ele_deg,int path_size)
{
    if(bbox.empty())	return;
	double f,fov_h,fov_v;
    f = sqrt((double)(w*w+h*h))/(2.0*tan(deg2rad(fov_deg/2.0)));
    fov_h = (float)(2.0*(atan(w/(2.0*f))));
    fov_v = (float)(2.0*(atan(h/(2.0*f))));
    double Lx = 2.0 * alt_above_ground * tan(fov_h/2.0);
    double Ly = 2.0 * alt_above_ground * tan(fov_v/2.0);

    // Oblique
    double azi_deg = 0.0;
    xy_locations new_path;
    ge_locations new_path_ge;
    char dir[256];

    int count = (int)bbox.size();
    for(int i=0;i<count;i++)
    {
        xy_location &a = bbox[i % count];
        xy_location &b = bbox[(i+1) % count];
        sprintf(dir,"%s/mission_%d_",strMissionsPath,i+1);
        shift_path(ortho_path,alt_above_ground,ele_deg,c.x,c.y,a.x,a.y,b.x,b.y,new_path,azi_deg);
		xy2ge(new_path,new_path_ge);

		for(int j=0;j<new_path_ge.size();j++)
		{
			ge_location &p = new_path_ge[j];
			p.alt = alt_above_ground;
		}
        save_multi_polygon_as_kml_ex(new_path_ge,dir,"MapViewer",Lx,Ly,path_size,azi_deg,ele_deg);
    }

    // Ortho
    sprintf(dir,"%s/mission_%d_",strMissionsPath,0);
    ge_locations ortho_path_ge;
    xy2ge(ortho_path,ortho_path_ge);

	for(int j=0;j<ortho_path_ge.size();j++)
	{
		ge_location &p = ortho_path_ge[j];
		p.alt = alt_above_ground;
	}
	save_multi_polygon_as_kml_ex(ortho_path_ge,dir,"MapViewer",Lx,Ly,path_size,0.0,-90.0);
}
//---------------------------------------------------------------------------
//  Determines the intersection point of the line segment defined by points A and B
//  with the line segment defined by points C and D.
//
//  Returns YES if the intersection point was found, and stores that point in X,Y.
//  Returns NO if there is no determinable intersection point, in which case X,Y will
//  be unmodified.
//---------------------------------------------------------------------
bool lineSegmentIntersection(
double Ax, double Ay,
double Bx, double By,
double Cx, double Cy,
double Dx, double Dy,
double *X, double *Y) {

  double  distAB, theCos, theSin, newX, ABpos ;

  //  Fail if either line segment is zero-length.
  if ((Ax==Bx && Ay==By) || (Cx==Dx && Cy==Dy)) return false;

  //  Fail if the segments share an end-point.
  if ((Ax==Cx && Ay==Cy) || (Bx==Cx && By==Cy) ||  (Ax==Dx && Ay==Dy) || (Bx==Dx && By==Dy)) {
	return false; }

  //  (1) Translate the system so that point A is on the origin.
  Bx-=Ax; By-=Ay;
  Cx-=Ax; Cy-=Ay;
  Dx-=Ax; Dy-=Ay;

  //  Discover the length of segment A-B.
  distAB=sqrt(Bx*Bx+By*By);

  //  (2) Rotate the system so that point B is on the positive X axis.
  theCos=Bx/distAB;
  theSin=By/distAB;
  newX=Cx*theCos+Cy*theSin;
  Cy  =Cy*theCos-Cx*theSin; Cx=newX;
  newX=Dx*theCos+Dy*theSin;
  Dy  =Dy*theCos-Dx*theSin; Dx=newX;

  //  Fail if segment C-D doesn't cross line A-B.
  if ((Cy<0. && Dy<0.) || (Cy>=0. && Dy>=0.)) return false;

  //  (3) Discover the position of the intersection point along line A-B.
  ABpos=Dx+(Cx-Dx)*Dy/(Dy-Cy);

  //  Fail if segment C-D crosses line A-B outside of segment A-B.
  if (ABpos<0. || ABpos>distAB) return false;

  //  (4) Apply the discovered position to line A-B in the original coordinate system.
  *X=Ax+ABpos*theCos;
  *Y=Ay+ABpos*theSin;

  //  Success.
  return true;
}
//---------------------------------------------------------------------
#define  CIRCLE_RADIANS     6.283185307179586476925286766559
#define  MAX_SEGS        10000
//---------------------------------------------------------------------
//  Determine the radian angle of the specified point (as it relates to the
//  origin).
//
//  Warning:  Do not pass zero in both parameters, as this will cause a division-
//            by-zero.
//---------------------------------------------------------------------
double angleOf(double x, double y)
{
	double dist=sqrt(x*x+y*y);
	if (y >= 0.0)
		return acos( x/dist);
	else
		return acos(-x/dist)+.5*CIRCLE_RADIANS;
}
//---------------------------------------------------------------------
//  Returns the perimeter polygon in newX and newY (which must have at least
//  MAX_SEGS elements each to prevent the possibility of overrun).  "corners" is
//  used to pass in the size of the original polygon, and to return the size of
//  the new, perimeter polygon.
//
//  If for any reason the procedure fails, it will return NO in its bool return
//  value, in which case the values in "newX", "newY", and "corners" should not
//  be trusted.
//---------------------------------------------------------------------
bool polygonPerimeter(double *x, double *y, int *corners, double *newX, double *newY)
{
	double  segSx[MAX_SEGS], segSy[MAX_SEGS], segEx[MAX_SEGS], segEy[MAX_SEGS];
	double  segAngle[MAX_SEGS], intersectX, intersectY ;
	double  startX=x[0], startY=y[0], lastAngle=.5*CIRCLE_RADIANS;
	double  a, b, c, d, e = 0, f = 0, angleDif, bestAngleDif ;
	int     j=(*corners)-1, segs=0 ;

	if ((*corners) > MAX_SEGS) return false;

	//  1,3.  Reformulate the polygon as a set of line segments, and choose a
	//        starting point that must be on the perimeter.
	for(int i=0;i<(*corners);i++)
	{
		if(x[i]!=x[j] || y[i]!=y[j])
		{
			segSx[segs] = x[i];
			segSy[segs] = y[i];
			segEx[segs] = x[j];
			segEy[segs++] = y[j];
		}
		j = i;
		if((y[i]>startY) || (y[i]==startY && x[i]<startX))
		{
			startX = x[i];
			startY = y[i];
		}
	}
	if (segs == 0) return false;

	//  2.  Break the segments up at their intersection points.
	for(int i=0;i<segs-1;i++)
	{
		for(j=i+1;j<segs;j++)
		{
			if(lineSegmentIntersection(segSx[i],segSy[i],segEx[i],segEy[i],segSx[j],segSy[j],segEx[j],segEy[j],&intersectX,&intersectY))
			{
				if((intersectX != segSx[i] || intersectY != segSy[i]) &&  (intersectX != segEx[i] || intersectY != segEy[i]))
				{
					if (segs == MAX_SEGS) return false;
					segSx[segs] = segSx[i];
					segSy[segs] = segSy[i];
					segEx[segs] = intersectX;
					segEy[segs++] = intersectY;
					segSx[i] = intersectX;
					segSy[i] = intersectY;
				}
				if((intersectX != segSx[j] || intersectY != segSy[j]) &&  (intersectX != segEx[j] || intersectY != segEy[j]))
				{
					if (segs == MAX_SEGS) return false;
					segSx[segs] = segSx[j];
					segSy[segs] = segSy[j];
					segEx[segs] = intersectX;
					segEy[segs++] = intersectY;
					segSx[j] = intersectX;
					segSy[j] = intersectY;
				}
			}
		}
	}

	//  Calculate the angle of each segment.
	for(int i=0;i<segs;i++) segAngle[i] = angleOf(segEx[i]-segSx[i],segEy[i]-segSy[i]);

	//  4.  Build the perimeter polygon.
	c = startX;
	d = startY;
	a = c - 1.0;
	b = d;
	newX[0] = c;
	newY[0] = d;
	*corners = 1;
	while(true)
	{
		bestAngleDif = CIRCLE_RADIANS;
		for(int i=0;i<segs;i++)
		{
			if(segSx[i] == c && segSy[i] == d && (segEx[i] != a || segEy[i] != b))
			{
				angleDif = lastAngle-segAngle[i];
				while(angleDif >= CIRCLE_RADIANS) angleDif -= CIRCLE_RADIANS;
				while(angleDif < 0) angleDif += CIRCLE_RADIANS;
				if(angleDif < bestAngleDif)
				{
					bestAngleDif = angleDif;
					e = segEx[i];
					f = segEy[i];
				}
			}
			if(segEx[i] == c && segEy[i] == d && (segSx[i] != a || segSy[i] != b))
			{
				angleDif = lastAngle-segAngle[i]+.5*CIRCLE_RADIANS;
				while(angleDif >= CIRCLE_RADIANS) angleDif -= CIRCLE_RADIANS;
				while(angleDif < 0) angleDif += CIRCLE_RADIANS;
				if(angleDif < bestAngleDif)
				{
					bestAngleDif = angleDif;
					e = segSx[i];
					f = segSy[i];
				}
			}
		}
		if((*corners) > 1 && c == newX[0] && d == newY[0] && e == newX[1] && f == newY[1])
		{
			(*corners)--;
			return true;
		}
		if(bestAngleDif == CIRCLE_RADIANS || (*corners) == MAX_SEGS) return false;
		newX[*corners] = e;
		lastAngle -= bestAngleDif+.5*CIRCLE_RADIANS;
		newY[(*corners)++] = f;
		a = c;
		b = d;
		c = e;
		d = f;
	}
}
//---------------------------------------------------------------------
bool polygonPerimeter(xy_locations &path,xy_locations &new_path)
{
	new_path.clear();
	int corners = static_cast<int>(path.size());
//	double *x = new double[corners];
//	double *y = new double[corners];
//	double *newX = new double[corners];
//	double *newY = new double[corners];

	double  x[MAX_SEGS], y[MAX_SEGS], newX[MAX_SEGS], newY[MAX_SEGS];

	for(int i=0;i<corners;i++)
	{
		xy_location &p = path[i];
		x[i] = p.x;
		y[i] = p.y;
	}
	bool res = polygonPerimeter(x,y,&corners,newX,newY);
	xy_location q;
	for(int i=0;i<corners;i++)
	{
		q.x = newX[i];
		q.y = newY[i];
		new_path.push_back(q);
	}

//	if(x)
//	{
//		delete[] x;
//		x = NULL;
//	}
//	if(y)
//	{
//		delete[] y;
//		y = NULL;
//	}
//	if(newX)
//	{
//		delete[] newX;
//		newX = NULL;
//	}
//	if(newY)
//	{
//		delete[] newY;
//		newY = NULL;
//	}
	return res;
}
//---------------------------------------------------------------------
/*
void insetPolygon(double *x, double *y, int corners, double insetDist) {

  double  startX=x[0], startY=y[0], a, b, c, d, e, f ;
  int     i ;

  //  Polygon must have at least three corners to be inset.
  if (corners<3) return;

  //  Inset the polygon.
  c=x[corners-1]; d=y[corners-1]; e=x[0]; f=y[0];
  for (i=0; i<corners-1; i++) {
    a=c; b=d; c=e; d=f; e=x[i+1]; f=y[i+1];
    insetCorner(a,b,c,d,e,f,&x[i],&y[i],insetDist); }
  insetCorner(c,d,e,f,startX,startY,&x[i],&y[i],insetDist); }



//  Given the sequentially connected points (a,b), (c,d), and (e,f), this
//  function returns, in (C,D), a bevel-inset replacement for point (c,d).
//
//  Note:  If vectors (a,b)->(c,d) and (c,d)->(e,f) are exactly 180° opposed,
//         or if either segment is zero-length, this function will do
//         nothing; i.e. point (C,D) will not be set.

void insetCorner(
double  a, double  b,   //  previous point
double  c, double  d,   //  current point that needs to be inset
double  e, double  f,   //  next point
double *C, double *D,   //  storage location for new, inset point
double insetDist) {     //  amount of inset (perpendicular to each line segment)

  double  c1=c, d1=d, c2=c, d2=d, dx1, dy1, dist1, dx2, dy2, dist2, insetX, insetY ;

  //  Calculate length of line segments.
  dx1=c-a; dy1=d-b; dist1=sqrt(dx1*dx1+dy1*dy1);
  dx2=e-c; dy2=f-d; dist2=sqrt(dx2*dx2+dy2*dy2);

  //  Exit if either segment is zero-length.
  if (dist1==0. || dist2==0.) return;

  //  Inset each of the two line segments.
  insetX= dy1/dist1*insetDist; a+=insetX; c1+=insetX;
  insetY=-dx1/dist1*insetDist; b+=insetY; d1+=insetY;
  insetX= dy2/dist2*insetDist; e+=insetX; c2+=insetX;
  insetY=-dx2/dist2*insetDist; f+=insetY; d2+=insetY;

  //  If inset segments connect perfectly, return the connection point.
  if (c1==c2 && d1==d2) {
    *C=c1; *D=d1; return; }

  //  Return the intersection point of the two inset segments (if any).
  if (lineIntersection(a,b,c1,d1,c2,d2,e,f,&insetX,&insetY)) {
	*C=insetX; *D=insetY; }}
//---------------------------------------------------------------------
#define  CIRCLE_RADIANS  6.283185307179586476925286766559
//---------------------------------------------------------------------
//  Determines the radian angle of the specified point (as it relates to the origin).
//
//  Warning:  Do not pass zero in both parameters, as this will cause division-by-zero.
//---------------------------------------------------------------------
double angleOf(double x, double y) {

  double  dist=sqrt(x*x+y*y) ;

  if (y>=0.) return acos( x/dist)                  ;
  else       return acos(-x/dist)+.5*CIRCLE_RADIANS; }
//---------------------------------------------------------------------
//  Pass in a set of 2D points in x,y,points.  Returns a polygon in polyX,polyY,polyCorners.
//
//  To be safe, polyX and polyY should have enough space to store all the points passed in x,y,points.
//---------------------------------------------------------------------
void findSmallestPolygon(double *x, double *y, long points, double *polyX, double *polyY, long *polyCorners) {

  double  newX=x[0], newY=y[0], xDif, yDif, oldAngle=.5*CIRCLE_RADIANS, newAngle, angleDif, minAngleDif ;
  long    i ;

  //  Find a starting point.
  for (i=0; i<points; i++) if (y[i]>newY || y[i]==newY && x[i]<newX) {
	newX=x[i]; newY=y[i]; }
  *polyCorners=0;

  //  Polygon-construction loop.
  while (!(*polyCorners) || newX!=polyX[0] || newY!=polyY[0]) {
	polyX[*polyCorners]=newX;
	polyY[*polyCorners]=newY; minAngleDif=CIRCLE_RADIANS;
	for (i=0; i<points; i++) {
	  xDif=x[i]-polyX[*polyCorners];
	  yDif=y[i]-polyY[*polyCorners];
	  if (xDif || yDif) {
		newAngle=angleOf(xDif,yDif);     angleDif =oldAngle-newAngle;
		while (angleDif< 0.            ) angleDif+=CIRCLE_RADIANS;
		while (angleDif>=CIRCLE_RADIANS) angleDif-=CIRCLE_RADIANS;
		if (angleDif<minAngleDif) {
		  minAngleDif=angleDif; newX=x[i]; newY=y[i]; }}}
	(*polyCorners)++; oldAngle+=.5*CIRCLE_RADIANS-minAngleDif; }}
//---------------------------------------------------------------------
double splinePolyArea(double *poly) {

  #define  SPLINE   9999.   //  These constants must be well outside
  #define  END     -9999.   //  the range of your polygon.

  double  area=0., a, b, Sx, Sy, Ex, Ey ;
  int     i=0, j, k ;

  while (poly[i]!=END) {

	j=i+2; if (poly[i]==SPLINE) j++;
	if (poly[j]==END) j=0;

	if (poly[i]!=SPLINE && poly[j]!=SPLINE) {   //  STRAIGHT LINE
	  area+=(poly[i]+poly[j])*(poly[i+1]-poly[j-1]); }

	else if (poly[j]==SPLINE) {                 //  SPLINE CURVE
	  a=poly[j+1]; b=poly[j+2]; k=j+3; if (poly[k]==END) k=0;
	  if (poly[i]!=SPLINE) {
		Sx=poly[i]; Sy=poly[i+1]; }
	  else {   //  interpolate hard corner
		Sx=(poly[i+1]+poly[j+1])/2.; Sy=(poly[i+2]+poly[j+2])/2.; }
	  if (poly[k]!=SPLINE) {
		Ex=poly[k]; Ey=poly[k+1]; }
	  else {   //  interpolate hard corner
		Ex=(poly[j+1]+poly[k+1])/2.; Ey=(poly[j+2]+poly[k+2])/2.; }
	  area+= (Sx+Ex)*(Sy-Ey);
	  area+=((Sx+a )*(Sy-b )+(a+Ex)*(b-Ey)+(Ex+Sx)*(Ey-Sy))*(2./3.); }

	if (poly[i]==SPLINE) i++;
	i+=2; }

  return area*.5; }
//---------------------------------------------------------------------
double splineXMax(double Sx, double a, double Ex) {

  double  c=Sx-a, d=c+Ex-a, F, X, max=Sx ;

  if (Ex>Sx) max=Ex;
  if (d!=0.) {
    F=c/d;
    if (F>0. && F<1.) {
      X=Sx-c*F; if (X>max) max=X; }}

  return max; }



double splineXMin(double Sx, double a, double Ex) {

  double  c=Sx-a, d=c+Ex-a, F, X, min=Sx ;

  if (Ex<Sx) min=Ex;
  if (d!=0.) {
    F=c/d;
    if (F>0. && F<1.) {
      X=Sx-c*F; if (X<min) min=X; }}

  return min; }



double splineYMax(double Sy, double b, double Ey) {

  double  c=Sy-b, d=c+Ey-b, F, Y, max=Sy ;

  if (Ey>Sy) max=Ey;
  if (d!=0.) {
    F=c/d;
    if (F>0. && F<1.) {
      Y=Sy-c*F; if (Y>max) max=Y; }}

  return max; }



double splineYMin(double Sy, double b, double Ey) {

  double c=Sy-b, d=c+Ey-b, F, Y, min=Sy ;

  if (Ey<Sy) min=Ey;
  if (d!=0.) {
    F=c/d;
    if (F>0. && F<1.) {
      Y=Sy-c*F; if (Y<min) min=Y; }}

  return min; }

//  Determines the intersection point of the line defined by points A and B with the
//  line defined by points C and D.
//
//  Returns YES if the intersection point was found, and stores that point in X,Y.
//  Returns NO if there is no determinable intersection point, in which case X,Y will
//  be unmodified.

bool lineIntersection(
double Ax, double Ay,
double Bx, double By,
double Cx, double Cy,
double Dx, double Dy,
double *X, double *Y) {

  double  distAB, theCos, theSin, newX, ABpos ;

  //  Fail if either line is undefined.
  if (Ax==Bx && Ay==By || Cx==Dx && Cy==Dy) return NO;

  //  (1) Translate the system so that point A is on the origin.
  Bx-=Ax; By-=Ay;
  Cx-=Ax; Cy-=Ay;
  Dx-=Ax; Dy-=Ay;

  //  Discover the length of segment A-B.
  distAB=sqrt(Bx*Bx+By*By);

  //  (2) Rotate the system so that point B is on the positive X axis.
  theCos=Bx/distAB;
  theSin=By/distAB;
  newX=Cx*theCos+Cy*theSin;
  Cy  =Cy*theCos-Cx*theSin; Cx=newX;
  newX=Dx*theCos+Dy*theSin;
  Dy  =Dy*theCos-Dx*theSin; Dx=newX;

  //  Fail if the lines are parallel.
  if (Cy==Dy) return NO;

  //  (3) Discover the position of the intersection point along line A-B.
  ABpos=Dx+(Cx-Dx)*Dy/(Dy-Cy);

  //  (4) Apply the discovered position to line A-B in the original coordinate system.
  *X=Ax+ABpos*theCos;
  *Y=Ay+ABpos*theSin;

  //  Success.
  return YES; }
*/
//---------------------------------------------------------------------
#define PI 3.14
#define R 8.31 // Gas constant
#define G 6.67e-11
#define k 8.988e-9
#define PlanetMass 5.974e24
#define PlanetRadius 6378000
//---------------------------------------------------------------------------
double Gravity(double h,double time_step,double gravity,bool const_gravity)
{
	if(const_gravity)
	{
		return(gravity*time_step*time_step);
	}
	else
	{
		double M=PlanetMass,
				r=PlanetRadius,
				t=time_step,
				F=(t*t)*((M*G)/((r+h)*(r+h)));
		return(F);
	}
}
//---------------------------------------------------------------------------
void solve_projectile(double &gun_lon,double &gun_lat,double target_lon,double target_lat,int iterations,double z0,double time_step,double velocity0,double angle0,double diameter0,double mass0,double wind0,double error,double dencity0,double cofficient0,double temp0,double gravity0,bool const_gravity0)
{
	double l,x,y,z,lon,lat,alt,terrain_alt,lv,zv,t,r,A,p,p0,C,m,T,w,maxZ,maxL;
	double gun_X,gun_Y;
	double target_X,target_Y;
	double L,dL;
	double V,beta;
	double kx,ky;
	for(int i=0;i<iterations;i++)
	{
        double terrain_alt0 = mv_get_height(gun_lon,gun_lat);

        l = 0;
        z = z0;
        if(z < terrain_alt0)    z = terrain_alt0;
        maxZ = 0;
        maxL = 0;

		LL2UTM2(gun_lat,gun_lon,gun_Y,gun_X);
		LL2UTM2(target_lat,target_lon,target_Y,target_X);

		L = sqrt((target_X - gun_X)*(target_X - gun_X)+(target_Y - gun_Y)*(target_Y - gun_Y));
		kx = (target_X - gun_X)/L;
		ky = (target_Y - gun_Y)/L;

		// first of all get timestep
		t = time_step;

		// get the rest of the initial values
		lv = velocity0*cos(angle0/180*PI)*t;
		zv = velocity0*sin(angle0/180*PI)*t;
		r = diameter0/2.0;
		A = r*r*PI;
		p0 = dencity0;
		C = cofficient0;
		m = mass0;
		T = temp0;
		w = wind0*t;

		// do the simulation
		while(l <= 2*L)
		{
			// kml
			x = gun_X + l * kx;
			y = gun_Y + l * ky;
			UTM2LL2(y,x,lat,lon);
			alt = z;
			terrain_alt = mv_get_height(lon,lat);

			// check for new max value
			if(z > maxZ)
			{
				maxZ = z;
			}
			if(l > maxL)
			{
				maxL = l;
			}

			// move
			l += lv;
			z += zv;

			// calculate dencity
			p = p0*exp(-(Gravity(z,time_step,gravity0,const_gravity0)*z)/(R*T));

			// calculate velocity
			// the wind w is only relative velocity
			V = sqrt((lv-w)*(lv-w)+zv*zv);

			// calculate angle
			if(V != 0)
			{
				beta = asin(zv/V);
			}
			else
			{
				// if the total velocity is zero the angle does not matter
				beta = 0;
			}

			// calculate  drag
			V = (0.5*p*V*V*A*C)/m;

			//apply drag
			if(lv > 0)
			{
				lv -= V*cos(beta);
			}
			else
			{
				lv += V*cos(beta);
			}
			if(zv>0)
			{
				zv -= V*sin(beta);
			}
			else
			{
				zv += V*sin(beta);
			}

			//apply gravity
			zv -= Gravity(z,time_step,gravity0,const_gravity0);

			if(alt < terrain_alt) break;
		} // end main loop

		dL = L - maxL;
		x = gun_X + dL * kx;
		y = gun_Y + dL * ky;
		UTM2LL2(y,x,gun_lat,gun_lon);
		if(fabs(dL) < error)    break;
	}
}
//---------------------------------------------------------------------------
void save_projectile(const char *filename,double gun_lon,double gun_lat,double target_lon,double target_lat,double z0,double time_step,double velocity0,double angle0,double diameter0,double mass0,double wind0,double error,double dencity0,double cofficient0,double temp0,double gravity0,bool const_gravity0)
{
	double l,x,y,z,lon,lat,alt,terrain_alt,lv,zv,t,r,A,p,p0,C,m,T,w,maxZ,maxL;
	double gun_X,gun_Y;
	double target_X,target_Y;
	double L,dL;
	double V,beta;
	double kx,ky;
    double terrain_alt0 = mv_get_height(gun_lon,gun_lat);

	l = 0;
	z = z0;
	if(z < terrain_alt0)    z = terrain_alt0;
	maxZ = 0;
	maxL = 0;

	LL2UTM2(gun_lat,gun_lon,gun_Y,gun_X);
	LL2UTM2(target_lat,target_lon,target_Y,target_X);

	L = sqrt((target_X - gun_X)*(target_X - gun_X)+(target_Y - gun_Y)*(target_Y - gun_Y));
	kx = (target_X - gun_X)/L;
	ky = (target_Y - gun_Y)/L;

	// first of all get timestep
	t = time_step;

	// get the rest of the initial values
	lv = velocity0*cos(angle0/180*PI)*t;
	zv = velocity0*sin(angle0/180*PI)*t;
	r = diameter0/2.0;
	A = r*r*PI;
	p0 = dencity0;
	C = cofficient0;
	m = mass0;
	T = temp0;
	w = wind0*t;

	ofstream myfile;
	myfile.open (filename);

	// do the simulation
	while(l <= 2*L)
	{
		// kml
		x = gun_X + l * kx;
		y = gun_Y + l * ky;
		UTM2LL2(y,x,lat,lon);
		alt = z;
		terrain_alt = mv_get_height(lon,lat);

		myfile << l << "," << z << std::endl;

		// check for new max value
		if(z > maxZ)
		{
			maxZ = z;
		}
		if(l > maxL)
		{
			maxL = l;
		}

		// move
		l += lv;
		z += zv;

		// calculate dencity
		p = p0*exp(-(Gravity(z,time_step,gravity0,const_gravity0)*z)/(R*T));

		// calculate velocity
		// the wind w is only relative velocity
		V = sqrt((lv-w)*(lv-w)+zv*zv);

		// calculate angle
		if(V != 0)
		{
			beta = asin(zv/V);
		}
		else
		{
			// if the total velocity is zero the angle does not matter
			beta = 0;
		}

		// calculate  drag
		V = (0.5*p*V*V*A*C)/m;

		//apply drag
		if(lv > 0)
		{
			lv -= V*cos(beta);
		}
		else
		{
			lv += V*cos(beta);
		}
		if(zv>0)
		{
			zv -= V*sin(beta);
		}
		else
		{
			zv += V*sin(beta);
		}

		//apply gravity
		zv -= Gravity(z,time_step,gravity0,const_gravity0);

		if(alt < terrain_alt) break;
	} // end main loop
	myfile.close();
}
//---------------------------------------------------------------------------
