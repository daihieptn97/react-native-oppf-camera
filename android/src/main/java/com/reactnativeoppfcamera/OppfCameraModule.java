package com.reactnativeoppfcamera;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.google.gson.Gson;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.sdk.CameraSdk;

import java.lang.invoke.MethodHandle;

@ReactModule(name = OppfCameraModule.NAME)
public class OppfCameraModule extends ReactContextBaseJavaModule {
    public static final String NAME = "OppfCamera";
    public static final CameraSdk cameraSdk = new CameraSdk();
    private ReactApplicationContext reactContext;

    public OppfCameraModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    // Example method
    // See https://reactnative.dev/docs/native-modules-android
    @ReactMethod
    public void multiply(int a, int b, Promise promise) {
        promise.resolve(a * b);
    }

//    private void sendToRN(String eventName, String data) {
//        String eventSendToRN = "CameraConfigListener";
//        Log.d(CameraSdk.TAG_DEBUG, "send to RN " + eventName + " " + data);
//        // Create map for params
//        WritableMap payload = Arguments.createMap();
//        // Put data to map
//        payload.putString("eventName", eventName);
//        payload.putString("data", data);
//        // Get EventEmitter from context and send event thanks to it
//        reactContext
//            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//            .emit(eventSendToRN, payload);
//    }

    @ReactMethod
    public void registerOnFunDeviceWiFiConfigListener(Callback callback) {
        cameraSdk.registerOnFunDeviceWiFiConfigListenerSdk(new OnFunDeviceWiFiConfigListener() {
            @Override
            public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
//                sendToRN("onDeviceWiFiConfigSetted", new Gson().toJson(funDevice));
//                callback.invoke(new Gson().toJson(funDevice));
            }
        });
    }

    @ReactMethod
    public void onSmartConfig(String wifiName, String passWifi, Callback callback) {
        Log.d("DEBUG123123", "iouasdhyasfhyadksfhukas dfldsafadsfadsifadshifhads;fadsfhildsf");


        cameraSdk.startSmartConfig(reactContext, passWifi, wifiName, new OnFunDeviceWiFiConfigListener() {
            @Override
            public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
//                callback.invoke(new Gson().toJson(funDevice));
//                Log.d(CameraSdk.TAG_DEBUG, new Gson().toJson(funDevice).toString());
            }
        });
    }

    public static native int nativeMultiply(int a, int b);
}
