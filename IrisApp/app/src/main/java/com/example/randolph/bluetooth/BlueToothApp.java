// TODO: implement asynctask so that it can callback.
package com.example.randolph.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BlueToothApp extends Application {

    private static final String arduinoBTAddress = "00:BA:55:56:86:33";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice arduinoBTDevice;
    private BluetoothSocket arduinoBTSocket;
    private BluetoothListenTask mBluetoothListenTask;
    private InputStream arduinoInStream;
    private OutputStream arduinoOutStream;

    private boolean connectedToArduino = false;

    public boolean connect() {
        Context context = getApplicationContext();

        if (VERSION.SDK_INT <= VERSION_CODES.JELLY_BEAN_MR1)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        else
            mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                return connectToArduino();
            }
            else
                Log.e("Bluetooth Application", "Bluetooth not enabled.");
        }

        // not connected
        return false;
    }

    public boolean connectToArduino() {
        try {
            // get bluetooth device with specific MAC address
            arduinoBTDevice = mBluetoothAdapter.getRemoteDevice(arduinoBTAddress);
            // create RFCOMM socket to establish connection with device
            arduinoBTSocket = arduinoBTDevice.createRfcommSocketToServiceRecord(MY_UUID);
            // establish connection
            arduinoBTSocket.connect();

            arduinoOutStream = arduinoBTSocket.getOutputStream();
            arduinoInStream = arduinoBTSocket.getInputStream();

            mBluetoothListenTask = new BluetoothListenTask();
            mBluetoothListenTask.execute();

            connectedToArduino = true;
            return true;
        }
        catch (IllegalArgumentException ex) {
            Log.e("Bluetooth Application", Log.getStackTraceString(ex));
        }
        catch (IOException ex) {
            Log.e("Bluetooth Application", Log.getStackTraceString(ex));

            // close bluetooth connection
            try {
                arduinoBTSocket.close();
            }
            catch (Exception e) {
            }
        }

        // not connected
        return false;
    }

    public void write(char msg) {
        if (connectedToArduino) {
            try {
                arduinoOutStream.write(msg);
            }
            catch (IOException ex) {
                Log.e("Bluetooth Application", Log.getStackTraceString(ex));
            }
        }
        else {
            Log.e("Bluetooth Application", "Not connected to bluetooth device.");
        }
    }

    public void write(byte[] msg) {
        if (connectedToArduino) {
            try {
                arduinoOutStream.write(msg);
            }
            catch (IOException ex) {
                Log.e("Bluetooth Application", Log.getStackTraceString(ex));
            }
        }
        else {
            Log.e("Bluetooth Application", "Not connected to bluetooth device.");
        }
    }

    private class BluetoothListenTask extends AsyncTask<Void, String, Void> {

        private boolean running = true;

        public void pause() {
            running = false;
        }

        protected Void doInBackground(Void... params) {
            try {
                String inputFromArduino = new String();
                while (running) {
                    int bytesAvailable = arduinoInStream.available();
                    if (bytesAvailable > 0) {
                        byte[] buffer = new byte[bytesAvailable];
                        arduinoInStream.read(buffer);
                        inputFromArduino = inputFromArduino + new String(buffer);
                        // need to make sure the full message is obtained
                        if (inputFromArduino.contains("!")) {
                            publishProgress(inputFromArduino.trim());
                            inputFromArduino = "";
                        }
                    }
                }
            }
            catch (IOException ex) {
                Log.e("Bluetooth Application", Log.getStackTraceString(ex));
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String fullString = new String();
            for (String s : progress)
                fullString = fullString + s;
            //msgTextView.setText(fullString);
        }
    }
}
