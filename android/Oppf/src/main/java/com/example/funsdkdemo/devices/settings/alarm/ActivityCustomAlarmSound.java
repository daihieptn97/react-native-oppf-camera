package com.example.funsdkdemo.devices.settings.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.example.funsdkdemo.manager.AudioPlayManager;
import com.example.funsdkdemo.manager.RecordingManager;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.OPFileBean;

import java.nio.ByteBuffer;

public class ActivityCustomAlarmSound extends ActivityDemo implements IFunSDKResult, View.OnClickListener,
        RecordingManager.OnRecordingListener, AudioPlayManager.OnAudioPlayListener {
    private static final int RECORD_MAX_TIME = 3; //录音时长(录音文件大小有限制，这里录音时长设置为最长3秒)
    private static final int SEND_AUDIO_DATE_RATE = 32 * 1024;
    private ImageView mIvRecord;
    private TextView mTvRecordTime;
    private Button mBtnPlay;
    private Button mBtnUpload;

    private int mUserId;
    private FunDevice mFunDevice;
    private boolean mIsIPC;
    private RecordingManager mRecordingManager;
    private AudioPlayManager mAudioPlayManager;
    private ByteBuffer mData;
    private int mDataSize;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_alarm_sound);
        TextView textTitle = findViewById(R.id.textViewInTopLayout);
        textTitle.setText(R.string.record_sound);

        ImageButton btnBack = findViewById(R.id.backBtnInTopLayout);
        btnBack.setOnClickListener(this);

        mIvRecord = findViewById(R.id.iv_record);
        mIvRecord.setOnClickListener(this);
        mTvRecordTime = findViewById(R.id.tv_record_time_length);
        mBtnPlay = findViewById(R.id.btn_audition);
        mBtnPlay.setOnClickListener(this);
        mBtnUpload = findViewById(R.id.btn_upload);
        mBtnUpload.setOnClickListener(this);

        setAuditionUploadEnable(false);
        initData();
    }

    private void initData() {
        mUserId = FunSDK.GetId(mUserId, this);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        int devId = intent.getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(devId);
        if (null == mFunDevice) {
            finish();
            return;
        }
        mIsIPC = intent.getBooleanExtra("isIPC", false);
        mRecordingManager = new RecordingManager(this, RECORD_MAX_TIME);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backBtnInTopLayout) {
            finish();
        } else if (id == R.id.iv_record) {
            mIvRecord.setSelected(!mIvRecord.isSelected());
            if (mIvRecord.isSelected()) {
                //录音
                if (mData != null) {
                    mData.clear();
                    mData = null;
                    mDataSize = 0;
                }
                if (mRecordingManager != null) {
                    mRecordingManager.startRecording();
                }
            } else {
                //停止录音
                if (mRecordingManager != null) {
                    mRecordingManager.stopRecording();
                }
            }
        } else if (id == R.id.btn_audition) {//试听录音
            if (mData != null && mDataSize > 0) {
                if (mAudioPlayManager == null) {
                    mAudioPlayManager = new AudioPlayManager(mData, mDataSize, this);
                }
                mAudioPlayManager.startPlay();
            }
        } else if (id == R.id.btn_upload) {//上传录音
            if (mData != null && mDataSize > 0) {
                if (mIsIPC) {
                    showWaitDialog();
                    //*录音文件大小 fileSize 是G711a格式的文件大小 IPC最大64K，NVR最大42K
                    //*设备保存录音文件的格式是G711a，APP直接发送的是原始录音pcm格式，sdk做了转换, pcm转G711a文件大小变为原来的1/2
                    //*录音文件必须16字节对齐
                    OPFileBean opFileBean = new OPFileBean();
                    opFileBean.setFileType(OPFileBean.MEDIA_TYPE_AUDIO);
                    opFileBean.setFileSize(mDataSize / 2);
                    opFileBean.setFileName("customAlarmVoice.pcm");
                    opFileBean.setFileNumber(0);
                    opFileBean.setAction("Send");
                    OPFileBean.Parameter.AudioFormat audioFormat = opFileBean.getParameter().getAudioFormat();
                    audioFormat.setBitRate(128);
                    audioFormat.setSampleBit(8);
                    audioFormat.setSampleRate(8000);
                    JSONObject sendObj = new JSONObject();
                    sendObj.put("OPFile", opFileBean);
                    FunSDK.DevStartFileTransfer(mUserId, mFunDevice.getDevSn(), JSON.toJSONString(sendObj), 5000, 0);
                } else {
                    //NVR设备选择通道发送
                    mFunDevice.voiceData = mData;
                    mFunDevice.voiceDataSize = mDataSize;
                    Intent intent = new Intent(this, ActivitySelectChannel.class);
                    intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onRecording(final int time) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setAuditionUploadEnable(false);
                mTvRecordTime.setText(formatTimes(time));
            }
        });
    }

    @Override
    public void onComplete(final ByteBuffer audioData, final int dataSize) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mIvRecord.setSelected(false);
                setAuditionUploadEnable(true);
            }
        });
        if (mRecordingManager != null) {
            mRecordingManager.stopRecording();
        }
        mData = audioData;
        mDataSize = dataSize;
    }

    @Override
    public void onPlayTime(final int time) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTvRecordTime.setText(formatTimes(time));
            }
        });
    }

    @Override
    public void onPlayCompleted() {
        mAudioPlayManager = null;
    }

    private void setAuditionUploadEnable(boolean enable) {
        mBtnPlay.setEnabled(enable);
        mBtnUpload.setEnabled(enable);
    }

    public String formatTimes(int seconds) {
        int MM = seconds / 60;
        int HH = MM / 60;
        int SS = seconds % 60;
        if (HH == 0 && MM == 0) {
            return "00:" + String.format("%02d", SS);
        }
        if (HH == 0) {
            return String.format("%02d:%02d", MM, SS);
        } else {
            MM -= HH * 60;
            return String.format("%02d:%02d:%02d", HH, MM, SS);
        }
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            //开始
            case EUIMSG.DEV_START_FILE_TRANSFER:
                if (message.arg1 >= 0) {
                    sendAudioDataToDev(0);
                } else {
                    showToast(FunError.getErrorStr(message.arg1));
                }
                break;
            //传数据
            case EUIMSG.DEV_FILE_DATA_TRANSFER:
                if (message.arg1 >= 0) {
                    if (msgContent.seq != -1) {
                        sendAudioDataToDev(msgContent.seq);
                    } else {
                        hideWaitDialog();
                        showToast(getString(R.string.upload_success));
                    }
                } else {
                    hideWaitDialog();
                    showToast(FunError.getErrorStr(message.arg1));
                }
                break;
            //停止传输
            case EUIMSG.DEV_STOP_FILE_TRANSFER:
                break;
            default:
                break;
        }
        return 0;
    }

    private void sendAudioDataToDev(int alreadySendLength) {
        byte[] sendData;
        int length;
        synchronized (mData) {
            //单次发送数据最大32K
            if (mData.position() + SEND_AUDIO_DATE_RATE <= mDataSize) {
                length = SEND_AUDIO_DATE_RATE;
            } else {
                length = mDataSize - mData.position();
            }
            sendData = new byte[length];
            mData.get(sendData, 0, length);
            boolean isEndData = mData.position() >= mDataSize;
            alreadySendLength += length;
            //最后一次传输，标志位置为1
            FunSDK.DevFileDataTransfer(mUserId, mFunDevice.getDevSn(), sendData, isEndData ? 1 : 0,
                    60 * 1000, isEndData ? -1 : alreadySendLength);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunSDK.UnRegUser(mUserId);
        if (mFunDevice != null && mFunDevice.voiceData != null) {
            mFunDevice.voiceData.clear();
            mFunDevice.voiceData = null;
            mFunDevice.voiceDataSize = 0;
        }
    }
}
