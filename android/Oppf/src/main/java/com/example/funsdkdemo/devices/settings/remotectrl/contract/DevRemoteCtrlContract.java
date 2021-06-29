package com.example.funsdkdemo.devices.settings.remotectrl.contract;

import android.content.Context;
import android.view.ViewGroup;

import com.lib.funsdk.support.models.FunDevice;

/**
 * @author hws
 * @class describe
 * @time 2019-11-22 9:39
 */
public class DevRemoteCtrlContract {
    /**
     * 鼠标控制
     */
    public enum MOUSE_CTRL {
        /**
         * 移动
         */
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        /**
         * 左击
         */
        LEFT_DOWN,
        /**
         * 右击
         */
        RIGHT_DOWN,
        /**
         * 左双击
         */
        LEFT_DOUBLE_DOWN,
        /**
         * 退出
         */
        ESC
    }

    public interface IDevRemoteCtrlView {
        Context getContext();
        void onCheckSupportResult(boolean isSupport);
    }

    public interface IDevRemoteCtrlPresenter {
        FunDevice getFunDevice();
        void setFunDevice(FunDevice funDevice);
        void checkIsSupport();
        void initPlayer(ViewGroup playView);
        void ctrlMouse(MOUSE_CTRL mouseCtrl, boolean isDown);
    }
}
