package com.group4.khoatritoan.k_it.ui.main.tab.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.custom.LogAdapter;
import com.group4.khoatritoan.k_it.databinding.FragmentLogBinding;
import com.group4.khoatritoan.k_it.entity.Log;

import java.util.ArrayList;
import java.util.List;

public class LogFragment extends Fragment {

	private LogViewModel viewModel;
	private FragmentLogBinding binding;

	private LogAdapter adapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {

		binding = FragmentLogBinding.inflate(inflater, container, false);
		binding.setLifecycleOwner(getViewLifecycleOwner());

		adapter = new LogAdapter(new ArrayList<>());

		viewModel = new ViewModelProvider(this)
				.get(LogViewModel.class);
		binding.setViewModel(viewModel);
		binding.setAdapter(adapter);
		binding.setLayoutManager(new LinearLayoutManager(getActivity()));

		setUpDatePicker();

		viewModel.getStartEndMilliseconds().observe(getViewLifecycleOwner(), viewModel::onStartEndMillisecondsChange);

		viewModel.getLogs().observe(getViewLifecycleOwner(), value -> {
			adapter.setLogs(viewModel.filterLogs());
		});

		viewModel.getMapButtons().observe(getViewLifecycleOwner(), value -> {
			adapter.setLogs(viewModel.filterLogs());
		});

		viewModel.getIsNewest().observe(getViewLifecycleOwner(), value -> {
			List <Log> newList = viewModel.getLogs().getValue();
			newList = viewModel.newestFilter(newList);
			adapter.setLogs(newList);
		});

		return binding.getRoot();
	}

	private void setUpDatePicker() {
		MaterialDatePicker<Pair<Long, Long>> datePicker = createMaterialDatePicker();
		datePicker.addOnPositiveButtonClickListener(viewModel::onPositiveButtonClick);

		binding.btnSelectDate.setOnClickListener(v -> datePicker.show(
				getActivity().getSupportFragmentManager(),
				"DATE"
		));
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

	@Override
	public void onResume() {
		super.onResume();

		//Cập nhật lại nếu người dùng thay đổi ngôn ngữ
		viewModel.getStartEndMilliseconds().setValue(viewModel.getStartEndMilliseconds().getValue());
	}
}