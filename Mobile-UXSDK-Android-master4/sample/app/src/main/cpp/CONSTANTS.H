//constants.h

#ifndef CONSTANTS_H
#define CONSTANTS_H

#include "LatLong-UTMconversion.h"

const double PI = 3.14159265;
const double FOURTHPI = PI / 4;
const double deg2rad = PI / 180;
const double rad2deg = 180.0 / PI;

static Ellipsoid ellipsoid[] =
{//  id, Ellipsoid name, Equatorial Radius, square of eccentricity	
	Ellipsoid(-1, const_cast<char *>("Placeholder"), 0, 0),//placeholder only, To allow array indices to match id numbers
	Ellipsoid(1, const_cast<char *>("Airy"), 6377563, 0.00667054),
	Ellipsoid( 2, const_cast<char *>("Australian National"), 6378160, 0.006694542),
	Ellipsoid( 3, const_cast<char *>("Bessel 1841"), 6377397, 0.006674372),
	Ellipsoid( 4, const_cast<char *>("Bessel 1841 (Nambia) "), 6377484, 0.006674372),
	Ellipsoid( 5, const_cast<char *>("Clarke 1866"), 6378206, 0.006768658),
	Ellipsoid( 6, const_cast<char *>("Clarke 1880"), 6378249, 0.006803511),
	Ellipsoid( 7, const_cast<char *>("Everest"), 6377276, 0.006637847),
	Ellipsoid( 8, const_cast<char *>("Fischer 1960 (Mercury) "), 6378166, 0.006693422),
	Ellipsoid( 9, const_cast<char *>("Fischer 1968"), 6378150, 0.006693422),
	Ellipsoid( 10, const_cast<char *>("GRS 1967"), 6378160, 0.006694605),
	Ellipsoid( 11, const_cast<char *>("GRS 1980"), 6378137, 0.00669438),
	Ellipsoid( 12, const_cast<char *>("Helmert 1906"), 6378200, 0.006693422),
	Ellipsoid( 13, const_cast<char *>("Hough"), 6378270, 0.00672267),
	Ellipsoid( 14, const_cast<char *>("International"), 6378388, 0.00672267),
	Ellipsoid( 15, const_cast<char *>("Krassovsky"), 6378245, 0.006693422),
	Ellipsoid( 16, const_cast<char *>("Modified Airy"), 6377340, 0.00667054),
	Ellipsoid( 17, const_cast<char *>("Modified Everest"), 6377304, 0.006637847),
	Ellipsoid( 18, const_cast<char *>("Modified Fischer 1960"), 6378155, 0.006693422),
	Ellipsoid( 19, const_cast<char *>("South American 1969"), 6378160, 0.006694542),
	Ellipsoid( 20, const_cast<char *>("WGS 60"), 6378165, 0.006693422),
	Ellipsoid( 21, const_cast<char *>("WGS 66"), 6378145, 0.006694542),
	Ellipsoid( 22, const_cast<char *>("WGS-72"), 6378135, 0.006694318),
	Ellipsoid( 23, const_cast<char *>("WGS-84"), 6378137, 0.00669438)
};

#endif
