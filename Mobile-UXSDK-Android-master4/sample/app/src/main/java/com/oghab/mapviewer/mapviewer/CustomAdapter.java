package com.oghab.mapviewer.mapviewer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import java.io.File;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<File> files;
    LayoutInflater inflter;
    private int selectedPosition = -1;
    GridView simpleGridView = null;

    public CustomAdapter(Context applicationContext, List<File> files, GridView simpleGridView) {
        this.context = applicationContext;
        this.files = files;
        this.simpleGridView = simpleGridView;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public void setSelectedPosition(int position)
    {
        selectedPosition = position;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        return files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.icons_gridview, null); // inflate the layout
        ImageView icon = view.findViewById(R.id.icon); // get the reference of ImageView
        Bitmap bmp = BitmapFactory.decodeFile(files.get(i).getAbsolutePath());
        icon.setImageBitmap(bmp); // set logo images

        if(i == selectedPosition){
            icon.setBackgroundColor(Color.GREEN);
            icon.setBackgroundResource(R.drawable.customborder2);
//            MainActivity.iv_icon.setImageBitmap(bmp);
//            MainActivity.iv_icon.invalidate();
        }else{
            icon.setBackgroundResource(R.drawable.customborder);
        }

        return view;
    }
}
