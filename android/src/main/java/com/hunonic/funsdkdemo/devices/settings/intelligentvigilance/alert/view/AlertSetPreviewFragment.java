package com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hunonic.funsdkdemo.R;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.presenter.AlertSetPreviewPresenter;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.bean.smartanalyze.Points;
import com.xm.ui.widget.drawgeometry.listener.RevokeStateListener;
import com.xm.ui.widget.drawgeometry.view.DrawGeometry;

import java.util.List;

public class AlertSetPreviewFragment extends Fragment implements RevokeStateListener {
    private View mLayout;
    private FunVideoView funVideoView;
    private static final int CONVERT_PARAMETER = 8192;
    private AlertSetPreviewPresenter mPresenter;
    private DrawGeometry mDrawGeometry;
    private int mDirection;
    private String devId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_alert_set_preview, container);
        funVideoView = mLayout.findViewById(R.id.video_view);
        initData();
        return mLayout;
    }

    private void initData() {
        mDrawGeometry = (DrawGeometry) mLayout.findViewById(R.id.shape_view);
        mDrawGeometry.setOnRevokeStateListener(this);
        mPresenter = new AlertSetPreviewPresenter(mDrawGeometry);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (null != funVideoView) {
            funVideoView.setRealDevice(devId,0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != funVideoView) {
            funVideoView.stopPlayback();
        }
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public void setDrawGeometryType(int type) {
        if (mDrawGeometry != null) {
            mDrawGeometry.setGeometryType(type);
        }
    }

    public void revert() {
        if (mDrawGeometry != null) {
            mDrawGeometry.revertToDefaultPoints();
        }
    }

    public void retreatStep() {
        if (mDrawGeometry != null) {
            mDrawGeometry.retreatToPreviousOperationPoints();
        }
    }


    public List<Points> getConvertPoint() {
       return mPresenter.getConvertPoint(mDrawGeometry.getWidth(), mDrawGeometry.getHeight());
    }

    public void initAlertDirection(int direction) {
        this.mDirection = direction;
        mDrawGeometry.initDirection(direction);
    }

    public void setAlertDirection(int direction) {
        this.mDirection = direction;
        mDrawGeometry.setDirection(direction);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    public void setConvertPoint(List<Points> list, int size) {
    	if(size > 0) {
	    	List<Points> _list = list.subList(0, size);
	    	mPresenter.setConvertPoint(_list, mDrawGeometry.getWidth(), mDrawGeometry.getHeight());
    	}
    }

    @Override
    public void onRevokeEnable(boolean enable) {
        ((AlertSetActivity) getActivity()).changeRevokeState(enable);
    }
}
