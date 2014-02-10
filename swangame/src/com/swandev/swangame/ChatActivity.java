package com.swandev.swangame;

import io.socket.IOAcknowledge;

import java.util.List;

import org.json.JSONArray;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

@ContentView(R.layout.chat_layout)
public class ChatActivity extends SwanRoboActivity {

	@InjectView(R.id.submit_button)
	Button submit_button;

	@InjectView(R.id.chat_log)
	ListView chat_log;

	@InjectView(R.id.user_text)
	EditText userTextField;

	@Inject
	SocketIOState socketIO;

	List<ChatLogAdapter.LogPair> chat_history = Lists.newArrayList();
	private String nickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nickname = socketIO.getNickname();

		submit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String chat_text = userTextField.getText().toString();
				userTextField.getText().clear();

				socketIO.getClient().emit(SocketIOEvents.USER_MESSAGE,
						new JSONArray().put(chat_text));
			}
		});

		socketIO.on(SocketIOEvents.MESSAGE_TO_ROOM, new EventCallback() {

			@Override
			public void onEvent(IOAcknowledge ack, Object... args) {
				final String username = (String) args[0];
				final String entry = (String) args[1];
				
				runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		            	String nameToDisplay = username;
						if (username.equals(nickname)) {
							nameToDisplay = "me";
						}
						chat_history.add(new ChatLogAdapter.LogPair(nameToDisplay, entry));

						chat_log.setAdapter(new ChatLogAdapter(ChatActivity.this,
								chat_history));
		            }
		        });
			}
		});
	}
}
