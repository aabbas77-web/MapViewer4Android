package com.oghab.mapviewer.mapviewer;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import com.oghab.mapviewer.MainActivity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
////import org.osmdroid.views.overlay.MyMarker;

//public class mv_Marker extends MyMarker {
public class mv_Marker extends MyMarker {
    private TextPaint textPaint;
//    String strInfo = " MapViewer\n Speed: 10 m/s\n Direction: 45 deg";
    String strInfo1 = "";
    String strInfo2 = "";
    DisplayMetrics dm;

    public mv_Marker(MapView mapView) {
        super(mapView);

        // Get the display metrics
        Resources resources = MainActivity.ctx.getResources();
        dm = resources.getDisplayMetrics();

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(dm.density * 24);
        textPaint.setColor(Color.GREEN);
        //        textPaint.setStyle(Paint.Style.STROKE);
        //        textPaint.setStrokeWidth(1);
    }

    public void setTextSize(int fontSize) {
        try
        {
            textPaint.setTextSize(dm.density * fontSize);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setInfo1(String text) {
        try
        {
            strInfo1 = text;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public String getInfo1() {
        return strInfo1;
    }

    public void setInfo2(String text) {
        try
        {
            strInfo2 = text;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public String getInfo2() {
        return strInfo2;
    }

    @Override public void draw(Canvas canvas, Projection pj) {
        super.draw( canvas, pj);
        if (mIcon == null)
            return;
        if (!isEnabled())
            return;
//        canvas.save();
//        pj.save(canvas, false, false);

        pj.toPixels(mPosition, mPositionPixels);

        // Info
        if(pj.getZoomLevel() >= MainActivity.dMarkersZoomLevel) {
            String strInfo = strInfo1;
            if(strInfo2.length() > 0) {
                strInfo += "\n" + strInfo2;
            }
            if ((strInfo1.length() > 0) || (strInfo2.length() > 0)) {
                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
                int layout_width = 300;
                StaticLayout staticLayout = new StaticLayout(strInfo, textPaint, layout_width, alignment, 1.0f, 0, false);

                Paint mTextBackground = new Paint();
                mTextBackground.setColor(0x80000000);
                mTextBackground.setStyle(Paint.Style.FILL);

//                int dx = 150;
//                int dy = 210;
                int dx = staticLayout.getWidth()/2;
                int dy = staticLayout.getHeight();
                Rect rectangle = new Rect();
//                rectangle.set(mPositionPixels.x - dx, mPositionPixels.y, mPositionPixels.x + dx, mPositionPixels.y + dy);
//                rectangle.set( -dx, 0, dx, dy);
                rectangle.set( 0, 0, 2*dx, dy);
//                Rect rectangle = MainActivity.getTextBackgroundSize(mPositionPixels.x, mPositionPixels.y, strInfo, textPaint);
//                pj.save(canvas, false, false);
                canvas.save();
//                canvas.translate(mPositionPixels.x - dx, mPositionPixels.y);
                canvas.translate(mPositionPixels.x, mPositionPixels.y);
                canvas.rotate(-pj.getOrientation() - mBearing);
                canvas.drawRect(rectangle, mTextBackground);
                staticLayout.draw(canvas);
                canvas.restore();
//                pj.restore(canvas, false);
            }
        }

        // draw marker
//        float rotationOnScreen = (mFlat ? -mBearing : -pj.getOrientation()-mBearing);
//        drawAt(canvas, mPositionPixels.x, mPositionPixels.y, rotationOnScreen);
//
//        if (isInfoWindowShown()) {
//            //showInfoWindow();
//            mInfoWindow.draw();
//        }

//        canvas.restore();
//        pj.restore(canvas, false);
    }
}
