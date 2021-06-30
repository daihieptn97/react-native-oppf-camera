package com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.hunonic.funsdkdemo.R;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.model.FunctionViewItemElement;
import com.hunonic.funsdkdemo.devices.settings.intelligentvigilance.alert.presenter.AlertSetFunctionPresenter;
import com.hunonic.funsdkdemo.widget.SmartAnalyzeFunctionView;

import java.util.List;

import static com.manager.db.Define.ALERT_AREA_TYPE;
import static com.manager.db.Define.ALERT_lINE_TYPE;
import static com.manager.db.Define.GOODS_RETENTION_TYPE;
import static com.manager.db.Define.STOLEN_GOODS_TYPE;


public class AlertSetFunctionFragment extends Fragment implements View.OnClickListener,
        SmartAnalyzeFunctionView.OnItemClickListener, AlertSetFunctionInterface {
    private int mRuleType;
    private View mLayout;
    private LinearLayout mAlertAreaSetting;
    private Button mBoundaryAlertDirection;
    private Button mAlertLineTriggerDirection;
    private Button mGoodsApplicationScenarios;
    private RelativeLayout mContainer;
    private SmartAnalyzeFunctionView mFunctionView;
    private AlertSetFunctionPresenter mFunctionPresenter;
    private TextView mSave;
    private TextView mRevert;
    private TextView mRevoke;
    private int itemPos = -1;
    private int edgeCount = 0;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFunctionPresenter = new AlertSetFunctionPresenter(getContext(),this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayout = inflater.inflate(R.layout.fragment_alert_set_function, container);
        mAlertAreaSetting = (LinearLayout) mLayout.findViewById(R.id.alert_area_setting);
        mBoundaryAlertDirection = (Button) mLayout.findViewById(R.id.boundary_alert_direction);
        mBoundaryAlertDirection.setOnClickListener(this);
        mAlertLineTriggerDirection = (Button) mLayout.findViewById(R.id.alert_line_trigger_direction);
        mGoodsApplicationScenarios = (Button) mLayout.findViewById(R.id.goods_application_scenarios);
        mContainer = (RelativeLayout) mLayout.findViewById(R.id.layoutRoot);
        mSave = mLayout.findViewById(R.id.smart_analyze_save);
        mSave.setOnClickListener(this);
        mRevoke = mLayout.findViewById(R.id.smart_analyze_revoke);
        mRevert = mLayout.findViewById(R.id.smart_analyze_revert);
        mRevoke.setOnClickListener(this);
        mRevert.setOnClickListener(this);
        return mLayout;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRuleType = ((AlertSetActivity) getActivity()).getRuleType();
        initFunctionView();
    }

    private void initFunctionView() {
        List<FunctionViewItemElement> functionList = mFunctionPresenter.initFunctionViewData(mRuleType);
        if (functionList == null) {
            return;
        }
        switch (mRuleType) {
            case ALERT_lINE_TYPE:
                mAlertLineTriggerDirection.setVisibility(View.VISIBLE);
                break;
            case ALERT_AREA_TYPE:
                mAlertAreaSetting.setVisibility(View.VISIBLE);
                break;
            case GOODS_RETENTION_TYPE:
                mGoodsApplicationScenarios.setVisibility(View.VISIBLE);
                break;
            case STOLEN_GOODS_TYPE:
                mGoodsApplicationScenarios.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        if (mFunctionView == null) {
            mFunctionView = new SmartAnalyzeFunctionView(getActivity(), functionList);
            mFunctionView.setOnItemClickListener(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mContainer.addView(mFunctionView, 1, layoutParams);
        }else {
            mFunctionView.setData(functionList);
        }
        initData();
    }

    private void initData() {
        if (itemPos == -1) {
            itemPos = 0;
        }
        mFunctionView.setItemSelected(itemPos);
        if (mFunctionPresenter.isDirectionDlgShow()) {
            mBoundaryAlertDirection.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.smart_analyze_save) {
            ((AlertSetActivity) getActivity()).saveConfig();
        } else if (id == R.id.smart_analyze_revoke) {
            ((AlertSetActivity) getActivity()).retreatStep();
        } else if (id == R.id.smart_analyze_revert) {
            ((AlertSetActivity) getActivity()).revert();
            mFunctionView.setItemUnSelected();
        } else if (id == R.id.boundary_alert_direction) {
            ((AlertSetActivity) getActivity()).showAlertDirectionDialog();
        }

    }

    @Override
    public void onItemClick(View view, int position, String label) {
        mFunctionPresenter.showShapeOnCanvas(position, mRuleType);
    }

    @Override
    public void setShapeType(int type) {
        ((AlertSetActivity) getActivity()).setShapeType(type);
    }

    @Override
    public void initAlertLineType(int lineType) {
        this.itemPos = lineType;
        if (mFunctionView != null) {
            mFunctionView.setItemSelected(itemPos);
        }
    }

    @Override
    public void setAlertLineType(int position) {
        try {
            ((AlertSetActivity) getActivity()).setAlertLineDirection(position);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAlertAreaEdgeCount(int edgeCount) {
        this.edgeCount = edgeCount;
        if (edgeCount <= 6) {
            itemPos = edgeCount - 3;
        }else if (edgeCount == 8){
            itemPos = 4;
        }else {
            itemPos = 5;
        }
        if (mFunctionView != null) {
            mFunctionView.setItemSelected(itemPos);
        }
    }

    @Override
    public void setDirectionMask(String directionMask) {
        mFunctionPresenter.setDirectionMask(directionMask);
        initFunctionView();
    }

    @Override
    public void setAreaMask(String areaMask) {
        mFunctionPresenter.setAreaMask(areaMask);
        initFunctionView();
    }

    public void changeRevokeState(boolean state) {
        mRevoke.setEnabled(state);
    }

    @Override
    public void onDestroy() {
        mFunctionPresenter.onDestroy();
        mFunctionPresenter = null;
        super.onDestroy();
    }
}
