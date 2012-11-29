package com.xtremeprog.memoriv2;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.xtremeprog.memoriv2.adapters.MemoriListAdapter;
import com.xtremeprog.memoriv2.utils.Preferences;
import com.xtremeprog.memoriv2.utils.Utils;

public class MemoriActivity extends Activity implements
		LoaderCallbacks<Cursor>, OnClickListener {

	private MemoriListAdapter memori_adapter;
	private static final int MEMORI_LOADER = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memori);

		Context context = getBaseContext();

		Button btn_cloud_memori = (Button) findViewById(R.id.btn_cloud_memori);
		btn_cloud_memori.setOnClickListener(this);

		ListView lst_memori = (ListView) findViewById(R.id.lst_memori);
		memori_adapter = new MemoriListAdapter(context);
		lst_memori.setAdapter(memori_adapter);

		getLoaderManager().initLoader(MEMORI_LOADER, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_memori, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		if (id == MEMORI_LOADER) {
			String[] projections = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE,
					MediaStore.Images.Media.DISPLAY_NAME,
					MediaStore.Images.Media.MIME_TYPE,
					MediaStore.Images.Media.TITLE,
					MediaStore.Images.Media.DATE_ADDED,
					MediaStore.Images.Media.DATE_MODIFIED,
					MediaStore.Images.Media.DESCRIPTION,
					MediaStore.Images.Media.IS_PRIVATE,
					MediaStore.Images.Media.LATITUDE,
					MediaStore.Images.Media.LONGITUDE,
					MediaStore.Images.Media.DATE_TAKEN,
					MediaStore.Images.Media.ORIENTATION,
					MediaStore.Images.Media.MINI_THUMB_MAGIC,
					MediaStore.Images.Media.BUCKET_ID,
					MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

			return new CursorLoader(this, Images.Media.EXTERNAL_CONTENT_URI,
					projections, null, null, MediaStore.Images.Media.DATE_TAKEN
							+ " DESC");
		}
		throw new IllegalArgumentException("Unknow loader ID!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		memori_adapter.load_memoris(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		memori_adapter.load_memoris(null);
	}

	@Override
	public void onClick(View v) {		
		if (v.getId() == R.id.btn_cloud_memori) {
			Context context = getBaseContext();
			
			Date now = new Date();
			long expire = Preferences.getExpire(context) * 1000;

			if (Preferences.getToken(context) != null
					&& now.getTime() < expire) {
				Intent intent = new Intent(getBaseContext(),
						CloudMemoriActivity.class);
				startActivity(intent);
			} else {
				new LoginTask().execute(context.getString(R.string.username),
						context.getString(R.string.password));
			}			
		}
	}

	private class LoginTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			return Utils.login(getApplicationContext(), params[0], params[1]);
		}

		@Override
		protected void onPostExecute(JSONObject ret) {
			super.onPostExecute(ret);
			if (ret.has("token")) {
				Intent intent = new Intent(getBaseContext(),
						CloudMemoriActivity.class);
				startActivity(intent);
			} else {
				try {
					Toast.makeText(getApplicationContext(),
							ret.getString("message"), Toast.LENGTH_SHORT)
							.show();
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Login failed!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
