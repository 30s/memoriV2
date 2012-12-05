package com.xtremeprog.memoriv2;

import com.xtremeprog.memoriv2.adapters.OwnerListAdapter;
import com.xtremeprog.memoriv2.models.CloudMemori;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class CloudPhotoActivity extends Activity {

	private CloudMemori memori;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud_photo);

		TextView txt_guid = (TextView) findViewById(R.id.txt_guid);
		TextView txt_start_timestamp = (TextView) findViewById(R.id.txt_start_timestamp);
		ListView lst_owners = (ListView) findViewById(R.id.lst_owners);
		// GridView grid_photos = (GridView) findViewById(R.id.grid_photos);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			memori = extras.getParcelable("memori");
			txt_guid.setText(memori.get_guid());
			txt_start_timestamp.setText(memori.get_start_timestamp() + "");
			OwnerListAdapter owner_adpter = new OwnerListAdapter(memori.get_owners());
			lst_owners.setAdapter(owner_adpter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_cloud_photo, menu);
		return true;
	}

}
