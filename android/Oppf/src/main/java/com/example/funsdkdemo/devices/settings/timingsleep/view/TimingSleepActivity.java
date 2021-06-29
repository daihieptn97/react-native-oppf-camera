package com.example.funsdkdemo.devices.settings.timingsleep.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.funsdkdemo.R;
import com.example.funsdkdemo.devices.settings.timingsleep.persenter.TimingSleepPersenter;
import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.bean.StringUtils;
import com.xm.ui.dialog.SingleSelectionDlg;
import com.xm.ui.dialog.TimePickBottomDialog;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hws on 2018-07-05.
 * 休眠
 */

public class TimingSleepActivity extends AppCompatActivity
        implements ITimingSleepView , TimePickBottomDialog.OnDatePickerListener,View.OnClickListener {
    public static final int TYPE_START = 0;
    public static final int TYPE_END = 1;
    private TimingSleepPersenter mPersenter;
    private XTitleBar mXTitleBar;
    private ListSelectItem mListOpenTimingSleep;
    private ListSelectItem mListStartTime;
    private ListSelectItem mListEndTime;
    private ListSelectItem mListRepeat;
    private TimePickBottomDialog timePick;
    private SingleSelectionDlg singleSelectionDlg;
    private int[] mStartTime = new int[2];
    private int[] mEndTime = new int[2];
    private boolean mIsRepeat = false;
    private FunDevice mFunDevice;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devset_timing_sleep);
        initView();
        initData();
    }

    private void initView() {
        mXTitleBar = findViewById(R.id.timing_sleep_title);
        mXTitleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        mXTitleBar.setRightTvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    Date startDate = format.parse(mListStartTime.getRightText());
                    Date endDate = format.parse(mListEndTime.getRightText());
                    mStartTime[0] = startDate.getHours();
                    mStartTime[1] = startDate.getMinutes();
                    mEndTime[0] = endDate.getHours();
                    mEndTime[1] = endDate.getMinutes();
                    if (mStartTime[0] == mEndTime[0] && mStartTime[1] == mEndTime[1]) {
                        Toast.makeText(TimingSleepActivity.this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mPersenter.setSleepSwitch(mListOpenTimingSleep.getRightValue() == SDKCONST.Switch.Open);
                mPersenter.setSleepTime(mStartTime,mEndTime);
                mPersenter.setRepeat(mIsRepeat);
                mPersenter.saveConfig();
            }
        });

        mListOpenTimingSleep = findViewById(R.id.Open_Timing_Sleep);
        mListOpenTimingSleep.setOnRightClick(new ListSelectItem.OnRightImageClickListener() {
            @Override
            public void onClick(ListSelectItem listSelectItem, View view) {
                boolean isOpen = mListOpenTimingSleep.getRightValue() == SDKCONST.Switch.Open;
                if (isOpen) {
                    findViewById(R.id.timing_sleep_ll).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.timing_sleep_ll).setVisibility(View.GONE);
                }
            }
        });

        mListStartTime = findViewById(R.id.start_time);
        mListStartTime.setOnClickListener(this);

        mListEndTime = findViewById(R.id.end_time);
        mListEndTime.setOnClickListener(this);

        mListRepeat = findViewById(R.id.timing_sleep_repeat);
        mListRepeat.setOnClickListener(this);
    }

    private void initData() {
        mPersenter = new TimingSleepPersenter(mFunDevice.getDevSn(),this);
        mPersenter.getConfig();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void updateSleepSwitch(boolean isOpen) {
        mListOpenTimingSleep.setRightImage(isOpen ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        if (isOpen) {
            findViewById(R.id.timing_sleep_ll).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.timing_sleep_ll).setVisibility(View.GONE);
        }
    }

    @Override
    public void updateSleepTime(int[] startTime, int[] endTime) {
        mStartTime = startTime;
        mEndTime = endTime;
        mListStartTime.setRightText(String.format("%02d:%02d",startTime[0],startTime[1]));
        if (startTime[0] > endTime[0] ||
                (startTime[0] == endTime[0] && startTime[1] > endTime[1])) {
            mListEndTime.setRightText(String.format("%02d:%02d",endTime[0],endTime[1]) + "(" + FunSDK.TS("Next_Day") + ")");
        }else {
            mListEndTime.setRightText(String.format("%02d:%02d",endTime[0],endTime[1]));
        }
    }

    @Override
    public void updateSleepRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
        mListRepeat.setRightText(isRepeat ? FunSDK.TS("Repeat") : FunSDK.TS("Only"));
    }

    @Override
    public void onTimePicked(String year, String month, String day, String hour, String minute, int seq) {
        if (seq == TYPE_START) {
            mStartTime[0] = Integer.parseInt(hour);
            mStartTime[1] = Integer.parseInt(minute);
            mListStartTime.setRightText(hour + ":" + minute);
            if (mStartTime[0] == mEndTime[0] && mStartTime[1] == mEndTime[1]) {
                Toast.makeText(this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_LONG).show();
            }
        }else if (seq == TYPE_END) {
            mEndTime[0] = Integer.parseInt(hour);
            mEndTime[1] = Integer.parseInt(minute);
            if (mStartTime[0] == mEndTime[0] && mStartTime[1] == mEndTime[1]) {
                Toast.makeText(this, FunSDK.TS("Start_And_End_Time_Unable_Equal"), Toast.LENGTH_LONG).show();
            }
            if (mStartTime[0] > mEndTime[0] ||
                    (mStartTime[0] == mEndTime[0] && mStartTime[1] > mEndTime[1])) {
                mListEndTime.setRightText(hour + ":" + minute + "(" + FunSDK.TS("Next_Day") + ")");
            }else {
                mListEndTime.setRightText(hour + ":" + minute);
            }
        }
    }

    @Override
    public void onDestroy() {
        mPersenter.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start_time) {
            if (timePick == null) {
                timePick = new TimePickBottomDialog();
                timePick.setOnDatePickerListener(this);
            }
            timePick.setSeq(TYPE_START);
            timePick.setTimes(mStartTime[0], mStartTime[1]);
            timePick.show(getFragmentManager(), "startTime");
        } else if (id == R.id.end_time) {
            if (timePick == null) {
                timePick = new TimePickBottomDialog();
                timePick.setOnDatePickerListener(this);
            }
            timePick.setSeq(TYPE_END);
            timePick.setTimes(mEndTime[0], mEndTime[1]);
            timePick.show(getFragmentManager(), "endTime");
        } else if (id == R.id.timing_sleep_repeat) {
            if (singleSelectionDlg == null) {
                singleSelectionDlg = new SingleSelectionDlg();
                singleSelectionDlg.setOnSingleSelectionListener(new SingleSelectionDlg.OnSingleSelectionListener() {
                    @Override
                    public void onOptionSelected(String option) {
                        mIsRepeat = StringUtils.contrast(option, FunSDK.TS("Repeat"));
                        mListRepeat.setRightText(option);
                    }
                });
            }
            singleSelectionDlg.setCurSelectedOption(mIsRepeat ?
                    FunSDK.TS("Repeat") : FunSDK.TS("Only"));
            singleSelectionDlg.show(getFragmentManager(), "SingleSelection");
        }
    }
}
