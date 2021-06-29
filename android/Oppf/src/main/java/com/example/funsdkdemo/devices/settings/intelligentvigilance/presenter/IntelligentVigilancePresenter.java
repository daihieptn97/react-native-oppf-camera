package com.example.funsdkdemo.devices.settings.intelligentvigilance.presenter;

import android.os.Message;

import com.basic.G;
import com.example.funsdkdemo.devices.settings.intelligentvigilance.constract.IntelligentVigilanceConstract;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;

import com.lib.sdk.bean.ChannelHumanRuleLimitBean;
import com.lib.sdk.bean.DetectTrackBean;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.HumanDetectionBean;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;

import static com.lib.sdk.bean.HumanDetectionBean.IA_TRIPWIRE;


/**
 * @author hws
 * @name XMEye_Android
 * @class name：com.mobile.myeye.setting.humandetect.presenter
 * @class 人形检测
 * @time 2019-05-06 15:59
 */
public class IntelligentVigilancePresenter implements IntelligentVigilanceConstract.IHumanDetectPresenter,IFunSDKResult {
    private HumanDetectionBean humanDetectionBean;
    private ChannelHumanRuleLimitBean channelHumanRuleLimitBean;
    private DetectTrackBean detectTrackBean;
    private IntelligentVigilanceConstract.IHumanDetectView iHumanDetectView;
    private int userId;
    private String devId;
    public IntelligentVigilancePresenter(String devId,IntelligentVigilanceConstract.IHumanDetectView iHumanDetectView) {
        this.devId = devId;
        this.iHumanDetectView = iHumanDetectView;
        userId = FunSDK.GetId(userId,this);
    }

    @Override
    public void updateHumanDetect() {
        FunSDK.DevCmdGeneral(userId,devId,1360,
                JsonConfig.HUMAN_RULE_LIMIT,
                -1,5000,null,-1,0);
    }

    @Override
    public void saveHumanDetect() {
        if (humanDetectionBean == null) {
            return;
        }
        FunSDK.DevSetConfigByJson(userId,devId,JsonConfig.DETECT_HUMAN_DETECTION,
                HandleConfigData.getSendData(HandleConfigData.getFullName(JsonConfig.DETECT_HUMAN_DETECTION,
                        0),"0x08",humanDetectionBean),
                0,5000,0);
    }

    @Override
    public boolean isHumanDetectEnable() {
        return humanDetectionBean != null ? humanDetectionBean.isEnable() : false;
    }

    @Override
    public void setHumanDetectEnable(boolean enable) {
        if (humanDetectionBean != null) {
            humanDetectionBean.setEnable(enable);
        }
    }

    @Override
    public boolean isShowTrack() {
        return humanDetectionBean != null ? humanDetectionBean.isShowTrack() : false;
    }

    @Override
    public void setShowTrack(boolean isShow) {
        if (humanDetectionBean != null) {
            humanDetectionBean.setShowTrack(isShow);
        }
    }

    @Override
    public int getRuleType() {
        if (humanDetectionBean != null) {
            if (!humanDetectionBean.getPedRules().isEmpty()) {
                return humanDetectionBean.getPedRules().get(0).getRuleType();
            }
        }
        return IA_TRIPWIRE;
    }

    @Override
    public void setRuleType(int ruleType) {
        if (humanDetectionBean != null && !humanDetectionBean.getPedRules().isEmpty()) {
            humanDetectionBean.getPedRules().get(0).setRuleType(ruleType);
        }
    }

    @Override
    public HumanDetectionBean getHumanDetection() {
        return humanDetectionBean;
    }

    @Override
    public void setHumanDetection(HumanDetectionBean humanDetection) {
        this.humanDetectionBean = humanDetection;
    }

    @Override
    public boolean isRuleEnable() {
        return humanDetectionBean != null ? humanDetectionBean.getPedRules().get(0).isEnable() : false;
    }

    @Override
    public void setRuleEnable(boolean enable) {
        if (humanDetectionBean != null) {
            humanDetectionBean.getPedRules().get(0).setEnable(enable);
        }
    }

    @Override
    public boolean isTrackSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isShowTrack();
        }
        return false;
    }

    @Override
    public boolean isLineSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isSupportLine();
        }
        return false;
    }

    @Override
    public boolean isAreaSupport() {
        if (channelHumanRuleLimitBean != null) {
            return channelHumanRuleLimitBean.isSupportArea();
        }
        return false;
    }

    @Override
    public boolean isTrackDetectEnable() {
        if (detectTrackBean != null) {
            return detectTrackBean.getEnable() == SDKCONST.Switch.Open;
        }
        return false;
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        if (message.arg1 < 0) {
            System.out.println("获取配置失败");
            return 0;
        }
        if (message.what == EUIMSG.DEV_GET_JSON) {
            if (StringUtils.contrast(msgContent.str, JsonConfig.DETECT_HUMAN_DETECTION)) {
                if (msgContent.pData != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(G.ToString(msgContent.pData), HumanDetectionBean.class)) {
                        humanDetectionBean = (HumanDetectionBean) handleConfigData.getObj();
                        if (humanDetectionBean != null && iHumanDetectView != null) {
                            iHumanDetectView.updateHumanDetectResult(true);
                            return 0;
                        }
                    }
                }
                if (iHumanDetectView != null) {
                    iHumanDetectView.updateHumanDetectResult(false);
                }
            }else if (StringUtils.contrast(msgContent.str,JsonConfig.CFG_DETECT_TRACK)) {
                if (msgContent.pData != null) {
                    HandleConfigData handleConfigData = new HandleConfigData();
                    if (handleConfigData.getDataObj(G.ToString(msgContent.pData), DetectTrackBean.class)) {
                        detectTrackBean = (DetectTrackBean) handleConfigData.getObj();
                    }
                }
            }
        }else if (message.what == EUIMSG.DEV_SET_JSON) {
            if (StringUtils.contrast(msgContent.str, JsonConfig.DETECT_HUMAN_DETECTION)) {
                if (iHumanDetectView != null) {
                    iHumanDetectView.saveHumanDetectResult(true);
                }
            }
        }else if (message.what == EUIMSG.DEV_CMD_EN) {
            if(StringUtils.contrast(msgContent.str,JsonConfig.HUMAN_RULE_LIMIT)) {
                HandleConfigData handleConfigData = new HandleConfigData();
                if(handleConfigData.getDataObj(G.ToString(msgContent.pData),ChannelHumanRuleLimitBean.class)) {
                    this.channelHumanRuleLimitBean = (ChannelHumanRuleLimitBean) handleConfigData.getObj();
                    FunSDK.DevGetConfigByJson(userId, devId, JsonConfig.DETECT_HUMAN_DETECTION,
                            4096,0,5000,0);
                    FunSDK.DevGetConfigByJson(userId,devId,JsonConfig.CFG_DETECT_TRACK,
                            4096,-1,5000,0);
                }
            }
        }
        return 0;
    }

    public ChannelHumanRuleLimitBean getChannelHumanRuleLimitBean() {
        return channelHumanRuleLimitBean;
    }

}
