package com.group4.khoatritoan.k_it.custom;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.khoatritoan.k_it.databinding.ItemLogBinding;
import com.group4.khoatritoan.k_it.entity.Log;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

	private List<Log> logs;

	public LogAdapter(List<Log> logs) {
		this.logs = logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
		this.notifyDataSetChanged();
	}

	public List<Log> getLogs() {
		return logs;
	}


//	@Override
//	public int getItemViewType(int position) {
//		return super.getItemViewType(position);
//	}

	@NonNull @NotNull @Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

		ItemLogBinding binding = ItemLogBinding.inflate(
				LayoutInflater.from(parent.getContext()), parent, false
		);
		return new ViewHolder(binding);
	}

	@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

		Log log = logs.get(position);

		// Số thứ tự nên bắt đầu sẽ là 1 thay vì 0
		holder.tvNO.setText(position + 1 + "");

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();

		date.setTime(log.start);
		holder.tvStart.setText(formatter.format(date));

		date.setTime(log.end);
		holder.tvEnd.setText(formatter.format(date));

		if (log.isReceived) {
			holder.tvIsReceived.setText("✔");
			holder.tvIsReceived.setTextColor(Color.parseColor("#F83AD879"));
		}
		else {
			holder.tvIsReceived.setText("✖");
			holder.tvIsReceived.setTextColor(Color.parseColor("#F8FF0000"));
		}
	}

	@Override
	public int getItemCount() {
		return (logs != null) ? logs.size() : 0;
	}

	//#region ViewHolder

	static class ViewHolder extends RecyclerView.ViewHolder {

		TextView tvNO;
		TextView tvStart;
		TextView tvEnd;
		TextView tvIsReceived;

		public ViewHolder(ItemLogBinding binding) {
			super(binding.getRoot());

			this.tvNO = binding.tvNO;
			this.tvStart = binding.tvStart;
			this.tvEnd = binding.tvEnd;
			this.tvIsReceived = binding.tvIsReceived;
		}
	}

	//#endregion
}