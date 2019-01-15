package net.redcomdata.application.base;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import net.redcomdata.application.R;
import net.redcomdata.application.utils.GetImgUtil;
import net.redcomdata.application.utils.ImageUtil;

import java.io.File;

public class BaseWebViewActivity extends BaseActivity {
    private GetImgUtil getImgUtil;
    private static final String TAG = BaseWebViewActivity.class.getSimpleName();
    protected WebView webView;
    private BaseChromeClient chromeClient;
    public static final int SCAN_PERMISSION = 3012;

    public BaseWebViewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    protected void initView() {
        this.webView = findViewById(R.id.web_view);
        getImgUtil = new GetImgUtil(this);
        webView.setWebViewClient(new BaseWebViewClient());
        chromeClient = new BaseChromeClient(this, getImgUtil);
        webView.setWebChromeClient(chromeClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new JavaScriptObject(this,webView), "NativeApp");
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        //webView.loadUrl("javascript:callJS()");
    }

    protected void initEvent() {

    }

    protected void loadUrl(String url) {
        if (null != url) {
            this.webView.loadUrl(url);
        }
    }

    public void onBackPressed() {
        if (this.webView.canGoBack()) {
            this.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getImgUtil != null) {
            getImgUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if(requestCode==SCAN_PERMISSION){
            Intent intent = new Intent(BaseWebViewActivity.this, CaptureActivity.class);
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(true);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(true);//是否扫描条形码 默认为true
            config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为淡蓝色
            config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
            config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, BaseActivity.RICH_SCAN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getImgUtil != null) {
            getImgUtil.onActivityResult(requestCode, resultCode, data);
        }
        // 扫描二维码/条码回传
        if (requestCode == RICH_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                webView.loadUrl("javascript:richScanSuccess('"+content+"')");
            }
        }
    }

    public static class BaseWebViewClient extends WebViewClient {
        public BaseWebViewClient() {

        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG, "shouldOverrideUrlLoading:url:" + url);
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                return true;
            }
            // 处理自定义scheme
            if (!url.contains("http")) {
                Log.e(TAG, "url:" + url);
                try {
                    // 以下固定写法
                    final Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    view.getContext().startActivity(intent);
                } catch (Exception e) {
                    // 防止没有安装的情况
                    e.printStackTrace();
                }
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();

        }
    }

    public static class BaseChromeClient extends WebChromeClient {
        private GetImgUtil getImgUtil;
        private ValueCallback<Uri> uploadMessage;
        private ValueCallback<Uri[]> uploadMessageAboveL;
        private Context context;

        public BaseChromeClient(Context context, GetImgUtil getImgUtil) {
            this.getImgUtil = getImgUtil;
            this.context = context;
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            Log.e(TAG, "openFileChooser");
            uploadMessage = valueCallback;
            chooeseImage();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            Log.e(TAG, "openFileChooser");
            uploadMessage = valueCallback;
            chooeseImage();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            chooeseImage();
            return true;
        }

        private void chooeseImage() {
            getImgUtil.setOnGetImgCallBack(new GetImgUtil.OnGetImgCallBack() {//添加获取文件监听
                @Override
                public void onSuccess(String mCameraFilePath) {//获取到的图片地址
                    File file = ImageUtil.compressImage(mCameraFilePath, 300);
                    Uri result = Uri.fromFile(file);
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(result);
                    }
                    if (uploadMessageAboveL != null) {
                        uploadMessageAboveL.onReceiveValue(new Uri[]{result});
                    }
                }

                @Override
                public void onFail() {
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                    }
                    if (uploadMessageAboveL != null) {
                        uploadMessageAboveL.onReceiveValue(null);
                    }
                }
            });
            new AlertDialog.Builder(context)
                    .setTitle("上传图片")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (uploadMessage != null) {
                                uploadMessage.onReceiveValue(null);
                            }
                            if (uploadMessageAboveL != null) {
                                uploadMessageAboveL.onReceiveValue(null);
                            }
                        }
                    })
                    .setItems(new String[]{"拍照", "图库选取"},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    switch (which) {
                                        case 0:
                                            getImgUtil.openCamera();
                                            break;
                                        case 1:
                                            getImgUtil.openFileChoice();
                                            break;
                                    }

                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            }).show();
        }

        @Override
        //扩容
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(requiredStorage * 2);
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.e("h5端的log", String.format("%s -- From line %s of %s", message, lineNumber, sourceID));
        }
    }


}
