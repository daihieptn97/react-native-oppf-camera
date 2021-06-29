package com.example.funsdkdemo.devices.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.basic.G;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.CameraParamExBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.SystemManageShutDown;
import com.lib.sdk.bean.doorlock.OPDoorLockProCmd;
import com.xm.ui.widget.SpinnerSelectItem;

/**
 * @author hws
 * @name FunSDKDemo_Android_Old2018
 * @class name：com.example.funsdkdemo.devices
 * @class 门锁
 * @time 2019-03-26 17:49
 */
public class ActivityGuideDeviceDoorLock extends ActivityDemo
        implements IFunSDKResult,AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener{
    private FunDevice funDevice;
    private int userId;
    private SpinnerSelectItem ssiSleepTime;
    private Spinner sleepTimeSpinner;
    private boolean isSpinnerTouched = false;
    private SystemManageShutDown sysManageShutDown;
    private String[] sleepArray = new String[]{"15s", "30s"};
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_doorlock);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.backBtnInTopLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ssiSleepTime = findViewById(R.id.ssi_shutdown_time);
        sleepTimeSpinner = ssiSleepTime.getSpinner();

        initSpinnerText(sleepTimeSpinner, sleepArray, new int[]{15, 30});
        sleepTimeSpinner.setOnItemSelectedListener(onSpinnerSelected);
        sleepTimeSpinner.setOnTouchListener(onSpinnerTouched);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        int devPos = intent.getIntExtra("FUN_DEVICE_ID", 0);
        funDevice = FunSupport.getInstance().findDeviceById(devPos);
        userId = FunSDK.GetId(userId,this);

        FunSDK.DevGetConfigByJson(userId,
                funDevice.getDevSn(),
                JsonConfig.SYSTEM_MANAGE_SHUTDOWN,
                1024,
                -1,
                5000,
                0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_CMD_EN:
                if (StringUtils.contrast(msgContent.str,JsonConfig.DOOR_LOCK_UNLOCK)) {
                    if (message.arg1 >= 0) {
                        Toast.makeText(this, R.string.door_lock_open_s, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, R.string.door_lock_open_f, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case EUIMSG.DEV_GET_JSON:
                if (StringUtils.contrast(msgContent.str,JsonConfig.SYSTEM_FUNCTION)) {
                    if (message.arg1 >= 0) {
                        HandleConfigData handleConfigData = new HandleConfigData();
                        if (handleConfigData.getDataObj(G.ToString(msgContent.pData), SystemFunctionBean.class)) {
                            SystemFunctionBean systemFunctionBean = (SystemFunctionBean) handleConfigData.getObj();
                            if (systemFunctionBean != null && systemFunctionBean.OtherFunction.SupportCorridorMode) {
                                FunSDK.DevGetConfigByJson(userId, funDevice.getDevSn(),
                                        JsonConfig.CAMERA_PARAMEX, 1024, 0, 5000, 0);
                            }
                        }
                    }else {
                        Toast.makeText(this, getString(R.string.get_config_f) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }
                }else if (StringUtils.contrast(msgContent.str,JsonConfig.CAMERA_PARAMEX)) {
                    if (message.arg1 >= 0) {
                        HandleConfigData handleConfigData = new HandleConfigData();
                        if (handleConfigData.getDataObj(G.ToString(msgContent.pData), CameraParamExBean.class)) {
                            CameraParamExBean cameraParamExBean = (CameraParamExBean) handleConfigData.getObj();
                            if (cameraParamExBean != null) {
                                cameraParamExBean.CorridorMode = ++cameraParamExBean.CorridorMode % 4;
                                FunSDK.DevSetConfigByJson(userId, funDevice.getDevSn(), JsonConfig.CAMERA_PARAMEX, HandleConfigData
                                                .getSendData(HandleConfigData.getFullName(JsonConfig.CAMERA_PARAMEX, 0), "0x01", cameraParamExBean),
                                        0, 5000, 0);
                            }
                        }
                    }else {
                        Toast.makeText(this, getString(R.string.get_config_f) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }
                }else if (StringUtils.contrast(msgContent.str,JsonConfig.SYSTEM_MANAGE_SHUTDOWN)) {
                    if (message.arg1 >= 0) {
                        HandleConfigData handleConfigData = new HandleConfigData();
                        if (handleConfigData.getDataObj(G.ToString(msgContent.pData), SystemManageShutDown.class)) {
                            sysManageShutDown = (SystemManageShutDown) handleConfigData.getObj();
                            if (sysManageShutDown != null) {
                                setValue(sleepTimeSpinner, sysManageShutDown.ShutDownMode);
                            }
                        }
                    }else {
                        Toast.makeText(this, getString(R.string.get_config_f) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case EUIMSG.DEV_SET_JSON:
                if (StringUtils.contrast(msgContent.str,JsonConfig.CAMERA_PARAMEX)) {
                    if (message.arg1 >= 0) {
                        Toast.makeText(this, getString(R.string.flip_s) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, getString(R.string.flip_f) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }
                }else if (StringUtils.contrast(msgContent.str,JsonConfig.SYSTEM_MANAGE_SHUTDOWN)) {
                    if (message.arg1 >= 0) {
                        Toast.makeText(this, getString(R.string.set_config_s) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, getString(R.string.set_config_f) + ":" + message.arg1, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        return 0;
    }

    public void onUnlock(View view) {
        OPDoorLockProCmd cmd = new OPDoorLockProCmd();
        cmd.Cmd = JsonConfig.DOOR_LOCK_UNLOCK;
        cmd.Arg2 = "111111";//开锁的密码
        FunSDK.DevCmdGeneral(userId,
                funDevice.getDevSn(),
                OPDoorLockProCmd.JSON_ID,
                cmd.Cmd,
                0,
                5000,
                HandleConfigData.getSendData(OPDoorLockProCmd.JSON_NAME,
                        "0x08",cmd).getBytes(),
                -1,
                0);
    }

    //翻转
    public void onFlip(View view) {
        //首先要判断该设备是否支持翻转 通过能力集判断  SupportCorridorMode;//是否支持走廊模式，就是90度旋转
        FunSDK.DevGetConfigByJson(userId, funDevice.getDevSn(),
                JsonConfig.SYSTEM_FUNCTION, 8192, 0, 5000, 0);
    }

    private AdapterView.OnItemSelectedListener onSpinnerSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(isSpinnerTouched && sysManageShutDown != null){
                int sleepTime = getIntValue(sleepTimeSpinner);
                sysManageShutDown.ShutDownMode = sleepTime;
                String jsonData = HandleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.SYSTEM_MANAGE_SHUTDOWN,-1),"0x08",sysManageShutDown);
                FunSDK.DevSetConfigByJson(userId,
                        funDevice.getDevSn(),
                        JsonConfig.SYSTEM_MANAGE_SHUTDOWN,
                        jsonData,
                        -1,
                        5000,
                        0);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnTouchListener onSpinnerTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                isSpinnerTouched = true;
            }
            return false;
        }
    };
}
