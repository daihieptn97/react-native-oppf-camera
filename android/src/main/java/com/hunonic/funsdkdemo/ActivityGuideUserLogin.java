package com.hunonic.funsdkdemo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunonic.common.DialogSavedUsers;
import com.hunonic.common.DialogSavedUsers.OnSavedUserSelectListener;
import com.hunonic.common.UIFactory;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunLoginListener;
import com.lib.funsdk.support.models.FunLoginType;

public class ActivityGuideUserLogin extends ActivityDemo implements OnClickListener, OnFunLoginListener {

	
	private TextView mTextTitle = null;
	private ImageButton mBtnBack = null;
	
	private EditText mEditUserName = null;
	private EditText mEditPassWord = null;
	private ImageButton mBtnLoginHistory = null;
	private Button mBtnLogin = null;
	private Button mBtnForgotPasswd = null;
	private Button mBtnLoginByWeibo = null;
	private Button mBtnLoginByQQ = null;
	private Button mBtnRegister = null;
	
	private Button mBtnCheckSavePasswd = null;
	private Button mBtnCheckAutoLogin = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_user_login);
		
		mTextTitle = (TextView)findViewById(R.id.textViewInTopLayout);
		
		mBtnBack = (ImageButton)findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);
		
		mTextTitle.setText(R.string.guide_module_title_user_login);
		
		mEditUserName = (EditText)findViewById(R.id.userLoginUserName);
		mEditPassWord = (EditText)findViewById(R.id.userLoginPasswd);
		mBtnLogin = (Button)findViewById(R.id.userLoginBtn);
		mBtnLogin.setOnClickListener(this);
		
		mBtnForgotPasswd = (Button)findViewById(R.id.userloginForgotPasswd);
		mBtnForgotPasswd.setOnClickListener(this);
		
		mBtnLoginHistory = (ImageButton)findViewById(R.id.btnLoginHistory);
		mBtnLoginHistory.setOnClickListener(this);
		
		mBtnLoginByWeibo = (Button)findViewById(R.id.userLoginByWeibo);
		mBtnLoginByQQ = (Button)findViewById(R.id.userLoginByQQ);
		UIFactory.setLeftDrawable(this, mBtnLoginByWeibo,
				R.drawable.user_icon_other_login_weibo, 24, 24);
		UIFactory.setLeftDrawable(this, mBtnLoginByQQ,
				R.drawable.user_icon_other_login_qq, 24, 24);
		mBtnLoginByWeibo.setOnClickListener(this);
		mBtnLoginByQQ.setOnClickListener(this);
		
		mBtnCheckSavePasswd = (Button)findViewById(R.id.checkboxSavePassword);
		UIFactory.setLeftDrawable(this, mBtnCheckSavePasswd,
				R.drawable.icon_check, 24, 24);
		mBtnCheckAutoLogin = (Button)findViewById(R.id.checkboxAutoLogin);
		UIFactory.setLeftDrawable(this, mBtnCheckAutoLogin,
				R.drawable.icon_check, 24, 24);
		mBtnCheckSavePasswd.setOnClickListener(this);
		mBtnCheckAutoLogin.setOnClickListener(this);
		
		mBtnCheckSavePasswd.setSelected(FunSupport.getInstance().getSavePasswordAfterLogin());
		mBtnCheckAutoLogin.setSelected(FunSupport.getInstance().getAutoLogin());
		
		mBtnRegister = (Button)findViewById(R.id.userRegister);
		mBtnRegister.setOnClickListener(this);
		
		// 显示上一次保存的用户名和密码
		mEditUserName.setText(FunSupport.getInstance().getSavedUserName());
		mEditPassWord.setText(FunSupport.getInstance().getSavedPassword());
		
		// 用户相关的操作,必须切换网络访问方式
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
		
		// 注册监听(用户登录相关)
		FunSupport.getInstance().registerOnFunLoginListener(this);
	}
	

	@Override
	protected void onDestroy() {
		
		// 注销监听(用户登录相关)
		FunSupport.getInstance().removeOnFunLoginListener(this);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.backBtnInTopLayout) {
			finish();
		} else if (id == R.id.userLoginByWeibo || id == R.id.userLoginByQQ) {
			showToast("暂不开放第三方账号登录");
		} else if (id == R.id.btnLoginHistory) {// 显示登录历史
			showLoginHistory();
		} else if (id == R.id.userLoginBtn) {
			tryToLogin();
		} else if (id == R.id.userloginForgotPasswd) {
			enterForgotPassword();
		} else if (id == R.id.userRegister) {
			enterUserRegister();
		} else if (id == R.id.checkboxSavePassword) {
			if (mBtnCheckSavePasswd.isSelected()) {
				mBtnCheckSavePasswd.setSelected(false);
				FunSupport.getInstance().setSavePasswordAfterLogin(false);
			} else {
				mBtnCheckSavePasswd.setSelected(true);
				FunSupport.getInstance().setSavePasswordAfterLogin(true);
			}
		} else if (id == R.id.checkboxAutoLogin) {
			if (mBtnCheckAutoLogin.isSelected()) {
				mBtnCheckAutoLogin.setSelected(false);
				FunSupport.getInstance().setAutoLogin(false);
			} else {
				mBtnCheckAutoLogin.setSelected(true);
				FunSupport.getInstance().setAutoLogin(true);
			}
		}
	}
	
	private void showLoginHistory() {
		DialogSavedUsers dialog = new DialogSavedUsers(this,
				FunSupport.getInstance().getSavedUserNames(),
				new OnSavedUserSelectListener() {
					
					@Override
					public void onSavedUserSelected(String userName) {
						String passWord = FunSupport.getInstance().getSavedPassword(userName);
						if ( null != passWord 
								&& null != mEditUserName
								&& null != mEditPassWord ) {
							mEditUserName.setText(userName);
							mEditPassWord.setText(passWord);
							mBtnLogin.requestFocus();
						}
					}
				});
		dialog.show();
	}
	
	private void tryToLogin() {
		String userName = mEditUserName.getText().toString();
		String passWord = mEditPassWord.getText().toString();
		
		if ( null == userName || userName.length() == 0 ) {
			// 用户名为空
			showToast(R.string.user_login_error_emptyusername);
			return;
		}
		
		if ( null == passWord || passWord.length() == 0 ) {
			// 密码为空
			showToast(R.string.user_login_error_emptypassword);
			return;
		}
		
		showWaitDialog();
		
		if ( !FunSupport.getInstance().login(userName, passWord) ) {
			showToast(R.string.guide_message_error_call);
		}
	}
	
	
	private void enterForgotPassword() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityGuideUserForgetPassw.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	private void enterUserRegister() {
		Intent intent = new Intent();
		intent.setClass(this, ActivityGuideUserRegister.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	@Override
	public void onLoginSuccess() {
		hideWaitDialog();
		showToast(R.string.user_register_login_success);
		
		// 显示用户信息
		showUserInfo();
	}


	@Override
	public void onLoginFailed(Integer errCode) {
		hideWaitDialog();
		showToast(FunError.getErrorStr(errCode));
	}
	
	@Override
	public void onLogout() {
		
	}

	public void showUserInfo() {
		Intent intent = new Intent(this, ActivityGuideDeviceList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
		finish();
	}

}
