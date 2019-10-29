# H5_ex0
Android+Html5混合开发

相关知识点
webView的使用
Json的解析和生成(本案例使用GSON)
Html和js基础(为方便, 本案例使用了jQuery)
Java和js的交互


案例分析
说混合开发, 其实就是在WebView上显示本地的Html文件, 所以我们要解决的问题就是如何将Java的数据传送到Html文件中并且通过JS进行动态的显示. 本案例的思路是, 在Activity中生成Json数据(这些Json数据都是假数据, 在项目可以直接从网络中获取Json数据), Json数据通过与JS的交互, 在JS中接收到消息, 然后动态生成Html的Item显示在WebView上! 并且每个item都有相应的点击事件, 点击后回调Android系统的Toast, 弹出当前点击内容.

搭建布局
额..其实就是一个WebView

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    android:orientation="vertical"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.lulu.weichatfriends.MainActivity">
    <WebView
        android:id="@+id/main_web_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>

实体类准备
本例中用于封装json数据

public class FriendsZone {
    private String name;
    private String icon;
    private String content;

    //getter和setter...
}
JS 支持类
这个类用于和JS进行交互.

/**
 * Created by Lulu on 2016/10/27.
 * JS支持类
 */
public class JsSupport {
    private Context mContext;
    private String json;

    public JsSupport(Context context) {
        mContext = context;
    }
    public void setJson(String json) {
        this.json = json;
    }

    @JavascriptInterface
    public String getJson(){
        return json;
    }

    @JavascriptInterface
    public void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}

@JavascriptInterface这个注解说明,该方法可以在js中调用. 上述代码中的两个方法, 在后面的Js可以通过window调用. 这个两个方法刚好能够演示了, Java向JS传递数据和JS回传数据给Java代码

WebView的准备
WebView的使用有很多需要注意的地方, 咱们分步来说:

step1: 在Activity中初始化WebView

mWebView = (WebView) findViewById(R.id.main_web_view);
//解决点击链接跳转浏览器问题
mWebView.setWebViewClient(new WebViewClient());
//js支持
WebSettings settings = mWebView.getSettings();
settings.setJavaScriptEnabled(true);
//允许访问assets目录
settings.setAllowFileAccess(true);
//设置WebView排版算法, 实现单列显示, 不允许横向移动
settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//assets文件路径
String path = "file:///android_asset/index.html";
//添加Json数据
addJson();
//加载Html页面
mWebView.loadUrl(path);
Note: assets文件的路径大家先不用管 后面会说. 上面的addJson()方法接下来会说

step2: addJson()方法, 生成Json数据 传给JsSupport类

private void addJson() {
    JsSupport jsSupport = new JsSupport(this);
    List<FriendsZone> zones = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
        zones.add(new FriendsZone("鹿鹿" + i, "images/icon.png", "这里是Html测试数据, 这里是Html测试数据, 这里是Html测试数据" + i));
    }
    Gson gson = new Gson();
    String json = gson.toJson(zones);
    Log.d(TAG, "addJson: json => " + json);
    jsSupport.setJson(json);
    //添加js交互接口, 并指明js中对象的调用名称
    mWebView.addJavascriptInterface(jsSupport, "weichat");
}
Note: Json数据传到JsSupport类之后, 内部会有一个getJson()方法可被js调用, 完成数据传递

step3: 这一步算是一个小细节, 对于咱们的案例没啥大用处. 就是当你的网页跳转后, 用户按返回键会返回到上一个页面而不是退出整个Activity 重写onBackPressed()方法

@Override
public void onBackPressed() {
    if (mWebView.canGoBack()) {
        mWebView.goBack();
    } else {
        super.onBackPressed();
    }
}
Html和js部分
这个地方算是今天一个重点了, 接下来分步骤来说如何在Android工程中创建Html和js文件

step1: 在src/main目录下 创建 assets 文件夹, 在创建好的文件夹中创建index.html文件(名字随意), 接着可以创建你想要的文件或文件夹, 如图所示

Paste_Image.png

Note: js目录下存放是jquery库, 不要忘记添加. 在这里就可以解释webView中path = "file:///android_asset/index.html"; 这是固定代码格式, 官方文档中有写

step2: 完成index.html文件, 实现与Android系统数据交互

<img id="head_background" src="images/background.jpg" />
<script>
	var json = window.weichat.getJson();
	var infos = eval(json);
	for(var i = 0; i < infos.length; i++) {
		info = infos[i];
		var img = info.icon;
		var userName = info.name;
		var content = info.content;
		$("#head_background").after("<div ><div id='nav'><img src=" + img + " /></div><div id='info'><div id='userName'>" + userName + "</div><p id='content'>" + content + "</p></div></div>");
		$("#userName").click(
			function() {
				var str = $(this).text();
				window.weichat.showToast(str);
			}
		)
		$("#content").click(
			function() {
				var str = $(this).text();
				window.weichat.showToast(str);
			}
		)
	}
</script>
