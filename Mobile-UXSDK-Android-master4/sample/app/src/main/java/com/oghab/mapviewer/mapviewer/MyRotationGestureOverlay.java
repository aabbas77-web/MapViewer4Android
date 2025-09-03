package com.oghab.mapviewer.mapviewer;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

public class MyRotationGestureOverlay extends RotationGestureOverlay {
    protected OnRotateListener mOnRotateListener = null;
    private MapView mMapView;

    public MyRotationGestureOverlay(MapView mapView) {
        super(mapView);
        mMapView = mapView;
    }

    public interface OnRotateListener {
        abstract void onRotate(float angle);
    }

    long timeLastSet = 0L;
    final long deltaTime = 25L;
    float currentAngle = 0f;

    @Override
    public void onRotate(float deltaAngle) {
//        super.onRotate(deltaAngle);
//        if(mOnRotateListener != null){
//            mOnRotateListener.onRotate(mMapView.getMapOrientation());
//        }
        currentAngle += deltaAngle;
        if (System.currentTimeMillis() - deltaTime > timeLastSet) {
            timeLastSet = System.currentTimeMillis();
            mMapView.setMapOrientation(mMapView.getMapOrientation() + currentAngle);
            if(mOnRotateListener != null){
                mOnRotateListener.onRotate(mMapView.getMapOrientation());
            }
        }
    }

    public void setOnRotateListener(OnRotateListener listener) {
        mOnRotateListener = listener;
    }
}
