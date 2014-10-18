package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class TimeLineActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_time_line);
	}

}
