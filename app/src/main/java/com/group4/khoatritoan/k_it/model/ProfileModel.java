package com.group4.khoatritoan.k_it.model;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.khoatritoan.k_it.custom.LocalRepository;

import static com.group4.khoatritoan.k_it.custom.DatabasePath.TURN_ON_NOTIFICATION_PATH;

public class ProfileModel {

	//#region Firebase

	public FirebaseUser getCurrentUser() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		return auth.getCurrentUser();
	}

	public void reauthenticate(AuthCredential credential, OnCompleteListener<Void> callback) {
		getCurrentUser().reauthenticate(credential).addOnCompleteListener(callback);
	}

	public void updatePassword(String newPassword, OnCompleteListener<Void> callback) {
		getCurrentUser().updatePassword(newPassword).addOnCompleteListener(callback);
	}

	public void signOut(Context context) {

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(TURN_ON_NOTIFICATION_PATH);
		ref.setValue(false);

		LocalRepository repository = new LocalRepository(context);
		repository.resetHomeConfig();

		FirebaseAuth.getInstance().signOut();
	}

	//#endregion
}
