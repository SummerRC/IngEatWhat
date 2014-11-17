package com.ing.eatwhat.activity;

import com.ing.eatwhat.R;
import com.ing.eatwhat.entity.User;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingActivity extends Activity implements
		SeekBar.OnSeekBarChangeListener {

	private TextView tx_sensitive;
	private SeekBar frag_more_seekbar;
	private Button bt_frag_more_save;
	private int sensitive;
	// 测试
	public int progress = 0;
	public SharedPreferences sensitiveSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		
		// 获得当前灵敏度
		sensitiveSp = getSharedPreferences(User.userName + "config", MODE_PRIVATE);
		sensitive = sensitiveSp.getInt("sensitive", 0);

		frag_more_seekbar = (SeekBar) findViewById(R.id.sensitivebar);
		frag_more_seekbar.setProgress(sensitive);
		frag_more_seekbar.setOnSeekBarChangeListener(this);
		
		tx_sensitive = (TextView) findViewById(R.id.sensitiveText);
		tx_sensitive.setText("当前灵敏度为:" + String.valueOf(sensitive));
		
		bt_frag_more_save = (Button) findViewById(R.id.bt_frag_more_save);
		bt_frag_more_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
				finish();
			}
		});
	}

	public void save() {
		Editor editor = sensitiveSp.edit();
		editor.putInt("sensitive", this.progress);
		editor.commit();
		
		User.sensitivity = this.progress;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// 拖动的时候
			this.progress = progress;
			tx_sensitive.setText("当前灵敏度:" + progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// 开始拖动的时候
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// 结束拖动的时候
	}
}
