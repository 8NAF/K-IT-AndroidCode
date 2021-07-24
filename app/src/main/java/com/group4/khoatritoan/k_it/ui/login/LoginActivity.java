package com.group4.khoatritoan.k_it.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.MySnackbar;
import com.group4.khoatritoan.k_it.databinding.ActivityLoginBinding;
import com.group4.khoatritoan.k_it.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
	
	public static final int FORGOT_PASSWORD_CODE = 1;

	ActivityLoginBinding binding;
	LoginViewModel loginViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {
			FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		}
		catch (Exception ignored) {}

		super.onCreate(savedInstanceState);

		loginViewModel = new ViewModelProvider(this)
				.get(LoginViewModel.class);

		binding = DataBindingUtil.setContentView(
				this,
				R.layout.activity_login
		);
		binding.setViewModel(loginViewModel);

	}

	@Override
	protected void onStart() {
		super.onStart();

		FirebaseAuth auth = FirebaseAuth.getInstance();
		// Nếu người dùng đã đăng nhập thì chuyển đến màn hình chính
		if (auth.getCurrentUser() != null) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == FORGOT_PASSWORD_CODE) {

			if (resultCode == Activity.RESULT_OK) {
				MySnackbar.show(binding.tvForgotPassword, getString(R.string.success_send_reset_password), null);
			}
		}
	}
}