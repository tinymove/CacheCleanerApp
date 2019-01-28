package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.location.SettingInjectorService;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

public class RecyclerVievAdapted extends RecyclerView.Adapter<RecyclerVievAdapted.ViewHolder> {
    private static final String TAG = "RecyclerVievAdapted";

    private List<AppStruct> list;
    private Context mContext;
    private PackageManager pm;
    private Method getPackageSizeInfo;
    long x;

    public RecyclerVievAdapted(List<AppStruct> list, Context mContext) {
        Log.d(TAG, "RecyclerVievAdapted: Constructor");
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row, parent, false);

        pm = mContext.getPackageManager();
        try {
            getPackageSizeInfo = pm.getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        final AppStruct item = list.get(position);
        long temp = item.cacheSize;
        String s;
        if (temp > 1024) {
            temp = temp / 1024;
            if (temp > 1024) {
                temp = temp / 1024;
                s = String.valueOf(temp) + " MB";
            } else
                s = String.valueOf(temp) + " KB";
        } else
            s = String.valueOf(temp) + " B";

        holder.row_author.setText(s);
        holder.row_title.setText(item.info.loadLabel(pm));
        holder.row_image.setImageDrawable(item.info.loadIcon(pm));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Number of clicked row: " + position, Toast.LENGTH_SHORT).show();
                String packageName = item.info.activityInfo.packageName;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView row_image;
        public TextView row_title;
        public TextView row_author;
        RelativeLayout relativeLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: Contructor called");

//            itemView.setOnClickListener(this);

            row_image = itemView.findViewById(R.id.row_image);
            row_title = itemView.findViewById(R.id.row_title);
            row_author = itemView.findViewById(R.id.row_author);
            relativeLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}