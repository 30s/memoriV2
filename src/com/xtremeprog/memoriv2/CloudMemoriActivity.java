package com.xtremeprog.memoriv2;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xtremeprog.memoriv2.adapters.CloudMemoriListAdapter;
import com.xtremeprog.memoriv2.api.MemoriAPI;
import com.xtremeprog.memoriv2.models.CloudMemori;
import com.xtremeprog.memoriv2.utils.Preferences;
import com.xtremeprog.memoriv2.zxing.Intents;

public class CloudMemoriActivity extends Activity implements OnClickListener {

	private static final int RESULT_SCAN = 100;
	private CloudMemoriListAdapter memori_adapter;
	private MemoriAPI api_client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_memori);

		api_client = (MemoriAPI) MemoriAPI.getInstance(getBaseContext());

		TextView txt_username = (TextView) findViewById(R.id.txt_username);
		txt_username.setText("Cloud Account: "
				+ Preferences.getRememberedUsername(getApplicationContext()));

		Button btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_scan.setOnClickListener(this);
		
		Button btn_logout = (Button) findViewById(R.id.btn_logout);
		btn_logout.setOnClickListener(this);

		ListView lst_cloud_memori = (ListView) findViewById(R.id.lst_cloud_memori);
		memori_adapter = new CloudMemoriListAdapter(getApplicationContext());
		lst_cloud_memori.setAdapter(memori_adapter);

		new LoadMemoriTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cloud_memori, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_scan:
			intent = new Intent(Intents.Scan.ACTION);
			startActivityForResult(intent, RESULT_SCAN);
			break;
		case R.id.btn_logout:
			Preferences.expireToken(getApplicationContext());
			intent = new Intent(getBaseContext(), MemoriActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SCAN && resultCode == RESULT_OK) {
			String text = data.getExtras().getString(Intents.Scan.RESULT);
			memori_adapter.clear_memoris();
			new JoinMemoriTask().execute(text);
		}
	}

	private class LoadMemoriTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject ret = null;
			try {
				if (params.length != 0) {
					ret = api_client.memori(params[0], null);
				} else {
					ret = api_client.memori(null, null);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return ret;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null && result.has("objects")) {
				try {
					JSONArray json_array = result.getJSONArray("objects");
					for (int i = 0; i < json_array.length(); i++) {
						JSONObject j_memori = json_array.getJSONObject(i);
						String guid;
						String invite_code;
						long start_timestamp;
						ArrayList<String> owners = new ArrayList<String>();
						ArrayList<String> photos = new ArrayList<String>();
						guid = j_memori.getString("id");
						invite_code = j_memori.getString("invite_code");
						start_timestamp = j_memori.getLong("start_timestamp");
						JSONArray j_owners = j_memori.getJSONArray("owners");
						JSONArray j_photos = j_memori.getJSONArray("photos");
						for (int j = 0; j < j_owners.length(); j++) {
							owners.add(j_owners.getString(j));
						}
						for (int j = 0; j < j_photos.length(); j++) {
							photos.add(j_photos.getString(j));
						}
						memori_adapter.add_memori(new CloudMemori(guid,
								invite_code, start_timestamp, owners, photos));

					} // endfor
					memori_adapter.notifyDataSetChanged();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}// endif

			if (result != null && result.has("meta")) {
				String next = null;
				try {
					next = result.getJSONObject("meta").getString("next");
					if (!next.equals("null")) {
						new LoadMemoriTask().execute(next);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} // endif
		}
	}

	private class JoinMemoriTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String url = params[0];
			JSONObject ret = null;
			try {
				ret = api_client.memori_join(url, null);
				if (ret.has("status")) {
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
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				new LoadMemoriTask().execute();
			}
		}

	}
}
