package com.yh.imagebanner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yh.imagebanner.R;
import com.yh.imagebanner.Utils;

import java.util.List;

/**
 * Created by YH on 2017/3/14.
 */

public class ImageBannerFrameLayout extends FrameLayout implements ImageBannerViewGroup.ImageBannerViewGroupListener,ImageBannerViewGroup.ImageBannerListener {
    private ImageBannerViewGroup imageBannerViewgroup;
    private LinearLayout linearLayout;

    public ImageBannerFrameLayout(@NonNull Context context) {
        super(context);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }

    public ImageBannerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }

    public ImageBannerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initImageBannerViewGroup();
        initDotLinearLayout();
    }


    public void addBitmaps(List<Bitmap> list) {
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            addBitmapToImageBannerViewGroup(bitmap);
            addDotToLinearlayout();
        }
    }

    private void addDotToLinearlayout() {
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        iv.setLayoutParams(lp);
        iv.setImageResource(R.drawable.dot_nomal);
        linearLayout.addView(iv);
    }

    private void addBitmapToImageBannerViewGroup(Bitmap bitmap) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(Utils.width, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageBitmap(bitmap);
        imageBannerViewgroup.addView(imageView);
    }

    /**
     * 初始化自定义图片轮播功能核心类
     */
    private void initImageBannerViewGroup() {
        imageBannerViewgroup = new ImageBannerViewGroup(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageBannerViewgroup.setLayoutParams(lp);
        imageBannerViewgroup.setListenr(this);
        imageBannerViewgroup.setDotListener(this);
        addView(imageBannerViewgroup);
    }

    /**
     * 初始化圆点布局
     */
    private void initDotLinearLayout() {
        linearLayout = new LinearLayout(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setGravity(Gravity.CENTER);

        linearLayout.setBackgroundColor(Color.RED);
        addView(linearLayout);

        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        linearLayout.setLayoutParams(layoutParams);

        //3.0以后可以直接使用setAlpha,3.0之前的调用者不同，这里需注意
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            linearLayout.setAlpha(0.5f);
        } else {
            linearLayout.getBackground().setAlpha(100);
        }
    }

    @Override
    public void selectIamge(int pos) {
        int count = linearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView) linearLayout.getChildAt(i);
            if (i == pos) {
                iv.setImageResource(R.drawable.dot_black);
            } else {
                iv.setImageResource(R.drawable.dot_nomal);
            }
        }
    }

    @Override
    public void clickImageIndex(int pos) {
        Toast.makeText(getContext(), "点击了第张" + pos + "图", Toast.LENGTH_SHORT).show();
    }
}
