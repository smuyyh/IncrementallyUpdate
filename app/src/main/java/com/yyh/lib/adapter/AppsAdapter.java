package com.yyh.lib.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuyh.inc.update.R;

import java.util.ArrayList;

/**
 * @author yuyh.
 * @date 2016/7/14.
 */
public class AppsAdapter extends ArrayAdapter<ResolveInfo> {

    ArrayList<ResolveInfo> mApps;
    LayoutInflater inflater;
    PackageManager pm;

    public AppsAdapter(Context context, ArrayList<ResolveInfo> apps) {
        super(context, 0, apps);
        this.mApps = apps;
        pm = context.getPackageManager();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_apps, null);
            holder = new ViewHolder();
            holder.appImageView = (ImageView) convertView.findViewById(R.id.appImageView);
            holder.appNameTextView = (TextView) convertView.findViewById(R.id.appNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ResolveInfo app = mApps.get(position);
        CharSequence appName = app.loadLabel(pm);
        holder.appNameTextView.setText(appName);
        Drawable appIcon = app.loadIcon(pm);
        holder.appImageView.setImageDrawable(appIcon);
        return convertView;
    }

    private class ViewHolder {
        public ImageView appImageView;
        public TextView appNameTextView;
    }
}
