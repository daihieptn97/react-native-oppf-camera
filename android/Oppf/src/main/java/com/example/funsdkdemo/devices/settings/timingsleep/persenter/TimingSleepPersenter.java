package com.example.funsdkdemo.devices.settings.timingsleep.persenter;

import android.os.Message;
import android.widget.Toast;

import com.basic.G;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.devices.settings.timingsleep.view.ITimingSleepView;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.TimingSleepBean;

/**
 * Created by hws on 2018-07-05.
 */

public class TimingSleepPersenter implements ITimingSleepPersenter , IFunSDKResult{
    private ITimingSleepView mSettingView;
    private String mDevId;
    private int mUserId;
    private TimingSleepBean mTimingSleep;
    public TimingSleepPersenter(String devId, ITimingSleepView settingView) {
        this.mDevId = devId;
        this.mSettingView = settingView;
        mUserId = FunSDK.GetId(mUserId,this);
    }


    @Override
    public void getConfig() {
        FunSDK.DevGetConfigByJson(mUserId,mDevId,JsonConfig.CFG_TIMING_SLEEP,1024,0,5000,0);
    }

    @Override
    public void saveConfig() {
        String jsonData = HandleConfigData.getSendData(JsonConfig.CFG_TIMING_SLEEP,"0x08",mTimingSleep);
        FunSDK.DevSetConfigByJson(mUserId,mDevId,JsonConfig.CFG_TIMING_SLEEP,jsonData,0,5000,0);
    }

    public void onDestory() {
        FunSDK.UnRegUser(mUserId);
        mUserId = 0;
    }

    @Override
    public void setSleepSwitch(boolean isOpen) {
        if (mTimingSleep != null) {
            mTimingSleep.setEnable(isOpen);
            mTimingSleep.setManualWakeUp(false);
        }
    }

    @Override
    public void setSleepTime(int[] startTime, int[] endTime) {
        if (mTimingSleep != null) {
            TimingSleepBean.WorkPeriod workPeriod = mTimingSleep.getWorkPeriod();
            workPeriod.setsHour(startTime[0]);
            workPeriod.setsMinute(startTime[1]);
            workPeriod.seteHour(endTime[0]);
            workPeriod.seteMinute(endTime[1]);
        }
    }

    @Override
    public void setRepeat(boolean isRepeat) {
        if (mTimingSleep != null) {
            mTimingSleep.setRepeatType(isRepeat ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        }
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_GET_JSON:
                if (message.arg1 < 0) {
                    Toast.makeText(mSettingView.getActivity(), R.string.get_config_f,Toast.LENGTH_SHORT).show();
                    break;
                }

                if (StringUtils.contrast(msgContent.str,JsonConfig.CFG_TIMING_SLEEP)) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(G.ToString(msgContent.pData),TimingSleepBean.class)) {
                        mTimingSleep = (TimingSleepBean) handleConfigData.getObj();
                    }

                    if (mTimingSleep != null) {
                        mSettingView.updateSleepSwitch(mTimingSleep.isEnable());
                        mSettingView.updateSleepTime(mTimingSleep.getWorkPeriod().getStartTime(), mTimingSleep.getWorkPeriod().getEndTime());
                        mSettingView.updateSleepRepeat(mTimingSleep.getRepeatType() == SDKCONST.Switch.Open);
                    }
                }
                break;
            case EUIMSG.DEV_SET_JSON:
                if (message.arg1 < 0) {
                    Toast.makeText(mSettingView.getActivity(), R.string.set_config_f, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mSettingView.getActivity(), R.string.set_config_s, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return 0;
    }
}
