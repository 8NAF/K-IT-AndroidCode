package com.group4.khoatritoan.k_it.custom;

import androidx.lifecycle.Observer;

public abstract class SkippableObserver<T> implements Observer<T> {

	boolean isSkip = true;

	public boolean getIsSkip() {
		return isSkip;
	}

	public void setIsSkip(boolean value) {
		isSkip = value;
	}
}