package com.yyh.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyh.inc.update.R;
import com.yyh.lib.adapter.AppsAdapter;
import com.yyh.lib.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author yuyh.
 * @date 2016/7/14.
 */
public class ApplistActivity extends Activity implements AdapterView.OnItemClickListener {

    private GridView lvApps;
    private ArrayList<ResolveInfo> mApps;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_list);
        pm = getPackageManager();
        lvApps = (GridView) findViewById(R.id.lvApps);
        initApp();
    }


    /**
     * 获取app列表
     */
    private void initApp() {
        // 获取android设备的应用列表
        Intent intent = new Intent(Intent.ACTION_MAIN); // 动作匹配
        intent.addCategory(Intent.CATEGORY_LAUNCHER); // 类别匹配
        mApps = (ArrayList<ResolveInfo>) pm.queryIntentActivities(intent, 0);
        // 排序
        Collections.sort(mApps, new Comparator<ResolveInfo>() {

            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                // 排序规则
                PackageManager pm = getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm)
                        .toString(), b.loadLabel(pm).toString()); // 忽略大小写
            }
        });
        lvApps.setAdapter(new AppsAdapter(this, mApps));
        lvApps.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ResolveInfo app = mApps.get(position);
        String appDir = null;
        try {
            // 指定包名的程序源文件路径
            appDir = getPackageManager().getApplicationInfo(app.activityInfo.packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView tv = new TextView(this);
        tv.setText(appDir);
        final String finalAppDir = appDir;
        new AlertDialog.Builder(this)
                .setTitle("APK路径")
                .setView(tv)
                .setPositiveButton("拷贝到SD卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(finalAppDir);
                        new CopyTask().execute(finalAppDir,
                                Environment.getExternalStorageDirectory().toString() + "/" + finalAppDir.substring(finalAppDir.lastIndexOf("/") + 1));
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private class CopyTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            for (int i = 0; i < params.length; i += 2) {
                try {
                    File src = new File(params[i]);
                    File dest = new File(params[i]);
                    if (!dest.exists())
                        FileUtils.createFile(dest);

                    InputStream is;
                    OutputStream os = new FileOutputStream(params[i+1]);
                    is = new FileInputStream(src);
                    byte[] buffer = new byte[1024];
                    int length = is.read(buffer);
                    while (length > 0) {
                        os.write(buffer, 0, length);
                        length = is.read(buffer);
                    }
                    os.flush();
                    is.close();
                    os.close();
                } catch (Exception e) {
                    Log.e("TAG", e.toString());
                    handler.obtainMessage(1).sendToTarget();
                    return null;
                }
            }
            handler.obtainMessage(0).sendToTarget();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "copy successed", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "copy failured", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
