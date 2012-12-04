package com.xtremeprog.memoriv2.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xtremeprog.memoriv2.CloudMemoriActivity;
import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.api.MemoriAPI;
import com.xtremeprog.memoriv2.models.Memori;
import com.xtremeprog.memoriv2.models.Photo;
import com.xtremeprog.memoriv2.utils.Utils;

public class MemoriListAdapter extends BaseAdapter {

	private class ViewHolder {

		public TextView txt_photo_url;
		public ImageView img_photo;
		public TextView txt_count;
		public Button btn_upload;
		public Memori memori;
	}

	private final String TAG = MemoriListAdapter.class.getSimpleName();
	private ArrayList<Memori> lst_memori;
	private ImageLoader img_loader;
	private Context context;
	private Memori cur_memori;
	private MemoriAPI api_client;

	public MemoriListAdapter(Context context) {
		this.context = context;
		lst_memori = new ArrayList<Memori>();
		img_loader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).memoryCacheExtraOptions(80, 80).build();
		img_loader.init(config);
		api_client = (MemoriAPI) MemoriAPI.getInstance(context);
	}

	public void load_memoris(Cursor cursor) {
		lst_memori.clear();
		if (cursor == null) {
			return;
		}

		int col_date_taken = cursor
				.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
		int col_data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
		long last_date_taken = 0;
		Memori cur_memori = new Memori();
		while (cursor.moveToNext()) {
			long date_taken = cursor.getLong(col_date_taken);
			String data = cursor.getString(col_data);

			if ((last_date_taken != 0)
					&& (last_date_taken - date_taken > 3600000)) {
				lst_memori.add(cur_memori);
				cur_memori = new Memori();
			}
			cur_memori.add_photo(new Photo(data, date_taken));
			last_date_taken = date_taken;
		}
		if (cur_memori.get_photo_count() != 0) {
			lst_memori.add(cur_memori);
		}
		notifyDataSetInvalidated();
		Log.d(TAG, "Loading memori done!");
	}

	@Override
	public int getCount() {
		return lst_memori.size();
	}

	@Override
	public Object getItem(int position) {
		return lst_memori.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.memori_item, null);
			holder = buildTag(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Memori memori = (Memori) getItem(position);
		holder.memori = memori;
		holder.txt_photo_url.setText(memori.get_cover());
		img_loader.displayImage("file://" + memori.get_cover(),
				holder.img_photo);
		holder.txt_count.setText(memori.get_photo_count() + "");

		return convertView;
	}

	private ViewHolder buildTag(View convertView) {
		final ViewHolder holder;
		holder = new ViewHolder();
		holder.txt_photo_url = (TextView) convertView
				.findViewById(R.id.txt_photo_url);
		holder.img_photo = (ImageView) convertView.findViewById(R.id.img_photo);
		holder.txt_count = (TextView) convertView.findViewById(R.id.txt_count);
		holder.btn_upload = (Button) convertView.findViewById(R.id.btn_upload);
		holder.btn_upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new CreateMemoriTask().execute(holder.memori);
			}
		});
		return holder;
	}

	private class CreateMemoriTask extends AsyncTask<Memori, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Memori... params) {
			cur_memori = params[0];
			return Utils.create_memori(context, params[0]);
		}

		@Override
		protected void onPostExecute(JSONObject ret) {
			super.onPostExecute(ret);
			
			String guid;
			try {
				guid = ret.getString("id");
			} catch (JSONException e1) {
				Toast.makeText(context, "Create memori failed!",
						Toast.LENGTH_SHORT).show();				
				return;
			}
			
			for (int i = 0; i < cur_memori.get_photo_count(); i++) {
				new UploadPhotoTask().execute(guid, i + "");
			}
		}
	}

	private class UploadPhotoTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String guid = params[0];
			Photo photo = cur_memori.get_photo(Integer.parseInt(params[1]));
			try {
				JSONObject ret = api_client.photo_upload(new File(photo.getPath()), photo, null, guid, null);
				if ( ret.has("error_message") ) {
					Toast.makeText(context, ret.getString("error_message"),
							Toast.LENGTH_SHORT).show();				
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
