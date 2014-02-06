package com.swandev.swangame;

import com.google.inject.AbstractModule;

public class BindingsModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(SwanIOCallback.class).to(PatternIO.class);
	}

}
