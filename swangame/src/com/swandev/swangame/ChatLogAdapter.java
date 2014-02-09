package com.swandev.swangame;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatLogAdapter extends BaseAdapter {

	@Data
	@RequiredArgsConstructor(suppressConstructorProperties = true)
	public static class LogPair {
		private final String username;
		private final String entry;
	}

	private final List<LogPair> mHistory;
	private final LayoutInflater mInflater;

	public ChatLogAdapter(Context context, List<LogPair> history) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHistory = history;
	}

	@Override
	public int getCount() {
		return mHistory.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View retView = mInflater.inflate(R.layout.chat_entry_layout, null);

		TextView usernameField = (TextView) retView.findViewById(R.id.username);
		TextView entryField = (TextView) retView.findViewById(R.id.entry);

		LogPair log = mHistory.get(position);

		usernameField.setText(log.getUsername() + ":");
		entryField.setText(log.getEntry());

		if (position % 2 == 0) {
			retView.setBackgroundColor(Color.WHITE);
		} else {
			retView.setBackgroundColor(Color.GRAY);
		}

		return retView;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
