package com.group4.khoatritoan.k_it.model;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.khoatritoan.k_it.custom.LocalRepository;

public class LoginModel {

	private final LocalRepository repository;
	public LoginModel(Context context) {
		repository = new LocalRepository(context);
	}

	//#region Firebase

	public boolean isUserLoggedIn() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		return auth.getCurrentUser() != null;
	}

	public void signInWithEmailAndPassword(String username, String password, OnCompleteListener<AuthResult> callback) {
		FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(callback);
	}

	public void setPersistenceEnabled(boolean value) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		database.setPersistenceEnabled(value);
	}

	//#endregion
	//#region LocalRepository
	public boolean getIsFirstTimeLaunchApplication() {
		return repository.getIsFirstTimeLaunchApplication();
	}

	public void setIsFirstTimeLaunchApplication(boolean value) {
		repository.setIsFirstTimeLaunchApplication(value);
	}

	//#endregion
}