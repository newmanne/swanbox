package com.swandev.swangame;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

@ContentView(R.layout.pattern_layout)
public class PatternActivity extends SwanRoboActivity {

	@InjectView(R.id.red)
	Button redButton;
	@InjectResource(R.string.red)
	String red;
	@InjectView(R.id.green)
	Button greenButton;
	@InjectResource(R.string.green)
	String green;
	@InjectView(R.id.blue)
	Button blueButton;
	@InjectResource(R.string.blue)
	String blue;
	
	@InjectView(R.id.clear)
	Button clearButton;
	
	@Getter
	@InjectView(R.id.history_list)
	ListView history_list;
	
	@Getter
	@Setter
	List<String> history = Lists.newArrayList();
	
	@Getter
	List<Button> patternButtons;
	
	@Getter
	@Setter
	List<String> pattern = Lists.newArrayList();
	
	@Inject
	SocketIOState socketIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init the buttons
		redButton.setOnClickListener(new UpdatePatternOnClickListener(red));
		greenButton.setOnClickListener(new UpdatePatternOnClickListener(green));
		blueButton.setOnClickListener(new UpdatePatternOnClickListener(blue));
		clearButton.setOnClickListener(new ClearHistoryOnClickListener());
		patternButtons = Lists.newArrayList(redButton, greenButton, blueButton);
		
		setButtonEnables(true);
//		socketIO.getClient().on("PATTERN_REQUESTED_FROM_CLIENT", new EventCallback() {
//			
//			@Override
//			public void onEvent(JSONArray args, Acknowledge ack) {
//				setButtonEnables(true);
//				setPattern((List<String>)args.opt(0));
//			}
//		});
//		socketIO.getClient().emitEvent("PATTERN_REQUESTED_FROM_SERVER");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void setButtonEnables(boolean status) {
		for (Button button : getPatternButtons()) {
			button.setEnabled(status);
		}
	}
	
	@RequiredArgsConstructor(suppressConstructorProperties=true)
	public class UpdatePatternOnClickListener implements View.OnClickListener {

		final String patternString;
		
		public final static String PATTERN_ENTERED = "PATTERN_ENTERED";
		
		// TODO: this code is super non-modular...
		// TODO: can try using acknowledges and doing server side validation so other users can watch the pattern as you input it
		
		@Override
		public void onClick(View v) {
			final List<String> history = getHistory();
			history.add(patternString);
			
			// Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
			
			SwanUtils.updateListViewWithList(PatternActivity.this, getHistory_list(), history);
			
//			if (pattern.isEmpty()) {
//				socketIO.getClient().emit(PATTERN_ENTERED, new JSONArray().put(ImmutableMap.of("Valid", true, "Extension", patternString)));
//				setButtonEnables(false);
//			} else {
//				final String nextPatternElement = pattern.get(0);
//				if (patternString.equals(nextPatternElement)) {
//					pattern.remove(0); //pop
//				} else {
//					socketIO.getClient().emit(PATTERN_ENTERED, new JSONArray().put(ImmutableMap.of("Valid", false)));
//					setButtonEnables(false);
//				}
			//}
			
		}
		
	}
	
	@RequiredArgsConstructor(suppressConstructorProperties=true)
	public class ClearHistoryOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			final List<String> history = getHistory();
			history.clear();
			SwanUtils.updateListViewWithList(PatternActivity.this, getHistory_list(), history);
			
		}
		
	}

}
