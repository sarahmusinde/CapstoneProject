package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Locale;

public class Command extends AppCompatActivity {
    TextToSpeech textToSpeech;
    SwipeListener myswipeListener;
    RelativeLayout relativeLayoutcommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        //Text to speech inialization
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","US");
                    textToSpeech.setLanguage(localeToUse);


                    textToSpeech.speak("here are the importants commands\nread\ntime\nbattery\nturn on robot\n turn off robot\nweather\nbattery\nexit\nlocation\nswip right to go to main menu or swip left to listen again", TextToSpeech.QUEUE_FLUSH, null);

                }
            }
        });

        relativeLayoutcommand=(RelativeLayout) findViewById(R.id.command);
        //initialize listener
        myswipeListener=new SwipeListener(relativeLayoutcommand);
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
                                    Intent intent = new Intent(getApplicationContext(),Command.class);

                                    startActivity(intent);
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