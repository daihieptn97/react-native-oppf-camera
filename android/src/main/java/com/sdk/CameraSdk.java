package com.sdk;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.hunonic.funsdkdemo.R;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.FunWifiPassword;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.utils.DeviceWifiManager;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.funsdk.support.utils.StringUtils;

public class CameraSdk {


    public static final String TAG_DEBUG = "DEBUG123123";

    public void registerOnFunDeviceWiFiConfigListenerSdk(OnFunDeviceWiFiConfigListener l) {
        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(l);
    }


    public void startSmartConfig(Context context, String wifiPwd, String wifiName, OnFunDeviceWiFiConfigListener l) {
        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(l);
        try {
            WifiManager wifiManage = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManage.getConnectionInfo();
            DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

            if (null == wifiInfo) {
                Log.d(TAG_DEBUG, String.valueOf(R.string.device_opt_set_wifi_info_error));
                return;
            }

            String ssid = wifiInfo.getSSID().replace("\"", "");

            Log.d(TAG_DEBUG, ssid);

            if (StringUtils.isStringNULL(ssid)) {
                Log.d(TAG_DEBUG, String.valueOf(R.string.device_opt_set_wifi_info_error));
                return;
            }

            ScanResult scanResult = DeviceWifiManager.getInstance(context).getCurScanResult(ssid);
            if (null == scanResult) {
                Log.d(TAG_DEBUG, String.valueOf(R.string.device_opt_set_wifi_info_error));
                return;
            }
            Log.d(TAG_DEBUG, scanResult.SSID);

            int pwdType = MyUtils.getEncrypPasswordType(scanResult.capabilities);

            if (pwdType != 0 && StringUtils.isStringNULL(wifiPwd)) {
                Log.d(TAG_DEBUG, String.valueOf(R.string.device_opt_set_wifi_info_error));
                return;
            }

            StringBuffer data = new StringBuffer();
            data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);

            Log.d(TAG_DEBUG, data.toString());

            String submask = "255.255.255.0";
            if (wifiDhcp.netmask == 0) {
                submask = "255.255.255.0";
            } else {
                submask = MyUtils.formatIpAddress(wifiDhcp.netmask);
            }


            Log.d(TAG_DEBUG, submask.toString());

            String mac = wifiInfo.getMacAddress();
            StringBuffer info = new StringBuffer();


            info.append("gateway:").append(MyUtils.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
                    .append(" dns1:").append(MyUtils.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
                    .append(MyUtils.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
                    .append(" ");


            Log.d(TAG_DEBUG, info.toString());

            FunSupport.getInstance().startWiFiQuickConfig(ssid,
                    data.toString(), info.toString(),
                    MyUtils.formatIpAddress(wifiDhcp.gateway),
                    pwdType, 0, mac, -1);

            FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopSmartConfig() {
        FunSupport.getInstance().stopWiFiQuickConfig();
    }


}
