package com.oghab.mapviewer.mapviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.oghab.mapviewer.R;

import java.util.ArrayList;

public class StringsAdapter extends ArrayAdapter<String> {
    private Context context;
    private StringsAdapter stringsAdapter;

    public StringsAdapter(Context context, ArrayList<String> list) {
        super(context, 0, list);
        this.context = context;
        stringsAdapter = this;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.string_layout, null);
        }

        TextView tv_string = view.findViewById(R.id.tv_string);
        tv_string.setText(stringsAdapter.getItem(position));

        Button b_delete = view.findViewById(R.id.b_delete);
        b_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stringsAdapter.remove(item);
            }
        });

        return view;
    }
}
