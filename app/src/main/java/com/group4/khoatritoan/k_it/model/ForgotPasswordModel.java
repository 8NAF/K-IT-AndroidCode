package com.group4.khoatritoan.k_it.model;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class ForgotPasswordModel {

	public void sendPasswordResetEmail(String email, OnCompleteListener<Void> callback) {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		auth.setLanguageCode(Locale.getDefault().getDisplayLanguage());
		auth.sendPasswordResetEmail(email).addOnCompleteListener(callback);
	}
}
