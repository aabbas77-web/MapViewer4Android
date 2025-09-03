package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * @author Ali Abbas
 */

public class FOV_Overlay extends Overlay {

	protected GeoPoint mCameraPos,mTargetPos,mHomePos;
	protected Point mCameraPx, mTargetPx, mHomePx, P0x, P1x, P2x, P3x;
	protected float mFOV;
	protected float mAzi;
	protected float mYaw;
	protected float mRadius;
	private Paint p_red,p_green,p_blue,p_yellow;
	protected Bitmap uav;

	MapView curr_mapView;

	public FOV_Overlay(Context context) {
		super();
		try
		{
            mCameraPos = new GeoPoint(0.0,0.0,0.0);
            mTargetPos = new GeoPoint(0.0,0.0,0.0);
            mHomePos = new GeoPoint(0.0,0.0,0.0);

			mCameraPx = new Point(0,0);
			mTargetPx = new Point(0,0);
			mHomePx = new Point(0,0);
			P0x = new Point(0,0);
			P1x = new Point(0,0);
			P2x = new Point(0,0);
			P3x = new Point(0,0);

			mFOV = 60.0f;
			mAzi = 0.0f;
			mYaw = 0.0f;
			mRadius = 3000.0f;

			Path watching_circle = new Path();
			Path mission_circle = new Path();
			Path pathDashLine = new Path();

			p_red = new Paint();
			p_red.setColor(0xFFCC0000);  // alpha.r.g.b
			p_red.setStyle(Paint.Style.FILL_AND_STROKE);
			p_red.setStrokeWidth(2.0F);
			p_red.setAntiAlias(true);
			p_red.setStrokeCap(Paint.Cap.BUTT);
			p_red.setDither(false);

			p_green = new Paint();
			p_green.setColor(0xFF00CC00);  // alpha.r.g.b
			p_green.setStyle(Paint.Style.FILL_AND_STROKE);
			p_green.setStrokeWidth(2.0F);
			p_green.setAntiAlias(true);
			p_green.setStrokeCap(Paint.Cap.BUTT);
			p_green.setDither(false);

            p_blue = new Paint();
            p_blue.setColor(0xFF0000CC);  // alpha.r.g.b
            p_blue.setStyle(Paint.Style.FILL_AND_STROKE);
            p_blue.setStrokeWidth(2.0F);
            p_blue.setAntiAlias(true);
            p_blue.setStrokeCap(Paint.Cap.BUTT);
            p_blue.setDither(false);

            p_yellow = new Paint();
            p_yellow.setColor(Color.YELLOW);  // alpha.r.g.b
            p_yellow.setARGB(255,255,255,0);
            p_yellow.setStyle(Paint.Style.STROKE);
//            p_yellow.setPathEffect(new DashPathEffect(new float[]{10f,40f}, 0));// bug: very slow
            p_yellow.setStrokeWidth(3.0F);
            p_yellow.setAntiAlias(true);
            p_yellow.setStrokeCap(Paint.Cap.BUTT);
            p_yellow.setDither(false);

//			if(MainActivity.bNavigation) {
//				uav = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction64);
////            cross = changeBitmapColor(cross,Color.GREEN);
////            cross = replaceColor(cross,Color.BLACK,Color.GREEN);
//				uav = MainActivity.addColor(uav,Color.GREEN);
//			}
//			else
				uav = BitmapFactory.decodeResource(context.getResources(), R.drawable.uav_icon2);

			update_calculations();
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

	public Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
		if(src == null) {
			return null;
		}
		// Source image size
		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = new int[width * height];
		//get pixels
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		for(int x = 0; x < pixels.length; ++x) {
//            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
			pixels[x] = (Math.abs(pixels[x] - fromColor) <= 16) ? targetColor : pixels[x];
		}
		// create result bitmap output
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
		//set pixels
		result.setPixels(pixels, 0, width, 0, 0, width, height);

		return result;
	}

	public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
	{
		Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
		Paint paint = new Paint();
		ColorFilter filter = new LightingColorFilter(color, 1);
		paint.setColorFilter(filter);
		Canvas canvas = new Canvas(resultBitmap);
		canvas.drawBitmap(resultBitmap, 0, 0, paint);
		return resultBitmap;
	}

	private void updatePath(Path path,float xc,float yc, float r, float d) {
		try
		{
			float r2 = r - d;
			path.reset();
			// draw circle
			path.addCircle(xc, yc, r, Path.Direction.CCW);
			path.addCircle(xc, yc, r2, Path.Direction.CW);
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

	public float getFOV(){
		return mFOV;
	}

	public void setFOV(float fov){
		mFOV = fov;
	}

	public float getAzi(){
		return mAzi;
	}

	public void setAzi(float azi){
		mAzi = azi;
	}

	public float getYaw(){
		return mYaw;
	}

	public void setYaw(float yaw){
		mYaw = yaw;
	}

	public float getRadius(){
		return mRadius;
	}

	public void setRadius(float r){
		mRadius = r;
	}

	public GeoPoint getCameraPos(){
		return mCameraPos.clone();
	}

	public void setCameraPos(GeoPoint position){
		mCameraPos = position.clone();
	}

    public GeoPoint getTargetPos(){
        return mTargetPos.clone();
    }

    public void setTargetPos(GeoPoint position){
        mTargetPos = position.clone();
    }

    public GeoPoint getHomePos(){
        return mHomePos.clone();
    }

    public void setHomePos(GeoPoint position){
        mHomePos = position.clone();
    }

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		draw(canvas, mapView, shadow, mapView.getProjection());

//		try
//		{
//			if (shadow)	return;
//			if (!isEnabled())   return;
////			if(mapView.isInLayout())	return;
////			if(mapView.isInEditMode())	return;
////			if(mapView.isInTouchMode())	return;
//
//			final Projection pj = mapView.getProjection();
//
//			GeoPoint P0 = mCameraPos.destinationPoint(mRadius, mAzi);
//			GeoPoint P1 = mCameraPos.destinationPoint(mRadius, mAzi - mFOV/2.0f);
//			GeoPoint P2 = mCameraPos.destinationPoint(mRadius, mAzi + mFOV/2.0f);
//			GeoPoint P3 = mCameraPos.destinationPoint(mMissionRadius, mAzi);
//
//			pj.toPixels(mCameraPos, mCameraPx);
//			pj.toPixels(mTargetPos, mTargetPx);
//			pj.toPixels(mHomePos, mHomePx);
//
//			pj.toPixels(P0, P0x);
//			pj.toPixels(P1, P1x);
//			pj.toPixels(P2, P2x);
//			pj.toPixels(P3, P3x);
//
//			// draw uav very slow
////			Matrix matrix = new Matrix();
////			matrix.reset();
////			matrix.postTranslate(-uav.getWidth() / 2.0f, -uav.getHeight() / 2.0f);
////			matrix.postRotate(mYaw);
////			matrix.postTranslate(mCameraPx.x, mCameraPx.y);
////			canvas.drawBitmap(uav, matrix, null);
//
//			// draw circle
//			// Bug 2018.11.19 (OSMDroid crashes when zooming)
////			double r = Math.sqrt((P0x.x - mCameraPx.x)*(P0x.x - mCameraPx.x)+(P0x.y - mCameraPx.y)*(P0x.y - mCameraPx.y));
////			updatePath(watching_circle,mCameraPx.x, mCameraPx.y, (float)r, 1.0f);
////			canvas.drawPath(watching_circle, p_red);
////
////			double R = Math.sqrt((P3x.x - mCameraPx.x)*(P3x.x - mCameraPx.x)+(P3x.y - mCameraPx.y)*(P3x.y - mCameraPx.y));
////			updatePath(mission_circle,mCameraPx.x, mCameraPx.y, (float)R, 1.0f);
////			canvas.drawPath(mission_circle, p_green);
//
//			canvas.drawLine(mCameraPx.x, mCameraPx.y, mTargetPx.x, mTargetPx.y, p_red);
//			canvas.drawLine(mCameraPx.x, mCameraPx.y, P0x.x, P0x.y, p_green);
//			canvas.drawLine(mCameraPx.x, mCameraPx.y, P1x.x, P1x.y, p_blue);
//			canvas.drawLine(mCameraPx.x, mCameraPx.y, P2x.x, P2x.y, p_blue);
//			canvas.drawLine(mCameraPx.x, mCameraPx.y, mHomePx.x, mHomePx.y, p_yellow);
////            pathDashLine.reset();
////            pathDashLine.moveTo(mCameraPx.x, mCameraPx.y);
////            pathDashLine.lineTo(mHomePx.x, mHomePx.y);
////            canvas.drawPath(pathDashLine, p_yellow);
//		}
//		catch (Throwable ex)
//		{
//			MainActivity.MyLog(ex);
//		}
	}

	public void update_calculations() {
		if(curr_mapView == null)	return;
		if(mCameraPos == null)	return;
		try
		{
			GeoPoint P0 = mCameraPos.destinationPoint(mRadius, mAzi);
			GeoPoint P1 = mCameraPos.destinationPoint(mRadius, mAzi - mFOV/2.0f);
			GeoPoint P2 = mCameraPos.destinationPoint(mRadius, mAzi + mFOV/2.0f);

			Projection curr_projection = curr_mapView.getProjection();
			if(curr_projection != null){
				curr_projection.toPixels(mCameraPos, mCameraPx);
				curr_projection.toPixels(mTargetPos, mTargetPx);
				curr_projection.toPixels(mHomePos, mHomePx);

				curr_projection.toPixels(P0, P0x);
				curr_projection.toPixels(P1, P1x);
				curr_projection.toPixels(P2, P2x);
			}
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow, Projection projection) {
		try
		{
			if (shadow)	return;
			if (!isEnabled())   return;
//			if(mapView.isInLayout())	return;
//			if(mapView.isInEditMode())	return;
//			if(mapView.isInTouchMode())	return;
			curr_mapView = mapView;
			update_calculations();

			// draw uav very slow
//			Matrix matrix = new Matrix();
//			matrix.reset();
//			matrix.postTranslate(-uav.getWidth() / 2.0f, -uav.getHeight() / 2.0f);
//			matrix.postRotate(mYaw);
//			matrix.postTranslate(mCameraPx.x, mCameraPx.y);
//			canvas.drawBitmap(uav, matrix, null);

			// draw circle
			// Bug 2018.11.19 (OSMDroid crashes when zooming)
//			double r = Math.sqrt((P0x.x - mCameraPx.x)*(P0x.x - mCameraPx.x)+(P0x.y - mCameraPx.y)*(P0x.y - mCameraPx.y));
//			updatePath(watching_circle,mCameraPx.x, mCameraPx.y, (float)r, 1.0f);
//			canvas.drawPath(watching_circle, p_red);
//
//			double R = Math.sqrt((P3x.x - mCameraPx.x)*(P3x.x - mCameraPx.x)+(P3x.y - mCameraPx.y)*(P3x.y - mCameraPx.y));
//			updatePath(mission_circle,mCameraPx.x, mCameraPx.y, (float)R, 1.0f);
//			canvas.drawPath(mission_circle, p_green);

			canvas.drawLine(mCameraPx.x, mCameraPx.y, mTargetPx.x, mTargetPx.y, p_red);
			canvas.drawLine(mCameraPx.x, mCameraPx.y, P0x.x, P0x.y, p_green);
			canvas.drawLine(mCameraPx.x, mCameraPx.y, P1x.x, P1x.y, p_blue);
			canvas.drawLine(mCameraPx.x, mCameraPx.y, P2x.x, P2x.y, p_blue);
			canvas.drawLine(mCameraPx.x, mCameraPx.y, mHomePx.x, mHomePx.y, p_yellow);

//            pathDashLine.reset();
//            pathDashLine.moveTo(mCameraPx.x, mCameraPx.y);
//            pathDashLine.lineTo(mHomePx.x, mHomePx.y);
//            canvas.drawPath(pathDashLine, p_yellow);
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}

}
