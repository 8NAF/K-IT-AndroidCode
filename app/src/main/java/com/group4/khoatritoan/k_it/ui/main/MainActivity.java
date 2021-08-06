package com.group4.khoatritoan.k_it.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setUpBottomNavigation();
	}

	private void setUpBottomNavigation() {
		BottomNavigationView navView = binding.navView;

		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home,
				R.id.navigation_log,
				R.id.navigation_profile
		).build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
	}

	boolean doubleBackToExitPressedOnce = false;
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			this.finish();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, getString(R.string.help_press_back_again_to_exit), Toast.LENGTH_SHORT).show();

		new Handler(Looper.getMainLooper())
				.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
	}
}