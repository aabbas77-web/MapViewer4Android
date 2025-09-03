package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.RectL;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.Locale;

//import android.support.annotation.NonNull;
//import android.support.annotation.NonNull;
//import android.support.annotation.NonNull;

//import androidx.annotation.NonNull;

//import androidx.annotation.NonNull;

public class Cross_Overlay extends Overlay {
    private Paint paint;
    private Paint pathPaint;
    private Paint bgPaint;
    private TextPaint textPaint;
    private Paint messagePaint;
    protected Point mTarget_Px,mGPS_Px,mGun_Px;
    DisplayMetrics dm;
    String strText = "";
    String strInfo = "";
    String strMessage = "";
    boolean bUpdated = false;
    // Constructor
    private Path path;
    protected Bitmap cross;
//    protected Bitmap compass;
    private boolean mShowPath = false;

    float Xmin,Xmax;
    float Ymin,Ymax;
    RectF frame = new RectF();

    public Cross_Overlay(Context context) {
        super();
        try
        {
            path = new Path();

            // Get the display metrics
            Resources resources = context.getResources();
            dm = resources.getDisplayMetrics();

            mTarget_Px = new Point(0,0);
            mGPS_Px = new Point(0,0);
            mGun_Px = new Point(0,0);

            // Get paint
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(dm.density * Tab_Map.map_text_size);
    //        paint.setColor(Color.RED);
//            paint.setColor(0x80FFFFFF);
            paint.setColor(0x80000000);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(dm.density * Tab_Map.map_text_size);
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(Typeface.MONOSPACE);

    //        textPaint.setStyle(Paint.Style.STROKE);
    //        textPaint.setStrokeWidth(1);

            messagePaint = new Paint();
            messagePaint.setAntiAlias(true);
            messagePaint.setTextSize(dm.density * Tab_Map.map_text_size);
            messagePaint.setColor(0x80FF0000);
            messagePaint.setStyle(Paint.Style.FILL_AND_STROKE);

            bgPaint = new TextPaint();
            bgPaint.setAntiAlias(true);
            bgPaint.setColor(0x80000000);
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setStrokeWidth(3);
            bgPaint.setTextSize(dm.density * Tab_Map.map_text_size);

            pathPaint = new TextPaint();
            pathPaint.setAntiAlias(true);
            pathPaint.setColor(Color.YELLOW);
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setStrokeWidth(3);
            pathPaint.setTextSize(dm.density * Tab_Map.map_text_size);

            cross = BitmapFactory.decodeResource(context.getResources(),R.drawable.crosshair3);
            cross = MainActivity.addColor(cross,Color.RED);

//            compass = BitmapFactory.decodeResource(context.getResources(),R.drawable.compass03);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void updatePath(float[] Ts,float[] Hs,float X1,float X2,float Y1,float Y2) {
        try {
            frame.left = X1;
            frame.top = Y1;
            frame.right = X2;
            frame.bottom = Y2;

            float x,Ax,Bx;
            float y,Ay,By;
            path.reset();

            Xmin = +1000000;
            Xmax = -1000000;
            Ymin = +1000000;
            Ymax = -1000000;
            bUpdated = false;
            for(int i=0;i<Hs.length;i++) {
                x = Ts[i];
                y = Hs[i];
                if(x < Xmin) {
                    Xmin = x;
                    bUpdated = true;
                }
                if(x > Xmax) {
                    Xmax = x;
                    bUpdated = true;
                }
                if(y < Ymin) {
                    Ymin = y;
                    bUpdated = true;
                }
                if(y > Ymax) {
                    Ymax = y;
                    bUpdated = true;
                }
            }

            Ax = (X2-X1)/(Xmax-Xmin);
            Bx = X1 - Ax*Xmin;

            Ay = (Y2-Y1)/(Ymax-Ymin);
            By = Y1 - Ay*Ymin;

            path.addRect(frame,Path.Direction.CW);
//            path.addRect(X1,Y1,X2,Y2,Path.Direction.CW);

            float X,Y;
            for(int i=0;i<Hs.length;i++) {
                X = Ax*Ts[i]+Bx;
                Y = Ay*Hs[i]+By;
                if(i == 0)
                    path.moveTo(X, Y);
                else
                    path.lineTo(X, Y);
            }

        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setText(String text) {
        try
        {
            strText = text;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setInfo(String text) {
        try
        {
            strInfo = text;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setMessage(String text) {
        try
        {
            strMessage = text;
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setShowPath(boolean bShowPath) {
        try {
            mShowPath = bShowPath;
            if(!bShowPath)  path.reset();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void setTextColor(int color) {
        try
        {
            textPaint.setColor(color);
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
            pathPaint.setTextSize(dm.density * size);
            messagePaint.setTextSize(dm.density * size);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        draw(canvas, map, shadow, map.getProjection());
//        try
//        {
//            if (!isEnabled())   return;
//            if (shadow) return;
//            final Projection pj = map.getProjection();
//
//            // Draw compass
////            pj.save(canvas, false, false);
////            canvas.rotate(pj.getOrientation(),compass.getWidth()/2.0f, compass.getHeight()/2.0f);
////            canvas.drawBitmap(compass,0,0,null);
////            pj.restore(canvas, false);
//
////            pj.save(canvas, false, false);
////            char cDeg = (char)0x00B0;
////            int heading = (360 + Math.round(pj.getOrientation())) % 360;
////            canvas.drawText(String.format(Locale.ENGLISH, "%d", heading)+cDeg,0,compass.getHeight()/2.0f,textPaint);
////            pj.restore(canvas, false);
//
////            Matrix matrix = new Matrix();
////            matrix.setRotate(-pj.getOrientation(), compass.getWidth()/2.0f, compass.getHeight()/2.0f);
////            canvas.drawBitmap(compass, matrix, null);
//
//            int width = canvas.getWidth();
//            int height = canvas.getHeight();
//
//            float x;
//            float y;
//
//            x = width/2.0f;
//            y = height/2.0f;
//
//            // Draw the text
//            pj.save(canvas, false, false);
//
//            //        canvas.drawText("\u2A01", x, y, textPaint);// unicode cross character
//            //        updatePath(canvas.getWidth(), canvas.getHeight(), 30);
//            if(mShowPath){
//                canvas.drawPath(path, pathPaint);
//
//                String text;
//                text = String.format(Locale.ENGLISH, "%d", (int)Ymin);
//                canvas.drawText(text, frame.right, frame.top, textPaint);
//                text = String.format(Locale.ENGLISH, "%d", (int)Ymax);
//                canvas.drawText(text, frame.right, frame.bottom + textPaint.getTextSize(), textPaint);
//            }
//
//            canvas.drawBitmap(cross,x-cross.getWidth()/2.0f,y-cross.getHeight()/2.0f,pathPaint);
//
//            if(!strText.isEmpty()) {
//                y = height;
//                Rect background = MainActivity.getTextBackgroundSize(0, y, strText, textPaint);
//                canvas.drawRect(background, paint);
////            canvas.drawText(strText, 0, y-textPaint.getTextSize(), textPaint);
////            canvas.drawText(strText, 0, y + (background.height()-textPaint.getTextSize())/2.0f, textPaint);
//                canvas.drawText(strText, 0, y - textPaint.getTextSize()/2.0f + dm.density*2.0f, textPaint);
//            }
//
////            background = getTextBackgroundSize(0, 0, strInfo, textPaint);
////            canvas.drawRect(background, paint);
////            canvas.drawText(strInfo, 10, 30, textPaint);
//
//            // Info
//            if((!strInfo.isEmpty()) && (Tab_Map.map_status)) {
//                Rect rec = MainActivity.getTextBackgroundSize(0, 0, " LON:"+MainActivity.CoordinateToDMS(-99.99)+" N ", textPaint);
////                int layout_width = 500;
//                int layout_width = rec.width();
//                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
//                StaticLayout staticLayout = new StaticLayout(strInfo, textPaint, layout_width, alignment, 1.0f, 0, false);
//
//                Paint mTextBackground = new Paint();
//                mTextBackground.setColor(0x80000000);
//                mTextBackground.setStyle(Paint.Style.FILL);
//
//                Rect rectangle = new Rect();
//                rectangle.set(0, 0, staticLayout.getWidth(), staticLayout.getHeight());
//                canvas.save();
//                canvas.translate(canvas.getWidth() - staticLayout.getWidth(), 0);
////                canvas.rotate(-pj.getOrientation());
//                canvas.drawRect(rectangle, mTextBackground);
//
//                staticLayout.draw(canvas);
//                canvas.restore();
//            }
//
//            if(!strMessage.isEmpty()) {
//                y = 3*height/4.0f;
//                Rect background = MainActivity.getTextBackgroundSize(0, y, strMessage, textPaint);
//                canvas.drawRect(background, messagePaint);
//                canvas.drawText(strMessage, 0, y - textPaint.getTextSize()/2.0f + dm.density*2.0f, textPaint);
//            }
//
//            pj.restore(canvas, false);
//        }
//        catch (Throwable ex)
//        {
//            MainActivity.MyLog(ex);
//        }
    }

    public void draw(Canvas canvas, MapView map, boolean shadow, Projection projection) {
        try
        {
            if (!isEnabled())   return;
            if (shadow) return;
            final Projection pj = projection;

            // Draw compass
//            pj.save(canvas, false, false);
//            canvas.rotate(pj.getOrientation(),compass.getWidth()/2.0f, compass.getHeight()/2.0f);
//            canvas.drawBitmap(compass,0,0,null);
//            pj.restore(canvas, false);

//            pj.save(canvas, false, false);
//            char cDeg = (char)0x00B0;
//            int heading = (360 + Math.round(pj.getOrientation())) % 360;
//            canvas.drawText(String.format(Locale.ENGLISH, "%d", heading)+cDeg,0,compass.getHeight()/2.0f,textPaint);
//            pj.restore(canvas, false);

//            Matrix matrix = new Matrix();
//            matrix.setRotate(-pj.getOrientation(), compass.getWidth()/2.0f, compass.getHeight()/2.0f);
//            canvas.drawBitmap(compass, matrix, null);

            int width = canvas.getWidth();
            int height = canvas.getHeight();

            float x;
            float y;

            x = width/2.0f;
            y = height/2.0f;

            // Draw the text
            pj.save(canvas, false, false);

            //        canvas.drawText("\u2A01", x, y, textPaint);// unicode cross character
            //        updatePath(canvas.getWidth(), canvas.getHeight(), 30);
            if(mShowPath){
                canvas.drawRect(frame, bgPaint);
                canvas.drawPath(path, pathPaint);

                if(bUpdated) {
                    String text;
                    text = String.format(Locale.ENGLISH, "%d", (int) Ymin);
                    Rect background1 = MainActivity.getTextBackgroundSize(frame.right, frame.top, text, textPaint);
                    canvas.drawRect(background1, paint);
                    canvas.drawText(text, frame.right, frame.top - textPaint.getTextSize() / 2.0f + dm.density * 2.0f, textPaint);

                    text = String.format(Locale.ENGLISH, "%d", (int) Ymax);
                    Rect background2 = MainActivity.getTextBackgroundSize(frame.right, frame.bottom - background1.height(), text, textPaint);
                    canvas.drawRect(background2, paint);
                    canvas.drawText(text, frame.right, frame.bottom - background2.height() - textPaint.getTextSize() / 2.0f + dm.density * 2.0f, textPaint);
                }
            }

            canvas.drawBitmap(cross,x-cross.getWidth()/2.0f,y-cross.getHeight()/2.0f,pathPaint);

            if(!strText.isEmpty()) {
                y = height;
                Rect background = MainActivity.getTextBackgroundSize(0, y, strText, textPaint);
                canvas.drawRect(background, paint);
//            canvas.drawText(strText, 0, y-textPaint.getTextSize(), textPaint);
//            canvas.drawText(strText, 0, y + (background.height()-textPaint.getTextSize())/2.0f, textPaint);
                canvas.drawText(strText, 0, y - textPaint.getTextSize()/2.0f + dm.density*2.0f, textPaint);
            }

//            background = getTextBackgroundSize(0, 0, strInfo, textPaint);
//            canvas.drawRect(background, paint);
//            canvas.drawText(strInfo, 10, 30, textPaint);

            // Info
            if((!strInfo.isEmpty()) && (Tab_Map.map_status)) {
                Rect rec = MainActivity.getTextBackgroundSize(0, 0, " LON:"+MainActivity.CoordinateToDMS(-99.99)+" N ", textPaint);
//                int layout_width = 500;
                int layout_width = rec.width();
                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
                StaticLayout staticLayout = new StaticLayout(strInfo, textPaint, layout_width, alignment, 1.0f, 0, false);

                Paint mTextBackground = new Paint();
                mTextBackground.setColor(0x80000000);
                mTextBackground.setStyle(Paint.Style.FILL);

                Rect rectangle = new Rect();
                rectangle.set(0, 0, staticLayout.getWidth(), staticLayout.getHeight());
                canvas.save();
                canvas.translate(canvas.getWidth() - staticLayout.getWidth(), 0);
//                canvas.rotate(-pj.getOrientation());
                canvas.drawRect(rectangle, mTextBackground);

                staticLayout.draw(canvas);
                canvas.restore();
            }

            if(!strMessage.isEmpty()) {
                y = 3*height/4.0f;
                Rect background = MainActivity.getTextBackgroundSize(0, y, strMessage, textPaint);
                canvas.drawRect(background, messagePaint);
                canvas.drawText(strMessage, 0, y - textPaint.getTextSize()/2.0f + dm.density*2.0f, textPaint);
            }

            pj.restore(canvas, false);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }
}
