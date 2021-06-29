package com.example.funsdkdemo.devices.settings.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.example.funsdkdemo.R;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.JsonConfig;

/**
 * @author hws
 * @class 其他配置 通过发送json和获取json内容来显示
 */
public class ActivityDeviceOtherConfigGet extends Activity implements IFunSDKResult{
    private Spinner mSpSelectSetFun;
    private TextView mTvShowReceiveJson;
    private int mUserId;
    private FunDevice mFunDevice;

    private ConfigInfo[] mSendConfigInfo = new ConfigInfo[]
            {
                    new ConfigInfo(JsonConfig.ALARM_PIR,0),
                    new ConfigInfo(JsonConfig.SET_ENABLE_VIDEO,-1),
                    new ConfigInfo(JsonConfig.CFG_PMS,-1),
                    new ConfigInfo(JsonConfig.CFG_NOTIFY_LIGHT,-1),
                    new ConfigInfo(JsonConfig.CAPTURE_PRIORITY,-1),
                    new ConfigInfo(JsonConfig.CFG_FORCE_SHUT_DOWN_MODE,-1),
                    new ConfigInfo(JsonConfig.ALARM_PIR,0),
                    new ConfigInfo(JsonConfig.IDR_NO_DISTURB,-1)

            };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_other_set);
        initView();
        initData();
    }

    private void initView() {
        mSpSelectSetFun = findViewById(R.id.sp_select_set_function);
        mTvShowReceiveJson = findViewById(R.id.tv_show_receive_config);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        mUserId = FunSDK.GetId(mUserId,this);
        int devPos = intent.getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(devPos);
    }

    public void onGetConfig(View view) {
        int position = mSpSelectSetFun.getSelectedItemPosition();
        ConfigInfo configInfo = mSendConfigInfo[position];
        if (configInfo != null) {
            FunSDK.DevGetConfigByJson(mUserId,mFunDevice.getDevSn(),configInfo.jsonName,1024,configInfo.chnId,5000,0);
        }
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        if (message.arg1 >= 0) {
            mTvShowReceiveJson.setText(G.ToString(msgContent.pData));
        }else {
            Toast.makeText(this,R.string.get_config_f,Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    class ConfigInfo {
        String jsonName;
        int chnId;

        ConfigInfo(String jsonName,int chnId) {
            this.jsonName = jsonName;
            this.chnId = chnId;
        }
    }
}
