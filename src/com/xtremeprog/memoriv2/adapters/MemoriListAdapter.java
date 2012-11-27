package com.xtremeprog.memoriv2.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.models.Memori;

public class MemoriListAdapter extends BaseAdapter {

	private final String TAG = MemoriListAdapter.class.getSimpleName();
	private ArrayList<Memori> lst_memori;
	private ImageLoader img_loader;
	
	public MemoriListAdapter(Context context) {
		lst_memori = new ArrayList<Memori>();
		img_loader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        	.memoryCacheExtraOptions(80, 80).build();
		img_loader.init(config);		
	}
	
	public void load_memoris(Cursor cursor) {
		lst_memori.clear();
		if ( cursor == null ) {
			return;
		}
		
		int col_date_taken = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
		int col_data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);		
		long last_date_taken = 0;
		Memori cur_memori = new Memori();
		while ( cursor.moveToNext() ) {
			long date_taken = cursor.getLong(col_date_taken);
			String data = cursor.getString(col_data);
			
			if ( (last_date_taken != 0) && ( last_date_taken - date_taken > 3600000 ) ) {
				lst_memori.add(cur_memori);				
				cur_memori = new Memori();
			}
			cur_memori.add_photo(data);
			last_date_taken = date_taken;
		}
		if ( cur_memori.get_photo_count() != 0 ) {
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
		ViewGroup view = (convertView instanceof ViewGroup) ? (ViewGroup) convertView
				: (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
						R.layout.memori_item, null);
		
		Memori memori = (Memori) getItem(position);
		TextView txt_photo_url = (TextView) view.findViewById(R.id.txt_photo_url);
		txt_photo_url.setText(memori.get_cover());
		ImageView img_photo = (ImageView) view.findViewById(R.id.img_photo);
		img_loader.displayImage("file://" + memori.get_cover(), img_photo);
		
		return view;
	}

}
