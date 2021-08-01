package com.group4.khoatritoan.k_it.ui.forgot_password;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;

import com.group4.khoatritoan.k_it.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

	ActivityForgotPasswordBinding binding;
	ForgotPasswordViewModel forgotPasswordViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		forgotPasswordViewModel = new ViewModelProvider(this)
				.get(ForgotPasswordViewModel.class);
		binding.setViewModel(forgotPasswordViewModel);
	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		super.onBackPressed();
	}
}