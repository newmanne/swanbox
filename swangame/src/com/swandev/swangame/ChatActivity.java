package com.swandev.swangame;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.collect.Lists;

@ContentView(R.layout.chat_layout)
public class ChatActivity extends SwanRoboActivity {

	@InjectView(R.id.submit_button)
	Button submit_button;

	@InjectView(R.id.chat_log)
	ListView chat_log;

	@InjectView(R.id.user_text)
	EditText userTextField;

	List<ChatLogAdapter.LogPair> chat_history = Lists.newArrayList();
	String nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nickname = "w";
		// nickname = SocketIOState.get_nickname();

		submit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chat_text = userTextField.getText().toString();
				userTextField.getText().clear();

				chat_history
						.add(new ChatLogAdapter.LogPair(nickname, chat_text));

				chat_log.setAdapter(new ChatLogAdapter(ChatActivity.this,
						chat_history));
			}
		});
	}
}
