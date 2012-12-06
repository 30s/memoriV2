package com.xtremeprog.memoriv2.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xtremeprog.memoriv2.R;
import com.xtremeprog.memoriv2.utils.Preferences;
import com.xtremeprog.memoriv2.utils.Utils;

public class CloudPhotoListAdapter extends BaseAdapter {

	private ArrayList<String> photos;
	private ImageLoader img_loader;
	private DisplayImageOptions options;

	public CloudPhotoListAdapter(Context context, ArrayList<String> photos) {
		this.photos = photos;
		options = Utils.getCloudOptions();
		img_loader = Utils.getImageLoader(context);
	}

	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Object getItem(int position) {
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_cloud_photo, null);
		}

		String photo = (String) getItem(position);
		ImageView img_cloud_photo = (ImageView) convertView
				.findViewById(R.id.img_cloud_photo);
		img_loader.displayImage(Preferences.getServer(parent.getContext())
				+ "/photo/" + photo + "/?size=285", img_cloud_photo, options);

		return convertView;
	}

}
