package com.oghab.mapviewer.mapviewer;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class IPsAdapter extends RecyclerView.Adapter<IPsAdapter.ViewHolder> {
    static public int selectedPos = RecyclerView.NO_POSITION;
    public ViewHolder holder = null;
    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private final int listItemLayout;
    public final ArrayList<tcp_user> users_list;
    // Constructor of the class
    public IPsAdapter(int layoutId, ArrayList<tcp_user> users0) {
        listItemLayout = layoutId;
        this.users_list = users0;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return users_list == null ? 0 : users_list.size();
    }

    // specify the row layout file and click for each row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ViewHolder holder = null;
        try{
            View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
            holder = new ViewHolder(view);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return Objects.requireNonNull(holder);
    }
    // load data in each row element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int listPosition) {
        try{
            tcp_user user = users_list.get(listPosition);
            String ip = user.getIP();
            holder.itemView.setSelected(selectedPos == listPosition);

            if(selectedPos == listPosition) {
                holder.itemView.setFocusable(true);
                holder.itemView.setFocusableInTouchMode(true);
                holder.itemView.requestFocus();
            }

            holder.tv_ip.setText(ip);
            holder.tv_name.setText(user.getName());
            holder.avatar.setImageURI(Uri.fromFile(new File(user.getAvatarPath())));
            if(Tab_Messenger.is_ip_connected(ip)) {
                holder.avatar.setStrokeColorResource(R.color.green);
                holder.tv_user_connected.setBackgroundResource(R.drawable.circle_green);
            }
            else
            {
                holder.avatar.setStrokeColorResource(R.color.red);
                holder.tv_user_connected.setBackgroundResource(R.drawable.circle_red);
            }
            holder.tv_messages_count.setText(String.valueOf(Tab_Messenger.get_ip_messages_count(ip)));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout ll_view;
        public TextView tv_ip;
        public TextView tv_name;
        public TextView tv_messages_count;
        public ShapeableImageView avatar;
        public ImageView tv_user_connected;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            ll_view = itemView.findViewById(R.id.ll_view);
            ll_view.setOnClickListener(this);

            tv_ip = itemView.findViewById(R.id.tv_ip);
            tv_ip.setOnClickListener(this);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_name.setOnClickListener(this);

            avatar = itemView.findViewById(R.id.avatar);
            avatar.setOnClickListener(this);

            tv_messages_count = itemView.findViewById(R.id.tv_messages_count);
            tv_messages_count.setOnClickListener(this);

            tv_user_connected = itemView.findViewById(R.id.tv_user_connected);
            tv_user_connected.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try{
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);

                Tab_Messenger.connect_with_waiting(tv_ip.getText().toString());
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }
}
