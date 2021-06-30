package com.hunonic.funsdkdemo.devices.settings.other;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hunonic.funsdkdemo.R;

public class ActivityDeviceOtherConfigSet extends Activity {
    private RecyclerView mRvShowConfig;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_other_config_set);
        initView();
    }

    private void initView() {
        mRvShowConfig = findViewById(R.id.rv_device_other_config_set);
    }

    class ShowConfig extends RecyclerView.Adapter<ShowConfig.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHodler extends RecyclerView.ViewHolder {

            public ViewHodler(View itemView) {
                super(itemView);
            }
        }
    }
}
