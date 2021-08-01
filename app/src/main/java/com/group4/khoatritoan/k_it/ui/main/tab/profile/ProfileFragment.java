package com.group4.khoatritoan.k_it.ui.main.tab.profile;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.FragmentProfileBinding;

//TODO: Chưa xử lý được khi người dùng gỡ cài đặt hoặc xóa dữ liệu app
//Cần xét turnOnNotification thành false

public class ProfileFragment extends Fragment {

	private FragmentProfileBinding binding;
	private ProfileViewModel profileViewModel;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentProfileBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
		binding.setViewModel(profileViewModel);

		FloatingActionButton fabSignOut = binding.fabSignOut;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			fabSignOut.setTooltipText(getString(R.string.label_sign_out));
		}

		return binding.getRoot();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}