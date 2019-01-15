package net.redcomdata.application.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import net.redcomdata.application.R;
import net.redcomdata.application.adapter.GuideAdapter;
import net.redcomdata.application.base.BaseActivity;
import net.redcomdata.application.utils.SpType;
import net.redcomdata.application.view.ViewPagerPoint;


/**
 * Created by chenxiaoli on 2017/5/9 0028.
 * 引导页
 */
public class GuideActivity extends BaseActivity {


    ViewPager viewPager;
    ViewPagerPoint vpPoint;
    Button btnNext;
   private int[] imgs = {R.mipmap.splash};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        viewPager = findViewById(R.id.view_pager);
        vpPoint = findViewById(R.id.vp_point);
        btnNext = findViewById(R.id.btn_next);
        GuideAdapter adapter = new GuideAdapter(this,imgs);
        viewPager.setAdapter(adapter);
        vpPoint.setmPointDis(10);
        vpPoint.setZhijin(9);
        vpPoint.setWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == imgs.length-1) {
                    vpPoint.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    vpPoint.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initEvent() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getBoolean(SpType.NOT_FIRST_OPEN, false) == false) {
                    sp.edit().putBoolean(SpType.NOT_FIRST_OPEN, true).commit();
                }
                finish();
            }
        });
    }


}
