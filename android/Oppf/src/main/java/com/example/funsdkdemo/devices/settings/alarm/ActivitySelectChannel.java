package com.example.funsdkdemo.devices.settings.alarm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.basic.G;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.lib.EFUN_ERROR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.DigitalHumanAbility;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.OPFileBean;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ActivitySelectChannel extends ActivityDemo implements View.OnClickListener,
        IFunSDKResult, OnFunDeviceOptListener, AdapterView.OnItemClickListener {
    private static final int SEND_AUDIO_DATE_RATE = 32 * 1024;
    private ListView mListView;
    private Button mBtnUpload;

    private int mUserId;
    private int mTransferHandle;
    private FunDevice mFunDevice;
    private ByteBuffer mData;
    private int mDataSize;
    private int[] mChannels;
    private ChannelAdapter mAdapter;
    private List<ChannelItem> mChannelItemList = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_channel);
        TextView textTitle = findViewById(R.id.textViewInTopLayout);
        textTitle.setText(R.string.select_channel);

        ImageButton btnBack = findViewById(R.id.backBtnInTopLayout);
        btnBack.setOnClickListener(this);

        mListView = findViewById(R.id.listView_channel);
        mBtnUpload = findViewById(R.id.btn_upload);
        mBtnUpload.setOnClickListener(this);

        mAdapter = new ChannelAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        initData();
    }

    private void initData() {
        mUserId = FunSDK.GetId(mUserId, this);
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        int devId = intent.getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(devId);
        if (mFunDevice == null || mFunDevice.voiceData == null
                || mFunDevice.voiceDataSize <= 0) {
            finish();
            return;
        }
        mData = mFunDevice.voiceData;
        mDataSize = mFunDevice.voiceDataSize;
        SystemInfo systemInfo = (SystemInfo) mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
        if (systemInfo == null) {
            FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
        } else {
            getChannelAbility(systemInfo);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backBtnInTopLayout) {
            finish();
        } else if (id == R.id.btn_upload) {
            int[] channels;
            int count = 0;
            for (int i = 0; i < mChannelItemList.size(); i++) {
                ChannelItem item = mChannelItemList.get(i);
                if (item.isSelect()) {
                    count++;
                }
            }
            channels = new int[count];
            int j = 0;
            for (int i = 0; i < mChannelItemList.size(); i++) {
                ChannelItem item = mChannelItemList.get(i);
                if (item.isSelect()) {
                    channels[j] = item.getChannel();
                    j++;
                }
            }
            if (channels.length <= 0) {
                showToast(getString(R.string.select_channel_tip));
                return;
            }
            if (mData != null) {
                mData.position(0);
            } else {
                return;
            }
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
            //发送NVR需要设置要发送的通道
            opFileBean.setChannel(channels);
            opFileBean.getParameter().setChannel(channels);
            OPFileBean.Parameter.AudioFormat audioFormat = opFileBean.getParameter().getAudioFormat();
            audioFormat.setBitRate(128);
            audioFormat.setSampleBit(8);
            audioFormat.setSampleRate(8000);
            JSONObject sendObj = new JSONObject();
            sendObj.put("OPFile", opFileBean);
            FunSDK.DevStartFileTransfer(mUserId, mFunDevice.getDevSn(), JSON.toJSONString(sendObj), 5000, 0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunSDK.UnRegUser(mUserId);
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        switch (message.what) {
            case EUIMSG.DEV_GET_JSON:
                if (msgContent.str.equals(JsonConfig.NET_DIGITAL_HUMAN_ABILITY)) {
                    DigitalHumanAbility digitalHumanAbility = new DigitalHumanAbility();
                    if (digitalHumanAbility.onParseArray(G.ToStringJson(msgContent.pData), JsonConfig.NET_DIGITAL_HUMAN_ABILITY)) {
                        List<DigitalHumanAbility> digitalHumanAbilityList = digitalHumanAbility.getDigitalHumanAbilityList();
                        //设置通道是否支持上传自定义报警音
                        for (int i = 0; i < digitalHumanAbilityList.size(); i++) {
                            DigitalHumanAbility ability = digitalHumanAbilityList.get(i);
                            ChannelItem item = mChannelItemList.get(i);
                            item.setEnable(ability.isSupportAlarmVoiceTipsType());
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
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
                        FunSDK.DevStopFileTransfer(mTransferHandle);
                    }
                } else {
                    if (message.arg1 == EFUN_ERROR.EE_MNETSDK_CHECK_FILE_SIZE) {
                        //提示哪些通道上传失败
                        String json = G.ToString(msgContent.pData);
                        try {
                            org.json.JSONObject object = new org.json.JSONObject(json);
                            if (object.has("OPFile")) {
                                org.json.JSONObject jsonObject = object.getJSONObject("OPFile");
                                if (jsonObject.has("Channel")) {
                                    JSONArray array = jsonObject.getJSONArray("Channel");
                                    showToast(getString(R.string.channel)
                                            + array.toString() + " " + getString(R.string.upload_failure));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideWaitDialog();
                            showToast(FunError.getErrorStr(message.arg1));
                        }
                    } else {
                        hideWaitDialog();
                        showToast(FunError.getErrorStr(message.arg1));
                    }
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
            mTransferHandle = FunSDK.DevFileDataTransfer(mUserId, mFunDevice.getDevSn(), sendData, isEndData ? 1 : 0,
                    60 * 1000, isEndData ? -1 : alreadySendLength);
        }
    }

    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        if (funDevice != null && funDevice.getId() == mFunDevice.getId()) {
            if (SystemInfo.CONFIG_NAME.equals(configName)) {
                SystemInfo systemInfoBean = (SystemInfo) mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
                getChannelAbility(systemInfoBean);
            }
        }
    }

    /**
     * 获取能力级
     */
    private void getChannelAbility(SystemInfo systemInfo) {
        int chnCount = mFunDevice.channel.nChnCount;
        mChannels = new int[chnCount - systemInfo.getVideoInChannel()];
        mChannelItemList.clear();
        for (int i = 0; i < mChannels.length; i++) {
            ChannelItem channelItem = new ChannelItem();
            mChannels[i] = systemInfo.getVideoInChannel() + i;
            channelItem.setChannel(mChannels[i]);
            if (mChannels[i] == mFunDevice.CurrChannel) {
                channelItem.setSelect(true);
            }
            mChannelItemList.add(channelItem);
        }
        mAdapter.notifyDataSetChanged();
        //通道参数传-1，返回的是所有通道的能力
        FunSDK.DevGetConfigByJson(mUserId, mFunDevice.getDevSn(),
                JsonConfig.NET_DIGITAL_HUMAN_ABILITY, 4096, -1, 5000, 0);
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
        if (funDevice.getId() == mFunDevice.getId()) {
            showToast(FunError.getErrorStr(errCode));
        }
    }

    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

    }

    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ChannelItem channelItem = mChannelItemList.get(position);
        if (channelItem.isEnable()) {
            channelItem.setSelect(!channelItem.isSelect());
            mAdapter.notifyDataSetChanged();
        }
    }

    public class ChannelAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mChannelItemList.size();
        }

        @Override
        public Object getItem(int i) {
            return mChannelItemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_alarm_voice, viewGroup, false);
                holder.relativeLayout = view.findViewById(R.id.layoutRoot);
                holder.ivSelect = view.findViewById(R.id.iv_select);
                holder.tvName = view.findViewById(R.id.tv_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ChannelItem channelItem = mChannelItemList.get(i);
            holder.tvName.setText(String.valueOf(channelItem.Channel + 1));
            if (channelItem.isEnable()) {
                holder.relativeLayout.setEnabled(true);
                holder.tvName.setTextColor(Color.BLACK);
            } else {
                holder.relativeLayout.setEnabled(false);
                holder.tvName.setTextColor(Color.LTGRAY);
            }
            if (channelItem.isSelect()) {
                holder.ivSelect.setImageResource(R.drawable.ic_check_sel);
            } else {
                holder.ivSelect.setImageResource(R.drawable.ic_check_nor);
            }
            return view;
        }

        class ViewHolder {
            RelativeLayout relativeLayout;
            ImageView ivSelect;
            TextView tvName;
        }
    }

    public static class ChannelItem {
        private int Channel;
        private boolean Select;
        private boolean Enable;

        public ChannelItem() {
        }

        public int getChannel() {
            return Channel;
        }

        public void setChannel(int channel) {
            Channel = channel;
        }

        public boolean isSelect() {
            return Select;
        }

        public void setSelect(boolean select) {
            Select = select;
        }

        public boolean isEnable() {
            return Enable;
        }

        public void setEnable(boolean enable) {
            Enable = enable;
        }
    }
}
