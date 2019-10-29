package com.bao.h5_ex0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "H5_ex0";
    WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient());//解决点连接跳转浏览器问题
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//js支持
        settings.setAllowFileAccess(true);  //允许访问assets目录
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String path = "file:///android_asset/index.html";
        addJson();
        mWebView.loadUrl(path);
    }

    private void addJson(){
        JsSupport jsSupport = new JsSupport(this);
        List<FriendZone> zones = new ArrayList<>();
        for(int i=0;i<100;i++){
            zones.add(new FriendZone("测试条目" + i,"images/icon.jpg","这里测试数据"));
        }
        Gson gson = new Gson();
        String json = gson.toJson(zones);
        Log.d(TAG,"addJson:json=>"+json);
        jsSupport.setJson(json);
        mWebView.addJavascriptInterface(jsSupport,"weichat");
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
