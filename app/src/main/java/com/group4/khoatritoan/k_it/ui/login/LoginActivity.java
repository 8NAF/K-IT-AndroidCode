package com.group4.khoatritoan.k_it.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.SnackbarHelper;
import com.group4.khoatritoan.k_it.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

	ActivityLoginBinding binding;
	LoginViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		viewModel = new ViewModelProvider(this)
				.get(LoginViewModel.class);
		binding.setViewModel(viewModel);
	}

	@Override
	protected void onStart() {
		super.onStart();
		viewModel.onFirstTimeLaunchApplication(this);
		viewModel.onUserIsLoggedIn(this);
	}

	public static final int FORGOT_PASSWORD_CODE = 1;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FORGOT_PASSWORD_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				SnackbarHelper.show(binding.tvForgotPassword, getString(R.string.success_send_reset_password), null);
			}
		}
	}
}