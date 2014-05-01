package com.uchi.uchianimals;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookConnectActivity extends Activity {

	PostToWall post;

	private static final String APP_ID = "587576081265593";
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private Facebook facebook;
	private String messageToPost;
	private EditText userMessage;

	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_network);
		userMessage = (EditText) findViewById(R.id.userMessage);
	}

	public void doNotShare(View button) {
		finish();
	}

	public void share(View button) {
		String facebookMessage = getIntent().getStringExtra("facebookMessage");
		if (facebookMessage == null) {
			try {
				String message = userMessage.getText().toString();
				facebookMessage = message + " - " + getString(R.string.facebookPost);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		messageToPost = facebookMessage;

		if (!facebook.isSessionValid()) {
			loginAndPostToWall();
		} else {
			new PostToWall().execute(facebookMessage);
		}
	}

	public void loginAndPostToWall() {
		facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}

	class PostToWall extends AsyncTask<String, Void, Boolean> {
		public Boolean doInBackground(String... message) {

			Bundle parameters = new Bundle();
			parameters.putString("message", message[0]);
			parameters.putString("description", "topic share");
			try {
				facebook.request("me");
				String response = facebook.request("me/feed", parameters, "POST");
				Log.d("Tests", "got response: " + response);
				if (response == null || response.equals("") || response.equals("false")) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			return false;
		}

		public void onPostExecute(Boolean result) {
			if (result) {
				showToast("posted successfully");
			} else {
				showToast("couldn't post to FB.");
			}
			finish();
		}
	}

	class LoginDialogListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			if (messageToPost != null) {
				post.execute(messageToPost);
			}
		}

		@Override
		public void onFacebookError(FacebookError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		@Override
		public void onError(DialogError error) {
			showToast("Authentication with Facebook failed!");
			finish();
		}

		@Override
		public void onCancel() {
			showToast("Authentication with Facebook cancelled!");
			finish();
		}
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	public void onBackClicked(View v) {
		finish();
	}

	public void onExitClick(View v) {
		finish();
	}
}