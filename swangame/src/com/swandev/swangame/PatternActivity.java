package com.swandev.swangame;

import io.socket.IOAcknowledge;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.json.JSONArray;
import org.json.JSONException;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

@ContentView(R.layout.pattern_layout)
public class PatternActivity extends SwanRoboActivity {

	@InjectView(R.id.red)
	Button redButton;
	@InjectView(R.id.green)
	Button greenButton;
	@InjectView(R.id.blue)
	Button blueButton;
	@InjectView(R.id.startGame)
	Button startGame;

	@InjectView(R.id.clear)
	Button clearButton;

	@Getter
	@InjectView(R.id.history_list)
	ListView historyList;

	@Getter
	@Setter
	List<String> history = Lists.newArrayList();

	@Getter
	List<Button> patternButtons;

	@Getter
	@Setter
	private List<String> pattern = Lists.newArrayList();

	@Inject
	SocketIOState socketIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init the buttons
		redButton.setOnClickListener(new UpdatePatternOnClickListener(getString(R.string.red)));
		greenButton.setOnClickListener(new UpdatePatternOnClickListener(getString(R.string.green)));
		blueButton.setOnClickListener(new UpdatePatternOnClickListener(getString(R.string.blue)));
		patternButtons = Lists.newArrayList(redButton, greenButton, blueButton);
		clearButton.setOnClickListener(new ClearHistoryOnClickListener());
		startGame.setVisibility(socketIO.isHost() ? View.VISIBLE : View.GONE);
		startGame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				socketIO.getClient().emit(SocketIOEvents.GAME_STARTED);
				startGame.setVisibility(View.GONE);
				startGame.setEnabled(false);
				socketIO.on(SocketIOEvents.PATTERN_REQUESTED, new EventCallback() {

					@Override
					public void onEvent(IOAcknowledge ack, Object... args) {
						final List<String> newPattern = Lists.newArrayList();
						JSONArray jsonArrray = (JSONArray) args[0];
						for (int i = 0; i < jsonArrray.length(); i++) {
							try {
								newPattern.add(jsonArrray.getString(i));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						setPattern(newPattern);
						setButtonEnables(true);
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void setButtonEnables(final boolean status) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				for (Button button : getPatternButtons()) {
					button.setEnabled(status);
				}
			}
		});
	}

	@RequiredArgsConstructor(suppressConstructorProperties = true)
	public class UpdatePatternOnClickListener implements View.OnClickListener {

		final String patternString;

		@Override
		public void onClick(View v) {
			final List<String> pattern = getPattern();
			final String nextPatternElement = getPattern().get(0);
			boolean isValid = true;
			pattern.remove(0); // pop
			history.add(patternString);
			SwanUtils.updateListViewWithList(PatternActivity.this, getHistoryList(), history);
			if (!patternString.equals(nextPatternElement)) {
				isValid = false;
			}
			if (!isValid || pattern.isEmpty()) {
				socketIO.getClient().emit(SocketIOEvents.PATTERN_ENTERED, new JSONArray().put(isValid));
				setButtonEnables(false);
				history.clear();
				SwanUtils.updateListViewWithList(PatternActivity.this, getHistoryList(), history);
			}
		}

	}

	@RequiredArgsConstructor(suppressConstructorProperties = true)
	public class ClearHistoryOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			final List<String> history = getHistory();
			history.clear();
			SwanUtils.updateListViewWithList(PatternActivity.this, getHistoryList(), history);
		}
	}

}
