package com.xtremeprog.memoriv2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class MemoriCursorAdapter extends SimpleCursorAdapter {

	private static int THUMBSIZE = 80;
	
	public MemoriCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
        	Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(value), THUMBSIZE, THUMBSIZE);
            v.setImageBitmap(ThumbImage);
        }
	}
	
}
