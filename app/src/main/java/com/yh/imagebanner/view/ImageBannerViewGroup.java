package com.yh.imagebanner.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YH on 2017/3/13.
 */

public class ImageBannerViewGroup extends ViewGroup {

    private int childrenCount;//viewgroup子视图的总个数
    private int childWidth;//子视图宽度
    private int chileHeight;//子视图高度

    private int x;//此时的x值，代表：第一次按下的位置横坐标、每一次移动过程中、移动之前的位置横坐标
    private int index = 0;//每张图片的索引

    /**
     * 实现底部圆点以及切换功能的思路：
     * 1.自定义一个继承FrameLayout的布局，利用FrameLayout布局的特性，
     * 2.在自定义FrameLayout的过程中去加载我们之前自定义的ImageBannerViewGroup
     */

    //自动轮播图
    private boolean isAuto = true;//默认开启自动轮播
    private Timer timer = new Timer();
    private TimerTask task;
    private Handler autoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://需要自动轮播
                    if (++index >= childrenCount) {//如果到了最后一张图片，将会跳到第一张图片，重新开始滑动
                        index = 0;
                    }
                    //计算距离
                    scrollTo(childWidth * index, 0);
                    dotListener.selectIamge(index);//通知小圆点进行切换
                    break;
            }
        }
    };
    /**
     * 实现图片的单击事件
     * 方法：可以利用一个点击变量开关进行判断，在用户离开屏幕的一瞬间去判断变量开关来判断用户的操作是点击还是移动
     */
    private boolean isClick;//true:代表点击事件，false:不是点击事件
    private ImageBannerListener listenr;

    private  ImageBannerViewGroupListener dotListener;

    public ImageBannerViewGroupListener getDotListener() {
        return dotListener;
    }

    public void setDotListener(ImageBannerViewGroupListener dotListener) {
        this.dotListener = dotListener;
    }

    public ImageBannerListener getListenr() {
        return listenr;
    }

    public void setListenr(ImageBannerListener listenr) {
        this.listenr = listenr;
    }
    //点击事件监听
    public interface ImageBannerListener {
        void clickImageIndex(int pos);//pos代表当前图片的索引值
    }

    //小圆点切换监听
    public interface ImageBannerViewGroupListener{
        void selectIamge(int pos);
    }

    /**
     * 采用Timer，TimerTask,Handler三者相结合
     * 抽取两个方法控制，是否启动自动轮播  startAuto(),stopAuto()
     *
     * @param context
     */

    public ImageBannerViewGroup(Context context) {
        super(context);
        init();
    }

    public ImageBannerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageBannerViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        task = new TimerTask() {
            @Override
            public void run() {
                if (isAuto) {
                    autoHandler.sendEmptyMessage(0);
                }
            }
        };
        timer.schedule(task, 100, 2500);
    }

    /**
     * 我们对于绘制来说，因为我们是自定义的viewgroup容器，针对于容器的绘制，其实就是容器内的子视图的绘制过程，
     * 那么我们只需要调用系统自带的绘制即可，也就是说，对于viewgroup的绘制过程，我们不需要在重写该方法，调用系统自带的即可
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 由于我们要实现的是一个ViewGroup的容器，那么我们就应该需要知道该容器中的所有子视图
         * 我们要想测量我们的viewGroup的宽度和高度，那么我们就必须先要去测量子视图的宽度和高度之和，
         * 然后才能知道我们的viewgroup的宽度和高度是多少
         */
        //1，求出子视图的个数
        childrenCount = getChildCount();//得到子视图的个数
        if (childrenCount == 0) {
            setMeasuredDimension(0, 0);
        } else {
            //2，测量子视图的宽度和高度
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            //此时，我们以第一个子视图为基准，也就是说我们的viewProU盘的高度就是我们的第一个子视图的高度，
            //宽度就是我们第一个子视图的 宽度*子视图的个数
            View view = getChildAt(0);
            //3，根据子视图的宽度和高度，来求出该viewgroup的宽度和高度
            childWidth = view.getMeasuredWidth();
            chileHeight = view.getMeasuredHeight();
            int width = view.getMeasuredWidth() * childrenCount;//所有子视图的宽度总和
            setMeasuredDimension(width, chileHeight);
        }
    }

    /**
     * 时间的传递过程中的调用方法：我们需要调用容器的拦截方法 onInterceptTouchEvent
     * 针对于怪方法，我们可以理解为  如果说该方法的返回值为true，那么我们自定义的viewgroup容器就会处理此次拦截事件
     * 如果说 返回值为false，那么我们自定义的viewgroup容器将不会接受此次事件的处理过程，将会继续向下传递该事件，
     * 针对于我们自定义的viewgroup，我们当然是希望我们的viewgroup容器处理接受事件，那么我们的返回值就是true
     * 如果返回true的话，真正处理该事件的方法是onTouchEvent方法
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 有2种方式实现轮播图的手动轮播
     * <p>
     * 1、利用scrollTo、scrollBy 完成
     * 2、liyong scroller 对象完成
     * <p>
     * 第一：我们在滑动屏幕图片的过程中，其实就是我们自定义viewgroup的子视图的移动过程，那么我们只需要知道滑动之前
     * 的横坐标和滑动之后的横坐标，此时我们就可以求出我们此过程中移动的距离，我们再利用scrollBy方法实现图片的滑动，
     * 所以我们需要有2个值是需要我们求出的：移动前、后的横坐标值
     * <p>
     * 第二：在我们第一次按下的那一瞬间，此时的移动前和后的值是相等的，也就是我们此时按下那一瞬间的那一个点的横坐标值
     * <p>
     * 第三：我们在不断的滑动过程中，是会不断的调用ACTION_MOVE方法，那么此时我们就应该讲移动之前的值和移动之后的值进行保存，
     * 以便我们能够算出滑动的距离
     * <p>
     * 第四：在我们抬起的那一瞬间，我们需要计算出我们此时将要滑动到哪张图片的位置上；
     * <p>
     * 我们此时就需要得出将要滑动到的那张图片的索引值
     * (我们当前viewgroup的滑动位置+我们的每一张图片的宽度/2）/我们每一张图片的宽度值
     * <p>
     * 此时可以利用scrollTo方法滑动到 该图片的位置上
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下事件
                stopAuto();//按下时，要停止自动轮播
                x = (int) event.getX();

                isClick = true;//设置为TRUE，此时此刻是点击事件
                break;
            case MotionEvent.ACTION_MOVE://滑动事件
                int moveX = (int) event.getX();
                int distance = moveX - x;
                scrollBy(-distance, 0);
                x = moveX;
                isClick = false;//改变变量状态，说明此时不是点击事件
                break;
            case MotionEvent.ACTION_UP://抬起事件
                startAuto();//抬起时，开始自动轮播
                int scrollX = getScrollX();
                index = (scrollX + childWidth / 2) / childWidth;
                if (index < 0) {//此时滑动到了最左边第一张图
                    index = 0;
                } else if (index > childrenCount - 1) {//此时滑动到最右边的最后一张图
                    index = childrenCount - 1;
                }

                if (isClick) {
                    listenr.clickImageIndex(index);
                } else {
                    scrollTo(index * childWidth, 0);
                    dotListener.selectIamge(index);//通知小圆点进行切换
                }

                break;
            default:
                break;
        }
        return true;//返回true，表示该viewgroup容器处理了该事件
    }

    /**
     * @param changed 当我们的viewgroup布局位置发生改变的为true，没有发生改变为false
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int leftMargin = 0;
            for (int i = 0; i < childrenCount; i++) {
                View view = getChildAt(i);
                view.layout(leftMargin, t, leftMargin + childWidth, chileHeight);
                leftMargin += childWidth;
            }
        }
    }

    private void startAuto() {
        isAuto = true;
    }

    private void stopAuto() {
        isAuto = false;
    }
}
