package com.example.funsdkdemo.devices.settings.pir.contract;

import com.lib.IFunSDKResult;
import com.lib.sdk.bean.AlarmInfoBean;

/**
 * @author hws
 * @class PIR 徘徊检测
 * @time 2020/3/25 13:58
 */
public interface PirSetContract {
    interface IPirSetView {
        void onGetConfigResult(boolean isSuccess);
        void onSetConfigResult(boolean isSuccess);
    }

    interface IPirSetPresenter extends IFunSDKResult {
        /**
         * PIR徘徊报警使能
         * @return
         */
        boolean isPirAlarmEnable();

        /**
         *徘徊检测时间、PIR唤醒门铃时间
         * @return
         */
        float getDuration();

        /**
         *报警计划时间
         * @return
         */
        AlarmInfoBean.PirTimeSections getPirTimeSection();

        /**
         * 获取Pir灵敏度
         * @return
         */
        int getPirSensitive();
    }
}
