package com.hunonic.funsdkdemo.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hunonic.funsdkdemo.R;

import java.lang.reflect.Field;

/**
 * @author zhangyongyong
 * @date 2018-02-02-18:52
 */

public class PermissionDialog extends DialogFragment implements View.OnClickListener {

    private View mView;
    private TextView mTitleTip;
    private String titleText;
    private OperateListener operateListener;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(0, R.style.DialogFragment_style);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.permission_dialog, null);
        mTitleTip = (TextView) mView.findViewById(R.id.title_tip);
        mTitleTip.setText(titleText);
        mView.findViewById(R.id.setting_permission).setOnClickListener(this);
        mView.findViewById(R.id.cancel_set).setOnClickListener(this);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public PermissionDialog setTitle(String text) {
        titleText = text;
        return this;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.setting_permission) {
            if (operateListener != null) {
                dismiss();
                operateListener.onConfirm();
            }
        } else if (id == R.id.cancel_set) {
            if (operateListener != null) {
                dismiss();
                operateListener.onCancel();
            }
        }
    }


    public PermissionDialog setOperateListener(OperateListener operateListener) {
        this.operateListener = operateListener;
        return this;

    }

    /***
     *
     * @param manager
     * @param tag
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            Field mDismissed = this.getClass().getSuperclass().getDeclaredField("mDismissed");
            Field mShownByMe = this.getClass().getSuperclass().getDeclaredField("mShownByMe");
            mDismissed.setAccessible(true);
            mShownByMe.setAccessible(true);
            mDismissed.setBoolean(this, false);
            mShownByMe.setBoolean(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }


    public interface OperateListener {
        /**
         * 取消设置
         */
        void onCancel();

        /**
         * 设置
         */
        void onConfirm();
    }
}
