package com.hunonic.funsdkdemo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.funsdk.support.FunSupport;

public class ActivityGuideUserDebug extends ActivityDemo implements OnClickListener {

	private ImageView image;
	private TextView text;
	private ImageButton imageb;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_debug);
		image = (ImageView) findViewById(R.id.debugswitch);
		image.setSelected(FunSupport.getInstance().getSaveNativePassword());
		image.setOnClickListener(this);
		text = (TextView) findViewById(R.id.textViewInTopLayout);
		text.setText(R.string.guide_module_title_user_debug);
		imageb = (ImageButton) findViewById(R.id.backBtnInTopLayout);
		imageb.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.backBtnInTopLayout) {
			finish();
		} else if (id == R.id.debugswitch) {
			image.setSelected(!image.isSelected());
			FunSupport.getInstance().setSaveNativePassword(image.isSelected());
		}
	}

}
