package edu.csumb.hci.listenerplugin;


import android.content.Context;
import android.content.Intent;

public class ServiceConstructor {

    private Context context;
    private Intent intent;



    public ServiceConstructor(Context context) {

        this.context = context;
        this.intent = new Intent(this.context, ListenToWearableService.class);

        this.context.startService(this.intent);



    }
}
