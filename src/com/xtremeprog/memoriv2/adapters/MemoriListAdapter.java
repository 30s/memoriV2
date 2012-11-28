package com.xtremeprog.memoriv2.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.models.Memori;
import com.xtremeprog.memoriv2.zxing.Contents;
import com.xtremeprog.memoriv2.zxing.Intents;

public class MemoriListAdapter extends BaseAdapter {
	
	private class ViewHolder {

		public TextView txt_photo_url;
		public ImageView img_photo;
		public TextView txt_count;
		public Button btn_qrcode;
		public Memori memori;}

	private final String TAG = MemoriListAdapter.class.getSimpleName();
	private ArrayList<Memori> lst_memori;
	private ImageLoader img_loader;
	private Context context;
	
	public MemoriListAdapter(Context context) {
		this.context = context;
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
		ViewHolder holder;
		if ( convertView == null ) {
			convertView =  LayoutInflater.from(parent.getContext()).inflate(
					R.layout.memori_item, null);
			holder = buildTag(convertView); 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}	
		
		Memori memori = (Memori) getItem(position);
		holder.memori = memori;
		holder.txt_photo_url.setText(memori.get_cover());
		img_loader.displayImage("file://" + memori.get_cover(), holder.img_photo);
		holder.txt_count.setText(memori.get_photo_count() + "");
		
		return convertView;
	}

	private ViewHolder buildTag(View convertView) {
		final ViewHolder holder;
		holder = new ViewHolder();
		holder.txt_photo_url = (TextView) convertView.findViewById(R.id.txt_photo_url);
		holder.img_photo = (ImageView) convertView.findViewById(R.id.img_photo);
		holder.txt_count = (TextView) convertView.findViewById(R.id.txt_count);
		holder.btn_qrcode = (Button) convertView.findViewById(R.id.btn_qrcode);
		holder.btn_qrcode.setOnClickListener(new View.OnClickListener() { 
			
			@Override
			public void onClick(View v) {
				Toast.makeText(MemoriListAdapter.this.context, holder.memori.get_photo_count() + "", Toast.LENGTH_SHORT).show();
			    Intent intent = new Intent(Intents.Encode.ACTION);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
			    intent.putExtra(Intents.Encode.DATA, "hello world!/" + holder.memori.get_photo_count());
			    intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
			    context.startActivity(intent);				
			}
		});
		return holder;
	}

}
