package com.oghab.mapviewer.utils;

import static android.view.PixelCopy.request;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.*;

import androidx.annotation.RequiresApi;

//import javax.inject.Inject;
//import com.example.dagger.ControllerScope;

@TargetApi(24)
//@ControllerScope
public class ViewScreenshot {

    public interface PostTake {

        void onSuccess(Bitmap bitmap);
        void onFailure(int error);
    }

//    @Inject
    public ViewScreenshot() {

    }

    public Bitmap getBitmapFromView(View view, int w, int h) {
        view.measure(w, h);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredWidth());
        view.layout(0, 0, w, h);
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredWidth(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLUE);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Bitmap bitmap = bmp.copy(bmp.getConfig(), true);
        view.setDrawingCacheEnabled(false);
        return bitmap;

//        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredWidth());
//        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredWidth(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
    }

//    static public Bitmap getBitmapFromView(View view)
//    {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
//    }

//    static public Bitmap getBitmapFromView(View view, int w, int h)
//    {
//        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.draw(canvas);
//        return bitmap;
//    }

    static public Bitmap getBitmapFromView(View view,int defaultColor)
    {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(defaultColor);
        view.draw(canvas);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void capturePicture(Activity activity) {
        Window window = activity.getWindow();
        View view = window.getDecorView();
//    private void capturePicture(SurfaceView surfaceView) {
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        request(window, bmp, i -> {
//            imageView.setImageBitmap(bmp); //"iv_Result" is the image view
        }, new Handler(Looper.getMainLooper()));
    }

    public void DrawBitmap(SurfaceView surfaceView, PostTake callback){
        if (callback == null) {
            throw new IllegalArgumentException("Screenshot request without a callback");
        }

        Bitmap surfaceBitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.OnPixelCopyFinishedListener listener = new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    callback.onSuccess(surfaceBitmap);
                } else {
                    callback.onFailure(copyResult);
                }
            }
        };
        try {
            PixelCopy.request(surfaceView, surfaceBitmap,listener,new Handler());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(26)
    public void take(View view, Activity activity, PostTake callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Screenshot request without a callback");
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        int[] location = new int[2];
        view.getLocationInWindow(location);

        Rect rect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
        PixelCopy.OnPixelCopyFinishedListener listener = new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    callback.onSuccess(bitmap);
                } else {
                    callback.onFailure(copyResult);
                }
            }
        };

        try {
            PixelCopy.request(activity.getWindow(), rect, bitmap, listener, new Handler());
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        }
    }
}
