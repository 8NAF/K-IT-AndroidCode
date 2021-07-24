package com.group4.khoatritoan.k_it.ui.main.tab.profile;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.util.Strings;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.MySnackbar;
import com.group4.khoatritoan.k_it.ui.login.LoginActivity;

import static com.group4.khoatritoan.k_it.custom.Utility.dismissKeyboard;


public class ProfileViewModel extends ViewModel {

	private MutableLiveData<String> oldPassword = new MutableLiveData<>();
	private MutableLiveData<String> newPassword = new MutableLiveData<>();
	private MutableLiveData<String> confirmedNewPassword = new MutableLiveData<>();
	private MediatorLiveData<Boolean> isEnabled = new MediatorLiveData<>();

	public ProfileViewModel() {
		Observer<String> callback = s -> {

			String _newPassword = newPassword.getValue();
			String _confirmedNewPassword = confirmedNewPassword.getValue();

			if (Strings.isEmptyOrWhitespace(_newPassword) || Strings.isEmptyOrWhitespace(_confirmedNewPassword)) {
				isEnabled.setValue(false);
				return;
			}

			Log.e("confirmedNewPassword", _confirmedNewPassword);
			Log.e("newPassword", _newPassword);

			isEnabled.setValue(_newPassword.equals(_confirmedNewPassword));
		};

		isEnabled.addSource(newPassword, callback);
		isEnabled.addSource(confirmedNewPassword, callback);
	}

	public MutableLiveData<String> getNewPassword() {
		return newPassword;
	}
	public MutableLiveData<String> getConfirmedNewPassword() {
		return confirmedNewPassword;
	}
	public MutableLiveData<String> getOldPassword() {
		return oldPassword;
	}
	public LiveData<Boolean> getIsEnabled() {
		return isEnabled;
	}

	public void onUpdatePassword(View view) {

		Activity activity = (Activity) view.getContext();

		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();

		dismissKeyboard(activity);

		try {

			AuthCredential credential = EmailAuthProvider
					.getCredential(currentUser.getEmail(), oldPassword.getValue());

			currentUser.reauthenticate(credential)
					.addOnCompleteListener(activity, task -> {
						if (task.isSuccessful()) {
							updatePassword(view);
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

	public void onSignOut(View view) {
		FirebaseAuth.getInstance().signOut();
		Activity activity = (Activity) view.getContext();
		activity.startActivity(new Intent(activity, LoginActivity.class));
		activity.finish();
	}

	private void updatePassword(View view) {
		Activity activity = (Activity) view.getContext();

		FirebaseAuth auth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = auth.getCurrentUser();

		try {
			currentUser.updatePassword(newPassword.getValue())
					.addOnSuccessListener(activity, none -> {
						showErrorMessage(view, activity.getString(R.string.success_update_password));
					})
					.addOnFailureListener(activity, exception -> {
						handleException(view, exception);
					});
		}
		catch (Exception e) {
			handleException(view, e);
		}


	}

	private void showErrorMessage(View parent, String errorMessage) {
		showErrorMessage(parent, errorMessage, null);
	}
	private void showErrorMessage(View parent, String errorMessage, Snackbar.Callback callback) {

		Log.e("Update Error", errorMessage);
		MySnackbar.show(parent, errorMessage, callback);
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

			Log.e("Update Pass Error Code", e.getErrorCode());

			switch (e.getErrorCode()) {
				case "ERROR_USER_NOT_FOUND":
					showErrorMessage(view, activity.getString(R.string.error_user_deleted), new Snackbar.Callback() {
						@Override
						public void onDismissed(Snackbar snackbar, int event) {
							activity.startActivity(new Intent(activity, LoginActivity.class));
							activity.finish();
						}
					});
					break;
				case "ERROR_USER_DISABLED":
					showErrorMessage(view, activity.getString(R.string.error_user_disabled), new Snackbar.Callback() {
						@Override
						public void onDismissed(Snackbar snackbar, int event) {
							activity.startActivity(new Intent(activity, LoginActivity.class));
							activity.finish();
						}
					});
					break;
				case "ERROR_WRONG_PASSWORD":
					showErrorMessage(view, activity.getString(R.string.error_wrong_password));
					break;
				case "ERROR_WEAK_PASSWORD":
					showErrorMessage(view, activity.getString(R.string.error_weak_password));
					break;
			}

		}
		catch (IllegalArgumentException e) {
			showErrorMessage(view, activity.getString(R.string.error_not_provide_password));
		}
		catch (Exception e) {
			showErrorMessage(view, e.getMessage());
		}
	}

}

