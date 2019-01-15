package net.redcomdata.application.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.redcomdata.application.bean.event.MessageEvent;
import net.redcomdata.application.bean.response.WxBean;
import net.redcomdata.application.utils.Constants;

import org.greenrobot.eventbus.EventBus;


import okhttp3.Call;


/**
 * Created by M on 2016/10/10.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    private BaseResp resp = null;
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token";
    // 获取用户个人信息
    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Log.e("111", "111");
        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        //登录
        String result = "";
        if (resp != null) {
            resp = resp;
        }
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) resp).code;
                getAcessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "操作取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    private void getAcessToken(String code) {
        OkHttpUtils
                .get()
                .tag(this)
                .url(GetCodeRequest)
                .addParams("appid", Constants.WX_APP_ID)
                .addParams("secret", Constants.APP_Secret)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (TextUtils.isEmpty(response)) {
                            return;
                        }
                        WxBean bean = new Gson().fromJson(response, WxBean.class);
                        getUserInfo(bean);
                    }
                });
    }

    private void getUserInfo(final WxBean bean) {
        OkHttpUtils
                .get()
                .tag(this)
                .url(GetCodeRequest)
                .addParams("access_token", bean.getAccessToken())
                .addParams("openid", bean.getOpenid())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (TextUtils.isEmpty(response)) {
                            return;
                        }
                        WxBean wxBean = new Gson().fromJson(response, WxBean.class);
                        wxBean.setAccessToken(bean.getAccessToken());
                        wxBean.setOpenId(bean.getOpenId());
                        wxBean.setUnionid(bean.getUnionid());
                        String jsonBean = new Gson().toJson(wxBean);
                        EventBus.getDefault().post(new MessageEvent(0,jsonBean));
                        finish();
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        Log.e("111", "555");
        finish();
    }

    @Override
    protected void onDestroy() {
        //  EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
