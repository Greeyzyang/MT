package com.example.connectapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.connectapplication.R;

import java.util.List;

public class PopupWindowAdapter extends BaseAdapter {

    private Context context;
    private List<String> filenames;

    public PopupWindowAdapter(Context context, List<String> filenames) {

        this.context= context;
        this.filenames = filenames;

    }

    @Override

    public int getCount() {

        return filenames.size();

    }

    @Override

    public Object getItem(int position) {

        return filenames.get(position);

    }

    @Override

    public long getItemId(int position) {

        return position;

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView ==null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_pop_item, null);
            holder.pop_version = convertView.findViewById(R.id.title_device_version);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pop_version.setText(filenames.get(position));

        return convertView;

    }

    class ViewHolder {
     private TextView pop_version;
    }

}

