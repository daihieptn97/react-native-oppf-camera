package com.hunonic.funsdkdemo.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.hunonic.funsdkdemo.R;

/**
 * @author hws
 * @class 测试
 * @time 2019-08-19 17:02
 */
public class TestActivity extends AppCompatActivity {
    private ViewGroup surface;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        surface = findViewById(R.id.fl_surface);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
    }

    public void onTest(View view) {

    }

}
