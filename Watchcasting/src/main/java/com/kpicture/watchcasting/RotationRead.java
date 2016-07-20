package com.kpicture.watchcasting;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class RotationRead extends Service implements SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private long timeElapsed = 0;
    private static final long TIME_TH = 300;
    private static final float TH = 3f;
    private int mouseState = 0;
    private Node phone;
    private Sensor rotation;
    private Sensor acceleration;
    private SensorManager sensorManager;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // every 20ms

        //sensorManager.registerListener(this, rotation, 50000); // every 20ms
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME);
        // every 20ms
        //sensorManager.registerListener(this, acceleration, 20000); // every 20ms
        sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_GAME);


        // connect to Companion app
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected())
            Log.i("ROTATION", "connected");
        else
            Log.i("ROTATIONS", "not connected");

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                phone = getConnectedNodesResult.getNodes().get(0);
                Log.i("ROTATION", "found node to connect to");
            }
        });

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this,rotation);
        sensorManager.unregisterListener(this, acceleration);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(Bundle bundle) {
       Log.e("ROTATION", "Succesfully connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("ROTATION", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("ROTATION", "Connection Failed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String message = "";
       try {
           switch( event.sensor.getType() ) {
               case Sensor.TYPE_ROTATION_VECTOR:
                   message += event.values[0] + "," + event.values[1] + "," +event.values[2] + "," + event.values[3] + "," + mouseState;
                   if (phone != null) {
                       Wearable.MessageApi.sendMessage(mGoogleApiClient, phone.getId(), "ORIENTATION", message.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                           @Override
                           public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                               //Log.e("WearableSensor", "Sensor data sent" + sendMessageResult.toString());
                           }
                       });
                   }
                   break;
               case Sensor.TYPE_ACCELEROMETER:

                   if (System.currentTimeMillis() - timeElapsed > TIME_TH && (event.values[0] > TH || event.values[0] < -TH) ) {
                       timeElapsed = System.currentTimeMillis();
                       if (mouseState == 0)
                       {
                           mouseState = 1;
                       }
                       else
                       {
                           mouseState = 0;
                       }
                   }
               default:
                break;
           }


       } catch (Exception e) {
            e.printStackTrace();
       }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        Log.e("WearableSensor", "Accuracy of sensor changed");
    }
}
