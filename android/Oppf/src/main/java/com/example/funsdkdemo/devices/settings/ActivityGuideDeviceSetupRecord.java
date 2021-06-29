package com.example.funsdkdemo.devices.settings;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.basic.G;
import com.example.common.DeviceConfigType;
import com.example.funsdkdemo.ActivityDemo;
import com.example.funsdkdemo.R;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.JSONCONFIG;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.CloudStorage;
import com.lib.funsdk.support.config.JsonConfig;
import com.lib.funsdk.support.config.RecordParam;
import com.lib.funsdk.support.config.RecordParamEx;
import com.lib.funsdk.support.config.SimplifyEncode;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityGuideDeviceSetupRecord extends ActivityDemo implements OnClickListener, OnFunDeviceOptListener, OnItemSelectedListener, OnSeekBarChangeListener, IFunSDKResult {

	
	private TextView mTextTitle = null;
	
	private ImageButton mBtnBack = null;
	
	private TextView mTextPreRecord = null;
	
	private SeekBar mBarPreRecord = null;
	
	private TextView mTextRecordLength = null;
	
	private SeekBar mBarRecordLength = null;
	
	private Spinner mSpinnerRecordMode = null;
	
	private ImageButton mRecordAudio = null;
	
	private Spinner mCloudStorage = null;

	private ImageButton mBtnSave = null;
	
	private FunDevice mFunDevice = null;
	
	private int mCloudPosition = 0;
	
	/**
	 * 本界面需要获取到的设备配置信息
	 */
	private String[] DEV_CONFIGS_FOR_CAMARA = {
			// 获取参数:SimplifyEncode -> audioEable
			SimplifyEncode.CONFIG_NAME,
			
			// 获取参数:RecordParam
			RecordParam.CONFIG_NAME,
			
			RecordParamEx.CONFIG_NAME,

			// 获取参数:CloudStorage  
			// CloudStorage.CONFIG_NAME
	};
	
	private final String[] DEV_CONFIGS_FOR_CHANNELS = {
			// 获取参数:RecordParam
			RecordParam.CONFIG_NAME,
	};

	private String[] DEV_CONFIGS = DEV_CONFIGS_FOR_CHANNELS;
	
	// 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
	private List<String> mSettingConfigs = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_device_setup_record);
		
		mTextTitle = (TextView)findViewById(R.id.textViewInTopLayout);
		
		mBtnBack = (ImageButton)findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);
		
		mTextTitle.setText(R.string.device_setup_record);
		
		mTextPreRecord = (TextView)findViewById(R.id.setupRecordPreValue);

		mBarPreRecord = (SeekBar)findViewById(R.id.setupRecordPreSeekbar);
		//mBarPreRecord.setMax(30);
		mBarPreRecord.setOnSeekBarChangeListener(this);
		
		mTextRecordLength = (TextView)findViewById(R.id.setupRecordLengthValue);

		mBarRecordLength = (SeekBar)findViewById(R.id.setupRecordLengthSeekbar);
		//mBarRecordLength.setMax(15);
		mBarRecordLength.setOnSeekBarChangeListener(this);
		
		mSpinnerRecordMode = (Spinner)findViewById(R.id.setupRecordModeSpinner);
		String[] recordMode = getResources().getStringArray(R.array.device_setup_record_mode_values);
		ArrayAdapter<String> adapterRecordMode = new ArrayAdapter<String>(this, R.layout.right_spinner_item, recordMode);
		adapterRecordMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerRecordMode.setAdapter(adapterRecordMode);
		Integer[] defValues = {1, 0, 2};
		mSpinnerRecordMode.setTag(defValues);
		mSpinnerRecordMode.setOnItemSelectedListener(this);
		
		mCloudStorage = (Spinner)findViewById(R.id.setupRecordCloudSpinner);
		String[] cloudStorage = getResources().getStringArray(R.array.device_setup_record_cloud_values);
		ArrayAdapter<String> adapterCloudStorage = new ArrayAdapter<String>(this, R.layout.right_spinner_item, cloudStorage);
		adapterCloudStorage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCloudStorage.setAdapter(adapterCloudStorage);
		Integer[] defCsValues = {0,1,2,3,4};
		mCloudStorage.setTag(defCsValues);
		mCloudStorage.setOnItemSelectedListener(this);

		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
		if ( null == funDevice ) {
			finish();
			return;
		}
		
		mBtnSave = (ImageButton)setNavagateRightButton(R.layout.imagebutton_save);
		mBtnSave.setOnClickListener(this);
		
		mFunDevice = funDevice;
		
		if (mFunDevice.channel.nChnCount == 1) {
			mRecordAudio = (ImageButton)findViewById(R.id.setupRecordAudioBtn);
			mRecordAudio.setOnClickListener(this);
			findViewById(R.id.layoutSetupRecordAudio).setVisibility(View.VISIBLE);
		}
		
		// 注册设备操作监听
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);

		SystemInfo info = (SystemInfo) mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
		if (info != null && info.getVideoInChannel() > mFunDevice.CurrChannel) {
			// 获取是否支持主辅码流录像
			int useId = FunSDK.RegUser(this);
			FunSDK.DevGetConfigByJson(useId, mFunDevice.getDevSn(), JSONCONFIG.SUPPORT_EXTRECORD, 4096, -1, 5000, 0);
		} else {
			DEV_CONFIGS = DEV_CONFIGS_FOR_CHANNELS;
			// 获取报警配置信息
			tryGetRecordConfig();
		}
	}
	

	@Override
	protected void onDestroy() {
		
		// 注销监听
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.backBtnInTopLayout) {// 返回/退出
			finish();
		} else if (id == R.id.setupRecordAudioBtn) {
			v.setSelected(!v.isSelected());
		} else if (id == R.id.btnSave) {    // 保存
			trySaveRecordConfig();
		}
	}
	
	private int getSpinnerValue(Spinner spinner) {
		Integer[] values = (Integer[])spinner.getTag();
		int position = spinner.getSelectedItemPosition();
		if ( position >= 0 && position < values.length ) {
			return values[position];
		}
		return 0;
	}
	
	private int getSpinnerPosition(Spinner spinner, int value) {
		Integer[] values = (Integer[])spinner.getTag();
		for ( int i = 0; i < values.length; i ++ ) {
			if ( values[i] == value ) {
				return i;
			}
		}
		return 0;
	}
	
	private boolean isCurrentUsefulConfig(String configName) {
		for ( int i = 0; i < DEV_CONFIGS.length; i ++ ) {
			if ( DEV_CONFIGS[i].equals(configName) ) {
				return true;
			}
		}
		return false;
	}

	private String getStringRecordMode(int i) {
		if (i == 0) {
			return "ClosedRecord";
		} else if (i == 1) {
			return "ManualRecord";
		} else {
			return "ConfigRecord";
		}
	}

	private int getIntRecordMode(String s) {
		if (s.equals("ClosedRecord")) {
			return 0;
		} else if (s.equals("ManualRecord")) {
			return 1;
		} else {
			return 2;
		}
	}

	private void setCloudValue(CloudStorage cfg) {
		if (null != cfg) {
			if (cfg.getEnableMsk() == 0)
			{
				mCloudStorage.setSelection(0);
			}
			else if (cfg.getEnableMsk() == 1)
			{
				if (cfg.getStreamType() == 0)
				{
					mCloudStorage.setSelection(3);
				}
				else
				{
					mCloudStorage.setSelection(4);
				}
			}
			else if (cfg.getEnableMsk() == 2)
			{
				if (cfg.getStreamType() == 0)
				{
					mCloudStorage.setSelection(1);
				}
				else
				{
					mCloudStorage.setSelection(2);
				}
			}
			mCloudPosition = mCloudStorage.getSelectedItemPosition();
		}
	}

	private void getCloudValue(CloudStorage cfg) {
		if (null != cfg) {
			switch (mCloudStorage.getSelectedItemPosition()) {
			case 0:
				cfg.setEnableMsk(0);
				break;
			case 1:
				cfg.setEnableMsk(2);
				cfg.setStreamType(0);
				cfg.setAlarmRecTypeMsk(3);
				break;
			case 2:
				cfg.setEnableMsk(2);
				cfg.setStreamType(1);
				cfg.setAlarmRecTypeMsk(3);
				break;
			case 3:
				cfg.setEnableMsk(1);
				cfg.setStreamType(0);
				break;
			case 4:
				cfg.setEnableMsk(1);
				cfg.setStreamType(1);
				break;
			default:
				break;
			}
		}
	}
	
	private void refreshRecordConfig() {

		SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
		if ( null != simplifyEncode ) {
			mRecordAudio.setSelected(simplifyEncode.mainFormat.AudioEnable);
		}
	
		RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
		if ( null != recordParam ) {

			mTextPreRecord.setText( String.valueOf(recordParam.getPreRecordTime()) + getString(R.string.device_setup_record_second));
			mBarPreRecord.setProgress(recordParam.getPreRecordTime());
			mTextRecordLength.setText( String.valueOf(recordParam.getPacketLength()) + getString(R.string.device_setup_record_minute));
			mBarRecordLength.setProgress(recordParam.getPacketLength());

			if(getIntRecordMode(recordParam.getRecordMode()) == 2)
			{
				boolean bNoramlRecord = MyUtils.getIntFromHex(recordParam.mask[0][0]) == 7;
				FunLog.i("setup record", "TTT--->" + recordParam.recordMode + "bNoramlRecord"
						+ (bNoramlRecord ? 1 : 2));
				mSpinnerRecordMode.setSelection(getSpinnerPosition(mSpinnerRecordMode, bNoramlRecord ? 1 : 2));
			}
			else
			{
				mSpinnerRecordMode.setSelection(getSpinnerPosition(mSpinnerRecordMode, getIntRecordMode(recordParam.getRecordMode())));
			}
		}
		
//		if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE)
//		{
//			findViewById(R.id.layoutSetupRecordCloud).setVisibility(View.VISIBLE);
//			findViewById(R.id.layoutRecordCloudUnderLine).setVisibility(View.VISIBLE);
//			CloudStorage cloudStorage = (CloudStorage)mFunDevice.getConfig(CloudStorage.CONFIG_NAME);
//			if ( null != cloudStorage ) {
//				setCloudValue(cloudStorage);
//			}
//		}
	}
	
	private void tryGetRecordConfig() {
		if ( null != mFunDevice ) {
			
			showWaitDialog();

			for ( String configName : DEV_CONFIGS ) {
				
				// 删除老的配置信息
				mFunDevice.invalidConfig(configName);
				
				if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE || configName != CloudStorage.CONFIG_NAME)
				{
					// 重新搜索新的配置信息
					if (contains(DeviceConfigType.DeviceConfigCommon, configName)) {
						 FunSupport.getInstance().requestDeviceConfig(mFunDevice,
						 configName);
					}else if (contains(DeviceConfigType.DeviceConfigByChannel, configName)) {
						FunSupport.getInstance().requestDeviceConfig(mFunDevice, configName, mFunDevice.CurrChannel);
					}
				}
			}
		}
	}
	
	/**
	 * 判断是否所有需要的配置都获取到了
	 * @return
	 */
	private boolean isAllConfigGetted() {
		for ( String configName : DEV_CONFIGS ) {
			if ( null == mFunDevice.getConfig(configName) ) {
				if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE || configName != CloudStorage.CONFIG_NAME)
				{
					return false;
				}
			}
		}
		return true;
	}

	private void trySaveRecordConfig() {
		boolean beSimplifyEncodeChanged = false;
		SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
		if ( null != simplifyEncode ) {

			if ( simplifyEncode.mainFormat.AudioEnable
					!= mRecordAudio.isSelected() ) {

				simplifyEncode.mainFormat.AudioEnable = mRecordAudio.isSelected();
				
				beSimplifyEncodeChanged = true;
			}
		}
		
		boolean beRecordParamChanged = false;
		RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
		if ( null != recordParam )
		{
			if(mBarPreRecord.getProgress() != recordParam.getPreRecordTime())
			{
				recordParam.setPreRecordTime(mBarPreRecord.getProgress());
				beRecordParamChanged = true;
			}
			
			if(mBarRecordLength.getProgress() != recordParam.getPacketLength())
			{
				recordParam.setPacketLength(mBarRecordLength.getProgress());
				beRecordParamChanged = true;
			}
			
			int mode = getSpinnerValue(mSpinnerRecordMode);

//			if(!getStringRecordMode(mode).equals(recordParam.getRecordMode()))
//			{
				recordParam.recordMode = getStringRecordMode(mode);
				// 如果是联动配置的话，把普通录像去掉
				for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {
					recordParam.mask[i][0] = MyUtils.getHexFromInt(mode == 2 ? 6 : 7);
				}
				beRecordParamChanged = true;
//			}
		}
		
		boolean beCloudParamChanged = false;
		CloudStorage cloudStorage = null;
		if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE)
		{
			cloudStorage = (CloudStorage)mFunDevice.getConfig(CloudStorage.CONFIG_NAME);
			if ( null != cloudStorage ) {
				if(mCloudPosition != mCloudStorage.getSelectedItemPosition())
				{
					getCloudValue(cloudStorage);
					beCloudParamChanged = true;
				}
			}
		}

		if ( beSimplifyEncodeChanged 
				|| beRecordParamChanged
				|| beCloudParamChanged ) {
			showWaitDialog();
			
			// 保存SimplifyEncode
			if ( beSimplifyEncodeChanged ) {
				synchronized (mSettingConfigs) {
					mSettingConfigs.add(simplifyEncode.getConfigName());
				}
				
				FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simplifyEncode);
			}

			if ( beRecordParamChanged ) {
				synchronized (mSettingConfigs) {
					mSettingConfigs.add(recordParam.getConfigName());
				}
				
				FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, recordParam);
			}
			
			if ( beCloudParamChanged && cloudStorage != null ) {
				synchronized (mSettingConfigs) {
					mSettingConfigs.add(cloudStorage.getConfigName());
				}
				
				FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cloudStorage);
			}
		} else {
			showToast(R.string.device_alarm_no_change);
		}
	}
	

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice,
			String configName, int nSeq) {
		if ( null != mFunDevice 
				&& funDevice.getId() == mFunDevice.getId()
				&& isCurrentUsefulConfig(configName) ) {
			if ( isAllConfigGetted() ) {
				hideWaitDialog();
			}
			
			refreshRecordConfig();
		}
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice,
			final String configName) {
		if ( null != mFunDevice 
				&& funDevice.getId() == mFunDevice.getId() ) {
			synchronized (mSettingConfigs) {
				if ( mSettingConfigs.contains(configName) ) {
					mSettingConfigs.remove(configName);
				}
				
				if ( mSettingConfigs.size() == 0 ) {
					hideWaitDialog();
				}
			}
			
			//refreshRecordConfig();
		}
	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice, 
			final String configName, final Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}

	@Override
	public void onDeviceChangeInfoSuccess(final FunDevice funDevice) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceChangeInfoFailed(final FunDevice funDevice, final Integer errCode) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceOptionSuccess(final FunDevice funDevice, final String option) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceOptionFailed(final FunDevice funDevice, final String option, final Integer errCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,  
            boolean fromUser) {

			if(seekBar.equals(mBarPreRecord))
			{
				mTextPreRecord.setText( String.valueOf(progress) + getString(R.string.device_setup_record_second));
			}
			else if(seekBar.equals(mBarRecordLength))
			{
				mTextRecordLength.setText( String.valueOf(progress) + getString(R.string.device_setup_record_minute));
			}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int OnFunSDKResult(Message message, MsgContent msgContent) {
		if (message.what == EUIMSG.DEV_GET_JSON) {
			if (JsonConfig.SUPPORT_EXTRECORD.equals(msgContent.str)) {
				if (message.arg1 > 0) {
					try {
						JSONObject jsonObject = new JSONObject(G.ToString(msgContent.pData));
						if (jsonObject.has(JsonConfig.SUPPORT_EXTRECORD)) {
							JSONObject object = jsonObject.getJSONObject(JsonConfig.SUPPORT_EXTRECORD);
							if (object.has("AbilityPram")) {
								String ability = object.getString("AbilityPram");
								// AbilityPram  0支持主码流录像，1支持辅码流录像，2都支持
								if ("0".equals(ability) || "0x00000000".equals(ability)) {
									DEV_CONFIGS_FOR_CAMARA = new String[]{
											// 获取参数:SimplifyEncode -> audioEable
											SimplifyEncode.CONFIG_NAME,
											// 获取参数:RecordParam
											RecordParam.CONFIG_NAME,
									};
								} else if ("1".equals(ability) || "0x00000001".equals(ability)) {
									DEV_CONFIGS_FOR_CAMARA = new String[] {
											// 获取参数:SimplifyEncode -> audioEable
											SimplifyEncode.CONFIG_NAME,
											RecordParamEx.CONFIG_NAME,
									};
								} else if ("2".equals(ability) || "0x00000002".equals(ability)) {
									DEV_CONFIGS_FOR_CAMARA = new String[] {
											// 获取参数:SimplifyEncode -> audioEable
											SimplifyEncode.CONFIG_NAME,
											// 获取参数:RecordParam
											RecordParam.CONFIG_NAME,
											RecordParamEx.CONFIG_NAME,
									};
								}
								DEV_CONFIGS = DEV_CONFIGS_FOR_CAMARA;
								// 获取报警配置信息
								tryGetRecordConfig();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					showToast(FunError.getErrorStr(message.arg1));
				}
			}
		}
		return 0;
	}
}
