package com.group4.khoatritoan.k_it.databinding;

import com.group4.khoatritoan.k_it.R;

public class Converter {

	public static int booleanToIdAutoMode(Boolean newValue) {
		return newValue ? R.id.btnTurnOnMode : R.id.btnTurnOffMode;
	}
}