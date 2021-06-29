package com.example.funsdkdemo.devices.settings.timingsleep.persenter;

/**
 * Created by hws on 2018-07-05.
 */

public interface ITimingSleepPersenter {
    void getConfig();
    void saveConfig();
    void setSleepSwitch(boolean isOpen);
    void setSleepTime(int[] startTime, int[] endTime);
    void setRepeat(boolean isRepeat);
}
