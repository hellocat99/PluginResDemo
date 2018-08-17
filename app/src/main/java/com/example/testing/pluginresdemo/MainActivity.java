package com.example.testing.pluginresdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private String pluginApkPath;
    private String pluginApkName;

    private ImageView imageView;
    private TextView textView;

    private boolean isPluginRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        needPermison();

        pluginApkName = "extra.apk";
        pluginApkPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        imageView = findViewById(R.id.img_icon);
        textView = findViewById(R.id.tv_button);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPluginRes) {
                    Drawable drawable = findPluginDrawable();
                    imageView.setImageDrawable(drawable);
                    isPluginRes = true;
                } else {
                    imageView.setImageResource(R.drawable.a);
                    isPluginRes = false;
                }
            }
        });
    }

    private void needPermison() {
        int i = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (i != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private Drawable findPluginDrawable() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(
                pluginApkPath + File.separator + pluginApkName,
                PackageManager.GET_ACTIVITIES);
        if (packageArchiveInfo != null) {
            String packageName = packageArchiveInfo.applicationInfo.packageName;
            Resources pluginResources = getPluginResources();
            if (pluginResources != null) {
                return pluginResources.getDrawable(pluginResources.getIdentifier("b", "drawable",
                        packageName));
            }
        }
        return null;
    }

    private Resources getPluginResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, pluginApkPath + File.separator + pluginApkName);
            Resources resources = getResources();
            Resources pluginResources = new Resources(assetManager, resources.getDisplayMetrics()
                    , resources.getConfiguration());
            return pluginResources;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

}
