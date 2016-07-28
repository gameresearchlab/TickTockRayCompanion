package edu.csumb.hci.listenerplugin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;


public class ListenToWearableService extends WearableListenerService {
    public static String message = "nothing";
    public Context con;
    public void onCreate() {
        con = this;
        new Thread(new Runnable()
        {
            @Override
            public void run() {

                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(con)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle connectionHint) {
                                Log.e("WATCHCASTING:", "onConnected: " + connectionHint);
                                // Now you can use the Data Layer API
                            }

                            @Override
                            public void onConnectionSuspended(int cause) {
                                Log.e("WATCHCASTING:", "onConnectionSuspended: " + cause);
                            }
                        })
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.e("WATCHCASTING:", "onConnectionFailed: " + result);
                            }
                        })
                        // Request access only to the Wearable API
                        .addApi(Wearable.API)
                        .build();

                    mGoogleApiClient.connect();

                Log.e("WATCHCASTING", "Watch connected? :" + mGoogleApiClient.isConnected());

                Wearable.MessageApi.addListener(mGoogleApiClient, new MessageApi.MessageListener() {
                    @Override
                    public void onMessageReceived(MessageEvent messageEvent) {
                        try {
                            message = (new String(messageEvent.getData()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public static String getCurrentData(){
        return message;
    }
}