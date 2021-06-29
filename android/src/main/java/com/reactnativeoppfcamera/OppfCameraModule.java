package com.reactnativeoppfcamera;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = OppfCameraModule.NAME)
public class OppfCameraModule extends ReactContextBaseJavaModule {
    public static final String NAME = "OppfCamera";
//    public static final CameraSdk cameraSdk = new CameraSdk();
    private ReactApplicationContext reactContext;

    public OppfCameraModule(ReactApplicationContext reactContext) {
        super(reactContext);
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
//        cameraSdk.registerOnFunDeviceWiFiConfigListenerSdk(new OnFunDeviceWiFiConfigListener() {
//            @Override
//            public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
////                sendToRN("onDeviceWiFiConfigSetted", new Gson().toJson(funDevice));
////                callback.invoke(new Gson().toJson(funDevice));
//            }
//        });
    }

    @ReactMethod
    public void onSmartConfig(String wifiName, String passWifi) {
//        cameraSdk.startSmartConfig(reactContext, passWifi, wifiName);
    }

    public static native int nativeMultiply(int a, int b);
}
