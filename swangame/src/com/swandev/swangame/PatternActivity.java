package com.swandev.swangame;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

@ContentView(R.layout.pattern_layout)
public class PatternActivity extends RoboActivity {

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
	
	@Getter
	List<Button> patternButtons = Lists.newArrayList(redButton, greenButton, blueButton);
	
	@Getter
	@Setter
	List<String> pattern = Lists.newArrayList();
	
	@Inject
	PatternIO patternIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init the buttons
		redButton.setOnClickListener(new UpdatePatternOnClickListener(red));
		greenButton.setOnClickListener(new UpdatePatternOnClickListener(green));
		blueButton.setOnClickListener(new UpdatePatternOnClickListener(blue));
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
		
		@Override
		public void onClick(View v) {
			final List<String> pattern = getPattern();
			if (pattern.isEmpty()) {
				patternIO.sendPatternSuccess(patternString);
				setButtonEnables(false);
			} else {
				final String nextPatternElement = pattern.get(0);
				if (patternString.equals(nextPatternElement)) {
					pattern.remove(0); //pop
				} else {
					patternIO.sendPatternFail();
				}
			}
			
		}
		
	}

	public void handlePattern(List<String> pattern) {
		setPattern(pattern);
		setButtonEnables(true);
	}

}
