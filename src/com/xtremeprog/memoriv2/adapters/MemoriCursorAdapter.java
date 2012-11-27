package com.xtremeprog.memoriv2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MemoriCursorAdapter extends SimpleCursorAdapter {

	private ImageLoader img_loader;
	
	public MemoriCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		img_loader = ImageLoader.getInstance();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        	.memoryCacheExtraOptions(80, 80).build();
		img_loader.init(config);
	}

	@Override
	public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
        	img_loader.displayImage("file://" + value, v);
        }
	}
	
}
