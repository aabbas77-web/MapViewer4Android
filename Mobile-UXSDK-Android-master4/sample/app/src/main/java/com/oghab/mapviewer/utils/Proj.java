package com.oghab.mapviewer.utils;

import com.oghab.mapviewer.MainActivity;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import java.util.Locale;

//CRSFactory crsFactory = new CRSFactory();
//CoordinateReferenceSystem WGS84 = crsFactory.createFromName("epsg:4326");
//CoordinateReferenceSystem UTM = crsFactory.createFromName("epsg:25833");

public class Proj {
    // WGS 84
//    static String wgs_84 = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
    static String wgs_84 = MainActivity.DecodeMessageJNI("tgfe`po,!59THX>nvube,!59THX>tqmmf,!ubmhopm>kpsq,");

    // Sphere Mercator ESRI:53004
//    static String esri_53004 = "+proj=merc +lon_0=0 +k=1 +x_0=0 +y_0=0 +a=6371000 +b=6371000 +units=m +no_defs";
    static String esri_53004 = MainActivity.DecodeMessageJNI("tgfe`po,!n>tujov,!1112847>c,!1112847>b,!1>1`z,!1>1`y,!2>l,!1>1`opm,!dsfn>kpsq,");

    // Popular Visualisation CRS / Mercator
//    static String epsg_3785 = "+proj=merc +lon_0=0 +k=1 +x_0=0 +y_0=0 +a=6378137 +b=6378137 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs";
    static String epsg_3785 = MainActivity.DecodeMessageJNI("tgfe`po,!n>tujov,!1-1-1-1-1-1-1>59thxpu,!8429847>c,!8429847>b,!1>1`z,!1>1`y,!2>l,!1>1`opm,!dsfn>kpsq,");

    // WGS 84 / World Mercator
//    static String epsg_3395 = "+proj=merc +lon_0=0 +k=1 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
    static String epsg_3395 = MainActivity.DecodeMessageJNI("tgfe`po,!n>tujov,!59THX>nvube,!59THX>tqmmf,!1>1`z,!1>1`y,!2>l,!1>1`opm,!dsfn>kpsq,");

    // NAD83
//    static String nad_83 = "+proj=longlat +ellps=GRS80 +datum=NAD83 +no_defs";
    static String nad_83 = MainActivity.DecodeMessageJNI("tgfe`po,!49EBO>nvube,!19TSH>tqmmf,!ubmhopm>kpsq,");

    // 2463-2491 = Pulkovo 1995 / Gauss-Kruger CM
    // 2492-2522 = Pulkovo 1942 / Gauss-Kruger CM
//    static String gauss_kruger_fmt = "+proj=tmerc +lat_0=0 +lon_0=%d +k=1 +x_0=%d +y_0=%d +ellps=krass +units=m +no_defs";
    static String gauss_kruger_fmt = MainActivity.DecodeMessageJNI("tgfe`po,!n>tujov,!ttbsl>tqmmf,!e&>1`z,!e&>1`y,!2>l,!e&>1`opm,!1>1`ubm,!dsfnu>kpsq,");

//    static String sk_42 = "+proj=longlat +ellps=krass +towgs84=23.57,-140.95,-79.8,0,0.35,0.79,-0.22 +no_defs";
    static String sk_42 = MainActivity.DecodeMessageJNI("tgfe`po,!33/1.-:8/1-64/1-1-9/:8.-6:/152.-86/43>59thxpu,!ttbsl>tqmmf,!ubmhopm>kpsq,");

    // 32601-32660 = WGS 84 / UTM zone N
//    static String utm_fmt = "+proj=utm +zone=%d +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
    static String utm_fmt = MainActivity.DecodeMessageJNI("tgfe`po,!n>tujov,!59THX>nvube,!59THX>tqmmf,!e&>fop{,!nuv>kpsq,");

    String Proj4ArgsByEpsg(int AEPSG){
        int I;
        if(AEPSG == 53004)
            return esri_53004;
        else
        if(AEPSG == 3785)
            return epsg_3785;
        else
        if(AEPSG == 3395)
            return epsg_3395;
        else
        if(AEPSG == 4269)
            return nad_83;
        else
        if(AEPSG == 4326)
            return wgs_84;
        else
        if((AEPSG >= 2463) && (AEPSG <= 2491)) {
            I = 21 + (AEPSG - 2463) * 6;
            if(I > 180) I = I - 360;
            return String.format(Locale.ENGLISH, gauss_kruger_fmt, I, 500000, 0);
        }
        else
        if((AEPSG >= 2492) && (AEPSG <= 2522)) {
            I = 9 + (AEPSG - 2492) * 6;
            if(I > 180) I = I - 360;
            return String.format(Locale.ENGLISH, gauss_kruger_fmt, I, 500000, 0);
        }
        else
        if((AEPSG >= 32601) && (AEPSG <= 32660)) {
            return String.format(Locale.ENGLISH, utm_fmt, AEPSG - 32600);
        }
        else
            return "";
    }

    static public int long_to_gauss_kruger_zone(double ALon){
        if(ALon > 0) {
            return (int)Math.floor(ALon / 6) + 1;
        }else{
            return (int)Math.floor((180 + ALon) / 6) + 30 + 1;
        }
    }

    static public String get_sk42_gauss_kruger_init(int AZone, boolean AIsNorth){
        int lon_0;
        int x_0, y_0;

        lon_0 = AZone * 6 - 3;
        if(AZone > 30){
            lon_0 = lon_0 - 360;
        }
        x_0 = AZone * 1000000 + 500000;
        if(AIsNorth)
            y_0 = 0;
        else
            y_0 = 10000000;
        return String.format(Locale.ENGLISH, gauss_kruger_fmt, lon_0, x_0, y_0);
    }

    static public String get_sk42_gauss_kruger_init(double ALon, double ALat){
        int zone = long_to_gauss_kruger_zone(ALon);
        return get_sk42_gauss_kruger_init(zone, (ALat > 0));
    }

    static public ProjCoordinate wgs84_to_sk42(double fLon,double fLat){
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", wgs_84);
        CoordinateReferenceSystem SK42 = crsFactory.createFromParameters("SK42", sk_42);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform wgsToSk42 = ctFactory.createTransform(WGS84, SK42);
        ProjCoordinate result = new ProjCoordinate();
        wgsToSk42.transform(new ProjCoordinate(fLon, fLat), result);
        return result;
    }

    static public ProjCoordinate sk42_to_wgs84(double fLon,double fLat){
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", wgs_84);
        CoordinateReferenceSystem SK42 = crsFactory.createFromParameters("SK42", sk_42);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform sk42ToWgs84 = ctFactory.createTransform(SK42,WGS84);
        ProjCoordinate result = new ProjCoordinate();
        sk42ToWgs84.transform(new ProjCoordinate(fLon, fLat), result);
        return result;
    }

    static public ProjCoordinate sk42_to_gauss_kruger(double ALon, double ALat){
        String gk_sk42 = get_sk42_gauss_kruger_init(ALon, ALat);
        int zone = long_to_gauss_kruger_zone(ALon);
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem GK42 = crsFactory.createFromParameters("GK42", gk_sk42);
        CoordinateReferenceSystem SK42 = crsFactory.createFromParameters("SK42", sk_42);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform sk42ToGk42 = ctFactory.createTransform(SK42,GK42);
        ProjCoordinate result = new ProjCoordinate();
        sk42ToGk42.transform(new ProjCoordinate(ALon, ALat), result);
        result.z = zone;
        return result;
    }

    static public ProjCoordinate gauss_kruger_to_sk42(double AX, double AY, int AZone, boolean AIsNorth){
        String gk_sk42 = get_sk42_gauss_kruger_init(AZone, AIsNorth);
        CRSFactory crsFactory = new CRSFactory();
        CoordinateReferenceSystem GK42 = crsFactory.createFromParameters("GK42", gk_sk42);
        CoordinateReferenceSystem SK42 = crsFactory.createFromParameters("SK42", sk_42);
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform sk42ToGk42 = ctFactory.createTransform(GK42,SK42);
        ProjCoordinate result = new ProjCoordinate();
        sk42ToGk42.transform(new ProjCoordinate(AX, AY), result);
        return result;
    }

    static public ProjCoordinate wgs84_to_gauss_kruger(double ALon, double ALat){
        ProjCoordinate res_sk42 = wgs84_to_sk42(ALon, ALat);
        return sk42_to_gauss_kruger(res_sk42.x,res_sk42.y);
    }

    static public ProjCoordinate gauss_kruger_to_wgs84(double AX, double AY, int AZone, boolean AIsNorth){
        ProjCoordinate res_sk42 = gauss_kruger_to_sk42(AX, AY, AZone, AIsNorth);
        return sk42_to_wgs84(res_sk42.x,res_sk42.y);
    }

}
