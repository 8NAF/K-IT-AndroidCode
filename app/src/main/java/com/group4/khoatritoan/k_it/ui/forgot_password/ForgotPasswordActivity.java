package com.group4.khoatritoan.k_it.ui.forgot_password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.ActivityForgotPasswordBinding;
import com.group4.khoatritoan.k_it.ui.login.LoginViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {


	ActivityForgotPasswordBinding binding;
	ForgotPasswordViewModel forgotPasswordViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		forgotPasswordViewModel = new ViewModelProvider(this)
				.get(ForgotPasswordViewModel.class);

		binding = DataBindingUtil.setContentView(
				this,
				R.layout.activity_forgot_password
		);
		binding.setViewModel(forgotPasswordViewModel);
	}

	@Override
	public void onBackPressed() {

		setResult(Activity.RESULT_CANCELED);
		super.onBackPressed();
	}
}