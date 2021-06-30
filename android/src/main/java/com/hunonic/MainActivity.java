package com.hunonic;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hunonic.funsdkdemo.R;
import com.sdk.CameraSdk;


public class MainActivity extends AppCompatActivity {

    private final CameraSdk cameraSdk = new CameraSdk();

    private EditText edtWifi, edtPassword;
    private Button btnSubmit;
    private Context context;
    private TextView txtResult;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        cameraSdk.registerOnFunDeviceWiFiConfigListenerSdk(funDevice -> {
            Log.d(CameraSdk.TAG_DEBUG, funDevice.toString());
            Toast.makeText(getApplicationContext(), "SUCESS" + funDevice.devIp, Toast.LENGTH_SHORT).show();
            txtResult.setText(funDevice.devIp + " - " + funDevice.devMac + " - " + funDevice.devSn);
            progressBar.setVisibility(View.GONE);

        });


        mappingView();

        progressBar.setVisibility(View.GONE);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        ssid = ssid.replace("\"", "");

        edtWifi.setText(ssid);
        edtPassword.setText("66668888");

        btnSubmit.setOnClickListener(v -> {
//            cameraSdk.startSmartConfig(getApplicationContext(), edtPassword.getText().toString(), edtWifi.getText().toString());
//            progressBar.setVisibility(View.VISIBLE);
        });

    }

    private void mappingView() {
        edtWifi = findViewById(R.id.edtWifiName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtResult = findViewById(R.id.txtResult);

        progressBar = findViewById(R.id.progressBar);
    }
}
