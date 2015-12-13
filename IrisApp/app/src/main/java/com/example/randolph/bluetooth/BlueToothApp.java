package com.example.randolph.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BlueToothApp extends Application {

    private static final String arduinoBTAddress = "00:BA:55:56:86:33";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String ADD_TAG = "-ADDTAG-";
    private static final String DELETE_TAG = "-DELETETAG-";
    private static final String ENABLE_TAG = "-ENABLETAG-";
    private static final String DISABLE_TAG = "-DISABLETAG-";
    private static final String LOSE_TAG = "-LOSETAG-";
    private static final String FOUND_TAG = "-FOUNDTAG-";
    private static final String SET_THRESHOLD = "-SETTHRESHOLD-";
    private static final String GET_THRESHOLD = "-GETTHRESHOLD-";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice arduinoBTDevice;
    private BluetoothSocket arduinoBTSocket;
    private InputStream arduinoInStream;
    private OutputStream arduinoOutStream;
    private String inputFromArduino;

    private boolean connectedToArduino = false;

    public boolean isConnected() {
        return connectedToArduino;
    }

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

            inputFromArduino = new String();

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

    public void write(String msg) {
        msg = "-" + msg + "-";
        if (connectedToArduino) {
            try {
                arduinoOutStream.write(msg.getBytes());
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

    public String read() {
        try {
            int bytesAvailable = arduinoInStream.available();
            if (bytesAvailable > 0) {
                byte[] buffer = new byte[bytesAvailable];
                arduinoInStream.read(buffer);
                inputFromArduino = inputFromArduino + new String(buffer);
                // need to make sure the full message is obtained
                Log.e("readFromArduino",inputFromArduino);
                if (inputFromArduino.contains("!")) {
                    inputFromArduino = inputFromArduino.trim();
                    String toReturn = inputFromArduino.substring(0, inputFromArduino.length() - 1);
                    inputFromArduino = "";
                    return toReturn;
                }
            }
        }
        catch (IOException ex) {
            Log.e("Bluetooth Application", Log.getStackTraceString(ex));
        }

        return null;
    }

    public void addTag() {
        write(ADD_TAG.getBytes());
    }

    public void deleteTag() {
        write(DELETE_TAG.getBytes());
    }

    public void disableTag() {
        write(DISABLE_TAG.getBytes());
    }

    public void enableTag() {
        write(ENABLE_TAG.getBytes());
    }

    public void loseTag() {
        write(LOSE_TAG.getBytes());
    }

    public void foundTag() {
        write(FOUND_TAG.getBytes());
    }

    public void getThreshold(){ write(GET_THRESHOLD.getBytes());}

    public void setThreshold(){ write(SET_THRESHOLD.getBytes());}

    public void close() {
        try {
            // close bluetooth connection
            arduinoBTSocket.close();
        }
        catch (Exception e) {
        }
    }
}
