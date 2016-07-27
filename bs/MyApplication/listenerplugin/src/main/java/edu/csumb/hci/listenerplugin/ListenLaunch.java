package edu.csumb.hci.listenerplugin;

import android.content.Intent;
import android.os.Bundle;


public class ListenLaunch extends UnityPlayerActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ListenToWearableService.class);
        startService(intent);


    }


}
