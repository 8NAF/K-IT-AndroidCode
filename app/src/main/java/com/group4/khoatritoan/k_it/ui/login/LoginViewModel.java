package com.group4.khoatritoan.k_it.ui.login;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.SnackbarHelper;
import com.group4.khoatritoan.k_it.model.LoginModel;
import com.group4.khoatritoan.k_it.ui.forgot_password.ForgotPasswordActivity;
import com.group4.khoatritoan.k_it.ui.main.MainActivity;

import static com.group4.khoatritoan.k_it.custom.Utility.dismissKeyboard;
import static com.group4.khoatritoan.k_it.custom.Utility.startAndFinishActivity;
import static com.group4.khoatritoan.k_it.ui.login.LoginActivity.FORGOT_PASSWORD_CODE;

public class LoginViewModel extends AndroidViewModel {

	private final LoginModel model;

	public LoginViewModel(Application application) {
		super(application);
		model = new LoginModel(application);
	}


	//#region LiveData

	private final MutableLiveData<String> username = new MutableLiveData<>();
	private final MutableLiveData<String> password = new MutableLiveData<>();

	public MutableLiveData<String> getUsername() {
		return username;
	}
	public MutableLiveData<String> getPassword() {
		return password;
	}

	//#endregion

	//#region private methods
	private void showErrorMessage(View parent, String errorMessage) {

		Log.e("Login Error", errorMessage);
		SnackbarHelper.show(parent, errorMessage, null);
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
	//#endregion

	//#region on-methods
	public void onLogin(View view) {

		dismissKeyboard(view);

		try {
			Activity activity = (Activity) view.getContext();
			model.signInWithEmailAndPassword(username.getValue(), password.getValue(), task -> {
				if (task.isSuccessful()) {
					startAndFinishActivity(activity, MainActivity.class);
				}
				else {
					handleException(view, task.getException());
				}
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

	public void onFirstTimeLaunchApplication(Context context) {
		if (model.getIsFirstTimeLaunchApplication()) {
			model.setPersistenceEnabled(true);
			model.setIsFirstTimeLaunchApplication(false);
		}
	}

	public void onUserIsLoggedIn(Activity activity) {
		if (model.isUserLoggedIn()) {
			Intent intent = new Intent(activity, MainActivity.class);
			activity.startActivity(intent);
			activity.finish();
		}
	}
	//#endregion
}