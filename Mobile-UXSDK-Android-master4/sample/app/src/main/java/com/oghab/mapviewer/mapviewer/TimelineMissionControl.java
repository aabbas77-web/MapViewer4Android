package com.oghab.mapviewer.mapviewer;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.oghab.mapviewer.MApplication;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.utils.GeneralUtils;
import com.oghab.mapviewer.utils.mv_utils;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.gimbal.Attitude;
import dji.common.gimbal.Rotation;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.Triggerable;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.timeline.TimelineMission;
import dji.sdk.mission.timeline.actions.AircraftYawAction;
import dji.sdk.mission.timeline.actions.GimbalAttitudeAction;
import dji.sdk.mission.timeline.actions.GoToAction;
import dji.sdk.mission.timeline.triggers.AircraftLandedTrigger;
import dji.sdk.mission.timeline.triggers.BatteryPowerLevelTrigger;
import dji.sdk.mission.timeline.triggers.Trigger;
import dji.sdk.mission.timeline.triggers.WaypointReachedTrigger;
import dji.sdk.products.Aircraft;

/**
 * Class for Timeline MissionControl.
 */
public class TimelineMissionControl {

    private MissionControl missionControl;
    private FlightController flightController;
    private TimelineEvent preEvent;
    private TimelineElement preElement;
    private DJIError preError;

    protected double homeLatitude = 181;
    protected double homeLongitude = 181;

    public WaypointMission mission = null;

    private void setResultToToast(final String text){
        try
        {
            MainActivity.activity.runOnUiThread(() -> {
                try {
//                        Toast.makeText(MainActivity.activity, text, Toast.LENGTH_SHORT).show();
                    MainActivity.MyLogInfo(text);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void setRunningResultToText(final String s) {
        try {
            setResultToToast(s);

//        post(new Runnable() {
//            @Override
//            public void run() {
//                if (runningInfoTV == null) {
//                    Toast.makeText(getContext(), "textview = null", Toast.LENGTH_SHORT).show();
//                } else {
//                    runningInfoTV.append(s + "\n");
//                }
//            }
//        });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void setTimelinePlanToText(final String s) {
        try {
        setResultToToast(s);

//        post(new Runnable() {
//            @Override
//            public void run() {
//                if (timelineInfoTV == null) {
//                    Toast.makeText(getContext(), "textview = null", Toast.LENGTH_SHORT).show();
//                } else {
//                    timelineInfoTV.append(s + "\n");
//                }
//            }
//        });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Demo on BatteryPowerLevelTrigger.  Once the batter remaining power is equal or less than the value,
     * the trigger's action will be called.
     *
     * @param triggerTarget which can be any action object or timeline object.
     */
    private void addBatteryPowerLevelTrigger(Triggerable triggerTarget) {
        try {
            float value = 20f;
            BatteryPowerLevelTrigger trigger = new BatteryPowerLevelTrigger();
            trigger.setPowerPercentageTriggerValue(value);
            addTrigger(trigger, triggerTarget, " at level " + value);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Demo on WaypointReachedTrigger.  Once the expected waypoint is reached in the waypoint mission execution process,
     * this trigger's action will be called. If user has some special things to do for this waypoint, the code can be put
     * in this trigger action method.
     *
     * @param triggerTarget
     */
    private void addWaypointReachedTrigger(Triggerable triggerTarget) {
        try {
            int value = 1;
            WaypointReachedTrigger trigger = new WaypointReachedTrigger();
            trigger.setWaypointIndex(value);
            addTrigger(trigger, triggerTarget, " at index " + value);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    /**
     * Demo on AircraftLandedTrigger. Once the aircraft is landed, this trigger action will be called if the timeline is
     * not finished yet.
     * @param triggerTarget
     */
    private void addAircraftLandedTrigger(Triggerable triggerTarget) {
        try {
            AircraftLandedTrigger trigger = new AircraftLandedTrigger();
            addTrigger(trigger, triggerTarget, "");
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private Trigger.Listener triggerListener = (trigger, event, error) -> {
        try {
            assert trigger != null;
            assert event != null;
            setRunningResultToText("Trigger " + trigger.getClass().getSimpleName() + " event is " + event.name() + (error==null? " ":error.getDescription()));
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    };

    private void initTrigger(final Trigger trigger) {
        try {
            trigger.addListener(triggerListener);
            trigger.setAction(() -> {
                try {
                    setRunningResultToText("Trigger " + trigger.getClass().getSimpleName() + " Action method onCall() is invoked");
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void addTrigger(Trigger trigger, Triggerable triggerTarget, String additionalComment) {
        try {
            if (triggerTarget != null) {

                initTrigger(trigger);
                List<Trigger> triggers = triggerTarget.getTriggers();
                if (triggers == null) {
                    triggers = new ArrayList<>();
                }

                triggers.add(trigger);
                triggerTarget.setTriggers(triggers);

                setTimelinePlanToText(triggerTarget.getClass().getSimpleName()
                                                  + " Trigger "
                                                  + triggerTarget.getTriggers().size()
                                                  + ") "
                                                  + trigger.getClass().getSimpleName()
                                                  + additionalComment);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void initKMLTimeline(int start_idx,float altitude,float speed,int point_count,boolean bCustomAlt,boolean bMultiView,int nPitch) {
        try {
            List<TimelineElement> elements = new ArrayList<>();

            missionControl = MissionControl.getInstance();
            MissionControl.Listener listener = (element, event, error) -> updateTimelineStatus(element, event, error);

            // reset gimbal
            MainActivity.tab_camera.resetGimbal();

            // Step 1: takeoff from the ground
//            setTimelinePlanToText("Step 1: takeoff from the ground");
//            elements.add(new TakeOffAction());

            // Step 2: reset the gimbal to horizontal angle in 2 seconds. this fpr movable gimbals like inspire1
//            setTimelinePlanToText("Step 2: reset the gimbal angles in 2 seconds");
//            Attitude attitude0 = new Attitude(Rotation.NO_ROTATION, Rotation.NO_ROTATION, Rotation.NO_ROTATION);
//            GimbalAttitudeAction gimbalAction0 = new GimbalAttitudeAction(attitude0);
//            gimbalAction0.setGimbalMode(GimbalMode.FREE);
//            gimbalAction0.setCompletionTime(2);
//            elements.add(gimbalAction0);

            // Step 4: start a waypoint mission while the aircraft is still recording the video
            setTimelinePlanToText("Step 4: start a waypoint mission");
            mission = MainActivity.tab_map.load_mission_kml_as_list(start_idx,altitude,speed,point_count,bCustomAlt,bMultiView,nPitch);
//            altitude = MainActivity.tab_map.mission_altitude;

            // Step 3: Go 10 meters from home point
//            setTimelinePlanToText("Step 3: Go "+Float.toString(altitude + 5.0f)+" meters from camera point");
//            elements.add(new GoToAction(altitude + 5.0f));

            // Step 2: reset the gimbal to horizontal angle in 2 seconds. this fpr movable gimbals like inspire1
//            setTimelinePlanToText("Step 5: set the gimbal pitch: "+Integer.toString(Tab_Map.mission_pitch)+" and gimbal yaw: "+Integer.toString(Tab_Map.mission_yaw)+" in 2 seconds");
//            Attitude attitude = new Attitude(Tab_Map.mission_pitch, 0, Tab_Map.mission_yaw);
//            GimbalAttitudeAction gimbalAction = new GimbalAttitudeAction(attitude);
//            gimbalAction.setGimbalMode(GimbalMode.FREE);
//            gimbalAction.setCompletionTime(2);
//            elements.add(gimbalAction);

            if(mission != null) {
                TimelineElement waypointMission = TimelineMission.elementFromWaypointMission(mission);
                elements.add(waypointMission);
                addWaypointReachedTrigger(waypointMission);
            }

            // Step 5: go back home
//            setTimelinePlanToText("Step 5: go back home");
//            elements.add(new GoHomeAction());

            addAircraftLandedTrigger(missionControl);
            addBatteryPowerLevelTrigger(missionControl);

            cleanTimelineDataAndLog();

            missionControl.scheduleElements(elements);
            missionControl.addListener(listener);

//            try {
//                MainActivity.activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            int N = mission.getWaypointList().size();
//                            MainActivity.tab_map.mission_seekbar.setMax(N);
//                            MainActivity.tab_map.mission_seekbar.setProgress(Tab_Map.start_idx + 1);
//                            MainActivity.tab_map.mission_progress.setText("Loading: "+Integer.toString(Tab_Map.start_idx + 1) + "/" + Integer.toString(Tab_Map.start_idx + N));
//                        } catch (Throwable ex) {
//                            MainActivity.MyLog(ex);
//                        }
//                    }
//                });
//            } catch (Throwable ex) {
//                MainActivity.MyLog(ex);
//            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void initFavoritesTimeline(int n,float altitude,int index) {
        try {
            List<TimelineElement> elems = new ArrayList<>();

            missionControl = MissionControl.getInstance();
            MissionControl.Listener listener = (element, event, error) -> updateTimelineStatus(element, event, error);

            //Step 1: takeoff from the ground
//        setTimelinePlanToText("Step 1: takeoff from the ground");
//        elems.add(new TakeOffAction());

            //Step 2: reset the gimbal to horizontal angle in 2 seconds.
            setTimelinePlanToText("Step 2: set the gimbal pitch -90 angle in 2 seconds");
            Attitude attitude = new Attitude(-90, Rotation.NO_ROTATION, Rotation.NO_ROTATION);
            GimbalAttitudeAction gimbalAction = new GimbalAttitudeAction(attitude);
            gimbalAction.setCompletionTime(2);
            if(gimbalAction.checkValidity() == null)    elems.add(gimbalAction);

            //Step 3: Go 10 meters from home point
            setTimelinePlanToText("Step 3: Go "+Float.toString(altitude)+" meters from camera point");
            elems.add(new GoToAction(altitude + 5.0f));

            //Step 4: start a waypoint mission while the aircraft is still recording the video
            setTimelinePlanToText("Step 4: start a waypoint mission");

//        List<WaypointMission> list = MainActivity.tab_map.load_favorites_kml_as_list(n,altitude,index);
            List<WaypointMission> list = MainActivity.tab_map.load_favorites_kml_as_list_ex(n,altitude,index);
            for(int i=0;i<list.size();i++)
            {
                WaypointMission mission = list.get(i);
                if(mission != null) {
                    TimelineElement waypointMission = TimelineMission.elementFromWaypointMission(mission);
                    elems.add(waypointMission);
                    addWaypointReachedTrigger(waypointMission);
                }
            }

            //Step 5: go back home
//            setTimelinePlanToText("Step 5: go back home");
//            elems.add(new GoHomeAction());

            addAircraftLandedTrigger(missionControl);
            addBatteryPowerLevelTrigger(missionControl);

            cleanTimelineDataAndLog();

            missionControl.scheduleElements(elems);
            missionControl.addListener(listener);

            startTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void GotoByTimeline(double lon,double lat,float altitude) {
        try {
            List<TimelineElement> elems = new ArrayList<>();

            missionControl = MissionControl.getInstance();
            if(missionControl == null)  return;
            MissionControl.Listener listener = (element, event, error) -> {
                try {
                    updateTimelineStatus(element, event, error);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            };

            // Step 1: Go 10 meters from home point
            setTimelinePlanToText("Step 1: Go " + Float.toString(altitude) + " meters from camera point");
            elems.add(new GoToAction(altitude + 5.0f));

            // Step 1: Go 10 meters from home point
            setTimelinePlanToText("Step 2: Go " + Float.toString(altitude) + " meters from camera point");
            elems.add(new GoToAction(new LocationCoordinate2D(lat, lon), altitude));

            addAircraftLandedTrigger(missionControl);
            addBatteryPowerLevelTrigger(missionControl);

            cleanTimelineDataAndLog();

            missionControl.scheduleElements(elems);
            missionControl.addListener(listener);

            startTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void PanoramaByTimeline(double lon,double lat,float altitude,float speed) {
        try {
            List<TimelineElement> elements = new ArrayList<>();

            missionControl = MissionControl.getInstance();
            MissionControl.Listener listener = (element, event, error) -> updateTimelineStatus(element, event, error);

            // reset gimbal
            MainActivity.tab_camera.resetGimbal();

            // start a waypoint mission while the aircraft is still recording the video
            setTimelinePlanToText("Step 4: start a waypoint mission");
            mission = MainActivity.tab_map.create_mission_panorama_as_list(lon,lat,altitude,speed);

            if(mission != null) {
                TimelineElement waypointMission = TimelineMission.elementFromWaypointMission(mission);
                elements.add(waypointMission);
                addWaypointReachedTrigger(waypointMission);
            }

            addAircraftLandedTrigger(missionControl);
            addBatteryPowerLevelTrigger(missionControl);

            cleanTimelineDataAndLog();

            missionControl.scheduleElements(elements);
            missionControl.addListener(listener);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

//    public void PanoramaByTimeline(double lon,double lat,float altitude)
//    {
//        try {
//            List<TimelineElement> elems = new ArrayList<>();
//
//            missionControl = MissionControl.getInstance();
//            if(missionControl == null)  return;
//            MissionControl.Listener listener = (element, event, error) -> {
//                try {
//                    updateTimelineStatus(element, event, error);
//                }
//                catch (Throwable ex)
//                {
//                    MainActivity.MyLog(ex);
//                }
//            };
//
//            // Step 1: Go 10 meters from home point
//            setTimelinePlanToText("Step 1: Go " + Float.toString(altitude) + " meters from camera point");
//            elems.add(new GoToAction(altitude + 5.0f));
//
//            // Step 1: Go 10 meters from home point
//            setTimelinePlanToText("Step 2: Go " + Float.toString(altitude) + " meters from camera point");
//            elems.add(new GoToAction(new LocationCoordinate2D(lat, lon), altitude));
//
//            addAircraftLandedTrigger(missionControl);
//            addBatteryPowerLevelTrigger(missionControl);
//
//            cleanTimelineDataAndLog();
//
//            missionControl.scheduleElements(elems);
//            missionControl.addListener(listener);
//
//            startTimeline();
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
//    }

    // The range of yaw is [-180, 180]
    // The range of pitch is [-90, 0]
    public void ChangeUAV_YawByTimeline(float yaw,float gimb_pitch) {
        try {
            List<TimelineElement> elems = new ArrayList<>();

            missionControl = MissionControl.getInstance();
            if(missionControl == null)  return;
            MissionControl.Listener listener = (element, event, error) -> {
                try {
                    updateTimelineStatus(element, event, error);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            };

            AircraftYawAction aircraftYawAction = new AircraftYawAction(yaw, true);
            if(aircraftYawAction.checkValidity() == null)    elems.add(aircraftYawAction);

//            MainActivity.tab_camera.rotateGimbal(0.0f, gimb_pitch, 0.0f);
            MainActivity.tab_camera.rotate_gimbal_pitch(gimb_pitch);

//            Attitude attitude;
//            if(MainActivity.tab_camera.isGimbalFeatureSupported(CapabilityKey.ADJUST_YAW))
//                attitude = new Attitude(gimb_pitch, 0, 0);
//            else
//                attitude = new Attitude(gimb_pitch, Rotation.NO_ROTATION, Rotation.NO_ROTATION);
//            GimbalAttitudeAction gimbalAction = new GimbalAttitudeAction(attitude);
//            if(gimbalAction.checkValidity() == null)    elems.add(gimbalAction);

/*
            // waypoint mission
            WaypointMission.Builder builder = new WaypointMission.Builder();
            builder.autoFlightSpeed(5f);
            builder.maxFlightSpeed(10f);
            builder.setExitMissionOnRCSignalLostEnabled(false);
            builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
            builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
            builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
            builder.headingMode(WaypointMissionHeadingMode.USING_WAYPOINT_HEADING);
            builder.repeatTimes(1);
            List<Waypoint> waypointList = new ArrayList<>();

            Waypoint mWaypoint;

            mWaypoint = new Waypoint(MainActivity.uav_lat, MainActivity.uav_lon, (float)MainActivity.uav_alt_above_ground);
            mWaypoint.altitude = (float)MainActivity.uav_alt_above_ground;
            mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, (int)yaw));
            mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, (int)gimb_pitch));
            waypointList.add(mWaypoint);

            mWaypoint = new Waypoint(MainActivity.uav_lat, MainActivity.uav_lon, (float)MainActivity.uav_alt_above_ground);
            mWaypoint.altitude = (float)MainActivity.uav_alt_above_ground;
            mWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, (int)yaw));
            mWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, (int)gimb_pitch));
            waypointList.add(mWaypoint);

            builder.waypointList(waypointList).waypointCount(waypointList.size());
            WaypointMission mission = builder.build();
            if(mission != null) {
                TimelineElement waypointMission = TimelineMission.elementFromWaypointMission(mission);
                elems.add(waypointMission);
                addWaypointReachedTrigger(waypointMission);
            }
*/
            addAircraftLandedTrigger(missionControl);
            addBatteryPowerLevelTrigger(missionControl);

            cleanTimelineDataAndLog();

            missionControl.scheduleElements(elems);
            missionControl.addListener(listener);

            startTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void updateTimelineStatus(@Nullable TimelineElement element, TimelineEvent event, DJIError error) {
        try {
            if (element == preElement && event == preEvent && error == preError) {
                return;
            }

            String strClassName;
            if (element != null) {
                if (element instanceof TimelineMission) {
                    strClassName = ((TimelineMission) element).getMissionObject().getClass().getSimpleName();
                    setRunningResultToText(strClassName
                            + " event is "
                            + event.toString()
                            + " "
                            + (error == null ? "" : error.getDescription()));
                } else {
                    strClassName = element.getClass().getSimpleName();
                    setRunningResultToText(strClassName
                            + " event is "
                            + event.toString()
                            + " "
                            + (error == null ? "" : error.getDescription()));
                }
            } else {
                setRunningResultToText("Timeline Event is " + event.toString() + " " + (error == null
                        ? ""
                        : "Failed:"
                        + error.getDescription()));
                if(event == TimelineEvent.FINISHED)// TODO Test Timeline event is finished
                {
                    try
                    {
                        MainActivity.activity.runOnUiThread(() -> {
                            try {
                                MainActivity.tab_map.b_timeline_start.setEnabled(true);
                            }
                            catch (Throwable ex)
                            {
                                MainActivity.MyLog(ex);
                            }
                        });
                    }
                    catch (Throwable ex)
                    {
                        MainActivity.MyLog(ex);
                    }
                }
            }

            preEvent = event;
            preElement = element;
            preError = error;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private int waypoint_i = 0;
    private int action_i = 0;
    private float altitude = 0;
    private long simulator_delay;
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void simulateTimeline() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity, R.style.CustomAlertDialog);
            builder.setTitle("Simulator delay [ms] ?");
            builder.setCancelable(false);

            // Set up the input
            final EditText input = new EditText(MainActivity.activity);
            input.setText("1000");
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            //        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            //            input.setInputType(InputType.TYPE_CLASS_TEXT);
            //            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(MainActivity.ctx.getString(R.string.ok), (dialog, which) -> {
                try
                {
                    simulator_delay = (long)mv_utils.parseDouble(input.getText().toString());

                    if (mission != null) {
                        MainActivity.tab_map.b_timeline_simulate.setEnabled(false);
                        Tab_Messenger.showToast("Mission started...");

                        //starting our task which update textview every 1000 ms
                        waypoint_i = 0;
                        action_i = 0;
                        MainActivity.bIsSimulating = true;

                        MainActivity.uav_yaw = 0;
                        MainActivity.uav_pitch = 0;
                        MainActivity.uav_roll = 0;

                        customHandler.postDelayed(updateTimerThread, 100);
                    } else {
                        Tab_Messenger.showToast("Init the timeline first by clicking the Init button");
                    }
                    MainActivity.hide_keyboard(input);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });
            builder.setNegativeButton(MainActivity.ctx.getString(R.string.cancel), (dialog, which) -> {
                try
                {
                    MainActivity.hide_keyboard(input);
                    dialog.cancel();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            builder.setCancelable(false);
            builder.show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    boolean bPhotoCapture = false;
    private Handler customHandler = new Handler();
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            try
            {
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            final int N = mission.getWaypointCount();
                            if (waypoint_i < N) {
                                Waypoint p = mission.getWaypointList().get(waypoint_i);
                                MainActivity.uav_lon = p.coordinate.getLongitude();
                                MainActivity.uav_lat = p.coordinate.getLatitude();
                                MainActivity.uav_alt_above_ground = p.altitude;
                                MainActivity.uav_ground_alt = MainActivity.GetHeightJNI(MainActivity.uav_lon,MainActivity.uav_lat);
                                MainActivity.uav_alt = MainActivity.uav_ground_alt + MainActivity.uav_alt_above_ground;
//                                MainActivity.uav_alt = MainActivity.home_alt + MainActivity.uav_alt_above_ground;

//                                if (waypoint_i <= 0) {
//                                    altitude = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
//                                }
//                                MainActivity.uav_alt = altitude + p.altitude;

                                bPhotoCapture = false;
                                final int M = p.waypointActions.size();
                                if(action_i < M)
                                {
                                    switch (p.waypointActions.get(action_i).actionType) {
//                                        case CAMERA_FOCUS: {
//                                            break;
//                                        }
//                                        case CAMERA_ZOOM: {
//                                            break;
//                                        }
                                        case GIMBAL_PITCH: {
                                            MainActivity.gimb_pitch = p.waypointActions.get(action_i).actionParam;
                                            break;
                                        }
                                        case ROTATE_AIRCRAFT: {
                                            MainActivity.gimb_yaw = p.waypointActions.get(action_i).actionParam;
                                            break;
                                        }
//                                        case STOP_RECORD: {
//                                            break;
//                                        }
//                                        case START_RECORD: {
//                                            break;
//                                        }
                                        case START_TAKE_PHOTO: {
                                            bPhotoCapture = true;
                                            break;
                                        }
//                                        case STAY: {
//                                            break;
//                                        }
                                    }

                                    if (bPhotoCapture) {
                                        MainActivity.gimb_roll = 0.0f;
// bug fixed at 19/5/2024
//                                        MainActivity.image_yaw_enc = (float)MainActivity.db_deg(MainActivity.uav_yaw + MainActivity.gimb_yaw);
//                                        MainActivity.image_pitch_enc = MainActivity.uav_pitch + MainActivity.gimb_pitch;
//                                        MainActivity.image_roll_enc = MainActivity.uav_roll + MainActivity.gimb_roll;

                                        MainActivity.image_yaw_enc = MainActivity.gimb_yaw;
                                        MainActivity.image_pitch_enc = MainActivity.gimb_pitch;
                                        MainActivity.image_roll_enc = MainActivity.gimb_roll;

                                        MainActivity.image_yaw = (float)MainActivity.db_deg(MainActivity.image_yaw_enc + MainActivity.dYaw);
                                        MainActivity.image_pitch = MainActivity.image_pitch_enc + MainActivity.dPitch;
                                        MainActivity.image_roll = MainActivity.image_roll_enc + MainActivity.dRoll;

                                        MainActivity.tab_camera.update_status(true);
                                        MainActivity.playCameraShutterSound();
                                    }

//                                    if (TcpServer.connected) {
//                                        MainActivity.tab_main.send_uav_status();
//                                        Thread.sleep(100);
//                                        if (bPhotoCapture) {
//                                            MainActivity.tab_main.send_photo_capture();
//                                            if (waypoint_i <= 0) {
//                                                for (int j = 0; j < 1; j++) {
//                                                    Thread.sleep(1000);
//                                                    MainActivity.tab_main.send_uav_status();
//                                                    Thread.sleep(1000);
//                                                    MainActivity.tab_main.send_photo_capture();
//                                                }
//                                            }
//                                        }
//                                    }
                                    action_i++;
                                }
                                else
                                {
                                    action_i = 0;
                                    waypoint_i++;
                                    try {
                                        MainActivity.activity.runOnUiThread(() -> {
                                            try {
                                                MainActivity.tab_camera.update_status(true);
                                                String strText = Integer.toString(Tab_Map.start_idx + waypoint_i) + "/" + Integer.toString(Tab_Map.start_idx + N);// Simulating
                                                MainActivity.tab_map.mission_progress.setText(strText);
                                                MainActivity.tab_camera.tv_mission_progress.setText(strText);
                                            } catch (Throwable ex) {
                                                MainActivity.MyLog(ex);
                                            }
                                        });
                                    } catch (Throwable ex) {
                                        MainActivity.MyLog(ex);
                                    }
                                }
                            } else {
                                waypoint_i = 0;
                                action_i = 0;
                                MainActivity.bIsSimulating = false;
                                MainActivity.tab_map.b_timeline_simulate.setEnabled(true);
                                Tab_Messenger.showToast("Mission finished...");
                            }

                            if (MainActivity.bIsSimulating)
                            {
                                if(bPhotoCapture)
                                    customHandler.postDelayed(this, simulator_delay);
                                else
                                    customHandler.postDelayed(this, 1);
                            }
                        }
                        catch (Throwable ex)
                        {
                            MainActivity.MyLog(ex);
                        }
                    }
                });
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        }
    };

//    private Runnable updateTimerThread = new Runnable() {
//        public void run() {
//            try
//            {
//                MainActivity.activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try
//                        {
//                            final int N = mission.getWaypointCount();
//                            if (waypoint_i < N) {
//                                Waypoint p = mission.getWaypointList().get(waypoint_i);
//                                MainActivity.uav_lon = p.coordinate.getLongitude();
//                                MainActivity.uav_lat = p.coordinate.getLatitude();
//                                if (waypoint_i <= 0) {
//                                    altitude = MainActivity.GetHeightJNI(MainActivity.uav_lon, MainActivity.uav_lat);
//                                }
//                                MainActivity.uav_alt = altitude + p.altitude;
//                                MainActivity.image_roll = 0;
//                                MainActivity.tab_camera.update_status(true);
//
//                                boolean bPhotoCapture = false;
//                                for (int i = 0; i < p.waypointActions.size(); i++) {
//                                    switch (p.waypointActions.get(i).actionType) {
////                                        case CAMERA_FOCUS: {
////                                            break;
////                                        }
////                                        case CAMERA_ZOOM: {
////                                            break;
////                                        }
//                                        case GIMBAL_PITCH: {
//                                            MainActivity.image_pitch = p.waypointActions.get(i).actionParam;
//                                            break;
//                                        }
//                                        case ROTATE_AIRCRAFT: {
//                                            MainActivity.image_yaw = p.waypointActions.get(i).actionParam;
//                                            MainActivity.tab_camera.update_status(true);
//                                            break;
//                                        }
////                                        case STOP_RECORD: {
////                                            break;
////                                        }
////                                        case START_RECORD: {
////                                            break;
////                                        }
//                                        case START_TAKE_PHOTO: {
//                                            bPhotoCapture = true;
//                                            break;
//                                        }
////                                        case STAY: {
////                                            break;
////                                        }
//                                    }
//
//                                    if (bPhotoCapture) {
//                                        MainActivity.playCameraShutterSound();
//                                        MainActivity.activity.runOnUiThread(new Runnable() {
//                                            public void run() {
//                                                String strText = Float.toString(MainActivity.image_yaw);
//                                                MainActivity.tab_map.mission_progress.setText(strText);
//                                                MainActivity.tab_main.send_uav_status();
//                                                Tab_Map.map.postInvalidate();
//                                                try {
//                                                    Thread.sleep(simulator_delay);
//                                                } catch (InterruptedException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    }
//
//                                    if (TcpServer.connected) {
//                                        MainActivity.tab_main.send_uav_status();
//                                        Thread.sleep(100);
//                                        if (bPhotoCapture) {
//                                            MainActivity.tab_main.send_photo_capture();
//                                            if (waypoint_i <= 0) {
//                                                for (int j = 0; j < 1; j++) {
//                                                    Thread.sleep(1000);
//                                                    MainActivity.tab_main.send_uav_status();
//                                                    Thread.sleep(1000);
//                                                    MainActivity.tab_main.send_photo_capture();
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                waypoint_i++;
//
//                                try {
//                                    MainActivity.activity.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            try {
//                                                MainActivity.tab_camera.update_status(true);
//                                                String strText = Integer.toString(Tab_Map.start_idx + waypoint_i) + "/" + Integer.toString(Tab_Map.start_idx + N)+" Simulating";
//                                                MainActivity.tab_map.mission_progress.setText(strText);
//                                                MainActivity.tab_camera.tv_mission_progress.setText(strText);
//                                            } catch (Throwable ex) {
//                                                MainActivity.MyLog(ex);
//                                            }
//                                        }
//                                    });
//                                } catch (Throwable ex) {
//                                    MainActivity.MyLog(ex);
//                                }
//                            } else {
//                                waypoint_i = 0;
//                                MainActivity.bIsSimulating = false;
//                                MainActivity.tab_map.b_timeline_simulate.setEnabled(true);
//                                ToastUtils.setResultToToast("Mission finished...");
//                            }
//
//                            if (MainActivity.bIsSimulating) customHandler.postDelayed(this, simulator_delay);
//                        }
//                        catch (Throwable ex)
//                        {
//                            MainActivity.MyLog(ex);
//                        }
//                    }
//                });
//            }
//            catch (Throwable ex)
//            {
//                MainActivity.MyLog(ex);
//            }
//        }
//    };

    public void startTimeline() {
        try {
            if (MissionControl.getInstance().scheduledCount() > 0) {
                MainActivity.tab_map.b_timeline_start.setEnabled(false);
                MainActivity.nCapturedFrames = 0;
                MissionControl.getInstance().startTimeline();
            } else {
                Tab_Messenger.showToast("Init the timeline first by clicking the LoadMission button");
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void stopTimeline() {
        try {
            MainActivity.bIsSimulating = false;
            MainActivity.tab_map.b_timeline_start.setEnabled(true);
            MainActivity.tab_map.b_timeline_simulate.setEnabled(true);
            MissionControl.getInstance().stopTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void pauseTimeline() {
        try {
            MissionControl.getInstance().pauseTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void resumeTimeline() {
        try {
            MissionControl.getInstance().resumeTimeline();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void cleanTimelineDataAndLog() {
        try {
            if (missionControl != null && missionControl.scheduledCount() > 0) {
                missionControl.unscheduleEverything();
                missionControl.removeAllListeners();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    protected void onAttachedToWindow() {
        try {
            BaseProduct product = MApplication.getProductInstance();
            if (product == null || !product.isConnected()) {
//                MainActivity.bDJIExists = false;
//                ToastUtils.setResultToToast("Disconnect");
                missionControl = null;
            } else {
//                MainActivity.bDJIExists = true;
                missionControl = MissionControl.getInstance();
                if (product instanceof Aircraft) {
                    flightController = ((Aircraft) product).getFlightController();
                }
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    protected void onDetachedFromWindow() {
        try {
            cleanTimelineDataAndLog();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void get_home_point() {
        try {
            if (MApplication.getProductInstance() instanceof Aircraft && !GeneralUtils.checkGpsCoordinate(
                    homeLatitude,
                    homeLongitude) && flightController != null) {
                flightController.getHomeLocation(new CommonCallbacks.CompletionCallbackWith<LocationCoordinate2D>() {
                    @Override
                    public void onSuccess(LocationCoordinate2D locationCoordinate2D) {
                        homeLatitude = locationCoordinate2D.getLatitude();
                        homeLongitude = locationCoordinate2D.getLongitude();
                        if (GeneralUtils.checkGpsCoordinate(homeLatitude, homeLongitude)) {
                            setTimelinePlanToText("home point latitude: " + homeLatitude + "\nhome point longitude: " + homeLongitude);
                        } else {
                            Tab_Messenger.showToast("Failed to get home coordinates: Invalid GPS coordinate");
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        if(djiError != null)    Tab_Messenger.showToast("Failed to get home coordinates: " + djiError.getDescription());
                    }
                });
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
