package com.oghab.mapviewer.mapviewer;

/*
  @author Ali Abbas
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;

import com.oghab.mapviewer.MainActivity;

public class FrameView extends View {
    private Paint paint;
    private final TextPaint textPaint = new TextPaint();
    private Bitmap bitmap = null;
    public String filename = "";
    private String text = null;

    static public int layoutWidth = 0;
    static public int layoutHeight = 0;
    DisplayMetrics dm;

    public FrameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        try
        {
            this.setVisibility(View.VISIBLE);

            // Get the display metrics
            Resources resources = context.getResources();
            dm = resources.getDisplayMetrics();

            paint = new Paint();
            paint.setColor(0xFFFFFFFF);  // alpha.r.g.b
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2.0F);
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

    public String get_filename(){
        return filename;
    }

    public void set_filename(String filename){
        this.filename = filename;
        if(filename == null)
            bitmap = null;
        else
            bitmap = BitmapFactory.decodeFile(filename);
        invalidate();
    }

    public void set_text(String text){
        this.text = text;
        invalidate();
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

    public void set_bitmap(Bitmap bmp){
        bitmap = bmp;
        if(bmp == null){
            filename = null;
        }
        invalidate();
    }

    public void my_draw(Canvas canvas) {
        try {
            layoutWidth = canvas.getWidth();
            layoutHeight = canvas.getHeight();
            if(bitmap != null){
                Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
                Rect dst = new Rect(0,0,layoutWidth,layoutHeight);
                canvas.drawBitmap(bitmap,src,dst,null);
            }
//            else{
//                canvas.drawRect(new Rect(0,0,layoutWidth,layoutHeight), paint);
//            }

            if(text != null){
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
//                int width = 500;
                Rect rec = MainActivity.getTextBackgroundSize(0, 0, " LON:"+Tab_Map.convert_coordinates(MainActivity.uav_lon,MainActivity.uav_lat,Tab_Map.map_coordinate_index,true,true)+" ", textPaint);
//                int layout_width = 500;
                int layout_width = rec.width();
                StaticLayout staticLayout = new StaticLayout(text, textPaint, layout_width, alignment, 1.0f, 0, false);

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
    public void draw(@NonNull Canvas canvas) {
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
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

}
