package com.xtremeprog.memoriv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xtremeprog.memoriv2.zxing.Intents;

public class CloudMemoriActivity extends Activity implements OnClickListener {

	private static final int RESULT_SCAN = 100;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_memori);
        
        TextView txt_username = (TextView) findViewById(R.id.txt_username);
        txt_username.setText("Cloud Account: " + getBaseContext().getString(R.string.username));
        
        Button btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cloud_memori, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.btn_scan) {
			Intent intent = new Intent(Intents.Scan.ACTION);
			startActivityForResult(intent, RESULT_SCAN);	
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_SCAN && resultCode == RESULT_OK) {
			String text = data.getExtras().getString(Intents.Scan.RESULT);
			Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
		}
	}    
}
