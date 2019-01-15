package net.redcomdata.application.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;


import com.zhy.http.okhttp.OkHttpUtils;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    public static final int RICH_SCAN = 801;
    private Toolbar mToolBar = null;
    private TextView mTxtTitle = null;
    private ProgressDialog mLoadingDialog = null;
    private Toast mToast = null;
    protected SharedPreferences sp;
    public SharedPreferences h5LocalStorage;

    public BaseActivity() {
    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        h5LocalStorage = getSharedPreferences("localStorage", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(this);
        super.onDestroy();
    }

    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        this.initView();
        this.initData();
        this.initEvent();
    }

    public void showToast(String text) {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            this.mToast.setText(text);
        }
        this.mToast.show();
    }

    public void showToast(@StringRes int resId) {
        this.showToast(this.getString(resId));
    }

    private void destroyToast() {
        if (this.mToast != null) {
            this.mToast.cancel();
            this.mToast = null;
        }

    }

    public void showLoadingDialog() {
        this.showLoadingDialog("加载中");
    }

    public void showLoadingDialog(String text) {
        if (null == this.mLoadingDialog) {
            this.mLoadingDialog = new ProgressDialog(this);
            this.mLoadingDialog.setCancelable(true);
            this.mLoadingDialog.setProgressStyle(0);
            this.mLoadingDialog.setMessage(text);
            this.mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (this.mLoadingDialog != null && this.mLoadingDialog.isShowing()) {
            this.mLoadingDialog.dismiss();
        }

        this.mLoadingDialog = null;
    }

    public void openActivity(Class<?> pClass) {
        this.openActivity(pClass, (Bundle) null);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }

        this.startActivity(intent);
    }

    public void openWebActivity(String uri) {
        Bundle bundle = new Bundle();
        bundle.putString("bundle_url_key", uri);
        this.openActivity(BaseWebViewActivity.class, bundle);
    }
}
