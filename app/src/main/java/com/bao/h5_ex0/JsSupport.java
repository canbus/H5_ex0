package com.bao.h5_ex0;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JsSupport {
    private Context mContext;
    private String json;

    public JsSupport(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public String getJson() {
        Log.d(MainActivity.TAG,"getJson");
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
    @JavascriptInterface
    public void showToast(String str){
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}
