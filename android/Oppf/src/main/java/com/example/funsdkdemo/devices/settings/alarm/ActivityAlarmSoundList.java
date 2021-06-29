package com.example.funsdkdemo.devices.settings.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basic.G;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.DetectMotion;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.AbilityVoiceTip;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.MotionDetectIPC;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityAlarmSoundList extends ActivityDemo implements View.OnClickListener,
        IFunSDKResult, AdapterView.OnItemClickListener, OnFunDeviceOptListener {
    private TextView mTextTitle;
    private ImageButton mBtnBack;
    private ImageButton mBtnSave;
    private RelativeLayout mRlAlarmVoiceSwitch;
    private ImageView mIvAlarmVoiceSwitch;
    private ListView mListView;
    private Button mBtnCustomVoice;


    private int mUserId;
    private boolean mIsIPC = false;// IPC； NVR
    private FunDevice mFunDevice;
    private DetectMotion mDetectMotion;
    private MotionDetectIPC mMotionDetectIPC;
    private List<AbilityVoiceTip.VoiceTip> mVoiceTips = new ArrayList<>();
    private AlarmVoiceAdapter mAdapter;
    private int mLastSelect;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_sound_list);

        mTextTitle = findViewById(R.id.textViewInTopLayout);

        mBtnBack = findViewById(R.id.backBtnInTopLayout);
        mBtnBack.setOnClickListener(this);

        mTextTitle.setText(R.string.alarm_sound);

        mRlAlarmVoiceSwitch = findViewById(R.id.rl_alarm_sound_switch);
        mIvAlarmVoiceSwitch = findViewById(R.id.iv_alarm_sound_switch);
        mIvAlarmVoiceSwitch.setOnClickListener(this);

        mBtnSave = (ImageButton) setNavagateRightButton(R.layout.imagebutton_save);
        mBtnSave.setOnClickListener(this);

        mListView = findViewById(R.id.listView);
        mBtnCustomVoice = findViewById(R.id.btn_custom_sound);
        mBtnCustomVoice.setOnClickListener(this);

        mAdapter = new AlarmVoiceAdapter(this, mVoiceTips);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        FunSupport.getInstance().registerOnFunDeviceOptListener(this);

        initData();
    }

    private void initData() {
        mUserId = FunSDK.GetId(mUserId,this);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        Bundle bundle = intent.getBundleExtra("data");
        if (bundle == null) {
            finish();
            return;
        }
        int devId = bundle.getInt("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(devId);
        if (null == mFunDevice) {
            finish();
            return;
        }
        mIsIPC = bundle.getBoolean("isIPC");
        showWaitDialog();
        if (mIsIPC) {
            mDetectMotion = (DetectMotion) mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
            //设置设备语言
            setDevLanguage();
        } else {
            //获取NVR当前选中的报警声类型
            FunSDK.DevGetConfigByJson(mUserId, mFunDevice.getDevSn(),
                    JsonConfig.DETECT_MOTION_DETECT_IPC, 4096, mFunDevice.CurrChannel, 5000, 0);
        }
    }

    /**
     * 设置设备语言
     * IPC 和 NVR 命令相同
     */
    private void setDevLanguage() {
        String language = Locale.getDefault().getLanguage();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", JsonConfig.BROWSER_LANGUAGE);
            JSONObject object = new JSONObject();
            jsonObject.put(JsonConfig.BROWSER_LANGUAGE, object);
            object.put("BrowserLanguageType", getLanguageType(language.contains("zh") ? "SimpChinese" : "English"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FunSDK.DevSetConfigByJson(mUserId, mFunDevice.getDevSn(),
                JsonConfig.BROWSER_LANGUAGE, jsonObject.toString(), -1, 5000, 0);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backBtnInTopLayout) {
            finish();
        } else if (id == R.id.iv_alarm_sound_switch) {
            mIvAlarmVoiceSwitch.setSelected(!mIvAlarmVoiceSwitch.isSelected());
        } else if (id == R.id.btnSave) {
            int voiceType = -1;
            for (int i = 0; i < mVoiceTips.size(); i++) {
                AbilityVoiceTip.VoiceTip voiceTip = mVoiceTips.get(i);
                if (voiceTip.selected) {
                    voiceType = voiceTip.VoiceEnum;
                    break;
                }
            }
            if (voiceType == -1) {
                return;
            }
            if (mIsIPC) {
                if (mDetectMotion != null) {
                    showWaitDialog();
                    mDetectMotion.event.VoiceType = voiceType;
                    mDetectMotion.event.VoiceEnable = mIvAlarmVoiceSwitch.isSelected();
                    FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, mDetectMotion);
                }
            } else {
                if (mMotionDetectIPC != null) {
                    showWaitDialog();
                    mMotionDetectIPC.setVoiceType(voiceType);
                    mMotionDetectIPC.setTipEnable(mIvAlarmVoiceSwitch.isSelected());
                    FunSDK.DevSetConfigByJson(mUserId, mFunDevice.getDevSn(), JsonConfig.DETECT_MOTION_DETECT_IPC,
                            mMotionDetectIPC.getSendMsg(), mFunDevice.CurrChannel, 5000, 0);
                }
            }
        } else if (id == R.id.btn_custom_sound) {
            Intent intent = new Intent(this, ActivityCustomAlarmSound.class);
            intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
            intent.putExtra("isIPC", mIsIPC);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AbilityVoiceTip.VoiceTip voiceTip = mVoiceTips.get(i);
        mVoiceTips.get(mLastSelect).selected = false;
        mLastSelect = i;
        voiceTip.selected = true;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_GET_JSON:
                if (message.arg1 < 0) {
                    hideWaitDialog();
                    showToast(FunError.getErrorStr(message.arg1));
                } else {
                    if (JsonConfig.DETECT_MOTION_DETECT_IPC.equals(msgContent.str)) {
                        mMotionDetectIPC = new MotionDetectIPC();
                        if (mMotionDetectIPC.onParse(G.ToString(msgContent.pData),
                                JsonConfig.DETECT_MOTION_DETECT_IPC, mFunDevice.CurrChannel)) {
                            //设置设备语言
                            setDevLanguage();
                        }
                    } else if (JsonConfig.ABILITY_VOICE_TIP_TYPE.equals(msgContent.str)) {
                        hideWaitDialog();
                        if (mVoiceTips.size() > 0) {
                            mVoiceTips.clear();
                        }
                        AbilityVoiceTip abilityVoiceTip = new AbilityVoiceTip();
                        //注意：NVR回调数据有.[0]，IPC回调没有
                        if (abilityVoiceTip.onParse(G.ToString(msgContent.pData),
                                JsonConfig.ABILITY_VOICE_TIP_TYPE, mFunDevice.CurrChannel)
                                || abilityVoiceTip.onParse(G.ToStringJson(msgContent.pData))) {
                            mVoiceTips.addAll(abilityVoiceTip.voiceTipList);
                            if (mIsIPC) {
                                //初始化IPC报警声开关
                                if (mDetectMotion != null) {
                                    mIvAlarmVoiceSwitch.setSelected(mDetectMotion.event.VoiceEnable);
                                    initVoiceList(mDetectMotion.event.VoiceType);
                                }
                            } else {
                                //初始化NVR的报警声开关
                                if (mMotionDetectIPC != null) {
                                    mIvAlarmVoiceSwitch.setSelected(mMotionDetectIPC.isVoiceEnable());
                                    initVoiceList(mMotionDetectIPC.getVoiceType());
                                }
                            }
                        }
                    }
                }
                break;
            case EUIMSG.DEV_SET_JSON:
                if (JsonConfig.BROWSER_LANGUAGE.equals(msgContent.str)) {
                    //获取报警声类型列表，IPC和NVR命令相同,NVR需要传通道
                    FunSDK.DevGetConfigByJson(mUserId, mFunDevice.getDevSn(), JsonConfig.ABILITY_VOICE_TIP_TYPE,
                            4096, mIsIPC ? -1 : mFunDevice.CurrChannel, 5000, 0);
                } else if (JsonConfig.DETECT_MOTION_DETECT_IPC.equals(msgContent.str)) {
                    hideWaitDialog();
                    if (message.arg1 < 0) {
                        showToast(FunError.getErrorStr(message.arg1));
                    } else {
                        showToast(getString(R.string.Save_Success));
                    }
                }
                break;
            default:
                break;
        }
        return 0;
    }

    private void initVoiceList(int selected) {
        for (int i = 0; i < mVoiceTips.size(); i++) {
            AbilityVoiceTip.VoiceTip voiceTip = mVoiceTips.get(i);
            if (selected == voiceTip.VoiceEnum) {
                voiceTip.selected = true;
                mLastSelect = i;
            } else {
                voiceTip.selected = false;
            }
            if (voiceTip.VoiceEnum == 550) {
                mBtnCustomVoice.setVisibility(View.VISIBLE);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设备支持的语言，枚举
     * "English", // 0
     * "SimpChinese", // 1
     * "TradChinese", // 2
     * "Italian", // 3
     * "Spanish", // 4
     * "Japanese", // 5
     * "Russian",  // 6
     * "French",  // 7
     * "German",  // 8
     * "Portugal", // 9
     * "Turkey",  // 10
     * "Poland",
     * "Romanian",
     * "Hungarian",
     * "Finnish",
     * "Estonian",
     * "Korean",  // 16
     * "Farsi",
     * "Dansk",
     * "Thai",
     * "Greek",
     * "Vietnamese",
     * "Ukrainian",
     * "Brazilian",
     * "Hebrew",
     * "Indonesian",
     * "Arabic",
     * "Swedish",
     * "Czech",
     * "Bulgarian",
     * "Slovakia",
     * "Dutch",
     * "Serbian",
     * "Croatian",
     * "Azerbaycan",
     */
    public static int getLanguageType(String name) {
        switch (name) {
            case "English":
                return 0;
            case "SimpChinese":
                return 1;
            case "TradChinese":
                return 2;
            case "Italian":
                return 3;
            case "Spanish":
                return 4;
            case "Japanese":
                return 5;
            case "Russian":
                return 6;
            case "French":
                return 7;
            case "German":
                return 8;
            case "Portugal":
                return 9;
            case "Turkey":
                return 10;
            case "Korean":
                return 16;
            default:
                break;
        }
        return 0;
    }

    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
        hideWaitDialog();
        if (funDevice != null && funDevice.getId() == mFunDevice.getId()) {
            if (DetectMotion.CONFIG_NAME.equals(configName)) {
                showToast(getString(R.string.Save_Success));
//                finish();
            }
        }
    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
        hideWaitDialog();
        if (funDevice != null && funDevice.getId() == mFunDevice.getId()) {
            if (DetectMotion.CONFIG_NAME.equals(configName)) {
                showToast(FunError.getErrorStr(errCode));
            }
        }
    }

    @Override
    public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

    }

    @Override
    public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }

    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    }
}
