package com.kpicture.watchcasting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        i = new Intent(this, RotationRead.class);
        Log.i("ROTATION", "Intent created");

        this.startService(i);
        Log.i("ROTATION", "Service started");

    }
    @Override
    public void onDestroy() {
        stopService(i);
        super.onDestroy();
    }
}
