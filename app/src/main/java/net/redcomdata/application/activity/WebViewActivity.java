package net.redcomdata.application.activity;

import android.os.Bundle;

import net.redcomdata.application.R;
import net.redcomdata.application.base.BaseWebViewActivity;

public class WebViewActivity extends BaseWebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
}
