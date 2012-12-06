package com.xtremeprog.memoriv2;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class LocalFragmentActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_local_fragment, menu);
        return true;
    }

    
}
