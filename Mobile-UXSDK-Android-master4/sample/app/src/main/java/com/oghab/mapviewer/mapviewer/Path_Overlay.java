package com.oghab.mapviewer.mapviewer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.oghab.mapviewer.MainActivity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.OverlayWithIW;

import java.util.List;

/**
 * @author Ali Abbas
 */

public class Path_Overlay extends OverlayWithIW {

    private Paint paint;
    private List<GeoPoint> points = null;
    private Point p = null;
    private boolean bUpdate = false;
    private float[] pts = null;

    public Path_Overlay() {
        super();
        try
        {
            p = new Point();

            paint = new Paint();
            paint.setColor(0xFFCC0000);  // alpha.r.g.b
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2.0F);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setDither(false);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public void update_points(List<GeoPoint> points) {
        try
        {
            if(points.size() > 0) {
                this.points = points.subList(0, points.size() - 1);
                bUpdate = true;
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    @Override public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        try
        {
            if (shadow)	return;

            if(bUpdate) {
                bUpdate = false;
                final Projection pj = mapView.getProjection();
                pts = new float[2 * points.size()];
                for (int i = 0; i < points.size(); i++) {
                    pj.toPixels(points.get(i), p);
                    pts[i * 2    ] = p.x;
                    pts[i * 2 + 1] = p.y;
                }
            }

            canvas.drawLines(pts,paint);
            canvas.drawPoints(pts,paint);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

}
