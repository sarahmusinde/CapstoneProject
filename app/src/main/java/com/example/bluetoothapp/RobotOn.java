package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

public class RobotOn extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextToSpeech textToSpeech;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private ConstraintLayout constraintLayout;
     SwipeListener swipeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_on);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","US");
                    textToSpeech.setLanguage(localeToUse);
                    textToSpeech.speak("Click on the screen to turn on the robot", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        // Initialize the Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Request Bluetooth permission if Bluetooth is not enabled
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Connect to the HC-06 module
        mDevice = mBluetoothAdapter.getRemoteDevice("00:22:06:01:E6:0C"); // Replace with the MAC address of your HC-06 module
        try {
            mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            mSocket.connect();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
          constraintLayout=(ConstraintLayout)findViewById(R.id.layoutOn);
         swipeListener= new SwipeListener(constraintLayout);

        // Set up a button to send a command to the Arduino
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand("1"); // Replace "command" with the command you want to send to the Arduino
            }
        });
    }
    private void sendCommand(String command) {
        try {
            mOutputStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SwipeListener implements View.OnTouchListener {
        //initializeVariables
        GestureDetector gestureDetector;
        //constructor
        SwipeListener(View view){
            //treshold value
            int threshold = 100;
            int velocity_treshold =100;
            //simple gesture detector
            GestureDetector.SimpleOnGestureListener listener=new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX()-e1.getX();
                    float yDiff=e2.getY()-e1.getY();
                    try{
                        //check condtion
                        if(Math.abs(xDiff)>Math.abs(yDiff)){
                            if(Math.abs(xDiff)>threshold
                                    && Math.abs(velocityX)>velocity_treshold){
                                if(xDiff> 0){
                                    //swip right

                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                                    startActivity(intent);


                                }//ends swip lesft
                                else{
                                    textToSpeech.speak("Swip right to go to main menu", TextToSpeech.QUEUE_FLUSH, null);
                                }
                                return true;
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            };
            //initialize gesture
            gestureDetector = new GestureDetector(listener);
            //setlistener
            view.setOnTouchListener(this);
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //return gesture event
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }

}