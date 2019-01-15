package net.redcomdata.application.activity;

import android.content.Intent;
import android.os.Bundle;


import net.redcomdata.application.R;
import net.redcomdata.application.base.BaseWebViewActivity;
import net.redcomdata.application.bean.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class MainActivity extends BaseWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
//        webView.loadUrl("file:///android_asset/jsTest.html");
//        webView.loadUrl("http://redacom.jiazuidi.com/app/login/index");
        webView.loadUrl("http://redacom.jiazuidi.com/app/login/auto");
        startActivity(new Intent(this,SplashActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMsgType()){
            case 0:
                webView.loadUrl("javascript:wxLoginSuccess('"+messageEvent.getMessage()+"')");
                break;
            case 1:
                webView.loadUrl("javascript:wxPayCallBack('"+messageEvent.getMessage()+"')");
                break;
            case 2:
                webView.loadUrl("javascript:aLiPayCallBack('"+messageEvent.getMessage()+"')");
                break;
        }

    }


    @Override
    protected void onDestroy() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
