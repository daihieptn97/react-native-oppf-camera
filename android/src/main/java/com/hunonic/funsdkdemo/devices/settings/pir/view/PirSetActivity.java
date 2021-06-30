package com.hunonic.funsdkdemo.devices.settings.pir.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hunonic.funsdkdemo.ActivityDemo;
import com.hunonic.funsdkdemo.R;
import com.hunonic.funsdkdemo.devices.settings.pir.contract.PirSetContract;
import com.hunonic.funsdkdemo.devices.settings.pir.presenter.PirSetPresenter;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.AlarmInfoBean;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

/**
 * @author hws
 * @class  PIR 徘徊检测
 * @time 2020/3/25 13:53
 */
public class PirSetActivity extends ActivityDemo implements PirSetContract.IPirSetView {
    private ListSelectItem listPirAlarmEnable;
    private ListSelectItem listPirAlarmTimes;
    private ListSelectItem listPirAlarmPlan;
    private ListSelectItem listPirAlarmStartEndTime;
    private ListSelectItem listPirAlarmRepeat;
    private ListSelectItem listPirSensitive;
    private FunDevice funDevice;
    private PirSetPresenter pirSetPresenter;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idrset_human_detection_act);
        initView();
        initData();
    }

    private void initView() {
        ((XTitleBar)findViewById(R.id.pir_set_title)).setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        listPirAlarmEnable = findViewById(R.id.detection_alarm);
        listPirAlarmTimes = findViewById(R.id.detection_duration);
        listPirAlarmPlan = findViewById(R.id.pir_time_section_one);
        listPirAlarmStartEndTime = findViewById(R.id.pir_start_end_time_one);
        listPirAlarmRepeat = findViewById(R.id.pir_week_one);
        listPirSensitive = findViewById(R.id.pir_sensitive);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        showWaitDialog();
        int devId = intent.getIntExtra("FUN_DEVICE_ID",0);
        funDevice = FunSupport.getInstance().findDeviceById(devId);
        pirSetPresenter = new PirSetPresenter(funDevice.getDevSn(),this);
    }

    @Override
    public void onGetConfigResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            AlarmInfoBean.PirTimeSections.PirTimeSection pirTimeSection = pirSetPresenter.getPirTimeSection().PirTimeSectionOne;
            listPirAlarmEnable.setRightText(pirSetPresenter.isPirAlarmEnable() + "");
            listPirAlarmTimes.setRightText(pirSetPresenter.getDuration() + "");
            listPirAlarmPlan.setRightText(pirTimeSection.Enable + "");
            String startEndTime = String.format("%s-%s",
                    pirTimeSection.StartTime,
                    pirTimeSection.EndTime);
            listPirAlarmStartEndTime.setRightText(startEndTime);
            listPirAlarmRepeat.setRightText("Mask:" + pirTimeSection.WeekMask);
            listPirSensitive.setRightText(pirSetPresenter.getPirSensitive() + "");
        }else {
            Toast.makeText(this, R.string.get_config_f, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSetConfigResult(boolean isSuccess) {

    }
}
