package com.example.niephox.methophotos.ViewControllers.ViewAdapters;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.niephox.methophotos.R;

public class LoginActivityView {
	private TextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;
	private Button mEmailSignInButton;
	private Button btRegister;
	public LoginActivityView(AppCompatActivity activity){
		activity.setContentView(R.layout.login_activity);
		mEmailView = activity.findViewById(R.id.email);
		mPasswordView = activity.findViewById(R.id.password);
		mEmailSignInButton = activity.findViewById(R.id.email_sign_in_button);
		btRegister = activity.findViewById(R.id.btRegister);
		mLoginFormView = activity.findViewById(R.id.login_form);
		mProgressView = activity.findViewById(R.id.login_progress);
	}

	public TextView getmEmailView() {
		return mEmailView;
	}

	public EditText getmPasswordView() {
		return mPasswordView;
	}

	public View getmProgressView() {
		return mProgressView;
	}

	public View getmLoginFormView() {
		return mLoginFormView;
	}

	public Button getmEmailSignInButton() {
		return mEmailSignInButton;
	}

	public Button getBtRegister() {
		return btRegister;
	}
}
