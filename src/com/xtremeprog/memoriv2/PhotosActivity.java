package com.xtremeprog.memoriv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import com.xtremeprog.memoriv2.adapters.PhotoListAdapter;
import com.xtremeprog.memoriv2.models.Memori;

public class PhotosActivity extends Activity {

    private PhotoListAdapter adapter;
	private Memori memori;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        
        GridView grid_photos = (GridView) findViewById(R.id.grid_photos);
        
        memori = getIntent().getExtras().getParcelable("memori");
        adapter = new PhotoListAdapter(getApplicationContext(), memori);
        grid_photos.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_photos, menu);
        return true;
    }

    
}
