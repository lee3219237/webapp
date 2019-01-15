package net.redcomdata.application.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.redcomdata.application.R;
import net.redcomdata.application.utils.DensityUtil;


/**
 * Created by chenxiaoli on 2017/4/28 0028.
 * viewpager小圆点
 */

public class ViewPagerPoint extends RelativeLayout {
    private Context context;
    private int mPointDis = 9;
    private int width;
    private int dotOnfocusBackgroundResource;
    private int dotFocusBackgroundResource;
    private int length;
    private int zhijin =7;
    private int pointzhijin ;
    private int pointWidth;


    public ViewPagerPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     *设置点个数，不设置则默认使用viewpager Adapter的条目数
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * 设置点间距
     *
     * @param mPointDis
     */
    public void setmPointDis(int mPointDis) {
        this.mPointDis = mPointDis;
    }

    public void setZhijin(int zhijin) {
        this.zhijin = zhijin;
    }

    /**
     * 设置背景圆点图
     *
     * @param dotFocusBackgroundResource
     */
    public void setDotFocusBackgroundResource(int dotFocusBackgroundResource) {
        this.dotFocusBackgroundResource = dotFocusBackgroundResource;
    }

    /**
     * 设置焦点原点图
     *
     * @param dotOnfocusBackgroundResource
     */

    public void setDotOnfocusBackgroundResource(int dotOnfocusBackgroundResource) {
        this.dotOnfocusBackgroundResource = dotOnfocusBackgroundResource;
    }

    /**
     * 关联viewpager
     * @param viewPager
     */

    public void setWithViewPager(ViewPager viewPager) {
        pointWidth =  DensityUtil.dip2px(context,zhijin+mPointDis);
        pointzhijin = DensityUtil.dip2px(context,zhijin);
        if (length == 0) {
            length = viewPager.getAdapter().getCount();
        }
        for (int i = 0; i < length; i++) {//添加背景小圆点
            final ImageView ivDotOnfocus = new ImageView(context);
            if (dotOnfocusBackgroundResource == 0) {
                ivDotOnfocus.setBackgroundResource(R.drawable.shape_grey_point);
            } else {
                ivDotOnfocus.setBackgroundResource(dotOnfocusBackgroundResource);
            }
            LayoutParams params = new LayoutParams(
                    pointzhijin, pointzhijin);
            params.leftMargin = pointWidth * i;
            ivDotOnfocus.setLayoutParams(params);
            if (width == 0) {
                width = ivDotOnfocus.getMeasuredWidth();
            }
            addView(ivDotOnfocus);
        }
        final ImageView ivDotFocus = new ImageView(context);//添加焦点小圆点
        if (dotFocusBackgroundResource == 0) {
            ivDotFocus.setBackgroundResource(R.drawable.shape_red_point);
        } else {
            ivDotFocus.setBackgroundResource(dotFocusBackgroundResource);
        }
        addView(ivDotFocus);
        ViewGroup.LayoutParams layoutParams =  ivDotFocus.getLayoutParams();
        layoutParams.height = pointzhijin;
        layoutParams.width = pointzhijin;
        ivDotFocus.setLayoutParams(layoutParams);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {//改变小圆点的位置
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LayoutParams params = new LayoutParams(
                        pointzhijin, pointzhijin);
                params.leftMargin = pointWidth * (position%length);
                ivDotFocus.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
