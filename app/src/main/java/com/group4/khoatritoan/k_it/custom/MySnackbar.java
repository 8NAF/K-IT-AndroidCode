package com.group4.khoatritoan.k_it.custom;

import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.group4.khoatritoan.k_it.R;

public class MySnackbar {

	private MySnackbar() {}

	public static void show (View parent, String message, Snackbar.Callback callback) {

		Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_INDEFINITE);
		View view = snackbar.getView();

		TextView tvError = view.findViewById(com.google.android.material.R.id.snackbar_text);
		tvError.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);
		tvError.setMaxLines(10);

//		R.dimen.design_bottom_navigation_height;

		try { snackbar.setAnchorView(R.id.nav_view); }
		catch (Exception ignored) {}

		snackbar.setAction(R.string.action_dismiss, v -> snackbar.dismiss());
		if (callback != null) {
			snackbar.addCallback(callback);
		}
		snackbar.show();
	}

}
