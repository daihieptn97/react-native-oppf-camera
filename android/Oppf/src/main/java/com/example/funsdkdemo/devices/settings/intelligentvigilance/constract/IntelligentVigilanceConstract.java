package com.example.funsdkdemo.devices.settings.intelligentvigilance.constract;


import com.lib.sdk.bean.HumanDetectionBean;

/**
 * @author hws
 * @name iCSee
 * @class name：com.mobile.myeye.setting.humandetect.constract
 * @class describe
 * @time 2019-05-06 16:00
 */
public class IntelligentVigilanceConstract {
    public interface IHumanDetectView {
        void updateHumanDetectResult(boolean isSuccess);
        void saveHumanDetectResult(boolean isSuccess);
    }

    public interface IHumanDetectPresenter {
        void updateHumanDetect();
        void saveHumanDetect();
        boolean isHumanDetectEnable();
        void setHumanDetectEnable(boolean enable);
        boolean isShowTrack();
        void setShowTrack(boolean isShow);
        int getRuleType();
        void setRuleType(int ruleType);
        HumanDetectionBean getHumanDetection();
        void setHumanDetection(HumanDetectionBean humanDetection);
        boolean isRuleEnable();
        void setRuleEnable(boolean enable);
		boolean isTrackSupport();
        boolean isLineSupport();
        boolean isAreaSupport();
        boolean isTrackDetectEnable();
    }
}
