package com.example.funsdkdemo.devices.settings.pir.presenter;

import android.os.Message;

import com.basic.G;
import com.example.funsdkdemo.devices.settings.pir.contract.PirSetContract;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.lib.sdk.bean.AlarmInfoBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;

/**
 * @author hws
 * @class  PIR 徘徊检测
 * @time 2020/3/25 13:58
 */
public class PirSetPresenter implements PirSetContract.IPirSetPresenter {
    private int userId;
    private String devId;
    private AlarmInfoBean alarmInfoBean;
    private PirSetContract.IPirSetView iPirSetView;
    public PirSetPresenter(String devId, PirSetContract.IPirSetView iPirSetView) {
        this.devId = devId;
        this.iPirSetView = iPirSetView;
        userId = FunSDK.GetId(userId,this);
        initData();
    }

    private void initData() {
        FunSDK.DevGetConfigByJson(userId, devId, JsonConfig.ALARM_PIR, 1024, 0, 5000, 0);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_GET_JSON:
                if (JsonConfig.ALARM_PIR.equals(msgContent.str)) {
                    if (message.arg1 < 0) {
                        System.out.println("获取配置失败");
                        if (alarmInfoBean != null) {
                            iPirSetView.onGetConfigResult(false);
                        }
                        return 0;
                    }else {
                        String jsonData = G.ToStringJson(msgContent.pData);
                        HandleConfigData<AlarmInfoBean> handleConfigData = new HandleConfigData<>();
                        if (handleConfigData.getDataObj(jsonData,AlarmInfoBean.class)) {
                            alarmInfoBean = handleConfigData.getObj();
                            if (alarmInfoBean != null) {
                                iPirSetView.onGetConfigResult(true);
                                return 0;
                            }
                        }
                    }
                    if (alarmInfoBean != null) {
                        iPirSetView.onGetConfigResult(false);
                    }
                }
                break;
            case EUIMSG.DEV_SET_JSON:
                if (JsonConfig.ALARM_PIR.equals(msgContent.str)) {
                    if (message.arg1 < 0) {
                        System.out.println("设置配置失败");
                        if (alarmInfoBean != null) {
                            iPirSetView.onSetConfigResult(false);
                        }
                    }else {
                        if (alarmInfoBean != null) {
                            iPirSetView.onSetConfigResult(true);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return 0;
    }

    @Override
    public boolean isPirAlarmEnable() {
        return alarmInfoBean != null && alarmInfoBean.Enable;
    }

    @Override
    public float getDuration() {
        return alarmInfoBean != null ? alarmInfoBean.PIRCheckTime : 0;
    }

    @Override
    public AlarmInfoBean.PirTimeSections getPirTimeSection() {
        return alarmInfoBean != null ? alarmInfoBean.PirTimeSection : null;
    }

    @Override
    public int getPirSensitive() {
        return alarmInfoBean != null ? alarmInfoBean.PirSensitive : 0;
    }
}
