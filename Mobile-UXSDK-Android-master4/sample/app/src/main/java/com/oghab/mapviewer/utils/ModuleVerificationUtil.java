package com.oghab.mapviewer.utils;

//import android.support.annotation.Nullable;

//import android.support.annotation.Nullable;

import androidx.annotation.Nullable;

import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;

import java.util.Objects;

import dji.common.product.Model;
import dji.sdk.accessory.AccessoryAggregation;
import dji.sdk.accessory.beacon.Beacon;
import dji.sdk.accessory.speaker.Speaker;
import dji.sdk.accessory.spotlight.Spotlight;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;

//import androidx.annotation.Nullable;

/**
 * Created by dji on 16/1/6.
 */
public class ModuleVerificationUtil {
    public static boolean isProductModuleAvailable() {
        try {
            return (null != MApplication.getProductInstance());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isAircraft() {
        try {
            return MApplication.getProductInstance() instanceof Aircraft;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isHandHeld() {
        try {
            return MApplication.getProductInstance() instanceof HandHeld;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isCameraModuleAvailable() {
        try {
            return isProductModuleAvailable() && (null != MApplication.getProductInstance().getCamera());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isPlaybackAvailable() {
        try {
            return isCameraModuleAvailable() && (null != MApplication.getProductInstance()
                    .getCamera()
                    .getPlaybackManager());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isMediaManagerAvailable() {
        try {
            return isCameraModuleAvailable() && (null != MApplication.getProductInstance()
                    .getCamera()
                    .getMediaManager());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isRemoteControllerAvailable() {
        try {
            return isProductModuleAvailable() && isAircraft() && (null != Objects.requireNonNull(MApplication.getAircraftInstance())
                    .getRemoteController());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isFlightControllerAvailable() {
        try {
            return isProductModuleAvailable() && isAircraft() && (null != Objects.requireNonNull(MApplication.getAircraftInstance())
                    .getFlightController());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isCompassAvailable() {
        try {
            return isFlightControllerAvailable() && isAircraft() && (null != Objects.requireNonNull(MApplication.getAircraftInstance())
                    .getFlightController()
                    .getCompass());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isFlightLimitationAvailable() {
        try {
            return isFlightControllerAvailable() && isAircraft();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isGimbalModuleAvailable() {
        try {
            return isProductModuleAvailable() && (null != MApplication.getProductInstance().getGimbal());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isAirlinkAvailable() {
        try {
            return isProductModuleAvailable() && (null != MApplication.getProductInstance().getAirLink());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isWiFiLinkAvailable() {
        try {
            return isAirlinkAvailable() && (null != MApplication.getProductInstance().getAirLink().getWiFiLink());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static boolean isLightbridgeLinkAvailable() {
        try {
            return isAirlinkAvailable() && (null != MApplication.getProductInstance()
                    .getAirLink()
                    .getLightbridgeLink());
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }

    public static AccessoryAggregation getAccessoryAggregation() {
        try {
            Aircraft aircraft = (Aircraft) MApplication.getProductInstance();

            if (aircraft != null && null != aircraft.getAccessoryAggregation()) {
                return aircraft.getAccessoryAggregation();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static Speaker getSpeaker() {
        try {
            Aircraft aircraft = (Aircraft) MApplication.getProductInstance();

            if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getSpeaker()) {
                return aircraft.getAccessoryAggregation().getSpeaker();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static Beacon getBeacon() {
        try {
            Aircraft aircraft = (Aircraft) MApplication.getProductInstance();

            if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getBeacon()) {
                return aircraft.getAccessoryAggregation().getBeacon();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static Spotlight getSpotlight() {
        try {
            Aircraft aircraft = (Aircraft) MApplication.getProductInstance();

            if (aircraft != null && null != aircraft.getAccessoryAggregation() && null != aircraft.getAccessoryAggregation().getSpotlight()) {
                return aircraft.getAccessoryAggregation().getSpotlight();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    @Nullable
    public static Simulator getSimulator() {
        try {
            Aircraft aircraft = MApplication.getAircraftInstance();
            if (aircraft != null) {
                FlightController flightController = aircraft.getFlightController();
                if (flightController != null) {
                    return flightController.getSimulator();
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    @Nullable
    public static FlightController getFlightController() {
        try {
            Aircraft aircraft = MApplication.getAircraftInstance();
            if (aircraft != null) {
                return aircraft.getFlightController();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return null;
    }

    public static boolean isMavic2Product() {
        try {
            BaseProduct baseProduct = MApplication.getProductInstance();
            if (baseProduct != null) {
                return baseProduct.getModel() == Model.MAVIC_2_PRO || baseProduct.getModel() == Model.MAVIC_2_ZOOM;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return false;
    }
}
