package com.group4.khoatritoan.k_it.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group4.khoatritoan.k_it.R;
import com.group4.khoatritoan.k_it.databinding.ItemNotificationBinding;
import com.group4.khoatritoan.k_it.model.NotificationModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

	private Context context;
	private List<NotificationModel> listNotifications;

	public NotificationAdapter(List<NotificationModel> listNotifications, Context context) {
		this.listNotifications = listNotifications;
		this.context = context;
	}

	public void setListNotifications(List<NotificationModel> listNotifications) {
		this.listNotifications = listNotifications;
		this.notifyDataSetChanged();
	}

	public List<NotificationModel> getListNotifications() {
		return listNotifications;
	}


//	@Override
//	public int getItemViewType(int position) {
//		return super.getItemViewType(position);
//	}

	@NonNull @NotNull @Override
	public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

		ItemNotificationBinding binding = ItemNotificationBinding.inflate(
				LayoutInflater.from(parent.getContext()), parent, false
		);
		return new ViewHolder(binding);
	}

	@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
	@Override
	public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {


		NotificationModel notification = listNotifications.get(position);

		// Số thứ tự nên bắt đầu bằng 1 thay vì 0
		holder.tvNO.setText(position + 1 + "");

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();

		date.setTime(notification.start);
		holder.tvStart.setText(formatter.format(date));

		date.setTime(notification.end);
		holder.tvEnd.setText(formatter.format(date));

		if (notification.isReceived) {
			holder.tvIsReceived.setText("✔");
			holder.tvIsReceived.setTextColor(context.getResources().getColor(R.color.safe));
		}
		else {
			holder.tvIsReceived.setText("✖");
			holder.tvIsReceived.setTextColor(context.getResources().getColor(R.color.danger));
		}
	}

	@Override
	public int getItemCount() {

		if (listNotifications != null)
			return listNotifications.size();
		return 0;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {

		TextView tvNO;
		TextView tvStart;
		TextView tvEnd;
		TextView tvIsReceived;


		public ViewHolder(ItemNotificationBinding binding) {
			super(binding.getRoot());

			this.tvNO = binding.tvNO;
			this.tvStart = binding.tvStart;
			this.tvEnd = binding.tvEnd;
			this.tvIsReceived = binding.tvIsReceived;
		}
	}
}
