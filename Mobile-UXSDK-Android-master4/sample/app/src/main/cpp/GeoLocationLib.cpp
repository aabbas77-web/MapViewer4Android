//---------------------------------------------------------------------------
#include <math.h>
#include <stdio.h>
#include <math.h>
#pragma hdrstop
//---------------------------------------------------------------------------
#include "GeoLocationLib.h"
//---------------------------------------------------------------------------
#ifndef MV_EMBEDED
#include "utilities.h"
#endif // MV_EMBEDED
//---------------------------------------------------------------------------
//#pragma package(smart_init)
//---------------------------------------------------------------------------
//---------------------------Localization------------------------------------
//---------------------------------------------------------------------------
#define EARTH_RADIUS	(6378137.0)
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
//---------------------------------------------------------------------------
t_dem_header s_header;
static FILE *s_fflash = NULL;
static int s_size = sizeof(float);
static bool s_is_wrd = false;
static bool s_is_byt = false;
static long s_offset0 = 0;
//---------------------------------------------------------------------------
typedef struct
{
	double x,y,z;
} mv_point3;
//---------------------------------------------------------------------------
void Convert_Geo_To_XY(double Lon,double Lat,int &zone,double &X,double &Y)
{
	try{
		//ANGLE0
		//7 CLS: INPUT "DRGA(toL)"; A, "DKEK(toL)" ;C, "SANI(toL)";D
		//8 CLS: INPUT "DRGA(ard)"; H, "DKEK(ard)" ;K, "SANI(ard)�;S
		//9 L=DEG (A, C, D) :F= DEG(H, K, S)
		//10 DIM Z(6)
		//11 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
		//15 CLS: INPUT "RKM ALMATEKA"; I
		//16 IF I=0 OR I>6 THEN 15
		//18 DL=L-Z (I)
		//20 LR= DL*PI/180
		//30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
		//32 E= 0.00676817* ( (COSF) ^2)
		//34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
		//36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
		//38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5
		//-	(TANF) ^2+9*E+4*E^2)
		//40 CLS: BEEP0: BEEP1: PRINT "X"; I; " = ";INTX, "Y";I; " = ";INTY

		double z[] = { 25.5, 28.5, 31.5, 34.5, 37.5, 40.5, 43.5 };
		double F = Lat * pi1 / 180.0;
		double DL = 0.0;

		if (Lon >= 24.0 && Lon < 27.0)
			zone = 1;
		else if (Lon >= 27.0 && Lon < 30.0)
			zone = 2;
		else if (Lon >= 30.0 && Lon < 33.0)
			zone = 3;
		else if (Lon >= 33.0 && Lon < 37.5)
			zone = 4;
		else if (Lon >= 37.5 && Lon < 39.0)
			zone = 5;
		else if (Lon >= 39.0 && Lon < 42.0)
			zone = 6;
		else
			zone = 7;

		DL = Lon - z[zone - 1];
		double LR = DL * pi1 / 180.0;

		//30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
		double N = 6377431.24 / sqrt(1.0 - (0.00672267 * pow(sin(F), 2.0)));//��� ����� ���� ���

		//32 E= 0.00676817* ( (COSF) ^2)
		double E = 0.00676817 * pow(cos(F),2.0);

		//34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
		double B = 111119.87 * Lat - 16107.03 * sin(2.0 * F) + 17.4 * sin(4.0 * F);

		//36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
		X = LR * N * cos(F) + (pow(LR, 3.0) / 6.0) * N * (pow(cos(F), 3.0)) * (1.0 - pow(tan(F), 2.0)) + E + 200000.0;

		//38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5 -	(TANF) ^2+9*E+4*E^2)
		Y = B + ((pow(LR, 2.0) / 2.0) * N * pow(cos(F), 2.0) * tan(F)) +
			((pow(LR, 4.0) / 24.0) * N * pow(cos(F), 4.0) * tan(F) * (5.0 - pow(tan(F), 2.0) + 9.0 * E + 4.0 * pow(E, 2.0)));
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void Convert_XY_To_Geo(double X,double Y,int zone,double &lon,double &lat)
{
	try{
		//  2 CLS:INPUT "X= "; X, "Y= "; Y, "RKM AL MANTEKA= "; I
		//3 DIM Z(6)
		//4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
		//5 X= (X- 200000)/0.99985: Y= Y/0.99985
		//6 FA=(Y- 3541905.65)/110934+32
		//8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
		//10 FA= (Y - BA)  / 110934 + FA
		//12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
		//14 E= 0.00676817 * ((COSFA) ^2)
		//16 C=((X/NA) ^2/2) * TANFA*(1+E) : D=(X/NA)^4/24*(TANFA*(5
		//+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
		//18 F=FA -(C -D)*180/PI
		//20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
		//22 L= (O- P)*180/PI+ Z(I)
		//24 CLS: PRINT "DEG(TOL) "; I; " = "DMS$(L), "DEG(ARD)"; I; "="DMS$ (F)

		//4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
		double z[] = { 25.5, 28.5, 31.5, 34.5, 37.5, 40.5, 43.5 };

		//5 X= (X- 200000)/0.99985: Y= Y/0.99985
		X = (X - 200000.0) / 0.99985;
		Y = Y / 0.99985;

		//6 FA=(Y- 3541905.65)/110934+32
		double FA = (Y - 3541905.65) / 110934.0 + 32.0;
		double FA1 = FA * pi1 / 180.0;

		//8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
		double BA = 111136.54 * FA - 16107.04 * sin(2.0 * FA1) + 16.97 * sin(4.0 * FA1);

		//10 FA= (Y - BA)  / 110934 + FA
		FA = (Y - BA) / 110934.0 + FA;
		FA1 = FA * pi1 / 180.0;

		//12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
		double NA = 6378388.0 / sqrt(1.0 - 0.00672267 * pow(sin(FA1), 2.0));

		//14 E= 0.00676817 * ((COSFA) ^2)
		double E = 0.00676817 * pow(cos(FA1), 2.0);

		//16 C=((X/NA) ^2/2) * TANFA*(1+E)
		double C = (pow((X / NA), 2.0) / 2.0) * tan(FA1) * (1.0 + E);

		//: D=(X/NA)^4/24*(TANFA*(5
		//+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
		double D = (pow((X / NA), 4.0) / 24.0) * (tan(FA1) * (5.0
															  + (3.0 * pow(tan(FA1), 2.0)) + (6.0 * E) - (6.0 * E * pow(tan(FA1), 2.0))
															  - (3.0 * pow(E, 2.0)) - (9.0 * pow(tan(FA1), 2.0) * pow(E, 2.0))));

		//18 F=FA -(C -D)*180/PI
		lat = FA - (C - D) * 180.0 / pi1;

		//20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
		double O = X / (NA * cos(FA1));
		double P = pow(X, 3.0) / (6.0 * pow(NA, 3.0) * cos(FA1)) * (1.0 + 2.0 * pow(tan(FA1), 2.0) + E);

		//22 L= (O- P)*180/PI+ Z(I)
		lon = (O - P) * 180.0 / pi1 + z[zone - 1];
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
//// Compute the UTM zone.
//double zone = floor((lon + 180.0) / 6.0) + 1.0;
//double x = 0.0;
//double y = 0.0;
//LatLonToUTMXY(DegToRad(lat), DegToRad(lon), zone, out x, out y);
//---------------------------------------------------------------------------
// from http://home.hiwaay.net/~taylorc/toolbox/geography/geoutm.html

//constant
double pi = pi1;// 3.14159265358979;
double sm_a = 6378137.0;//��� ��� ����� ������� ������
double sm_b = 6356752.314;//��� ��� ����� ������� ������
//double sm_EccSquared = 6.69437999013e-03;
double UTMScaleFactor = 0.9996;

// from http://home.hiwaay.net/~taylorc/toolbox/geography/geoutm.html
/*
* DegToRad
*
* Converts degrees to radians.
*����� �� ���� �������
*/
//double DegToRad(double deg)
//{
//	return (deg / 180.0 * pi);
//}




/*
* RadToDeg
*
* Converts radians to degrees.
*����� �� ������ �����
*/
//double RadToDeg(double rad)
//{
//	return (rad / pi * 180.0);
//}

/*
* ArcLengthOfMeridian
*
* Computes the ellipsoidal distance from the equator to a point at a
* given latitude.
*���� ������� ��������� ��� �� �������� � �� ��� ������ ������� phi
* Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
* GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
*
* Inputs:
*     phi - Latitude of the point, in radians.
*
* Globals:
*     sm_a - Ellipsoid model major axis.
*     sm_b - Ellipsoid model minor axis.
*
* Returns:
*     The ellipsoidal distance of the point from the equator, in meters.
*
*/
double ArcLengthOfMeridian(double phi)
{
	try{
		double alpha, beta, gamma, delta, epsilon, n;
		double result;

		/* Precalculate n */
		n = (sm_a - sm_b) / (sm_a + sm_b);

		/* Precalculate alpha */
		alpha = ((sm_a + sm_b) / 2.0)
				* (1.0 + (pow(n, 2.0) / 4.0) + (pow(n, 4.0) / 64.0));

		/* Precalculate beta */
		beta = (-3.0 * n / 2.0) + (9.0 * pow(n, 3.0) / 16.0)
			   + (-3.0 * pow(n, 5.0) / 32.0);

		/* Precalculate gamma */
		gamma = (15.0 * pow(n, 2.0) / 16.0)
				+ (-15.0 * pow(n, 4.0) / 32.0);

		/* Precalculate delta */
		delta = (-35.0 * pow(n, 3.0) / 48.0)
				+ (105.0 * pow(n, 5.0) / 256.0);

		/* Precalculate epsilon */
		epsilon = (315.0 * pow(n, 4.0) / 512.0);

		/* Now calculate the sum of the series and return */
		result = alpha
				 * (phi + (beta * sin(2.0 * phi))
					+ (gamma * sin(4.0 * phi))
					+ (delta * sin(6.0 * phi))
					+ (epsilon * sin(8.0 * phi)));

		return result;
	}
	catch(...){
		return 0;
	}
}


/*
* UTMCentralMeridian
*
* Determines the central meridian for the given UTM zone.
*����� �� ������ ������� ������� �������
* Inputs:
*     zone - An integer value designating the UTM zone, range [1,60].
*
* Returns:
*   The central meridian for the given UTM zone, in radians, or zero
*   if the UTM zone parameter is outside the range [1,60].
*   Range of the central meridian is the radian equivalent of [-177,+177].
*
*/
double UTMCentralMeridian(double zone)
{
	try{
		double cmeridian;

//	cmeridian = DegToRad(-183.0 + (zone * 6.0));
		cmeridian = deg2rad(-183.0 + (zone * 6.0));

		return cmeridian;
	}
	catch(...){
		return 0;
	}
}


/*
* FootpointLatitude
*
* Computes the footpoint latitude for use in converting transverse
* Mercator coordinates to ellipsoidal coordinates.
*���� "���� �����" �� ����� ������� �� ������� �� �������� �������� ��� ���������� ���������
* Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
*   GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
*
* Inputs:
*   y - The UTM northing coordinate, in meters.
*
* Returns:
*   The footpoint latitude, in radians.
*
*/
double FootpointLatitude(double y)
{
	try{
		double y_, alpha_, beta_, gamma_, delta_, epsilon_, n;
		double result;

		/* Precalculate n (Eq. 10.18) */
		n = (sm_a - sm_b) / (sm_a + sm_b);

		/* Precalculate alpha_ (Eq. 10.22) */
		/* (Same as alpha in Eq. 10.17) */
		alpha_ = ((sm_a + sm_b) / 2.0)
				 * (1 + (pow(n, 2.0) / 4) + (pow(n, 4.0) / 64));

		/* Precalculate y_ (Eq. 10.23) */
		y_ = y / alpha_;

		/* Precalculate beta_ (Eq. 10.22) */
		beta_ = (3.0 * n / 2.0) + (-27.0 * pow(n, 3.0) / 32.0)
				+ (269.0 * pow(n, 5.0) / 512.0);

		/* Precalculate gamma_ (Eq. 10.22) */
		gamma_ = (21.0 * pow(n, 2.0) / 16.0)
				 + (-55.0 * pow(n, 4.0) / 32.0);

		/* Precalculate delta_ (Eq. 10.22) */
		delta_ = (151.0 * pow(n, 3.0) / 96.0)
				 + (-417.0 * pow(n, 5.0) / 128.0);

		/* Precalculate epsilon_ (Eq. 10.22) */
		epsilon_ = (1097.0 * pow(n, 4.0) / 512.0);

		/* Now calculate the sum of the series (Eq. 10.21) */
		result = y_ + (beta_ * sin(2.0 * y_))
				 + (gamma_ * sin(4.0 * y_))
				 + (delta_ * sin(6.0 * y_))
				 + (epsilon_ * sin(8.0 * y_));

		return result;
	}
	catch(...){
		return 0;
	}
}


/*
* MapLatLonToXY
*
* Converts a latitude/longitude pair to x and y coordinates in the
* Transverse Mercator projection.  Note that Transverse Mercator is not
* the same as UTM; a scale factor is required to convert between them.
* ����� �� �� ���, �� ��� ��� ����� , ����� �� ���� ����� ����������������, ����� �� ����� �� ���� ����� �������� �������, ����� ������ ������� ������� ������
* Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
* GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
*
* Inputs:
*    phi - Latitude of the point, in radians.
*    lambda - Longitude of the point, in radians.
*    lambda0 - Longitude of the central meridian to be used, in radians.
*
* Outputs:
*    xy - A 2-element array containing the x and y coordinates
*         of the computed point.
*
* Returns:
*    The function does not return a value.
*
*/
void MapLatLonToXY(double phi,double lambda,double lambda0,double &x,double &y)
{
	try{
		double N, nu2, ep2, t, t2, l;
		double l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
		double tmp;

		/* Precalculate ep2 */
		ep2 = (pow(sm_a, 2.0) - pow(sm_b, 2.0)) / pow(sm_b, 2.0);

		/* Precalculate nu2 */
		nu2 = ep2 * pow(cos(phi), 2.0);

		/* Precalculate N */
		N = pow(sm_a, 2.0) / (sm_b * sqrt(1 + nu2));

		/* Precalculate t */
		t = tan(phi);
		t2 = t * t;
		tmp = (t2 * t2 * t2) - pow(t, 6.0);

		/* Precalculate l */
		l = lambda - lambda0;

		/* Precalculate coefficients for l**n in the equations below
           so a normal human being can read the expressions for easting
           and northing
           -- l**1 and l**2 have coefficients of 1.0 */
		l3coef = 1.0 - t2 + nu2;

		l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);

		l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2
				 - 58.0 * t2 * nu2;

		l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2
				 - 330.0 * t2 * nu2;

		l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);

		l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);

		/* Calculate easting (x) */
		x = N * cos(phi) * l
			+ (N / 6.0 * pow(cos(phi), 3.0) * l3coef * pow(l, 3.0))
			+ (N / 120.0 * pow(cos(phi), 5.0) * l5coef * pow(l, 5.0))
			+ (N / 5040.0 * pow(cos(phi), 7.0) * l7coef * pow(l, 7.0));

		/* Calculate northing (y) */
		y = ArcLengthOfMeridian(phi)
			+ (t / 2.0 * N * pow(cos(phi), 2.0) * pow(l, 2.0))
			+ (t / 24.0 * N * pow(cos(phi), 4.0) * l4coef * pow(l, 4.0))
			+ (t / 720.0 * N * pow(cos(phi), 6.0) * l6coef * pow(l, 6.0))
			+ (t / 40320.0 * N * pow(cos(phi), 8.0) * l8coef * pow(l, 8.0));
	}
	catch(...){

	}
}



/*
* MapXYToLatLon
*
* Converts x and y coordinates in the Transverse Mercator projection to
* a latitude/longitude pair.  Note that Transverse Mercator is not
* the same as UTM; a scale factor is required to convert between them.
*���� �� ����� ����� ��� �� ���, �� ���
* Reference: Hoffmann-Wellenhof, B., Lichtenegger, H., and Collins, J.,
*   GPS: Theory and Practice, 3rd ed.  New York: Springer-Verlag Wien, 1994.
*
* Inputs:
*   x - The easting of the point, in meters.
*   y - The northing of the point, in meters.
*   lambda0 - Longitude of the central meridian to be used, in radians.
*
* Outputs:
*   philambda - A 2-element containing the latitude and longitude
*               in radians.
*
* Returns:
*   The function does not return a value.
*
* Remarks:
*   The local variables Nf, nuf2, tf, and tf2 serve the same purpose as
*   N, nu2, t, and t2 in MapLatLonToXY, but they are computed with respect
*   to the footpoint latitude phif.
*
*   x1frac, x2frac, x2poly, x3poly, etc. are to enhance readability and
*   to optimize computations.
*
*/
void MapXYToLatLon(double x,double y,double lambda0,double &lat,double &lon)
{
	try{
		double phif, Nf, Nfpow, nuf2, ep2, tf, tf2, tf4, cf;
		double x1frac, x2frac, x3frac, x4frac, x5frac, x6frac, x7frac, x8frac;
		double x2poly, x3poly, x4poly, x5poly, x6poly, x7poly, x8poly;

		/* Get the value of phif, the footpoint latitude. */
		phif = FootpointLatitude(y);

		/* Precalculate ep2 */
		ep2 = (pow(sm_a, 2.0) - pow(sm_b, 2.0))
			  / pow(sm_b, 2.0);

		/* Precalculate cos (phif) */
		cf = cos(phif);

		/* Precalculate nuf2 */
		nuf2 = ep2 * pow(cf, 2.0);

		/* Precalculate Nf and initialize Nfpow */
		Nf = pow(sm_a, 2.0) / (sm_b * sqrt(1 + nuf2));
		Nfpow = Nf;

		/* Precalculate tf */
		tf = tan(phif);
		tf2 = tf * tf;
		tf4 = tf2 * tf2;

		/* Precalculate fractional coefficients for x**n in the equations
           below to simplify the expressions for latitude and longitude. */
		x1frac = 1.0 / (Nfpow * cf);

		Nfpow *= Nf;   /* now equals Nf**2) */
		x2frac = tf / (2.0 * Nfpow);

		Nfpow *= Nf;   /* now equals Nf**3) */
		x3frac = 1.0 / (6.0 * Nfpow * cf);

		Nfpow *= Nf;   /* now equals Nf**4) */
		x4frac = tf / (24.0 * Nfpow);

		Nfpow *= Nf;   /* now equals Nf**5) */
		x5frac = 1.0 / (120.0 * Nfpow * cf);

		Nfpow *= Nf;   /* now equals Nf**6) */
		x6frac = tf / (720.0 * Nfpow);

		Nfpow *= Nf;   /* now equals Nf**7) */
		x7frac = 1.0 / (5040.0 * Nfpow * cf);

		Nfpow *= Nf;   /* now equals Nf**8) */
		x8frac = tf / (40320.0 * Nfpow);

		/* Precalculate polynomial coefficients for x**n.
           -- x**1 does not have a polynomial coefficient. */
		x2poly = -1.0 - nuf2;

		x3poly = -1.0 - 2 * tf2 - nuf2;

		x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2
				 - 3.0 * (nuf2 * nuf2) - 9.0 * tf2 * (nuf2 * nuf2);

		x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;

		x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2
				 + 162.0 * tf2 * nuf2;

		x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);

		x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);

		/* Calculate latitude */
		lat = phif + x2frac * x2poly * (x * x)
			  + x4frac * x4poly * pow(x, 4.0)
			  + x6frac * x6poly * pow(x, 6.0)
			  + x8frac * x8poly * pow(x, 8.0);

		/* Calculate longitude */
		lon = lambda0 + x1frac * x
			  + x3frac * x3poly * pow(x, 3.0)
			  + x5frac * x5poly * pow(x, 5.0)
			  + x7frac * x7poly * pow(x, 7.0);
	}
	catch(...){

	}
}


/*
* LatLonToUTMXY
*
* Converts a latitude/longitude pair to x and y coordinates in the
* Universal Transverse Mercator projection.
*����� �� �� ��� , �� ��� ��� ����� , ����� ����� �������� �������
* Inputs:
*   lat - Latitude of the point, in radians.
*   lon - Longitude of the point, in radians.
*   zone - UTM zone to be used for calculating values for x and y.
*          If zone is less than 1 or greater than 60, the routine
*          will determine the appropriate zone from the value of lon.
*
* Outputs:
*   xy - A 2-element array where the UTM x and y values will be stored.
*
* Returns:
*   The UTM zone used for calculating the values of x and y.
*
*/
void LatLonToUTMXY(double lat,double lon,double zone,double &x,double &y)
{
	try{
		MapLatLonToXY(lat,lon,UTMCentralMeridian(zone),x,y);

		/* Adjust easting and northing for UTM system. */
		x = x * UTMScaleFactor + 500000.0;
		y = y * UTMScaleFactor;
		if (y < 0.0)
			y = y + 10000000.0;
	}
	catch(...){

	}
}



/*
* UTMXYToLatLon
*
* Converts x and y coordinates in the Universal Transverse Mercator
* projection to a latitude/longitude pair.
*���� �� ����� ����� ����� �������� ������� ��� �� ���, �� ���
* Inputs:
*	x - The easting of the point, in meters.
*	y - The northing of the point, in meters.
*	zone - The UTM zone in which the point lies.
*	southhemi - True if the point is in the southern hemisphere;
*               false otherwise.
*
* Outputs:
*	latlon - A 2-element array containing the latitude and
*            longitude of the point, in radians.
*
* Returns:
*	The function does not return a value.
*
*/
void UTMXYToLatLon(double x,double y,double zone,bool southhemi,double &lat,double &lon)
{
	try{
		double cmeridian;

		x = x - 500000;
		x = x / UTMScaleFactor;

		/* If in southern hemisphere, adjust y accordingly. */
		if (southhemi == true)
			y = y - 10000000.0;

		y = y / UTMScaleFactor;
		cmeridian = UTMCentralMeridian(zone);
		MapXYToLatLon(x,y,cmeridian,lat,lon);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
double distance3(mv_point3 *p1,mv_point3 *p2)
{
	try{
		double dx,dy,dz;
		dx = p2->x - p1->x;
		dy = p2->y - p1->y;
		dz = p2->z - p1->z;
		return sqrt(dx * dx + dy * dy + dz * dz);
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
static int bIsFlat = 0;
static float fFlatValue = 0.0;
//---------------------------------------------------------------------------
void mv_set_flat(int bFlat,float fValue)
{
	try{
		bIsFlat = bFlat;
		fFlatValue = fValue;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void mv_get_flat(int &bFlat,float &fValue)
{
	bFlat = bIsFlat;
	fValue = fFlatValue;
}
//---------------------------------------------------------------------------
double db_mod(double x,double y)
{
	try{
		long int n;
		double r;
		n = (long int)(x/y);
		r = x - n * y;
		return r;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
double db_deg(double x)
{
	try{
		return db_mod(x + 100.0*360.0,360.0);
	}
	catch(...){
		return x;
	}
}
//---------------------------------------------------------------------------
double db_change_deg(double x,double y)
{
	try{
		x = rad2deg2(x);
		y = rad2deg2(y);
		x += y;
		x = db_deg(x);
		return deg2rad(x);
	}
	catch(...){
		return x;
	}
}
//---------------------------------------------------------------------------
double db_change_deg_ex(double x,double y,double z)
{
	try{
		x = rad2deg2(x);
		y = rad2deg2(y);
		x += y;
		x = db_mod(x + 10*z,z);
		return deg2rad(x);
	}
	catch(...){
		return x;
	}
}
//---------------------------------------------------------------------------
int mv_save_dem_to_flash(char *address)
{
	try{
		int size;
		FILE *fflash;
//    if((fflash = fopen(address,"w")) == NULL)	return 0;
		if ((fflash = fopen(address, "wb")) == NULL)	return 0;
		//setvbuf(fflash,NULL,_IOFBF,0x100);

		fwrite(&s_header.w, sizeof(s_header.w), 1, fflash);
		fwrite(&s_header.h, sizeof(s_header.h), 1, fflash);
		fwrite(&s_header.xll, sizeof(s_header.xll), 1, fflash);
		fwrite(&s_header.yll, sizeof(s_header.yll), 1, fflash);
		fwrite(&s_header.cellsizeX, sizeof(s_header.cellsizeX), 1, fflash);
		fwrite(&s_header.cellsizeY, sizeof(s_header.cellsizeY), 1, fflash);
		fwrite(&s_header.minHeight, sizeof(s_header.minHeight), 1, fflash);
		fwrite(&s_header.maxHeight, sizeof(s_header.maxHeight), 1, fflash);
		fwrite(&s_header.bIsFlipped, sizeof(s_header.bIsFlipped), 1, fflash);

		if(s_fflash)
		{
			const int packet = 1024;
			float buf[packet];
			int count = 0;
			fseek(s_fflash, s_offset0, SEEK_SET);
			while(true)
			{
				count = fread(buf,1,packet,s_fflash);
				if(count > 0)	fwrite(buf, s_size, count, fflash);
				if(count != packet)	break;
			}
		}

		fclose(fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
int mv_load_dem_from_flash(char *address)
{
	try{
		int size;
//    if((s_fflash = fopen(address,"r")) == NULL)	return 0;
		if((s_fflash = fopen(address,"rb")) == NULL)	return 0;
		//setvbuf(s_fflash,NULL,_IOFBF,0x100);

		fread(&s_header.w,sizeof(s_header.w),1,s_fflash);
		fread(&s_header.h,sizeof(s_header.h),1,s_fflash);
		fread(&s_header.xll,sizeof(s_header.xll),1,s_fflash);
		fread(&s_header.yll,sizeof(s_header.yll),1,s_fflash);
		fread(&s_header.cellsizeX,sizeof(s_header.cellsizeX),1,s_fflash);
		fread(&s_header.cellsizeY,sizeof(s_header.cellsizeY),1,s_fflash);
		fread(&s_header.minHeight,sizeof(s_header.minHeight),1,s_fflash);
		fread(&s_header.maxHeight,sizeof(s_header.maxHeight),1,s_fflash);
		fread(&s_header.bIsFlipped,sizeof(s_header.bIsFlipped),1,s_fflash);

		s_size = sizeof(float);
		s_is_wrd = false;
		s_is_byt = false;
		s_offset0 = ftell(s_fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
int mv_save_dem_to_flash_wrd(char *address)
{
	try{
		int size;
		FILE *fflash;
//    if((fflash = fopen(address,"w")) == NULL)	return 0;
		if ((fflash = fopen(address, "wb")) == NULL)	return 0;
		//setvbuf(fflash,NULL,_IOFBF,0x100);

		fwrite(&s_header.w, sizeof(s_header.w), 1, fflash);
		fwrite(&s_header.h, sizeof(s_header.h), 1, fflash);
		fwrite(&s_header.xll, sizeof(s_header.xll), 1, fflash);
		fwrite(&s_header.yll, sizeof(s_header.yll), 1, fflash);
		fwrite(&s_header.cellsizeX, sizeof(s_header.cellsizeX), 1, fflash);
		fwrite(&s_header.cellsizeY, sizeof(s_header.cellsizeY), 1, fflash);
		fwrite(&s_header.minHeight, sizeof(s_header.minHeight), 1, fflash);
		fwrite(&s_header.maxHeight, sizeof(s_header.maxHeight), 1, fflash);
		fwrite(&s_header.bIsFlipped, sizeof(s_header.bIsFlipped), 1, fflash);

//	unsigned short int x;// [0 - 65535]
//	float a,b;
//	int v;
//	a = 65535.0 / (s_header.maxHeight - s_header.minHeight);
//	b = 0.0 - a * s_header.minHeight;

		if(s_fflash)
		{
			const int packet = 1024;
			float buf[packet];
			unsigned short int buf2[packet];
			int count = 0;
			fseek(s_fflash, s_offset0, SEEK_SET);
			while(true)
			{
				count = fread(buf,sizeof(float),packet,s_fflash);
				if(count > 0)
				{
					for(int i=0;i<count;i++)
					{
//						buf2[i] = round(a * buf[i] + b);
						buf2[i] = (unsigned short int)buf[i];
					}
					fwrite(buf2, sizeof(unsigned short int), count, fflash);
				}
				if(count != packet)	break;
			}
		}

		fclose(fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
int mv_load_dem_from_flash_wrd(char *address)
{
	try{
		int size;
//    if((s_fflash = fopen(address,"r")) == NULL)	return 0;
		if((s_fflash = fopen(address,"rb")) == NULL)	return 0;
		//setvbuf(s_fflash,NULL,_IOFBF,0x100);

		fread(&s_header.w,sizeof(s_header.w),1,s_fflash);
		fread(&s_header.h,sizeof(s_header.h),1,s_fflash);
		fread(&s_header.xll,sizeof(s_header.xll),1,s_fflash);
		fread(&s_header.yll,sizeof(s_header.yll),1,s_fflash);
		fread(&s_header.cellsizeX,sizeof(s_header.cellsizeX),1,s_fflash);
		fread(&s_header.cellsizeY,sizeof(s_header.cellsizeY),1,s_fflash);
		fread(&s_header.minHeight,sizeof(s_header.minHeight),1,s_fflash);
		fread(&s_header.maxHeight,sizeof(s_header.maxHeight),1,s_fflash);
		fread(&s_header.bIsFlipped,sizeof(s_header.bIsFlipped),1,s_fflash);

		s_size = sizeof(unsigned short int);
		s_is_wrd = true;
		s_is_byt = false;
		s_offset0 = ftell(s_fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
int mv_save_dem_to_flash_byt(char *address)
{
	try{
		int size;
		FILE *fflash;
//    if((fflash = fopen(address,"w")) == NULL)	return 0;
		if ((fflash = fopen(address, "wb")) == NULL)	return 0;
		//setvbuf(fflash,NULL,_IOFBF,0x100);

		fwrite(&s_header.w, sizeof(s_header.w), 1, fflash);
		fwrite(&s_header.h, sizeof(s_header.h), 1, fflash);
		fwrite(&s_header.xll, sizeof(s_header.xll), 1, fflash);
		fwrite(&s_header.yll, sizeof(s_header.yll), 1, fflash);
		fwrite(&s_header.cellsizeX, sizeof(s_header.cellsizeX), 1, fflash);
		fwrite(&s_header.cellsizeY, sizeof(s_header.cellsizeY), 1, fflash);
		fwrite(&s_header.minHeight, sizeof(s_header.minHeight), 1, fflash);
		fwrite(&s_header.maxHeight, sizeof(s_header.maxHeight), 1, fflash);
		fwrite(&s_header.bIsFlipped, sizeof(s_header.bIsFlipped), 1, fflash);

//	unsigned short int x;// [0 - 65535]
//	float a,b;
//	int v;
//	a = 65535.0 / (s_header.maxHeight - s_header.minHeight);
//	b = 0.0 - a * s_header.minHeight;

		if(s_fflash)
		{
			const int packet = 1024;
			float buf[packet];
			unsigned char buf2[packet];
			int count = 0;
			fseek(s_fflash, s_offset0, SEEK_SET);
			while(true)
			{
				count = fread(buf,sizeof(float),packet,s_fflash);
				if(count > 0)
				{
					for(int i=0;i<count;i++)
					{
//						buf2[i] = round(a * buf[i] + b);
						buf2[i] = (unsigned char)(buf[i]/15.0);
					}
					fwrite(buf2, sizeof(unsigned char), count, fflash);
				}
				if(count != packet)	break;
			}
		}

		fclose(fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
int mv_load_dem_from_flash_byt(char *address)
{
	try{
		int size;
//    if((s_fflash = fopen(address,"r")) == NULL)	return 0;
		if((s_fflash = fopen(address,"rb")) == NULL)	return 0;
		//setvbuf(s_fflash,NULL,_IOFBF,0x100);

		fread(&s_header.w,sizeof(s_header.w),1,s_fflash);
		fread(&s_header.h,sizeof(s_header.h),1,s_fflash);
		fread(&s_header.xll,sizeof(s_header.xll),1,s_fflash);
		fread(&s_header.yll,sizeof(s_header.yll),1,s_fflash);
		fread(&s_header.cellsizeX,sizeof(s_header.cellsizeX),1,s_fflash);
		fread(&s_header.cellsizeY,sizeof(s_header.cellsizeY),1,s_fflash);
		fread(&s_header.minHeight,sizeof(s_header.minHeight),1,s_fflash);
		fread(&s_header.maxHeight,sizeof(s_header.maxHeight),1,s_fflash);
		fread(&s_header.bIsFlipped,sizeof(s_header.bIsFlipped),1,s_fflash);

		s_size = sizeof(unsigned char);
		s_is_wrd = false;
		s_is_byt = true;
		s_offset0 = ftell(s_fflash);
	}
	catch(...){
		return 0;
	}
	return 1;
}
//---------------------------------------------------------------------------
#ifndef MV_EMBEDED
int mv_load_dem_from_dig(DIG_Map *pDIG_Map)
{
	if(!pDIG_Map)	return 0;
	s_header.w = pDIG_Map->w;
	s_header.h = pDIG_Map->h;
	s_header.xll = pDIG_Map->xll;
	s_header.yll = pDIG_Map->yll;
	s_header.cellsizeX = pDIG_Map->cellsizeX;
	s_header.cellsizeY = pDIG_Map->cellsizeY;
	s_header.minHeight = pDIG_Map->min_height;
	s_header.maxHeight = pDIG_Map->max_height;
	s_header.bIsFlipped = pDIG_Map->bIsFlipped;

//	    if((s_fflash = fopen(address,"r")) == NULL)	return 0;
	if((s_fflash = fopen(AnsiString(pDIG_Map->strFileName).c_str(),"rb")) == NULL)	return 0;
//		setvbuf(s_fflash,NULL,_IOFBF,0x100);

	s_offset0 = ftell(s_fflash);
	s_size = sizeof(float);
	s_is_wrd = false;
	s_is_byt = false;

//	int size = s_header.w * s_header.h;
//	if(s_map)
//	{
//		delete[] s_map;
//		s_map = NULL;
//	}
//	s_map = new float[size];
//	pDIG_Map->fs->ReadBuffer(s_map,sizeof(float)*size);
	return 1;
}
#endif // MV_EMBEDED
//---------------------------------------------------------------------------
// Google Projection
double ge_radius = EARTH_RADIUS;
void LL2GP(double Lat,double Lon,double *UTMNorthing, double *UTMEasting)
{
	try{
		double r = ge_radius;
		double b = r * _pi;
		double a = log(tan((90.0+Lat)*_pi / 360.0))/(_pi / 180.0);
		double custLat = a * b / 180.0;
		double custLon = Lon;
		custLon = custLon * b / 180.0;
		*UTMEasting = custLon;
		*UTMNorthing = custLat;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void GP2LL(double UTMNorthing,double UTMEasting,double *Lat,double *Lon)
{
	try{
		double r = ge_radius;
		double b = r * _pi;
		double lat_deg,lon_deg;
		lat_deg = (UTMNorthing / b) * 180.0;
		lon_deg = (UTMEasting / b) * 180.0;
		lat_deg = 180.0/_pi * (2.0 * atan(exp(lat_deg * _pi / 180.0)) - _pi / 2.0);
		*Lon = lon_deg;
		*Lat = lat_deg;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
// Ellipsoid
//double a = 6378249.200;
//double f_inv = 293.46602129362702;
//double f = 1.0 / f_inv;
//double e = 0.08227185;

// Ellipsoid
double a = EARTH_RADIUS;
double f_inv = 293.4660213;
double f = 1.0 / f_inv;
double e = 0.08227185;

// parameters
//double FE = 250000.00;
//double FN = 150000.00;
double FE = 0.0;
double FN = 0.0;
double lat0 = 0.31415927;
double lon0 = -1.34390352;
double k0 = 1.000000;

double n,t0,F,m0,r0;
//---------------------------------------------------------------------------
double m_fun(double lat)
{
	try{
		double slat = sin(lat);
		return cos(lat)/sqrt(1.0 - e*e*slat*slat);
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
double t_fun(double lat)
{
	try{
		double slat = sin(lat);
		return tan(_pi/4.0 - lat/2.0) / pow((1.0 - e*slat) / (1.0 + e*slat),e/2.0);
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
void set_LambertConic1SP_paramaeters(double _FE,double _FN,double _lat0,double _lon0,double _k0)
{
	try{
		FE = _FE;
		FN = _FN;
		lat0 = deg2rad(_lat0);
		lon0 = deg2rad(_lon0);
		k0 = _k0;

		e = sqrt(f * (2.0 - f));
		n = sin(lat0);
		t0 = t_fun(lat0);
		m0 = m_fun(lat0);
		F = m0 / (n * pow(t0,n));
		r0 = a * F * pow(t0,n) * k0;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void LL2LambertConic1SP(double lat,double lon,double *northing, double *easting)
{
	try
	{
		double r,theta,t;

		lat = deg2rad(lat);
		lon = deg2rad(lon);

		t = t_fun(lat);
		theta = n * (lon - lon0);
		r = a * F * pow(t,n) * k0;

		*easting = FE + r * sin(theta);
		*northing = FN + r0 - r * cos(theta);
	}
#ifndef MV_EMBEDED
	catch(Exception &e)
	{
		ADD_LOG(e.Message);
#else
	catch(...)
	{
#endif // MV_EMBEDED
		easting = 0;
		northing = 0;
	}
}
//---------------------------------------------------------------------------
void LambertConic1SP2LL(double northing, double easting,double *lat,double *lon)
{
	try
	{
		double theta1,r1,r12,r22,t1;
		double slat,p_lat;

		if(easting - FE != 0.0)
		theta1 = atan2(easting - FE,r0 - northing + FN);
		r12 = easting - FE;
		r22 = r0 - northing + FN;
		r1 = sqrt(r12*r12+r22*r22);
		if(n < 0)	r1 *= -1;
		t1 = pow(r1 / (a * k0 * F),1.0/n);

		*lat = _pi/2.0 - 2.0*atan(t1);
		p_lat = *lat;
		for(int i=0;i<10;i++)
		{
			slat = sin(*lat);
			*lat = _pi/2.0 - 2.0 * atan(t1 * pow((1.0 - e*slat) / (1.0 + e*slat),e/2.0));
			if(fabs(*lat - p_lat) <= 1e-6)	break;
			p_lat = *lat;
		}
		*lon = theta1 / n + lon0;

		*lat = rad2deg2(*lat);
		*lon = rad2deg2(*lon);
	}
#ifndef MV_EMBEDED
	catch(Exception &e)
	{
		ADD_LOG(e.Message);
#else
	catch(...)
	{
#endif // MV_EMBEDED
		lat = 0;
		lon = 0;
	}
}
//---------------------------------------------------------------------------
// Plannar Projection
void LL2XY(double Lat,double Lon,double *UTMNorthing, double *UTMEasting)
{
	try{
//	LL2GP(Lat,Lon,UTMNorthing,UTMEasting);
		LL2LambertConic1SP(Lat,Lon,UTMNorthing,UTMEasting);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void XY2LL(double UTMNorthing,double UTMEasting,double *Lat,double *Lon)
{
	try{
//	GP2LL(UTMNorthing,UTMEasting,Lat,Lon);
		LambertConic1SP2LL(UTMNorthing,UTMEasting,Lat,Lon);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
// degrees to meters conversion
double deg2m(double len_deg)
{
	try{
		return deg2rad(len_deg) * EARTH_RADIUS;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
float get_sample_val_flipped(int x, int y,bool bFlipped)
{
	try{
		if(bIsFlat > 0)	return fFlatValue;
		if(x < 0)	return 0.0;
		if(x >= s_header.w)	return 0.0;
		if(y < 0)	return 0.0;
		if(y >= s_header.h)	return 0.0;

		int offset;
		if(bFlipped)
			offset = (s_header.h - 1 - y)*s_header.w+x;
		else
			offset = y*s_header.w+x;

		float value = 0.0f;
		if(s_fflash)
		{
			fseek(s_fflash, s_offset0 + offset * s_size, SEEK_SET);
			if(s_is_wrd)
			{
				unsigned short int v = 0;
				fread(&v,s_size,1,s_fflash);
				value = v;
			}
			else
			if(s_is_byt)
			{
				unsigned char v = 0;
				fread(&v,s_size,1,s_fflash);
				value = 15.0 * v;
			}
			else
			{
				float v = 0.0f;
				fread(&v,s_size,1,s_fflash);
				value = v;
			}
		}
		return value;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
float get_sample_val(int x, int y)
{
	try{
		return get_sample_val_flipped(x,y,s_header.bIsFlipped);
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
float mv_get_height(double lon,double lat)
{
	try{
		if(bIsFlat > 0)	return fFlatValue;
		// returns the altitude of the terrain in meters above sea level (ASL)
		int llx,lly;
		double deriv;
		float az,bz,cz;
		int urx,ury,ulx,uly,lrx,lry;
		double x_sample = (lon - s_header.xll) / s_header.cellsizeX;
		double y_sample = (lat - s_header.yll) / s_header.cellsizeY;

		if ( x_sample > (double)(s_header.w-1) || x_sample < 0 ||
			 y_sample > (double)(s_header.h-1) || y_sample < 0 )
			return 0.0; // it's off our map

		llx = (int)x_sample;
		lly = (int)y_sample;

		if (x_sample == llx)
			deriv = 2; // anything > 1 would work
		else
			deriv = (y_sample - (double)lly) / (x_sample - (double)llx);

		if (deriv >= 1) // interpolate across the upper triangle of this box
		{
			urx = llx + 1;
			ury = lly + 1;
			ulx = llx;
			uly = lly + 1;

			// these variable names are from my notebook, page 72
			az = get_sample_val(llx, lly);
			bz = get_sample_val(ulx, uly);
			cz = get_sample_val(urx, ury);

			return	bz +
					  ((float)x_sample - (float)ulx) * (cz - bz) +
					  ((float)uly - (float)y_sample) * (az - bz);
		}
		else // interpolate across the lower triangle of this box
		{
			lrx = llx + 1;
			lry = lly;
			urx = llx + 1;
			ury = lly + 1;

			// also from page 72 of my notebook
			az = get_sample_val(llx, lly);
			bz = get_sample_val(urx, ury);
			cz = get_sample_val(lrx, lry);

			return	cz +
					  ((float)lrx - (float)x_sample) * (az - cz) +
					  ((float)y_sample - (float)lry) * (bz - cz);
		}
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
float mv_get_alt_asl(double x,double y)
{
	try{
		double lon,lat;
		XY2LL(y,x,&lat,&lon);
		return mv_get_height(lon,lat);
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
int find_intersection(mv_point3 *P1,mv_point3 *P2,mv_point3 *K,mv_point3 *P)
{
	try{
		double h1,h2;
		double L;
		h1 = mv_get_alt_asl(P1->x,P1->y);
		h2 = mv_get_alt_asl(P2->x,P2->y);
		if((P1->z > h1) && (P2->z > h2))	return 0;
		if((P1->z < h1) && (P2->z < h2))	return 0;

		L = distance3(P1,P2);
		P->x = P1->x + L * K->x / 2.0;
		P->y = P1->y + L * K->y / 2.0;
		P->z = P1->z + L * K->z / 2.0;
		return 1;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
double get_virtual_laser_distance(double laser_lon,double laser_lat,double laser_alt,double laser_azi,double laser_ele,double max_dist,double step,double *hit_lon,double *hit_lat,double *hit_alt)
{
	try{
		double ce,se,ca,sa,dt,laser_dist,terrain_height;
		mv_point3 P0,K,P,H;
		int is_ok;

		terrain_height = mv_get_height(laser_lon, laser_lat);
		if (laser_alt <= terrain_height)
		{
			printf("Camera Point Error: (alt <= terrain_height)\n");
			return 0.0;
		}

		LL2XY(laser_lat,laser_lon,&P0.y,&P0.x);
		P0.z = laser_alt;
		ca = cos(laser_azi);
		sa = sin(laser_azi);
		ce = cos(laser_ele);
		se = sin(laser_ele);
		K.x = ce * sa;
		K.y = ce * ca;
		K.z = se;

		H.x = P0.x + max_dist * K.x;
		H.y = P0.y + max_dist * K.y;
		H.z = P0.z + max_dist * K.z;

//	dt = deg2m(map_min(header.cellsizeX,header.cellsizeY))/2.0;
		dt = step;

		is_ok = 0;
		for(laser_dist=0.0;laser_dist<=max_dist;laser_dist+=dt)
		{
			P.x = P0.x + laser_dist * K.x;
			P.y = P0.y + laser_dist * K.y;
			P.z = P0.z + laser_dist * K.z;
			terrain_height = mv_get_alt_asl(P.x,P.y);
			if(P.z <= terrain_height)
			{
				is_ok = 1;
				H.x = P.x;
				H.y = P.y;
				H.z = P.z;
				break;
			}
		}

		// refine search using mid-point algorithm
		if(is_ok != 0)
		{
			mv_point3 P1,P2;
			P1.x = P0.x + (laser_dist - dt) * K.x;
			P1.y = P0.y + (laser_dist - dt) * K.y;
			P1.z = P0.z + (laser_dist - dt) * K.z;
			P2 = H;
			if(find_intersection(&P1,&P2,&K,&P))
			{
				H.x = P.x;
				H.y = P.y;
				H.z = P.z;
				laser_dist = distance3(&P0,&P);
			}
		}
		else
		{
			laser_dist = max_dist;
		}
		XY2LL(H.y,H.x,hit_lat,hit_lon);
		(*hit_alt) = H.z;
		return laser_dist;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
void calculate_angles(double lon1,double lat1,double alt1,double lon2,double lat2,double alt2,double *azi,double *ele)
{
	try{
		double len;
		double e1,n1,e2,n2;
		double h1,h2;
		LL2XY(lat1,lon1,&n1,&e1);
		LL2XY(lat2,lon2,&n2,&e2);

		// elevation
		h1 = alt1;
		h2 = alt2;
		len = sqrt((e2-e1)*(e2-e1)+(n2-n1)*(n2-n1));// Planimetric distance
		if(len > 0.0)
			(*ele) = atan2(h2 - h1,len);
		else
			(*ele) = 0.0;

		// azimuth
		if((*ele) > -pi_2 + deg2rad(1.0))// relative to horizon
		{
			if(n2-n1 != 0.0)
				(*azi) = atan2(e2-e1,n2-n1);
			else
			{
				if(e2-e1 > 0.0)
					(*azi) = +pi_2;
				else
				if(e2-e1 < 0.0)
					(*azi) = -pi_2;
				else
					(*azi) = 0.0;
			}
		}
		else
		{
			(*azi) = 0.0;
		}

		(*azi) = db_change_deg((*azi),0.0);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void transpose_matrix(double pSrc[3][3],double pDst[3][3])
{
	try{
		int x,y;
		for(y=0;y<3;y++)
		{
			for(x=0;x<3;x++)
			{
				pDst[y][x] = pSrc[x][y];
			}
		}
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void multiply_matrices(double pLeft[3][3],double pRight[3][3],double pDst[3][3])
{
	try{
		int x,y,k;
		double s;
		for(y=0;y<3;y++)
		{
			for(x=0;x<3;x++)
			{
				s = 0.0;
				for(k=0;k<3;k++)
				{
					s += pLeft[y][k] * pRight[k][x];
				}
				pDst[y][x] = s;
			}
		}
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void multiply_matrix_vector(double pMatrix[3][3],double pVector[3],double pDst[3])
{
	try{
		int y,k;
		double s;
		for(y=0;y<3;y++)
		{
			s = 0.0;
			for(k=0;k<3;k++)
			{
				s += pMatrix[y][k] * pVector[k];
			}
			pDst[y] = s;
		}
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void add_vectors(double pVector1[3],double pVector2[3],double pDst[3])
{
	try{
		int k;
		for(k=0;k<3;k++)
		{
			pDst[k] = pVector1[k] + pVector2[k];
		}
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
int mv_localize_target(int target_x,int target_y,double fov_h,double fov_v,int w,int h,double uav_lon,double uav_lat,double uav_alt,double uav_yaw,double uav_pitch,double uav_roll,double gimb_azi,double gimb_ele,double max_dist,double step,double *laser_dist,double *target_lon,double *target_lat,double *target_alt)
{
	if(fov_h * fov_v <= 0.0)	return 0;

	int is_ok = 1;
	try
	{
		double Pobj[3];
		double Pmav_i[3];
		double Rv_b[3][3],Rb_v[3][3];
		double Lc[3];
		double Rb_g[3][3],Rg_b[3][3];
		double Rg_c[3][3],Rc_g[3][3];
		double F0,f0;// bug f0 mixed with static version
		double gps_north,gps_east,gps_alt;
		double phi,theta,psi;
		double azi,ele;

		double pDst1[3][3];
		double pDst2[3][3];
		double pV1[3];
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		double cph;
		double sph;
		double cth;
		double sth;
		double cps;
		double sps;
		double caz;
		double saz;
		double cel;
		double sel;
		double fov_x,fov_y,fx,fy;
		double laser_azi,laser_ele;
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		azi = deg2rad(gimb_azi);
		ele = deg2rad(gimb_ele);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		(*target_lon) = 0.0;
		(*target_lat) = 0.0;
		(*target_alt) = 0.0;
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		phi = deg2rad(uav_roll);
		theta = deg2rad(uav_pitch);
		psi = deg2rad(uav_yaw);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		LL2XY(uav_lat,uav_lon,&gps_north,&gps_east);
		gps_alt = uav_alt;
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Pmav_i[0] = gps_north;
		Pmav_i[1] = gps_east;
		Pmav_i[2] = -gps_alt;
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		cph = cos(phi);
		sph = sin(phi);
		cth = cos(theta);
		sth = sin(theta);
		cps = cos(psi);
		sps = sin(psi);

		Rv_b[0][0] = cth*cps;
		Rv_b[0][1] = cth*sps;
		Rv_b[0][2] = -sth;

		Rv_b[1][0] = sph*sth*cps - cph*sps;
		Rv_b[1][1] = sph*sth*sps + cph*cps;
		Rv_b[1][2] = sph*cth;

		Rv_b[2][0] = cph*sth*cps + sph*sps;
		Rv_b[2][1] = cph*sth*sps - sph*cps;
		Rv_b[2][2] = cph*cth;

		transpose_matrix(Rv_b,Rb_v);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		caz = cos(azi);
		saz = sin(azi);
		cel = cos(ele);
		sel = sin(ele);

		Rb_g[0][0] = cel*caz;
		Rb_g[0][1] = cel*saz;
		Rb_g[0][2] = -sel;

		Rb_g[1][0] = -saz;
		Rb_g[1][1] = caz;
		Rb_g[1][2] = 0;

		Rb_g[2][0] = sel*caz;
		Rb_g[2][1] = sel*saz;
		Rb_g[2][2] = cel;

		transpose_matrix(Rb_g,Rg_b);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Rg_c[0][0] = 0;
		Rg_c[0][1] = 1;
		Rg_c[0][2] = 0;

		Rg_c[1][0] = 0;
		Rg_c[1][1] = 0;
		Rg_c[1][2] = 1;

		Rg_c[2][0] = 1;
		Rg_c[2][1] = 0;
		Rg_c[2][2] = 0;

		transpose_matrix(Rg_c,Rc_g);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		fov_x = deg2rad(fov_h);
		fov_y = deg2rad(fov_v);
		fx = w/(2.0*tan(fov_x/2.0));// in pixels
		fy = h/(2.0*tan(fov_y/2.0));// in pixels
		f0 = sqrt(fx * fx + fy * fy);
		F0 = sqrt(f0 * f0 + target_x * target_x + target_y * target_y);
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// find laser distance
		if((*laser_dist) < 0.0)
		{
			(*laser_dist) = 1.0;
			Lc[0] = target_x * (*laser_dist) / F0;
			Lc[1] = target_y * (*laser_dist) / F0;
			Lc[2] = f0 * (*laser_dist) / F0;
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//		Pobj = Pmav_i + Rb_v * Rg_b * Rc_g * Lc;
			multiply_matrices(Rb_v,Rg_b,pDst1);
			multiply_matrices(pDst1,Rc_g,pDst2);
			multiply_matrix_vector(pDst2,Lc,pV1);
			add_vectors(Pmav_i,pV1,Pobj);
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			XY2LL(Pobj[0],Pobj[1],target_lat,target_lon);
			(*target_alt) = -Pobj[2];
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			calculate_angles(uav_lon,uav_lat,uav_alt,*target_lon,*target_lat,*target_alt,&laser_azi,&laser_ele);
			(*laser_dist) = get_virtual_laser_distance(uav_lon,uav_lat,uav_alt,laser_azi,laser_ele,max_dist,step,target_lon,target_lat,target_alt);
		}
		else
		{
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			Lc[0] = target_x * (*laser_dist) / F0;
			Lc[1] = target_y * (*laser_dist) / F0;
			Lc[2] = f0 * (*laser_dist) / F0;
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//		Pobj = Pmav_i + Rb_v * Rg_b * Rc_g * Lc;
			multiply_matrices(Rb_v,Rg_b,pDst1);
			multiply_matrices(pDst1,Rc_g,pDst2);
			multiply_matrix_vector(pDst2,Lc,pV1);
			add_vectors(Pmav_i,pV1,Pobj);
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			XY2LL(Pobj[0],Pobj[1],target_lat,target_lon);
			(*target_alt) = -Pobj[2];
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		}
	}
	catch(...)
	{

    }
	return is_ok;
}
//---------------------------------------------------------------------------
double mv_utm_distance2(double lon1,double lat1,double lon2,double lat2)
{
	try{
		double e1,n1,e2,n2;
		LL2XY(lat1,lon1,&n1,&e1);
		LL2XY(lat2,lon2,&n2,&e2);
		return sqrt((e2-e1)*(e2-e1)+(n2-n1)*(n2-n1));
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
double mv_utm_distance3(double lon1,double lat1,double alt1,double lon2,double lat2,double alt2)
{
	try{
		double e1,n1,e2,n2;
		LL2XY(lat1,lon1,&n1,&e1);
		LL2XY(lat2,lon2,&n2,&e2);
		return sqrt((e2-e1)*(e2-e1)+(n2-n1)*(n2-n1)+(alt2-alt1)*(alt2-alt1));
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------
void mv_localization_start(double lon0,double lat0)
{
	try{
		// init projection
//	double FE = 3000000.000000;
//	double FN = 3000000.000000;
		double FE = 0.0;
		double FN = 0.0;
//	double lat0 = rad2deg(0.604756586);
//	double lon0 = rad2deg(0.651880476);
		double k0 = 0.99962560;
		set_LambertConic1SP_paramaeters(FE,FN,lat0,lon0,k0);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void mv_localization_end()
{
	try{
		if(s_fflash)
		{
			s_offset0 = ftell(s_fflash);
			fclose(s_fflash);
		}
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void header2str(t_dem_header &header,char *str)
{
	try{
		sprintf(str,"w = %d\r\nh = %d\r\nxll = %f\r\nyll = %f\r\ncellsizeX = %f\r\ncellsizeY = %f\r\nminHeight = %f\r\nmaxHeight = %f\r\nbIsFlipped = %d\r\n",
				header.w,header.h,header.xll,header.yll,header.cellsizeX,header.cellsizeY,header.minHeight,header.maxHeight,header.bIsFlipped);

//	sprintf(str,"w = %d\nh = %d\nxll = %f\nyll = %f\ncellsizeX = %f\ncellsizeY = %f\nminHeight = %f\nmaxHeight = %f\nbIsFlipped = %d\n",
//	header.w,header.h,header.xll,header.yll,header.cellsizeX,header.cellsizeY,header.minHeight,header.maxHeight,header.bIsFlipped);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void header2str(char *str)
{
	try{
		header2str(s_header,str);
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
#ifndef MV_EMBEDED
typedef struct {
  TLogPalette lPal;
  TPaletteEntry dummy[256];
} LogPal;
//---------------------------------------------------------------------------
void Bin_SaveAs_GrayScale_Height_Map(UnicodeString filename)
{
	Graphics::TBitmap *pBitmap = new Graphics::TBitmap();
	pBitmap->PixelFormat = pf8bit;
	pBitmap->Width = s_header.w;
	pBitmap->Height = s_header.h;

	LogPal SysPal;
	SysPal.lPal.palVersion = 0x300;
	SysPal.lPal.palNumEntries = 256;
	for(int i=0;i<SysPal.lPal.palNumEntries;i++)
	{
		SysPal.lPal.palPalEntry[i].peRed = i;
		SysPal.lPal.palPalEntry[i].peGreen = i;
		SysPal.lPal.palPalEntry[i].peBlue = i;
		SysPal.lPal.palPalEntry[i].peFlags = 0;
	}
	pBitmap->Palette = CreatePalette(&SysPal.lPal);

	Byte *pLine;
	float a,b;
	int v;
	a = 255.0 / (s_header.maxHeight - s_header.minHeight);
	b = 0.0 - a * s_header.minHeight;
	for(int Y=0;Y<s_header.h;Y++)
	{
		if(s_header.bIsFlipped)
			pLine = (Byte *)pBitmap->ScanLine[Y];
		else
			pLine = (Byte *)pBitmap->ScanLine[s_header.h - 1 - Y];
		for(int X=0;X<s_header.w;X++)
		{
			v = round(a * get_sample_val(X,Y) + b);
			if(v < 0)	v = 0;
			if(v > 255)	v = 255;
			pLine[X] = v;
		}
	}

	if(ExtractFileExt(filename).UpperCase() == ".JPG")
	{
		TJPEGImage *pJPEG = new TJPEGImage();
		pJPEG->Assign(pBitmap);
		pJPEG->Grayscale = true;
		pJPEG->PixelFormat = jf8Bit;
		pJPEG->CompressionQuality = 100;
		pJPEG->Compress();
		pJPEG->SaveToFile(filename);
		delete pJPEG;
	}
	else
	{
		pBitmap->SaveToFile(filename);
	}
	delete pBitmap;
}
#endif // MV_EMBEDED
//---------------------------------------------------------------------------
double pixel_size(double L,double w,double fov_deg,double t_deg)
{
	double d = 0;
	try{
		double fov_rad = deg2rad(fov_deg);
		double fov_rad_2 = fov_rad/2.0;
		double h = L*tan(fov_rad_2);

		double t_rad = deg2rad(t_deg);
		double D = fabs(h*sin(pi_2+fov_rad_2)/sin(pi_2-fov_rad_2-t_rad));
		d = 2.0*D/w;
	}
	catch(...){

	}
	return d;
}
//---------------------------------------------------------------------------
void CoordinateToDMS(double coord,int &deg,int &minutes,double &secs)
{
	try{
		double dlat,d,m,s;

		dlat = coord + (double)0.000001;

		d = (double)(long)dlat;
		m = (double)((dlat - d)*60.0);
		s = (double)((m - (double)(long)(m))*60.0);

		if(s >= 60.00)
		{
			if (m == 59)
			{
				d += 1;
				if (d < 0.0)
					d -= 1;
				else
					d += 1;

				m = 0;
				s -= 60.0;
			}
			else
			{
				s -= 60.00;
				s += 1;
			}
		}

		deg = (int)d;
		minutes = (int)m;
		secs = s;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
void DegMin2Decimal(double deg,double min,double &dec)
{
	try{
		dec = deg + min/60.0;
	}
	catch(...){

	}
}
//---------------------------------------------------------------------------
double DMS2Decimal(double d,double m,double s)
{
	try{
		return d + m/60.0 + s/3600.0;
	}
	catch(...){
		return 0;
	}
}
//---------------------------------------------------------------------------

