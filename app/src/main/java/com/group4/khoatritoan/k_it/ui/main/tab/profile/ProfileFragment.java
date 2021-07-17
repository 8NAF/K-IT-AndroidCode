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
import com.google.firebase.auth.FirebaseAuth;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

	private ProfileViewModel profileViewModel;
	private FragmentProfileBinding binding;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentProfileBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

		FirebaseAuth auth = FirebaseAuth.getInstance();
		binding.setUser(auth.getCurrentUser());
		binding.setViewModel(profileViewModel);

		FloatingActionButton fabSignOut = binding.fabSignOut;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			fabSignOut.setTooltipText(getString(R.string.label_sign_out));
		}

		View root = binding.getRoot();
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

}