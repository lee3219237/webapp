package net.redcomdata.application.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import net.redcomdata.application.R;
import net.redcomdata.application.activity.WebViewActivity;
import net.redcomdata.application.bean.PayResult;
import net.redcomdata.application.bean.event.MessageEvent;
import net.redcomdata.application.bean.response.WxBean;
import net.redcomdata.application.utils.Constants;
import net.redcomdata.application.utils.GetImgUtil;
import net.redcomdata.application.utils.MyToast;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * <pre>
 *     author : leede
 *     time   : 2018/09/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JavaScriptObject {
    private BaseActivity mActivity;
    private WebView mWebView;


    public JavaScriptObject(BaseActivity activity, WebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    /**
     * 注意这里的@JavascriptInterface注解， target是4.2以上都需要添加这个注解，否则无法调用
     *
     * @param text
     */
    @JavascriptInterface
    public void showToast(final String text) {
        MyToast.show(text);
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:androidCall('android调用js的方法：" + text + "')");
            }
        }, 0);
    }

    @JavascriptInterface
    public void showCenterToast(final String text) {
        MyToast.show(text);
    }

    @JavascriptInterface
    public void finish() {
        mActivity.finish();
    }

    @JavascriptInterface
    public void wxLogin() {
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                IWXAPI WXapi = WXAPIFactory.createWXAPI(mActivity, Constants.WX_APP_ID, true);
                WXapi.registerApp(Constants.WX_APP_ID);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";//获取个人用户信息的权限
                req.state = "wechat_sdk_lee";//防止攻击
                WXapi.sendReq(req);//向微信发送请求
            }
        }, 0);
    }

    /**
     * 微信支付
     */
    @JavascriptInterface
    public void wxPay(String orderJson) {
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(mActivity, null);
        // 将该app注册到微信
        PayReq request = new Gson().fromJson(orderJson, PayReq.class);
        msgApi.registerApp(request.appId);
//        request.appId = "wxd930ea5d5a258f4f";
//        request.partnerId = "1900000109";
//        request.prepayId= "1101000000140415649af9fc314aa427";
//        request.packageValue = "Sign=WXPay";
//        request.nonceStr= "1101000000140429eb40476f8896f4c9";
//        request.timeStamp= "1398746574";
//        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
        msgApi.sendReq(request);
    }

    /**
     * 支付宝支付
     */
    @JavascriptInterface
    public void aLiPay(final String orderInfo) {
        new Thread() {
            public void run() {
                PayTask alipay = new PayTask(mActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                PayResult payResult = new PayResult(result);
                String jsonBean = new Gson().toJson(payResult);
                EventBus.getDefault().post(new MessageEvent(2, jsonBean));
            }
        }.start();

    }

    /**
     * 扫一扫
     */
    @JavascriptInterface
    public void richScan() {
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, BaseWebViewActivity.SCAN_PERMISSION);
                } else {
                    Intent intent = new Intent(mActivity, CaptureActivity.class);
                    ZxingConfig config = new ZxingConfig();
                    config.setPlayBeep(true);//是否播放扫描声音 默认为true
                    config.setShake(true);//是否震动  默认为true
                    config.setDecodeBarCode(true);//是否扫描条形码 默认为true
                    config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为淡蓝色
                    config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
                    config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    mActivity.startActivityForResult(intent, BaseActivity.RICH_SCAN);
                }
            }
        }, 0);
    }

    /**
     * 打开新的web网页
     */
    @JavascriptInterface
    public void openNewPage(String url) {
        Intent intent = new Intent(mActivity, WebViewActivity.class);
        intent.putExtra("url", url);
        mActivity.startActivity(intent);
    }

    /**
     * 持久化本地存储set方法
     */
    @JavascriptInterface
    public void setLocalStorage(String key, String value) {
        mActivity.h5LocalStorage.edit().putString(key, value).commit();
    }

    /**
     * 持久化本地存储get方法
     */
    @JavascriptInterface
    public String getLocalStorage(String key) {
        String value = mActivity.h5LocalStorage.getString(key,"");
        return value;
    }


}
