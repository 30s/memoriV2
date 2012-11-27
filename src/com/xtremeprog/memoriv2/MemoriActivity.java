package com.xtremeprog.memoriv2;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MemoriActivity extends Activity implements LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter memori_adapter;
	private static final int MEMORI_LOADER = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memori);
        
		String[] columns = new String[] { MediaStore.Images.Media.DATA };
		int[] to = new int[] { R.id.txt_photo_url };
        memori_adapter = new SimpleCursorAdapter(this,
				R.layout.memori_item, null, columns, to, 0);
        ListView lst_memori = (ListView) findViewById(R.id.lst_memori);
        lst_memori.setAdapter(memori_adapter);
        
        getLoaderManager().initLoader(MEMORI_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_memori, menu);
        return true;
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id,
			Bundle bundle) {
		if ( id == MEMORI_LOADER ) {
			String[] projections = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.SIZE,
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
					MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
			
			return new CursorLoader(this, Images.Media.EXTERNAL_CONTENT_URI, projections, null,
					null, null);			
		}
		throw new IllegalArgumentException("Unknow loader ID!");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		memori_adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		memori_adapter.swapCursor(null);
	}    
}
