package com.example.randolph.irisapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothCommActivity extends Activity {

    private static final String arduinoBTAddress = "00:BA:55:56:86:33";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice arduinoBTDevice;
    private BluetoothSocket arduinoBTSocket;
    private BluetoothListenTask mBluetoothListenTask;
    private InputStream arduinoInStream;
    private OutputStream arduinoOutStream;
    private TextView mainTextView;
    private TextView msgTextView;

    private boolean connectedToArduino = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_comm);

        context = getApplicationContext();

        msgTextView = (TextView) findViewById(R.id.message_text_view);
        mainTextView = (TextView) findViewById(R.id.main_text_view);
        mainTextView.setText(String.format("Codename: %s\nSDK_INT: %d\nValue of JELLY_BEAN_MR1: %d",
                VERSION.CODENAME, VERSION.SDK_INT, VERSION_CODES.JELLY_BEAN_MR2));

        if (VERSION.SDK_INT <= VERSION_CODES.JELLY_BEAN_MR1)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        else
            mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                mainTextView.setText(String.format("Local bluetooth name: %s\nLocal bluetooth address: %s",
                        mBluetoothAdapter.getName(), mBluetoothAdapter.getAddress()));
                //getAllBondedDevices();
                connectToArduino();
            }
            else
                mainTextView.setText("Bluetooth not enabled.");
        }
    }

    private void connectToArduino() {
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
            Toast.makeText(context, "Established connection with Arduino!", Toast.LENGTH_SHORT).show();
        }
        catch (IllegalArgumentException ex) {
            Log.e("bluetooth exception", "Invalid address.");
            Toast.makeText(context, "Invalid MAC address specified.", Toast.LENGTH_SHORT).show();
        }
        catch (IOException ex) {
            Log.e("bluetooth exception", "Unable to establish connection.");
            Toast.makeText(context, "Unable to establish connection with Arduino!", Toast.LENGTH_SHORT).show();

            // close bluetooth connection
            try {
                arduinoBTSocket.close();
            }
            catch (Exception e) {
            }
        }
    }

    private void getAllBondedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        String btDevicesString = new String();

        for (BluetoothDevice btDevices : pairedDevices)
            btDevicesString = btDevices.getName() + " " + btDevices.getAddress() + "\n";

        if (pairedDevices.size() > 0)
            mainTextView.setText(btDevicesString);
        else
            mainTextView.setText("No paired devices.");
    }

    public void onLED(View v) {
        if (connectedToArduino) {
            try {
                arduinoOutStream.write('H');
            }
            catch (IOException ex) {
                Log.e("bluetooth exception", "Not connected to bluetooth device.");
            }
        }
        else {
            Toast.makeText(context, "Not connected to arduino.", Toast.LENGTH_SHORT).show();
        }
    }

    public void offLED(View v) {
        if (connectedToArduino) {
            try {
                arduinoOutStream.write('L');
            }
            catch (IOException ex) {
                Log.e("bluetooth exception", "Not connected to bluetooth device.");
            }
        }
        else {
            Toast.makeText(context, "Not connected to arduino.", Toast.LENGTH_SHORT).show();
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
                Log.e("bluetooth exception", "Not connected to bluetooth device.");
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            String fullString = new String();
            for (String s : progress)
                fullString = fullString + s;
            msgTextView.setText(fullString);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // stop listening task
            mBluetoothListenTask.pause();
            mBluetoothListenTask = null;
            // close bluetooth connection
            arduinoBTSocket.close();
        }
        catch (Exception e) {
        }
    }
}
