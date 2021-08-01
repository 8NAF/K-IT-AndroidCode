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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.AlarmManagerHelper;
import com.group4.khoatritoan.k_it.custom.SnackbarHelper;
import com.group4.khoatritoan.k_it.model.ProfileModel;
import com.group4.khoatritoan.k_it.ui.login.LoginActivity;

import static com.group4.khoatritoan.k_it.custom.Utility.dismissKeyboard;
import static com.group4.khoatritoan.k_it.custom.Utility.startAndFinishActivity;

public class ProfileViewModel extends ViewModel {

	//#region getter-setter

	private final ProfileModel model = new ProfileModel();
	private final MutableLiveData<String> oldPassword = new MutableLiveData<>();
	private final MutableLiveData<String> newPassword = new MutableLiveData<>();
	private final MutableLiveData<String> confirmedNewPassword = new MutableLiveData<>();
	private final MediatorLiveData<Boolean> isEnabled = new MediatorLiveData<>();

	public FirebaseUser getCurrentUser() {
		return model.getCurrentUser();
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

	//#endregion

	//#region init

	public ProfileViewModel() {
		initIsEnabled();
	}

	private void initIsEnabled() {
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

	//#endregion

	//#region on-methods

	public void onUpdatePassword(View view) {

		dismissKeyboard(view);

		try {

			AuthCredential credential = EmailAuthProvider
					.getCredential(getCurrentUser().getEmail(), oldPassword.getValue());

			model.reauthenticate(credential, task -> {
				if (task.isSuccessful()) {
					updatePassword(view);
					return;
				}
				handleException(view, task.getException());
			});
		} catch (Exception e) {
			handleException(view, e);
		}
	}

	public void onSignOut(View view) {

		Activity activity = (Activity) view.getContext();

		model.signOut(activity);

		AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(activity);
		alarmManagerHelper.cancel();
		startAndFinishActivity(activity, LoginActivity.class);
	}

	private void updatePassword(View view) {

		try {
			Activity activity = (Activity) view.getContext();

			model.updatePassword(newPassword.getValue(), task -> {
				if (!task.isSuccessful()) {
					handleException(view, task.getException());
					return;
				}
				resetPasswordFields();
				showErrorMessage(view, activity.getString(R.string.success_update_password));
			});
		} catch (Exception e) {
			handleException(view, e);
		}
	}

	private void resetPasswordFields() {
		oldPassword.setValue(newPassword.getValue());
		newPassword.setValue("");
		confirmedNewPassword.setValue("");
	}

	//#endregion

	//#region handle error, exception
	private void showErrorMessage(View parent, String errorMessage) {
		Log.e("Update Error", errorMessage);
		showErrorMessage(parent, errorMessage, null);
	}

	private void showErrorMessage(View parent, String errorMessage, Snackbar.Callback callback) {

		Log.e("Update Error", errorMessage);
		SnackbarHelper.show(parent, errorMessage, callback);
	}

	private void handleException(View view, Exception exception) {

		Activity activity = (Activity) view.getContext();

		try {
			throw exception;
		} catch (FirebaseTooManyRequestsException e) {
			showErrorMessage(view, activity.getString(R.string.error_too_many_fail_auth));
		} catch (FirebaseNetworkException e) {
			showErrorMessage(view, activity.getString(R.string.error_network));
		} catch (FirebaseAuthException e) {

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
		} catch (IllegalArgumentException e) {
			showErrorMessage(view, activity.getString(R.string.error_not_provide_password));
		} catch (Exception e) {
			showErrorMessage(view, e.getMessage());
		}
	}
	//#endregion
}