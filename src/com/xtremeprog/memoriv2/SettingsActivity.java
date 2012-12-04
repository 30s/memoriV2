package com.xtremeprog.memoriv2;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.xtremeprog.memoriv2.api.MemoriAPI;
import com.xtremeprog.memoriv2.utils.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {

	private MemoriAPI api_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		api_client = (MemoriAPI) MemoriAPI.getInstance(getApplicationContext());
		
		Button btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_save:
			EditText edit_username = (EditText) findViewById(R.id.edit_username);
			EditText edit_password = (EditText) findViewById(R.id.edit_password);
			new LoginTask().execute(edit_username.getText().toString(),
					edit_password.getText().toString());
			break;
		default:
			break;
		}
	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				JSONObject ret = api_client.account_login(params[0], params[1],
						null);
				if (ret.has("token")) {
					Preferences.setLoginInfo(getApplicationContext(),
							params[0],
							ret.getString("token"),
							ret.getString("refresh_token"),
							ret.getLong("expire"));
					return true;
				} 
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean ret) {
			super.onPostExecute(ret);
			if (ret) {
				Intent intent = new Intent(getBaseContext(),
						CloudMemoriActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"Username or password error!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
