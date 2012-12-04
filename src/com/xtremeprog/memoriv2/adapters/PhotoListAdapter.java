package com.xtremeprog.memoriv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.models.Memori;
import com.xtremeprog.memoriv2.models.Photo;

public class PhotoListAdapter extends BaseAdapter {

	public class ViewHolder {

		public Photo photo;
		public ImageView img_photo;

	}

	private Memori memori;
	private ImageLoader img_loader;
	private Context context;
	private ViewHolder holder;
	
	public PhotoListAdapter(Context context, Memori memori) {
		this.context = context;
		this.memori = memori;
		img_loader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).memoryCacheExtraOptions(80, 80).build();
		img_loader.init(config);		
	}
	
	@Override
	public int getCount() {
		return memori.get_photo_count();
	}

	@Override
	public Object getItem(int position) {
		return memori.get_photo(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if ( convertView == null ) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_photo, null);
			holder = buildTag(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Photo photo = (Photo) getItem(position);
		holder.photo = photo;
		holder.img_photo.setImageResource(R.drawable.empty_photo);
		img_loader.displayImage("file://" + photo.getPath(), holder.img_photo); 
		
		return convertView;
	}

	private ViewHolder buildTag(View convertView) {
		final ViewHolder holder;
		holder = new ViewHolder();
		holder.img_photo = (ImageView) convertView.findViewById(R.id.img_photo);
		
		return holder;
	}

}
