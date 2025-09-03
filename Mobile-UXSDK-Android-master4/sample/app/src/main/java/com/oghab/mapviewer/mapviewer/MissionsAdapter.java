package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by MapViewer on 09/04/2017.
 */

public class MissionsAdapter extends ArrayAdapter<String> {
    static public String strMissionName = "/";

    public MissionsAdapter(Context context, ArrayList<String> paths) {
        super(context, 0, paths);
    }

    public String get_files_count_text(String strPath)
    {
        int count = 0;
        File fileList[] = new File(strPath).listFiles();
        if(fileList != null)    count = fileList.length;
        return Integer.toString(count);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try
        {
            // Get the data item for this position
            String strText = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_mission_row, parent, false);
            }

            final View view = convertView;

            View.OnClickListener onClickListener = view1 -> {
                try
                {
                    // Access user from within the tag
                    strMissionName = (String) view1.getTag();
                    Tab_Map.strMissionPath = Tab_Map.strMissionDir + strMissionName;
                    MainActivity.tab_map.load_mission();

                    MainActivity.set_fullscreen();
                    // Show Alert
                    Tab_Map.missionsDialog.dismiss();
                }
                catch (Throwable ex)
                {
                    MainActivity.MyLog(ex);
                }
            };

            // Lookup view for data population
            TextView tv_path = convertView.findViewById(R.id.tv_mission_path);
            tv_path.setTag(strText);
            tv_path.setOnClickListener(onClickListener);

            // Populate the data into the template view using the data object
            tv_path.setText(strText);

            convertView.setTag(strText);
            convertView.setOnClickListener(onClickListener);
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        // Return the completed view to render on screen
        assert convertView != null;
        return convertView;
    }
}