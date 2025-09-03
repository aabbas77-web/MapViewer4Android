package com.oghab.mapviewer.mapviewer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.bonuspack.kml.KmlPlacemark;
import com.oghab.mapviewer.utils.mv_utils;

import org.osmdroid.util.GeoPoint;

import org.osmdroid.views.overlay.PolyOverlayWithIW;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by MapViewer on 09/04/2017.
 */

public class CitiesAdapter extends ArrayAdapter<City> implements AdapterView.OnItemSelectedListener{
    Context context;
    CitiesAdapter cities_adapter;
    int selected_position = -1;
    View mainView = null;
    public CitiesAdapter(Context context, ArrayList<City> cities) {
        super(context, 0, cities);
        this.context = context;
        cities_adapter = this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            // Get the data item for this position
            City city = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.simplerow, parent, false);
            }
            mainView = convertView;

            // Lookup view for data population
            LinearLayout ll_layout = convertView.findViewById(R.id.ll_layout);
            HorizontalScrollView hsv_layout = convertView.findViewById(R.id.hsv_layout);
            TextView tv_cityname = convertView.findViewById(R.id.tv_cityname);
            TextView tv_coordinates = convertView.findViewById(R.id.tv_coordinates);
            Button lv_goto = convertView.findViewById(R.id.lv_goto);
            Button lv_lookat = convertView.findViewById(R.id.lv_lookat);
            Button lv_panorama = convertView.findViewById(R.id.lv_panorama);
            Button lv_follow_path = convertView.findViewById(R.id.lv_follow_path);
            Button lv_stitch_path = convertView.findViewById(R.id.lv_stitch_path);
            Button lv_fake_gps = convertView.findViewById(R.id.lv_fake_gps);
            ImageView lv_delete = convertView.findViewById(R.id.lv_delete);
            TextView tv_info = convertView.findViewById(R.id.tv_info);
            tv_info.setText("");

            String strName;
            assert city != null;
            strName = "["+city.strName+"] ";
            city.fAlt = MainActivity.GetHeightJNI(city.fLon, city.fLat);

            // Populate the data into the template view using the data object
            tv_cityname.setText(strName);
            if(MainActivity.isDevelpoment()) {
                String strLon, strLat, strAlt, strCoordinates;
                tv_coordinates.setVisibility(View.VISIBLE);
                strLon = String.format(Locale.ENGLISH,"%.06f", city.fLon);
                strLat = String.format(Locale.ENGLISH,"%.06f", city.fLat);
                strAlt = String.format(Locale.ENGLISH,"%.01f", city.fAlt);
                strCoordinates = "("+strLon+", "+strLat+", "+strAlt+")";
                tv_coordinates.setText(strCoordinates);
            }else{
                tv_coordinates.setVisibility(View.GONE);
            }

            hsv_layout.setTag(city);
            hsv_layout.setOnClickListener(view -> {
                try
                {
                    // Access user from within the tag
                    City city1 = (City) view.getTag();

                    GeoPoint p = new GeoPoint(city1.fLat, city1.fLon, city1.fAlt);
                    MainActivity.tab_map.mapController.setZoom(17.0);
                    MainActivity.tab_map.mapController.setCenter(p);
                    Tab_Map.map.postInvalidate();

                    if(city1.placemark.overlay instanceof MyMarker) {
                        ((MyMarker) city1.placemark.overlay).showInfoWindow();
                    }else if(city1.placemark.overlay instanceof PolyOverlayWithIW) {
                        ((PolyOverlayWithIW) city1.placemark.overlay).showInfoWindow();
                    }

                    MainActivity.tab_map.hide_favorites();
                    MainActivity.tab_map.hide_search();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            ll_layout.setTag(city);
            ll_layout.setOnClickListener(view -> {
                try
                {
                    // Access user from within the tag
                    City city1 = (City) view.getTag();

//                    MainActivity.tab_map.set_poi_pos(city1.fLon, city1.fLat, city1.fAlt, city1.strName,true);

                    GeoPoint p = new GeoPoint(city1.fLat, city1.fLon, city1.fAlt);
                    MainActivity.tab_map.mapController.setZoom(17.0);
                    MainActivity.tab_map.mapController.setCenter(p);
                    Tab_Map.map.postInvalidate();

                    if((city1.placemark != null) && (city1.placemark.overlay != null)){
                        if(city1.placemark.overlay instanceof MyMarker) {
                            ((MyMarker) city1.placemark.overlay).showInfoWindow();
                        }else if(city1.placemark.overlay instanceof PolyOverlayWithIW) {
                            ((PolyOverlayWithIW) city1.placemark.overlay).showInfoWindow();
                        }
                    }

                    MainActivity.tab_map.hide_favorites();
                    MainActivity.tab_map.hide_search();

//                    view.setBackgroundColor(Color.GREEN);

//                    if(city1.geometry_type == City.POINT) {
//                        if(city1.overlay instanceof MyMarker) {
//                            ((MyMarker) city1.overlay).showInfoWindow();
//                        }
//                    }
//                    else
//                    if(city1.geometry_type == City.POLYLINE) {
//                        if(city1.overlay instanceof PolyOverlayWithIW) {
//                            ((PolyOverlayWithIW) city1.overlay).showInfoWindow();
//                        }
//                    }
//                    else
//                    if(city1.geometry_type == City.POLYGON) {
//                        if(city1.overlay instanceof PolyOverlayWithIW) {
//                            ((PolyOverlayWithIW) city1.overlay).showInfoWindow();
//                        }
//                    }

                    // Show Alert
//                    Toast.makeText(MainActivity.ctx, city1.strName , Toast.LENGTH_SHORT).show();
//                    Tab_Messenger.showToast(city1.strName);
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            });

            if(MainActivity.isDevelpoment()) {
                lv_delete.setVisibility(View.VISIBLE);
                lv_delete.setTag(city);
                lv_delete.setOnClickListener(view -> {
                    try {
                        City city20 = (City) view.getTag();
                        new AlertDialog.Builder(MainActivity.activity)
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.delete)
                                .setMessage(R.string.are_you_sure_delete)
                                .setPositiveButton(R.string.yes_message, (dialog, which) -> {
//                                MainActivity.tab_map.delete_city0(city20.index);
                                    MainActivity.tab_map.delete_city(city20);
//                                cities_adapter.remove(city20);
//                                Toast.makeText(MainActivity.ctx, "["+city20.strName +"] Deleted...", Toast.LENGTH_SHORT).show();
                                    Tab_Messenger.showToast("[" + city20.strName + "] Deleted...");
                                    MainActivity.hide_keyboard(null);
                                })
                                .setNegativeButton(R.string.no_message, (dialog, which) -> {
                                    MainActivity.hide_keyboard(null);
                                })
                                .show();
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                });
            } else {
                lv_delete.setVisibility(View.GONE);
            }

            if(city.geometry_type == City.POINT) {// Point
                convertView.setBackgroundColor(0x6FFF4F4F);

//                if(Tab_Map.cb_marks.isChecked()){
//                    convertView.setVisibility(View.VISIBLE);
//                }else{
//                    convertView.setVisibility(View.GONE);
//                }

                if (MainActivity.bNavigation) {
                    if(MainActivity.isDevelpoment()) {
                        lv_fake_gps.setVisibility(View.VISIBLE);
                        lv_fake_gps.setTag(city);
                        lv_fake_gps.setOnClickListener(view -> {
                            try {
                                // Access user from within the tag
                                City city16 = (City) view.getTag();
                                simulateFakeGPS(city16.index);
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    } else {
                        lv_fake_gps.setVisibility(View.GONE);
                    }
                } else {
                    lv_fake_gps.setVisibility(View.GONE);
                }

                if(MainActivity.isDevelpoment()) {
                    lv_goto.setVisibility(View.VISIBLE);
                    lv_goto.setTag(city);
                    lv_goto.setOnClickListener(view -> {
                        try {
                            if (MainActivity.tab_map.timeline == null) return;
                            String strText = MainActivity.tab_map.e_altitude.getText().toString();
                            float altitude = Float.parseFloat(strText);

                            // Access user from within the tag
                            City city12 = (City) view.getTag();

                            MainActivity.tab_map.timeline.GotoByTimeline(city12.fLon, city12.fLat, altitude);
//                        Toast.makeText(MainActivity.ctx, "Goto here...", Toast.LENGTH_SHORT).show();
                            Tab_Messenger.showToast("Goto here...");
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }else{
                    lv_goto.setVisibility(View.GONE);
                }

                if(MainActivity.isDevelpoment()) {
                    lv_lookat.setVisibility(View.VISIBLE);
                    lv_lookat.setTag(city);
                    lv_lookat.setOnClickListener(view -> {
                        try {
                            // Access user from within the tag
                            City city13 = (City) view.getTag();

//                            MainActivity.target_lon = city13.fLon;
//                            MainActivity.target_lat = city13.fLat;
//                            MainActivity.target_alt = city13.fAlt;
                            MainActivity.tab_map.set_target_pos(city13.fLon, city13.fLat, city13.fAlt, true);
                            MainActivity.tab_map.mapController.setCenter(MainActivity.tab_map.targetPoint);
                            MainActivity.tab_map.mapController.setZoom(17.0);
                            MainActivity.tab_camera.crosshairView.invalidate();

                            float[] res = MainActivity.CalculateAngles(MainActivity.uav_lon, MainActivity.uav_lat, MainActivity.uav_alt, MainActivity.target_lon, MainActivity.target_lat, MainActivity.target_alt);
                            MainActivity.lastYaw = (float) MainActivity.db_deg(Math.toDegrees(res[0]));
                            if (MainActivity.lastYaw >= 180.0) MainActivity.lastYaw -= 360.0f;
                            MainActivity.lastPitch = (float) Math.toDegrees(res[1]);
                            MainActivity.tab_map.timeline.ChangeUAV_YawByTimeline(MainActivity.lastYaw, MainActivity.lastPitch);

                            MainActivity.save_settings();
//                        Toast.makeText(MainActivity.ctx, "Look at...", Toast.LENGTH_SHORT).show();
                            Tab_Messenger.showToast("Look at...");
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }else{
                    lv_lookat.setVisibility(View.GONE);
                }

                if(MainActivity.isDevelpoment()) {
                    lv_panorama.setVisibility(View.VISIBLE);
                    lv_panorama.setTag(city);
                    lv_panorama.setOnClickListener(view -> {
                        try {
                            if (MainActivity.tab_map.timeline == null) return;
                            String strText = MainActivity.tab_map.e_altitude.getText().toString();
                            float altitude = Float.parseFloat(strText);

                            strText = MainActivity.tab_map.e_mission_speed.getText().toString();
                            float speed = Float.parseFloat(strText);

                            // Access user from within the tag
                            City city14 = (City) view.getTag();

                            MainActivity.tab_map.timeline.PanoramaByTimeline(city14.fLon, city14.fLat, altitude, speed);
//                        Toast.makeText(MainActivity.ctx, "Panorama...", Toast.LENGTH_SHORT).show();
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }else{
                    lv_panorama.setVisibility(View.GONE);
                }

                lv_follow_path.setVisibility(View.GONE);
                lv_stitch_path.setVisibility(View.GONE);
            }
            else if(city.geometry_type == City.POLYGON){// Polygon
                convertView.setBackgroundColor(0x6F4587C2);
                lv_goto.setVisibility(View.GONE);
                lv_lookat.setVisibility(View.GONE);
                lv_panorama.setVisibility(View.GONE);

                if(MainActivity.bNavigation){
                    lv_follow_path.setVisibility(View.GONE);
                    lv_stitch_path.setVisibility(View.GONE);
                    if(MainActivity.isDevelpoment()) {
                        lv_fake_gps.setVisibility(View.VISIBLE);
                        lv_fake_gps.setTag(city);
                        lv_fake_gps.setOnClickListener(view -> {
                            try {
                                // Access user from within the tag
                                City city16 = (City) view.getTag();
                                simulateFakeGPS(city16.index);
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    } else {
                        lv_fake_gps.setVisibility(View.GONE);
                    }
                }else{
                    lv_fake_gps.setVisibility(View.GONE);
                    if(MainActivity.isDevelpoment()) {
                        lv_follow_path.setVisibility(View.VISIBLE);
                        lv_follow_path.setTag(city);
                        lv_follow_path.setOnClickListener(view -> {
                            try {
                                // Access user from within the tag
                                City city15 = (City) view.getTag();

                                String strText;
                                strText = MainActivity.tab_map.e_altitude.getText().toString();
                                float altitude = Float.parseFloat(strText);
                                int n = 99;
                                MainActivity.tab_map.timeline.initFavoritesTimeline(n, altitude, city15.index);

//                        Toast.makeText(MainActivity.ctx, "Follow Path...", Toast.LENGTH_SHORT).show();
                                Tab_Messenger.showToast("Follow Path...");
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    }else{
                        lv_follow_path.setVisibility(View.GONE);
                    }

                    if(MainActivity.isDevelpoment())
                        lv_stitch_path.setVisibility(View.VISIBLE);
                    else
                        lv_stitch_path.setVisibility(View.GONE);
                    lv_stitch_path.setTag(city);
                    lv_stitch_path.setOnClickListener(view -> {
                        try {
                            // Access user from within the tag
                            City city16 = (City) view.getTag();
                            show_mission_dialog(city16);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }

                // area
//                double area,length;
//                area = MainActivity.tab_map.path_area(city.index);
//                length = MainActivity.tab_map.path_length(city.index);
////                String strInfo = "Length: "+ String.format(Locale.ENGLISH,"%.03f",length) +" [m], Area: " + String.format(Locale.ENGLISH,"%.03f",area) + " [m²]";
//                String strInfo = "Length: "+ String.format(Locale.ENGLISH,"%.03f",length/1000) +" [km], Area: " + String.format(Locale.ENGLISH,"%.03f", area / 1000000) + " [km²]";
//                tv_info.setText(strInfo);
            }else{// Polyline
                convertView.setBackgroundColor(0x6F4FFF4F);
                lv_goto.setVisibility(View.GONE);
                lv_lookat.setVisibility(View.GONE);
                lv_panorama.setVisibility(View.GONE);

                if(MainActivity.bNavigation){
                    lv_follow_path.setVisibility(View.GONE);
                    lv_stitch_path.setVisibility(View.GONE);
                    if(MainActivity.isDevelpoment()) {
                        lv_fake_gps.setVisibility(View.VISIBLE);
                        lv_fake_gps.setTag(city);
                        lv_fake_gps.setOnClickListener(view -> {
                            try {
                                // Access user from within the tag
                                City city16 = (City) view.getTag();
                                simulateFakeGPS(city16.index);
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    } else {
                        lv_fake_gps.setVisibility(View.GONE);
                    }
                }else{
                    lv_fake_gps.setVisibility(View.GONE);
                    if(MainActivity.isDevelpoment()) {
                        lv_follow_path.setVisibility(View.VISIBLE);
                        lv_follow_path.setTag(city);
                        lv_follow_path.setOnClickListener(view -> {
                            try {
                                // Access user from within the tag
                                City city15 = (City) view.getTag();

                                String strText;
                                strText = MainActivity.tab_map.e_altitude.getText().toString();
                                float altitude = Float.parseFloat(strText);
                                int n = 99;
                                MainActivity.tab_map.timeline.initFavoritesTimeline(n, altitude, city15.index);

//                        Toast.makeText(MainActivity.ctx, "Follow Path...", Toast.LENGTH_SHORT).show();
                                Tab_Messenger.showToast("Follow Path...");
                            } catch (Throwable ex) {
                                MainActivity.MyLog(ex);
                            }
                        });
                    }else{
                        lv_follow_path.setVisibility(View.GONE);
                    }

                    if(MainActivity.isDevelpoment())
                        lv_stitch_path.setVisibility(View.VISIBLE);
                    else
                        lv_stitch_path.setVisibility(View.GONE);
                    lv_stitch_path.setTag(city);
                    lv_stitch_path.setOnClickListener(view -> {
                        try {
                            // Access user from within the tag
                            City city16 = (City) view.getTag();
                            show_mission_dialog(city16);
                        } catch (Throwable ex) {
                            MainActivity.MyLog(ex);
                        }
                    });
                }

                // area
//                double area,length;
//                area = MainActivity.tab_map.path_area(city.index);
//                length = MainActivity.tab_map.path_length(city.index);
////                String strInfo = "Length: "+ String.format(Locale.ENGLISH,"%.03f",length) +" [m], Area: " + String.format(Locale.ENGLISH,"%.03f",area) + " [m²]";
//                String strInfo = "Length: "+ String.format(Locale.ENGLISH,"%.03f",length/1000.0) +" [km], Area: " + String.format(Locale.ENGLISH,"%.03f", area / 1000000) + " [km²]";
//                tv_info.setText(strInfo);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }

        // Return the completed view to render on screen
        assert convertView != null;
        return convertView;
    }

	public static void select_item(int position) {
		try
		{
			float fov_d;
			switch(position)
			{
                case 17:
                {
                    fov_d = 54.0f;
                    break;
                }
                case 16:
                {
                    fov_d = 82.6f;
                    break;
                }
                case 15:
                {
                    fov_d = 60.0f;
                    break;
                }
				case 14:
				{
					fov_d = 27.0f;
					break;
				}
				case 13:
				{
					fov_d = 47.0f;
					break;
				}
				case 12:
				case 5: {
					fov_d = 65.0f;
					break;
				}
				case 11:
				case 1: {
					fov_d = 84.0f;
					break;
				}
				case 10:
				{
					fov_d = 72.0f;
					break;
				}
				case 9:
				{
					fov_d = 81.9f;
					break;
				}
				case 8:
				{
					fov_d = 85.0f;
					break;
				}
				case 7:
				{
					fov_d = 78.8f;
					break;
				}
				case 6:
				{
					fov_d = 48.0f;
					break;
				}
				case 4:
				{
					fov_d = 83.0f;
					break;
				}
				case 3:
				{
					fov_d = 55.0f;
					break;
				}
				case 2:
				{
					fov_d = 77.0f;
					break;
				}
				case 0: default:
                {
                    fov_d = 94.0f;
                    break;
                }
			}
			double w,h,f;
			w = MainActivity.w;
			h = MainActivity.h;
			f = Math.sqrt(w*w+h*h)/(2.0*Math.tan(Math.toRadians(fov_d/2.0)));
			MainActivity.fov_d = fov_d;
			MainActivity.fov_h = (float)(2.0*Math.toDegrees(Math.atan(w/(2.0*f))));
			MainActivity.fov_v = (float)(2.0*Math.toDegrees(Math.atan(h/(2.0*f))));

            Tab_Camera.cameraSettingsIdx = position;
            SharedPreferences settings = MainActivity.ctx.getSharedPreferences("mapviewer_settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("cameraSettingsIdx", Tab_Camera.cameraSettingsIdx);
            editor.apply();

			Tab_Messenger.showToast(MainActivity.fov_h + " selected...");
		}
		catch (Throwable ex)
		{
			MainActivity.MyLog(ex);
		}
	}
	
	public static class MissinDialogFragment extends DialogFragment {
		/** The system calls this only when creating the layout in a dialog. */
		@NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
			// Get the layout inflater
			LayoutInflater inflater = requireActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			View mission_dialog = inflater.inflate(R.layout.mission_settings, null);

        EditText e_mission_altitude,e_path_size,e_percent,e_ele_deg;
        e_mission_altitude = mission_dialog.findViewById(R.id.e_mission_altitude);
        e_path_size = mission_dialog.findViewById(R.id.e_path_size);
        e_percent = mission_dialog.findViewById(R.id.e_percent);
        e_ele_deg = mission_dialog.findViewById(R.id.e_ele_deg);

        Spinner spinner = mission_dialog.findViewById(R.id.fov_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				select_item(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.ctx,
                R.array.fov_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(Tab_Camera.cameraSettingsIdx);

        Button b_ok = mission_dialog.findViewById(R.id.b_ok);
        b_ok.setOnClickListener(v -> {
            try
            {
                try {
                    double fov_deg,alt_above_ground,Xpercent,Ypercent,ele_deg;
                    int w,h,path_size;
                    String strText;
                    strText = e_mission_altitude.getText().toString();
                    alt_above_ground = mv_utils.parseDouble(strText);
                    strText = e_percent.getText().toString();
                    Xpercent = mv_utils.parseDouble(strText);
                    Ypercent = Xpercent;
                    strText = e_ele_deg.getText().toString();
                    ele_deg = mv_utils.parseDouble(strText);
                    strText = e_path_size.getText().toString();
                    path_size = (int)mv_utils.parseDouble(strText);

                    fov_deg = MainActivity.fov_d;
                    w = MainActivity.w;
                    h = MainActivity.h;

                    if(MainActivity.tab_map.stitch_path(s_city.index,fov_deg,w,h,alt_above_ground,Xpercent,Ypercent,ele_deg,path_size))
//                        Toast.makeText(MainActivity.ctx, "Path stitched...", Toast.LENGTH_SHORT).show();
                        Tab_Messenger.showToast("Path stitched...");
                    else
//                        Toast.makeText(MainActivity.ctx, "Path not stitched...!", Toast.LENGTH_SHORT).show();
                        Tab_Messenger.showToast("Path not stitched...!");
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }

                // Hide both the navigation bar and the status bar.
                MainActivity.hide_keyboard(mission_dialog);

                Objects.requireNonNull(MissinDialogFragment.this.getDialog()).dismiss();
            }
            catch (Throwable ex)
            {
                MainActivity.MyLog(ex);
            }
        });

        Button b_cancel = mission_dialog.findViewById(R.id.b_cancel);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide both the navigation bar and the status bar.
                MainActivity.hide_keyboard(mission_dialog);

                Objects.requireNonNull(MissinDialogFragment.this.getDialog()).dismiss();
            }
        });

			builder.setView(mission_dialog);
            return builder.create();
		}
	}

	static City s_city;
    private void show_mission_dialog(City city)
    {
        s_city = city;
        FragmentManager fragmentManager = MainActivity.activity.getSupportFragmentManager();
        MissinDialogFragment newFragment = new MissinDialogFragment();

        // The device is using a large layout, so show the fragment as a dialog
        newFragment.show(fragmentManager, "dialog");
    }

    private int point_i = 0;
    private long simulator_delay;
    ArrayList<GeoPoint> mCoordinates = null;
    public void simulateFakeGPS(int index) {
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

                    KmlPlacemark placemark = (KmlPlacemark)MainActivity.tab_map.kmlFavoritesDocument.mKmlRoot.mItems.get(index);

                    // polyline simplification
//                    mCoordinates = PointReducer.reduceWithTolerance(placemark.mGeometry.mCoordinates,0.000001);
                    mCoordinates = placemark.mGeometry.mCoordinates;

                    if (mCoordinates.size() > 0) {
                        MainActivity.tab_map.b_timeline_simulate.setEnabled(false);
                        Tab_Messenger.showToast("Mission started...");

                        //starting our task which update textview every 1000 ms
                        point_i = 0;
                        bIsSimulating = true;

                        customHandler.postDelayed(updateTimerThread, 100);
                    } else {
                        Tab_Messenger.showToast("Invalid path");
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

    static boolean bIsSimulating = false;
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
                            if (point_i < mCoordinates.size()) {
                                GeoPoint p = mCoordinates.get(point_i);
                                if(MainActivity.tab_map != null){
                                    MainActivity.tab_map.update_gps(p, true, false);
                                }
                                point_i++;
                            } else {
                                point_i = 0;
                                bIsSimulating = false;
                                MainActivity.tab_map.b_timeline_simulate.setEnabled(true);
                                Tab_Messenger.showToast("Mission finished...");
                            }

                            if (bIsSimulating)
                            {
                                customHandler.postDelayed(this, simulator_delay);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        view.setBackgroundColor(Color.GREEN);
        if(mainView != null) {
            mainView.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
