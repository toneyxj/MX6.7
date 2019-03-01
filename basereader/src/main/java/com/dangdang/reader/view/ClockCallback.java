package com.dangdang.reader.view;

public interface ClockCallback {
	void onTweenValueChanged(float timeRatio);

	void onTweenStarted();

	void onTweenFinished();

}
