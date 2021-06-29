package com.example.funsdkdemo.cloud;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.alarm.ActivityGuideDeviceAlarmResult;
import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmGroup;
import com.xm.ui.widget.XTitleBar;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * https://boss2.xmcsrv.net/index.do?user_id=&uuid=&lan=zh&appKey=&goods=&avatarUrl=
 * 参数说明：
 *          user_id,用户唯一标识
 *          uuid    ,设备序列号
 *          appKey，开放平台应用appKey
 *          goods，直接打开套餐标识
 *          avatarUrl，头像Url地址
 */
public class CloudWebActivity extends ActivityDemo {
    private static final String CLOUD_STORAGE_BASE_URL = "https://boss2.xmcsrv.net/index.do";
    public static final String GOODS_TYPE_FLOW = "net.cellular";//流量
    public static final String GOODS_TYPE_CLOUD_ENHANCE = "xmc.enhance";//云增强
    public static final String GOODS_TYPE_CLOUD_SMART = "xmc.ais";//云智能
    public static final String GOODS_TYPE_CLOUD_STORAGE = "xmc.css";//云存储
    public static final int CLOUD_TYPE_STORAGE = 0;
    public static final int CLOUD_TYPE_FLOW = 1;
    private static final int REQUEST_CODE = 0x08;
    private XTitleBar xtitle;
    private WebView webView;
    private String url;
    private HandleConfigData handleConfigData = new HandleConfigData();
    private AlarmGroup alarmGroup = null;
    private ProgressBar progressBar;
    private ImageView circleIvWhite;
    private TextView titleTv;
    private String goodsType;//商品类型
    private FunDevice funDevice;
    private String searchDate;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            transparencyStatusBar(this);
            setContentView(R.layout.activity_simple_web);
        }catch (Exception e) {
            Toast.makeText(this, "无法创建webView", Toast.LENGTH_LONG).show();
            finish();
            e.printStackTrace();
            return;
        }
        initView();
        initData();
    }

    @SuppressLint("JavascriptInterface")
    private void initView() {
        xtitle = findViewById(R.id.web_title);
        xtitle.setVisibility(View.GONE);
        webView = findViewById(R.id.web_view);
        progressBar = findViewById(R.id.progressbar);
        circleIvWhite = findViewById(R.id.iv_web_back);
        titleTv = findViewById(R.id.tv_web_title);
        circleIvWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloudWebActivity.this.finish();
            }
        });
        xtitle.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                CloudWebActivity.this.finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        int devId = intent.getIntExtra("FUN_DEVICE_ID",0);
        searchDate = intent.getStringExtra("searchDate");
        funDevice = FunSupport.getInstance().findDeviceById(devId);

        webView.getSettings().setTextZoom(100);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "XmAppJsSDK");
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";xm-android-m");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("ccy", "shouldOverrideUrlLoading = " + url);
                if (StringUtils.isStringNULL(url)) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                if (url.contains("alipays://platformapi")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {  //检测是否安装了支付宝(未安装则网页支付）
                        startActivity(intent);
                    }else {
                        Toast.makeText(CloudWebActivity.this, FunSDK.TS("Install_Alipay_Application"), Toast.LENGTH_LONG).show();
                    }

                    return true;
                }
                if (url.contains("weixin://wap/pay")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) { //检测是否安装了微信
                        startActivity(intent);
                        return true;
                    }else {
                        Toast.makeText(CloudWebActivity.this, FunSDK.TS("Install_WeChat_Application"), Toast.LENGTH_LONG).show();
                        view.loadUrl(CloudWebActivity.this.url);  //回主页
                    }
                }

                if (url.endsWith(".apk")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //完成支付跳回主界面后，要清空历史页面
                if (url.contains(CLOUD_STORAGE_BASE_URL)) {
                    view.clearHistory();
                }else {
                    if (url != null && url.contains("load=finish")) {
                        titleTv.setVisibility(View.INVISIBLE);
                        circleIvWhite.setVisibility(View.VISIBLE);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    Log.d("apple-progress",url);
                }
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                return super.onRenderProcessGone(view, detail);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (null != progressBar) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //允许https/http混合加载
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        url = createCloudUrl();
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    /**
     * 创建云存储url
     */
    private String createCloudUrl() {
        String userId = FunSDK.GetFunStrAttr(EFUN_ATTR.LOGIN_USER_ID);
        if (userId == null) {
            Toast.makeText(this,"需要账号登录才能正常打开云服务网页哦!",Toast.LENGTH_LONG).show();
            return null;
        }

        //只有中文或英文,
        String lan =  Locale.getDefault().getLanguage();
        if(lan.compareToIgnoreCase("zh") == 0){
            lan = "zh-CN";//不是zh_CN
        }else {
            lan = "en";
        }

        Map<String, String> urlMap = new LinkedHashMap<>();
            urlMap.put("user_id", userId);
            urlMap.put("lan",lan);
            urlMap.put("appKey",FunSupport.APP_KEY);
            urlMap.put("goods",goodsType);
            urlMap.put("avatarUrl","");//传入的是头像Url地址
            if (funDevice != null) {
            urlMap.put("uuid", funDevice.getDevSn());
            urlMap.put("devName", funDevice.getDevName());
        }

        return getUrl(CLOUD_STORAGE_BASE_URL, urlMap);
    }

    /**
     * url参数拼接
     */
    private String getUrl(String baseUrl, Map<String, String> values) {
        StringBuilder sb = new StringBuilder(baseUrl);

        if (values != null && !values.isEmpty()) {
            sb.append("?");
            for (Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAllWebViewCallback();
        if (webView != null){
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.removeAllViews();
            webView.destroy();

            ViewGroup view = (ViewGroup) getWindow().getDecorView();
            view.removeAllViews();
            ((ViewGroup)webView.getParent()).removeView(webView);
        }

        webView = null;
    }

    @JavascriptInterface
    public void closeWindow(){
        Log.d("apple","closeWindow");
        finish();
    }


    private void releaseAllWebViewCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                    e.printStackTrace();
            } catch (IllegalAccessException e) {
                    e.printStackTrace();
            }
        } else {
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                    e.printStackTrace();
            } catch (ClassNotFoundException e) {
                    e.printStackTrace();
            } catch (IllegalAccessException e) {
                    e.printStackTrace();
            }
        }
    }

    /**
     * 设置状态栏为全透明
     * 通过设置theme的方式无法达到全透明效果
     *
     * @param activity
     */
    @TargetApi(19)
    private static void transparencyStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0及其以上
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4及其以上
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * mediaType:0->图片(报警消息） 1->视频（录像回放）
     */
    @JavascriptInterface
    public void openCloudStorageList(int mediaType){
        switch (mediaType) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(this, ActivityGuideDeviceAlarmResult.class);
                intent.putExtra("FUN_DEVICE_ID", funDevice.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case 1:
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = format.parse(searchDate);
                    Intent it = new Intent(this, ActivityDevCloudPlayBack.class);
                    it.putExtra("year", data.getYear() + 1900);
                    it.putExtra("month", data.getMonth());
                    it.putExtra("day", data.getDate());
                    it.putExtra("FUN_DEVICE_ID",funDevice.getId());
                    startActivity(it);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
