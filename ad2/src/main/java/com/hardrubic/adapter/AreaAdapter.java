package com.hardrubic.adapter;


import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.hardrubic.entity.Area;
import java.util.List;

public class AreaAdapter extends ArrayAdapter<Area> {
    private Context mContext;
    private int resourceId;
    private List<Area> mAreaList;

    public AreaAdapter(Context context, int resourceId, List<Area> objects) {
        super(context, resourceId, objects);
        this.mContext = context;
        this.resourceId = resourceId;
        this.mAreaList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Area area = mAreaList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(resourceId, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_area_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(area.getName());
        return convertView;
    }

    class ViewHolder {
        TextView textView;
    }
}
