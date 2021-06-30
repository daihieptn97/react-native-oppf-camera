package com.hunonic.funsdkdemo.devices.settings.remotectrl.view;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hunonic.funsdkdemo.ActivityDemo;
import com.hunonic.funsdkdemo.R;
import com.hunonic.funsdkdemo.devices.settings.remotectrl.contract.DevRemoteCtrlContract;
import com.hunonic.funsdkdemo.devices.settings.remotectrl.presenter.DevRemoteCtrlPresenter;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.widget.FunVideoView;
import com.utils.XUtils;
import com.xm.ui.widget.XTitleBar;

/**
 * @author hws
 * @class 设备远程控制
 * @time 2019-11-22 9:38
 */
public class DevRemoteCtrlActivity extends ActivityDemo
        implements DevRemoteCtrlContract.IDevRemoteCtrlView, View.OnTouchListener {
    private static final float PLAY_WND_SCALE = 16f / 9f;
    //如果视频打开失败，重试一遍
    private boolean isTryStartMonitor = true;
    private ImageView ivMoveUp;
    private ImageView ivMoveDown;
    private ImageView ivMoveLeft;
    private ImageView ivMoveRight;
    private Button btnLeftClick;
    private Button btnRightClick;
    private Button btnEscClick;
    private DevRemoteCtrlPresenter presenter;
    private FunVideoView funVideoView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_remote_ctrl);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        ((XTitleBar)findViewById(R.id.xb_dev_remote_ctrl_title)).setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        funVideoView = findViewById(R.id.video_view);
        ivMoveUp = findViewById(R.id.remote_ctrl_up);
        ivMoveDown = findViewById(R.id.remote_ctrl_down);
        ivMoveLeft = findViewById(R.id.remote_ctrl_left);
        ivMoveRight = findViewById(R.id.remote_ctrl_right);
        btnLeftClick = findViewById(R.id.btn_left_click);
        btnRightClick = findViewById(R.id.btn_right_click);
        btnEscClick = findViewById(R.id.btn_esc_click);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) funVideoView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = XUtils.getScreenWidth(this);
            layoutParams.height = (int) (layoutParams.width / PLAY_WND_SCALE);
        }
    }

    private void initListener() {
        ivMoveUp.setOnTouchListener(this);
        ivMoveDown.setOnTouchListener(this);
        ivMoveLeft.setOnTouchListener(this);
        ivMoveRight.setOnTouchListener(this);
        btnLeftClick.setOnTouchListener(this);
        btnRightClick.setOnTouchListener(this);
        btnEscClick.setOnTouchListener(this);

        funVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int errorId) {
                if (errorId == -215124) {
                    if (isTryStartMonitor) {
                        isTryStartMonitor = false;
                        funVideoView.setRealDevice(presenter.getFunDevice().getDevSn(),
                                presenter.getFunDevice().channel.nChnCount);
                    }else {
                        Toast.makeText(DevRemoteCtrlActivity.this, R.string.TR_Open_Remote_Video_F, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        funVideoView.stopPlayback();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        int devId = intent.getIntExtra("FUN_DEVICE_ID",0);
        FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
        presenter = new DevRemoteCtrlPresenter(this);
        presenter.setFunDevice(funDevice);
        presenter.checkIsSupport();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isDown = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isDown = true;
        }else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            isDown = false;
        }else {
            return true;
        }

        int id = v.getId();
        if (id == R.id.remote_ctrl_up) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.MOVE_UP, isDown);
        } else if (id == R.id.remote_ctrl_down) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.MOVE_DOWN, isDown);
        } else if (id == R.id.remote_ctrl_left) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.MOVE_LEFT, isDown);
        } else if (id == R.id.remote_ctrl_right) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.MOVE_RIGHT, isDown);
        } else if (id == R.id.btn_left_click) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.LEFT_DOWN, isDown);
        } else if (id == R.id.btn_right_click) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.RIGHT_DOWN, isDown);
        } else if (id == R.id.btn_esc_click) {
            presenter.ctrlMouse(DevRemoteCtrlContract.MOUSE_CTRL.ESC, isDown);
        }

        return true;
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onCheckSupportResult(boolean isSupport) {
        if (isSupport) {
            funVideoView.setRealDevice(presenter.getFunDevice().getDevSn(),
                    presenter.getFunDevice().channel.nChnCount);
        }else {
            Toast.makeText(this, FunSDK.TS("TR_Not_Support_Function"), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
