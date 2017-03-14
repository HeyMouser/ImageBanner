package com.yh.imagebanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.yh.imagebanner.view.ImageBannerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageBannerFrameLayout mGroup;

    private int[] ids = new int[]{R.mipmap.banner, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGroup = (ImageBannerFrameLayout) findViewById(R.id.imageGroup);

        //需要计算出手机的宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Utils.width = dm.widthPixels;

        List<Bitmap> list = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ids[i]);
            list.add(bitmap);
        }
        mGroup.addBitmaps(list);
    }
}
