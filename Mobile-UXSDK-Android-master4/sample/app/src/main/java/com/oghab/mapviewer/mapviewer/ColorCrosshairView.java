package com.oghab.mapviewer.mapviewer;

/*
  @author Ali Abbas
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.location.Location;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.oghab.mapviewer.MainActivity;

import java.util.Locale;

public class ColorCrosshairView extends View {
    private Paint paint;
    private Path path;
    private final TextPaint textPaint = new TextPaint();

    public int layoutWidth = 0;
    public int layoutHeight = 0;

    float cx;
    float cy;

    public boolean camera_status = true;
    DisplayMetrics dm;

    public ColorCrosshairView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        try
        {
            this.setVisibility(View.VISIBLE);

            // Get the display metrics
            Resources resources = context.getResources();
            dm = resources.getDisplayMetrics();

            initPaint();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void initPaint() {
        try
        {
            setMinimumWidth(100);
            setMinimumHeight(100);

            path = new Path();

            paint = new Paint();
            paint.setColor(0xFF00CC00);  // alpha.r.g.b
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(3.0F);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setDither(false);
            paint.setTextSize(dm.density * Tab_Camera.cam_text_size);

            textPaint.setAntiAlias(true);
            textPaint.setTextSize(dm.density * Tab_Camera.cam_text_size);
            textPaint.setColor(0xFF00CC00);// blue
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setTextSize(float size) {
        try
        {
            paint.setTextSize(dm.density * size);
            textPaint.setTextSize(dm.density * size);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void updatePath(float w, float h) {
        try {
            cx = w / 2.0f;
            cy = h / 2.0f;
            float r1 = Math.min(w,h)/2.0f;
            float r2 = r1 - 5.0f;
            path.reset();

            // draw circle
            path.addCircle(cx, cy, r1, Path.Direction.CCW);
            path.addCircle(cx, cy, r2, Path.Direction.CW);

            // draw cross
            path.moveTo(cx, 0);
            path.lineTo(cx, h);
            path.moveTo(cx-r1, cy);
            path.lineTo(cx+r1, cy);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    static String strPrevText = "";
    static public String get_status(boolean update) {
//        if(!update)  return strPrevText;

        String strGPS = "";
        if(mv_LocationOverlay.curr_location != null) {
            strGPS = "GPS:" + String.format(Locale.ENGLISH, "%.06f", mv_LocationOverlay.curr_location.getLatitude()) + "," +
                    String.format(Locale.ENGLISH, "%.06f", mv_LocationOverlay.curr_location.getLongitude()) + "," +
                    String.format(Locale.ENGLISH, "%d", Math.round(mv_LocationOverlay.curr_location.getAltitude())) + "," +
                    String.format(Locale.ENGLISH, "%.02f", mv_LocationOverlay.curr_location.getBearing()) + "," +
                    String.format(Locale.ENGLISH, "%.02f", mv_LocationOverlay.curr_location.getSpeed());
        }

        String strMapStatus = "";
        if((Tab_Camera.sw_broadcast_map_status != null) && Tab_Camera.sw_broadcast_map_status.isChecked()) {
            strMapStatus = "MAP:" + String.format(Locale.ENGLISH, "%.06f", MainActivity.map_lat) + "," +
                    String.format(Locale.ENGLISH, "%.06f", MainActivity.map_lon) + "," +
                    String.format(Locale.ENGLISH, "%.02f", MainActivity.map_zoom) + "," +
                    String.format(Locale.ENGLISH, "%.02f", MainActivity.map_rot);
        }

//        String strUAV_GEO = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,0,true);
//        String strUAV_DMS = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,1,true);
//        String strUAV_UTM = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,2,true);
//        String strUAV_STM = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,3,true);
        String strUAV = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,Tab_Map.map_coordinate_index,true,true);
//        String strUAV = Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,0,true,false);// bug 2024.06.11: for sending correctly

//        String strTarget_GEO = Tab_Map.convert_coordinates(MainActivity.target_lon,MainActivity.target_lat,0,true);
//        String strTarget_DMS = Tab_Map.convert_coordinates(MainActivity.target_lon,MainActivity.target_lat,1,true);
//        String strTarget_UTM = Tab_Map.convert_coordinates(MainActivity.target_lon,MainActivity.target_lat,2,true);
//        String strTarget_STM = Tab_Map.convert_coordinates(MainActivity.target_lon,MainActivity.target_lat,3,true);
        String strTarget = Tab_Map.convert_coordinates(MainActivity.target_lon,MainActivity.target_lat,Tab_Map.map_coordinate_index,true,true);

        float[] res = MainActivity.CalculateAngles(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, MainActivity.target_lon, MainActivity.target_lat, MainActivity.target_alt);
        double azi_milliems = MainActivity.db_deg(Math.toDegrees(res[0]))*6000.0/360.0;
//                double ele_milliems = Math.toDegrees((double)res[1])*6000.0/360.0;

        float [] list = new float[2];
        Location.distanceBetween(MainActivity.uav_lat, MainActivity.uav_lon, MainActivity.target_lat, MainActivity.target_lon, list);
        float dist = Math.round(list[0]);
        float bearing = (float)MainActivity.db_deg(list[1]);

//                String strUAV_Lon_DMS = MainActivity.CoordinatesToDMSText(MainActivity.uav_lon);
//                String strUAV_Lat_DMS = MainActivity.CoordinatesToDMSText(MainActivity.uav_lat);
//
//                String strTarget_Lon_DMS = MainActivity.CoordinatesToDMSText(MainActivity.target_lon);
//                String strTarget_Lat_DMS = MainActivity.CoordinatesToDMSText(MainActivity.target_lat);

        String strStart = "  ";
        String strEnd = "  \n";
//        String strText = "\n\n\n";
//        String strText = "\n\n";
        String strText = "";
        if(!strGPS.equals(""))  strText += strStart + strGPS + strEnd;
        if(!strMapStatus.equals(""))  strText += strStart + strMapStatus + strEnd;
//                strText += strStart + getResources().getString(R.string.mapviewer) + ":" + strEnd;
//        if(!MApplication.isRealDevice()) strText += strStart + "Emulator" + strEnd;
        if (MainActivity.IsDemoVersionJNI())
            strText += strStart + "MapViewer [Demo Version!]" + strEnd;
        else
            strText += strStart + "MapViewer [Registered Version]" + strEnd;
        strText += strStart + "[UAV]--------------------------------" + strEnd;
//                strText += strStart + getResources().getString(R.string.gps_lon) + ": " + String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lon) + " [" + strUAV_Lon_DMS + "]" + strEnd;
//                strText += strStart + getResources().getString(R.string.gps_lat) + ": " + String.format(Locale.ENGLISH, "%.06f", MainActivity.uav_lat) + " [" + strUAV_Lat_DMS + "]" + strEnd;

//        strText += strStart + strUAV_GEO + strEnd;
//        strText += strStart + strUAV_DMS + strEnd;
//        strText += strStart + strUAV_UTM + strEnd;
//        strText += strStart + strUAV_STM + strEnd;
        strText += strStart + strUAV + strEnd;

        strText += strStart + "Altitude: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_alt) + strEnd;
        strText += strStart + "Altitude above ground: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_alt_above_ground) + strEnd;
        strText += strStart + "Ground altitude: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_ground_alt) + strEnd;

        strText += strStart + "[Target]--------------------------------" + strEnd;
//                strText += strStart + getResources().getString(R.string.target_lon) + ": " + String.format(Locale.ENGLISH, "%.06f", MainActivity.target_lon) + " [" + strTarget_Lon_DMS + "]" + strEnd;
//                strText += strStart + getResources().getString(R.string.target_lat) + ": " + String.format(Locale.ENGLISH, "%.06f", MainActivity.target_lat) + " [" + strTarget_Lat_DMS + "]" + strEnd;

//        strText += strStart + strTarget_GEO + strEnd;
//        strText += strStart + strTarget_DMS + strEnd;
//        strText += strStart + strTarget_UTM + strEnd;
//        strText += strStart + strTarget_STM + strEnd;
        strText += strStart + strTarget + strEnd;

        strText += strStart + "Target Alt: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.target_alt) + strEnd;
        strText += strStart + "Target Distance: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.laser_dist) + strEnd;
        strText += strStart + "Target Azimuth [milliem]: " + String.format(Locale.ENGLISH, "%.01f", azi_milliems) + strEnd;
        strText += strStart + "Distance: " + String.format(Locale.ENGLISH, "%.01f", dist) + strEnd;
        strText += strStart + "Bearing: " + String.format(Locale.ENGLISH, "%.01f", bearing)  + (char)0x00B0 + strEnd;
//                strText += strStart + "Elevation" + ": " + String.format(Locale.ENGLISH, "%.01f", ele_milliems) + strEnd;
//                strText += strStart + getResources().getString(R.string.place_name) + ": " + MainActivity.strName + strEnd;

        strText += strStart + "[Camera]--------------------------------" + strEnd;
        strText += strStart + "Yaw: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.image_yaw) + strEnd;
        strText += strStart + "Pitch: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.image_pitch) + strEnd;
        strText += strStart + "Roll: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.image_roll) + strEnd;
        strText += strStart + "UAV Yaw: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_yaw) + strEnd;
        strText += strStart + "UAV Pitch: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_pitch) + strEnd;
        strText += strStart + "UAV Roll: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.uav_roll) + strEnd;
//        strText += strStart + "Gimbal Yaw: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.gimb_yaw) + strEnd;
//        strText += strStart + "Gimbal Pitch: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.gimb_pitch) + strEnd;
//        strText += strStart + "Gimbal Roll: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.gimb_roll) + strEnd;

        strText += strStart + "dYaw: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.dYaw) + strEnd;
        strText += strStart + "dPitch: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.dPitch) + strEnd;
        strText += strStart + "dRoll: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.dRoll) + strEnd;

        strText += strStart + "Width: " + String.format(Locale.ENGLISH, "%d", MainActivity.w) + strEnd;
        strText += strStart + "Height: " + String.format(Locale.ENGLISH, "%d", MainActivity.h) + strEnd;
        strText += strStart + "FOV_h: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.fov_h) + strEnd;
        strText += strStart + "FOV_v: " + String.format(Locale.ENGLISH, "%.01f", MainActivity.fov_v) + strEnd;
//                strText += strStart + getResources().getString(R.string.lastYaw) + ": " + String.format(Locale.ENGLISH, "%.02f", MainActivity.lastYaw) + strEnd;
//                strText += strStart + getResources().getString(R.string.lastPitch) + ": " + String.format(Locale.ENGLISH, "%.02f", MainActivity.lastPitch) + strEnd;
        strText += strStart + "idx: " + String.format(Locale.ENGLISH, "%.01f", (float)Tab_Camera.idx) + strEnd;
        strPrevText = strText;
        return strText;
    }

    public void my_draw(Canvas canvas) {
        try {
            layoutWidth = canvas.getWidth();
            layoutHeight = canvas.getHeight();

//            updatePath(canvas.getWidth(), canvas.getHeight());

            canvas.drawPath(path, paint);
            if(camera_status)
            {
                String strText = get_status(true);
//                int width = canvas.getWidth();

                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
//                Configuration config = getResources().getConfiguration();
//                if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
//                    //in Right To Left layout
//                    alignment = Layout.Alignment.ALIGN_OPPOSITE;
//                }

//                Rect bounds = new Rect();
//                textPaint.getTextBounds(strText, 0, strText.length(), bounds);
//                width = bounds.width();
//                int layout_width = 500;
                Rect rec = MainActivity.getTextBackgroundSize(0, 0, " LON:"+Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,Tab_Map.map_coordinate_index,true,true)+" ", textPaint);
//                int layout_width = 500;
                int layout_width = rec.width();
                StaticLayout staticLayout = new StaticLayout(strText, textPaint, layout_width, alignment, 1.0f, 0, false);

                Paint mTextBackground = new Paint();
                mTextBackground.setColor(0x80000000);
                mTextBackground.setStyle(Paint.Style.FILL);

                Rect rectangle = new Rect();
                rectangle.set(0,0,staticLayout.getWidth(),staticLayout.getHeight());
//                textPaint.getTextBounds(strText, 0, strText.length(), rectangle);
                canvas.drawRect(rectangle, mTextBackground);

                staticLayout.draw(canvas);
//                staticLayout.draw(canvas,null,mTextBackground,0);
            }
        }
        catch(Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        try
        {
            if(!isInEditMode())
            {
                my_draw(canvas);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try
        {
            // We purposely disregard child measurements because act as a
            // wrapper to a SurfaceView that centers the camera preview instead
            // of stretching it.
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        try
        {
            super.onSizeChanged(w, h, oldw, oldh);
            updatePath(w, h);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setColor(int color) {
        try
        {
            paint.setColor(color);
//            invalidate();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        boolean isTouchInCircle = checkTouchInCircle(event.getX(), event.getY());
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (isTouchInCircle) {
//                    circleColor = highlightColor;
//                    invalidate();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isTouchInCircle) {
//                    circleColor = highlightColor;
//                } else {
//                    circleColor = normalColor
//                }
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                if (isTouchInCircle) {
//                    onClickCircle();
//                }
//                break;
//        }
//        return true;// register it has been handled
        return false;// register it has not been handled
    }

    // Because we call this from onTouchEvent, this code will be executed for both
    // normal touch events and for when the system calls this using Accessibility
//    @Override
//    public boolean performClick() {
//        super.performClick();
//
////        launchMissile();
//
//        return true;// register it has been handled
//    }

    // Circle click zone approximated as a square
//    private boolean checkTouchInCircle(float touchX, float touchY) {
//        if (touchX < circleCenterX + circleRadius
//                && touchX > circleCenterX - circleRadius
//                && touchY < circleCenterY + circleRadius
//                && touchY > circleCenterY - circleRadius) {
//            return true;
//        } else {
//            return false;
//        }
//    }
}
