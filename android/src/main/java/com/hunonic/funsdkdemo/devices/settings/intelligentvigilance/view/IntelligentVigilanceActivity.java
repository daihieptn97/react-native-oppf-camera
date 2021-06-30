package com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hunonic.funsdkdemo.ActivityDemo;
import com.hunonic.funsdkdemo.R;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.view.AlertSetActivity;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.constract.IntelligentVigilanceConstract;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.presenter.IntelligentVigilancePresenter;
import com.lib.SDKCONST;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.HumanDetectionBean;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import static com.lib.sdk.bean.HumanDetectionBean.IA_PERIMETER;
import static com.lib.sdk.bean.HumanDetectionBean.IA_TRIPWIRE;
import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;


/**
 * @author hws
 * @name XMEye_Android
 * @class 智能警戒
 * @time 2019-05-06 10:52
 */
public class IntelligentVigilanceActivity extends ActivityDemo
        implements XTitleBar.OnLeftClickListener, IntelligentVigilanceConstract.IHumanDetectView, View.OnClickListener {
    private ListSelectItem lsiSwitch;
    private ListSelectItem lsiTrack;
    private ListSelectItem lsiLine;
    private ListSelectItem lsiArea;
    private IntelligentVigilancePresenter presenter;
    private LinearLayout llPerimeter;
    private ListSelectItem lsiPerimeter;
    private FunDevice funDevice;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human_detection);
        initView();
        initData();
    }

    private void initView() {
        ((XTitleBar) findViewById(R.id.xb_dev_alarm_title)).setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        ((XTitleBar) findViewById(R.id.xb_dev_alarm_title)).setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showWaitDialog();
                presenter.saveHumanDetect();
            }
        });
        lsiSwitch = findViewById(R.id.lsi_human_detection_switch);
        lsiTrack = findViewById(R.id.lsi_human_detection_track);
        lsiLine = findViewById(R.id.lsi_human_detection_line);
        lsiArea = findViewById(R.id.lsi_human_detection_area);
        lsiPerimeter = findViewById(R.id.lsi_human_detection_perimeter);
        llPerimeter = findViewById(R.id.ll_human_detection_perimeter);
        lsiSwitch.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem parent, View v) {
                if (lsiSwitch.getRightValue() == SDKCONST.Switch.Open) {
                    presenter.setHumanDetectEnable(true);
                }else {
                    presenter.setHumanDetectEnable(false);
                }
            }
        });
        lsiTrack.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem parent, View v) {
                presenter.setShowTrack(lsiTrack.getRightValue() == SDKCONST.Switch.Open);
            }
        });
        lsiArea.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem parent, View v) {
                if (lsiLine.getRightValue() == SDKCONST.Switch.Open) {
                    presenter.setRuleType(IA_PERIMETER);
                    lsiLine.setRightImage(SDKCONST.Switch.Close);
                }
            }
        });
        lsiArea.setOnClickListener(this);
        lsiLine.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem parent, View v) {
                if (lsiArea.getRightValue() == SDKCONST.Switch.Open) {
                    presenter.setRuleType(IA_TRIPWIRE);
                    lsiArea.setRightImage(SDKCONST.Switch.Close);
                }
            }
        });
        lsiLine.setOnClickListener(this);

        lsiPerimeter.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem parent, View v) {
                if (lsiPerimeter.getRightValue() == SDKCONST.Switch.Open) {
                    llPerimeter.setVisibility(View.VISIBLE);
                    presenter.setRuleEnable(true);
                }else {
                    llPerimeter.setVisibility(View.GONE);
                    presenter.setRuleEnable(false);
                }
            }
        });

    }

    private void initData() {
        hideWaitDialog();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        int devId = intent.getIntExtra("FUN_DEVICE_ID",0);
        funDevice = FunSupport.getInstance().findDeviceById(devId);
        presenter = new IntelligentVigilancePresenter(funDevice.devSn,this);
        presenter.updateHumanDetect();
    }

    @Override
    public void onLeftclick() {

    }

    @Override
    public void updateHumanDetectResult(boolean isSuccess) {
        hideWaitDialog();
		lsiTrack.setVisibility(presenter.isTrackSupport() ? View.VISIBLE : View.GONE);
        lsiLine.setVisibility(presenter.isLineSupport() ? View.VISIBLE : View.GONE);
        lsiArea.setVisibility(presenter.isAreaSupport() ? View.VISIBLE : View.GONE);
        lsiSwitch.setRightImage(presenter.isHumanDetectEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        lsiTrack.setRightImage(presenter.isShowTrack() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        if (presenter.getRuleType() == IA_TRIPWIRE) {
            lsiLine.setRightImage(SDKCONST.Switch.Open);
            lsiArea.setRightImage(SDKCONST.Switch.Close);
        }else if (presenter.getRuleType() == IA_PERIMETER){
            lsiLine.setRightImage(SDKCONST.Switch.Close);
            lsiArea.setRightImage(SDKCONST.Switch.Open);
        }

        lsiPerimeter.setRightImage(presenter.isRuleEnable() ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        llPerimeter.setVisibility(presenter.isRuleEnable() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void saveHumanDetectResult(boolean isSuccess) {
        hideWaitDialog();
        if (isSuccess) {
            Toast.makeText(this, R.string.save, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ALERT_lINE_TYPE
                    || requestCode == ALERT_AREA_TYPE ) {
                HumanDetectionBean humanDetectionBean = (HumanDetectionBean) data.getSerializableExtra("HumanDetection");
                if (humanDetectionBean != null) {
                    presenter.setHumanDetection(humanDetectionBean);
                }
                if (requestCode == ALERT_lINE_TYPE) {
                    presenter.setRuleType(IA_TRIPWIRE);
                    lsiLine.setRightImage(SDKCONST.Switch.Open);
                    lsiArea.setRightImage(SDKCONST.Switch.Close);
                }else {
                    presenter.setRuleType(IA_PERIMETER);
                    lsiLine.setRightImage(SDKCONST.Switch.Close);
                    lsiArea.setRightImage(SDKCONST.Switch.Open);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.lsi_human_detection_area) {
            Intent intent = new Intent(this, AlertSetActivity.class);
            intent.putExtra("devId", funDevice.getDevSn());
            intent.putExtra("HumanDetection", presenter.getHumanDetection());
            intent.putExtra("RuleType", ALERT_AREA_TYPE);
            intent.putExtra("ChannelHumanRuleLimit", presenter.getChannelHumanRuleLimitBean());
            startActivityForResult(intent, ALERT_AREA_TYPE);
        } else if (id == R.id.lsi_human_detection_line) {
            Intent intent = new Intent(this, AlertSetActivity.class);
            intent.putExtra("devId", funDevice.getDevSn());
            intent.putExtra("HumanDetection", presenter.getHumanDetection());
            intent.putExtra("RuleType", ALERT_lINE_TYPE);
            intent.putExtra("ChannelHumanRuleLimit", presenter.getChannelHumanRuleLimitBean());
            startActivityForResult(intent, ALERT_lINE_TYPE);
        }
    }
}
