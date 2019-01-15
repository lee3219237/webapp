package net.redcomdata.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import net.redcomdata.application.R;
import net.redcomdata.application.base.BaseActivity;
import net.redcomdata.application.utils.SpType;


public class SplashActivity extends BaseActivity {

    private Handler handler = new Handler();
    private Runnable runnable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        runnable2 = new Runnable() {
            @Override
            public void run() {
                next();
            }
        };
        handler.postDelayed(runnable2,3000);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable2);
        super.onDestroy();
    }

    private void next(){
//        if (sp.getBoolean(SpType.NOT_FIRST_OPEN, false)) {
//
//        } else {
//            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
//        }
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
