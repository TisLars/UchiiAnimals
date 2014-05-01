package com.uchi.uchianimals;

import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class Options extends Activity {

	// music attributes
	private boolean mIsBound = false;
	private MusicService mServ;

	// Options elements
	private ToggleButton btnConference;

	private RadioButton optionDutch;
	private RadioButton optionEnglish;
	private CheckBox musicCheck;

	Locale locale;
	Configuration config = new Configuration();

	// sharedpreferences
	// SharedPreferences userPrefs;
	public static String userSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);

		optionDutch = (RadioButton) findViewById(R.id.optionDutch);
		optionEnglish = (RadioButton) findViewById(R.id.optionEnglish);
		musicCheck = (CheckBox) findViewById(R.id.musicCheck);
		btnConference = (ToggleButton) findViewById(R.id.btnConference);

		loadPrefs();

		// Respond to clicks
		optionDutch.setOnClickListener(userSettingsOnClick);
		optionEnglish.setOnClickListener(userSettingsOnClick);
		btnConference.setOnClickListener(userSettingsOnClick);

		doBindService();
		this.mServ = MainActivity.mServ;
	}

	private void loadPrefs() {
		SharedPreferences userPrefs = getSharedPreferences("userPrefs", 0);

		if (userPrefs.getBoolean("optionDutch", true)) {
			initializeLanguage("nl");
			optionDutch.setChecked(true);
		} else if (userPrefs.getBoolean("optionEnglish", true)) {
			initializeLanguage("en_US");
			optionEnglish.setChecked(true);
		}

		if (userPrefs.getBoolean("musicCheck", true)) {
			musicCheck.setChecked(true);
		} else {
			musicCheck.setChecked(false);
		}

	}

	private void savePrefs() {
		SharedPreferences userPrefs = getSharedPreferences("userPrefs", 0);
		SharedPreferences.Editor editor = userPrefs.edit();

		if (optionDutch.isChecked()) {
			editor.putBoolean("optionDutch", true);
			editor.putBoolean("optionEnglish", false);
		} else if (optionEnglish.isChecked()) {
			editor.putBoolean("optionEnglish", true);
			editor.putBoolean("optionDutch", false);
		}

		if (musicCheck.isChecked()) {
			editor.putBoolean("musicCheck", true);
		} else {
			editor.putBoolean("musicCheck", false);
		}
		editor.commit();
	}

	final OnClickListener userSettingsOnClick = new OnClickListener() {
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.optionDutch:
				initializeLanguage("nl");
				finish();
				break;
			case R.id.optionEnglish:
				initializeLanguage("en_US");
				finish();
				break;
			}
		}
	};

	private void initializeLanguage(String code) {
		locale = new Locale(code);
		Locale.setDefault(locale);
		config = new Configuration();
		config.locale = locale;
		getBaseContext().getApplicationContext().getResources().updateConfiguration(config, null);
	}

	private ServiceConnection Scon = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService() {
		bindService(new Intent(this, MusicService.class), Scon, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mServ != null) {
			mServ.resumeMusic();
		}
	}

	@Override
	protected void onPause() {
		super.onStop();
		doUnbindService();
		savePrefs();
		if (mServ != null) {
			mServ.pauseMusic();
		}
	}

	public void onBackClicked(View v) {
		finish();
	}

	public void onExitClick(View v) {
		finish();
	}
}