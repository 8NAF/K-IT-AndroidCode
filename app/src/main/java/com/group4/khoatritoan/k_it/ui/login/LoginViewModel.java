package com.group4.khoatritoan.k_it.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.MySnackbar;
import com.group4.khoatritoan.k_it.ui.forgot_password.ForgotPasswordActivity;
import com.group4.khoatritoan.k_it.ui.main.MainActivity;

import static com.group4.khoatritoan.k_it.custom.Utility.dismissKeyboard;
import static com.group4.khoatritoan.k_it.ui.login.LoginActivity.FORGOT_PASSWORD_CODE;

public class LoginViewModel extends ViewModel {

	private final MutableLiveData<String> username = new MutableLiveData<>();
	private final MutableLiveData<String> password = new MutableLiveData<>();

	public MutableLiveData<String> getUsername() {
		return username;
	}
	public MutableLiveData<String> getPassword() {
		return password;
	}

	public void onLogin(View view) {

		Activity activity = (Activity) view.getContext();
		dismissKeyboard(activity);

		try {
			FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
			firebaseAuth
					.signInWithEmailAndPassword(username.getValue(), password.getValue())
					.addOnSuccessListener(activity, authResult -> {
						activity.startActivity(new Intent(activity, MainActivity.class));
						activity.finish();
					})
					.addOnFailureListener(activity, exception -> {
						handleException(view, exception);
					});
		}
		catch (IllegalArgumentException exception) {
			handleException(view, exception);
		}
	}

	public void onForgotPassword(View view) {

		Activity activity = (Activity) view.getContext();
		Intent intent = new Intent(activity, ForgotPasswordActivity.class);
		activity.startActivityForResult(intent, FORGOT_PASSWORD_CODE);
	}

	private void showErrorMessage(View parent, String errorMessage) {

		Log.e("Login Error", errorMessage);
		MySnackbar.show(parent, errorMessage, null);
	}

	private void handleException(View view, Exception exception) {

		Activity activity = (Activity) view.getContext();

		try {
			throw exception;
		}
		catch (FirebaseTooManyRequestsException e) {
			showErrorMessage(view, activity.getString(R.string.error_too_many_fail_auth));
		}
		catch (FirebaseNetworkException e) {
			showErrorMessage(view, activity.getString(R.string.error_network));
		}
		catch (FirebaseAuthException e) {

			Log.e("Login Error Code", e.getErrorCode());

			switch (e.getErrorCode()) {
				case "ERROR_INVALID_EMAIL":
					showErrorMessage(view, activity.getString(R.string.error_invalid_email));
					break;
				case "ERROR_USER_NOT_FOUND":
					showErrorMessage(view, activity.getString(R.string.error_user_not_found));
					break;
				case "ERROR_WRONG_PASSWORD":
					showErrorMessage(view, activity.getString(R.string.error_wrong_password));
					break;
				case "ERROR_USER_DISABLED":
					showErrorMessage(view, activity.getString(R.string.error_user_disabled));
					break;
			}

		}
		catch (IllegalArgumentException e) {
			showErrorMessage(view, activity.getString(R.string.error_not_provide_information));
		}
		catch (Exception e) {
			showErrorMessage(view, e.getMessage());
		}
	}


}
