package com.group4.khoatritoan.k_it.ui.forgot_password;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.SnackbarHelper;
import com.group4.khoatritoan.k_it.model.ForgotPasswordModel;

import static com.group4.khoatritoan.k_it.custom.Utility.dismissKeyboard;

public class ForgotPasswordViewModel extends ViewModel {

	private final ForgotPasswordModel model = new ForgotPasswordModel();

	private final MutableLiveData<String> email = new MutableLiveData<>();
	public MutableLiveData<String> getEmail() {
		return email;
	}

	public void onSend(View view) {

		dismissKeyboard(view);

		try {
			Activity activity = (Activity) view.getContext();

			model.sendPasswordResetEmail(email.getValue(), task -> {
				if (task.isSuccessful()) {
					activity.setResult(Activity.RESULT_OK);
					activity.finish();
				}
				else {
					handleException(view, task.getException());
				}
			});
		}
		catch (Exception e) {
			handleException(view, e);
		}
	}

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
				case "ERROR_USER_DISABLED":
					showErrorMessage(view, activity.getString(R.string.error_user_disabled));
					break;
			}

		}
		catch (IllegalArgumentException e) {
			showErrorMessage(view, activity.getString(R.string.error_not_provide_email));
		}
		catch (Exception e) {
			showErrorMessage(view, e.getMessage());
		}
	}
}