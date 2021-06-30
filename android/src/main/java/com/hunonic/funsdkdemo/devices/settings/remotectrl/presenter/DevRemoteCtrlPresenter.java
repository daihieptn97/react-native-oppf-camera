package com.hunonic.funsdkdemo.devices.settings.remotectrl.presenter;

import android.os.Message;
import android.view.ViewGroup;

import com.basic.G;
import com.hunonic.funsdkdemo.devices.settings.remotectrl.contract.DevRemoteCtrlContract;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.HandleConfigData;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.OPRemoteCtrlBean;
import com.lib.sdk.bean.SystemFunctionBean;
import com.lib.sdk.bean.VideoOutBean;

/**
 * @author hws
 * @class describe
 * @time 2019-11-22 9:39
 */
public class DevRemoteCtrlPresenter implements
        DevRemoteCtrlContract.IDevRemoteCtrlPresenter, IFunSDKResult {
    private int userId;
    private int wndWidth;
    private int wndHeight;
    private boolean isKeepMove;
    private FunDevice funDevice;
    private VideoOutBean videoOutBean;
    private OPRemoteCtrlBean opRemoteCtrlBean;
    private DevRemoteCtrlContract.IDevRemoteCtrlView iDevRemoteCtrlView;
    public DevRemoteCtrlPresenter( DevRemoteCtrlContract.IDevRemoteCtrlView iDevRemoteCtrlView) {
        this.iDevRemoteCtrlView = iDevRemoteCtrlView;
        userId = FunSDK.GetId(userId,this);
    }

    @Override
    public FunDevice getFunDevice() {
        return funDevice;
    }

    @Override
    public void setFunDevice(FunDevice funDevice) {
        this.funDevice = funDevice;
    }

    @Override
    public void checkIsSupport() {
        FunSDK.DevGetConfigByJson(userId, funDevice.getDevSn(), JsonConfig.SYSTEM_FUNCTION, 4096, -1, 8000, 0);
    }

    @Override
    public void initPlayer(ViewGroup playView) {
        if (iDevRemoteCtrlView.getContext() == null || playView == null) {
            return;
        }

        wndWidth = playView.getWidth();
        wndHeight = playView.getHeight();
    }

    @Override
    public void ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL mouseCtrl, boolean isDown) {
        synchronized (opRemoteCtrlBean) {
            isKeepMove = isDown;
        }

        switch (mouseCtrl) {
            case MOVE_UP:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(OPRemoteCtrlBean.RemoteCtrlEventType.XM_MOUSEMOVE);
                opRemoteCtrlBean.setPosition(0,-5);
                break;
            case MOVE_DOWN:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(OPRemoteCtrlBean.RemoteCtrlEventType.XM_MOUSEMOVE);
                opRemoteCtrlBean.setPosition(0,5);
                break;
            case MOVE_LEFT:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(OPRemoteCtrlBean.RemoteCtrlEventType.XM_MOUSEMOVE);
                opRemoteCtrlBean.setPosition(-5,0);
                break;
            case MOVE_RIGHT:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(OPRemoteCtrlBean.RemoteCtrlEventType.XM_MOUSEMOVE);
                opRemoteCtrlBean.setPosition(5,0);
                break;
            case LEFT_DOWN:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(isDown ? OPRemoteCtrlBean.RemoteCtrlEventType.XM_LBUTTONDOWN
                        : OPRemoteCtrlBean.RemoteCtrlEventType.XM_LBUTTONUP);
                break;
            case RIGHT_DOWN:
                opRemoteCtrlBean.setParameter("0x2");
                opRemoteCtrlBean.setActionEvent(isDown ? OPRemoteCtrlBean.RemoteCtrlEventType.XM_RBUTTONDOWN
                        : OPRemoteCtrlBean.RemoteCtrlEventType.XM_RBUTTONUP);
                break;
            case LEFT_DOUBLE_DOWN:
                break;
            case ESC:
                opRemoteCtrlBean.setActionEvent(isDown ? OPRemoteCtrlBean.RemoteCtrlEventType.XM_KEYDOWN
                        : OPRemoteCtrlBean.RemoteCtrlEventType.XM_KEYUP);
                opRemoteCtrlBean.setParameter("0x1B");
                break;
            default:
                break;
        }

        sendCtrlMouseToDev();
    }

    private void sendCtrlMouseToDev() {
        String jsonData = HandleConfigData.getSendData(OPRemoteCtrlBean.JSON_NAME,
                "0x08",opRemoteCtrlBean);
        FunSDK.DevCmdGeneral(userId, funDevice.getDevSn(), OPRemoteCtrlBean.CMD_ID,
                OPRemoteCtrlBean.JSON_NAME, 4096, 5000,jsonData.getBytes(), -1,0);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        if (message.arg1 < 0) {
            iDevRemoteCtrlView.onCheckSupportResult(false);
        }

        switch (message.what) {
            case EUIMSG.DEV_GET_JSON:
                if (JsonConfig.SYSTEM_FUNCTION.equals(msgContent.str)) {
                    HandleConfigData<SystemFunctionBean> handleConfigData = new HandleConfigData<>();
                    if (handleConfigData.getDataObj(G.ToStringJson(msgContent.pData), SystemFunctionBean.class)) {
                        SystemFunctionBean systemFunction = handleConfigData.getObj();
                        if (systemFunction != null) {
                            iDevRemoteCtrlView.onCheckSupportResult(systemFunction.OtherFunction.SupportSysRemoteCtrl);
                            FunSDK.DevGetConfigByJson(userId, funDevice.getDevSn(), VideoOutBean.JSON_NAME, 4096, -1, 5000, 0);
                            break;
                        }
                    }

                    iDevRemoteCtrlView.onCheckSupportResult(false);
                }else if (VideoOutBean.JSON_NAME.equals(msgContent.str)) {
                    HandleConfigData<VideoOutBean> handleConfigData = new HandleConfigData<>();
                    if (handleConfigData.getDataObj(G.ToStringJson(msgContent.pData), VideoOutBean.class)) {
                        videoOutBean = handleConfigData.getObj();
                        if (videoOutBean != null) {
                            opRemoteCtrlBean = new OPRemoteCtrlBean();
                            opRemoteCtrlBean.setActionEvent(OPRemoteCtrlBean.RemoteCtrlEventType.XM_MOUSEMOVE);
                            opRemoteCtrlBean.initPosition(videoOutBean.getMode().getWidth(),
                                    videoOutBean.getMode().getHeight(),wndWidth / 2,wndHeight / 2);
                            sendCtrlMouseToDev();
                        }
                    }
                }
                break;
                case EUIMSG.DEV_CMD_EN:
                    if (OPRemoteCtrlBean.JSON_NAME.equals(msgContent.str)) {
                        synchronized (opRemoteCtrlBean) {
                            if (isKeepMove) {
                                opRemoteCtrlBean.keepMove();
                                sendCtrlMouseToDev();
                            }
                        }
                    }
                    break;
            default:
                break;
        }
        return 0;
    }
}
