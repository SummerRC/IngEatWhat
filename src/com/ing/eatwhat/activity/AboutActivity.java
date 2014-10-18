package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.app.Activity;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
	}

	public void bt_back(View v) {
		finish();
	}
}
