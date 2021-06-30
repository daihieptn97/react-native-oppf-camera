package com.hunonic.funsdkdemo.devices.monitor;

import com.hunonic.funsdkdemo.ActivityGuide;
import com.hunonic.funsdkdemo.ActivityGuideDeviceListAP;
import com.hunonic.funsdkdemo.ActivityGuideDeviceListLan;
import com.hunonic.funsdkdemo.ActivityGuideDeviceSNLogin;
import com.hunonic.funsdkdemo.DemoModule;
import com.hunonic.funsdkdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-24.
 */

public class ActivityGuideMediaRealPlay extends ActivityGuide{

    private static List<DemoModule> mMoudules = new ArrayList<DemoModule>();

    static {
        // 2.1 连接设备(通过序列号连接)
        mMoudules.add(new DemoModule(-1,
                R.string.guide_module_title_device_sn,
                -1,
                ActivityGuideDeviceSNLogin.class));

        // 2.2 连接设备(附近AP直连)
        mMoudules.add(new DemoModule(-1,
                R.string.guide_module_title_device_ap,
                -1,
                ActivityGuideDeviceListAP.class));

        // 2.3 连接设备(局域网内)
        mMoudules.add(new DemoModule(-1,
                R.string.guide_module_title_device_lan,
                -1,
                ActivityGuideDeviceListLan.class));
    }
    @Override
    protected List<DemoModule> getGuideModules() {
        return mMoudules;
    }
}
