package com.group4.khoatritoan.k_it.ui.main.tab.logs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.NotificationAdapter;
import com.group4.khoatritoan.k_it.databinding.FragmentLogsBinding;
import com.group4.khoatritoan.k_it.model.NotificationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogsFragment extends Fragment {

	private LogsViewModel logsViewModel;
	private FragmentLogsBinding binding;

	private NotificationAdapter adapter;
	private RecyclerView rvNotifications;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentLogsBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		logsViewModel = new ViewModelProvider(this)
				.get(LogsViewModel.class);
		binding.setViewModel(logsViewModel);

		setupRecyclerView();

		MaterialDatePicker<Pair<Long, Long>> datePicker = createMaterialDatePicker();
		binding.btnSelectDate.setOnClickListener(v -> {
			datePicker.show(getActivity().getSupportFragmentManager(), "DATE");
		});
		datePicker.addOnPositiveButtonClickListener(logsViewModel::onPositiveButtonClick);
		logsViewModel.getStartEndMilliseconds().observe(getViewLifecycleOwner(), logsViewModel::onStartEndMillisecondsChange);
		logsViewModel.getListNotifications().observe(getViewLifecycleOwner(), value -> {
			adapter.setListNotifications(logsViewModel.transformListNotifications());
		});

		logsViewModel.getIsNewest().observe(getViewLifecycleOwner(), value -> {
			List<NotificationModel> list = adapter.getListNotifications();
			Collections.reverse(list);
			adapter.setListNotifications(list);
		});

		logsViewModel.getFilterIsReceived().observe(getViewLifecycleOwner(), value -> {
			adapter.setListNotifications(logsViewModel.transformListNotifications());
		});

		MaterialButtonToggleGroup tgIsReceived = binding.tgIsReceived;
		Boolean filter = logsViewModel.getFilterIsReceived().getValue();
		if (filter == null) {
			tgIsReceived.check(R.id.btnTrue);
			tgIsReceived.check(R.id.btnFalse);
		}
		else if (filter) {
			tgIsReceived.check(R.id.btnTrue);
			tgIsReceived.uncheck(R.id.btnFalse);
		}
		else {
			tgIsReceived.uncheck(R.id.btnTrue);
			tgIsReceived.check(R.id.btnFalse);
		}
		tgIsReceived.addOnButtonCheckedListener(logsViewModel::onButtonChecked);

		return binding.getRoot();
	}

	private void setupRecyclerView() {
		adapter = new NotificationAdapter(new ArrayList<>(), getContext());

		rvNotifications = binding.rvNotifications;
		rvNotifications.setAdapter(adapter);
		rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
		rvNotifications.setHasFixedSize(true);
	}

	private MaterialDatePicker<Pair<Long, Long>> createMaterialDatePicker() {
		MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder
				.dateRangePicker()
				.setTitleText(R.string.label_select_date);
		return builder.build();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}