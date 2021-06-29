package com.example.funsdkdemo.devices.settings.timingsleep.view;

import android.app.Activity;

/**
 * Created by hws on 2018-07-05.
 */

public interface ITimingSleepView {
    Activity getActivity();
    void updateSleepSwitch(boolean isOpen);
    void updateSleepTime(int[] startTime, int[] endTime);
    void updateSleepRepeat(boolean isRepeat);
}
