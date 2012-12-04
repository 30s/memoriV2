package com.xtremeprog.memoriv2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtremeprog.memoriv2.adapters.CloudMemoriListAdapter;
import com.xtremeprog.memoriv2.models.CloudMemori;
import com.xtremeprog.memoriv2.utils.Utils;
import com.xtremeprog.memoriv2.zxing.Intents;

public class CloudMemoriActivity extends Activity implements OnClickListener {

	private static final int RESULT_SCAN = 100;
	private ArrayList<JSONObject> memoris;
	private CloudMemoriListAdapter memori_adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_memori);

		memoris = new ArrayList<JSONObject>();

		TextView txt_username = (TextView) findViewById(R.id.txt_username);
		txt_username.setText("Cloud Account: "
				+ getBaseContext().getString(R.string.username));

		Button btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_scan.setOnClickListener(this);

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
		if (v.getId() == R.id.btn_scan) {
			Intent intent = new Intent(Intents.Scan.ACTION);
			startActivityForResult(intent, RESULT_SCAN);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SCAN && resultCode == RESULT_OK) {
			String text = data.getExtras().getString(Intents.Scan.RESULT);
			Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
		}
	}

	private class LoadMemoriTask extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			return Utils.load_memori_list(getApplicationContext(), memoris);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			Context context = getApplicationContext();
			try {
				String message = result.getString("message");
				if (message.equals("401")) {
					String username = context.getString(R.string.username);
					String password = context.getString(R.string.password);
					// login
				}
			} catch (JSONException e) {
				for (int i = 0; i < memoris.size(); i++) {
					JSONObject j_memori = memoris.get(i);
					String guid;
					String invite_code;
					long start_timestamp;
					ArrayList<String> owners = new ArrayList<String>();
					ArrayList<String> photos = new ArrayList<String>();
					try {
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
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
				memori_adapter.notifyDataSetChanged();
			}
		}
	}
}
