package com.example.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import androidx.core.content.ContextCompat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
   EditText editText;

    TextToSpeech textToSpeech;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    ImageButton btnCircle;


    SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
    RelativeLayout relativeLayout;
    String currentDateAndTime = sdf.format(new Date());
    private Vibrator vibrator;
    SwipeListener swipeListener;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();


         relativeLayout=(RelativeLayout) findViewById(R.id.content);
         //initialize listener
        swipeListener=new SwipeListener(relativeLayout);


        btnCircle =(ImageButton) findViewById(R.id.btnCircle);
        editText= (EditText) findViewById(R.id.editText);
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE) ;

        //Text to speech inialization
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","US");
                    textToSpeech.setLanguage(localeToUse);
                    textToSpeech.speak("you are in main menu! Swipt right to speak or speak left to listen the commands", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });



        //when the text is changed

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask(){
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run(){

                        String  str = editText.getText().toString();
                        if(str.equals("Hello")){
                            editText.setText("Hello, how can i help you");
                            textToSpeech.speak("Hello, how can i help you", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("What is your name")){
                            editText.setText("My name is SDRB");
                            textToSpeech.speak("My name is SDRB", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("How are you")){
                            editText.setText("I m a robot, i am never tired");
                            textToSpeech.speak("I m a robot, i am never tired ", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("What can I ask you")){
                            editText.setText("you can ask me anything to help you");
                            textToSpeech.speak("you can ask me anything to help you", TextToSpeech.QUEUE_FLUSH,null,null);
                        }
                        else if(str.equals("Tell me a joke")){
                            editText.setText("two sheep said baa in the field, other one said shit i wanna say that");
                            textToSpeech.speak("two sheep said baa in the field, other one said shit i wanna say that", TextToSpeech.QUEUE_FLUSH,null,null);
                        }

                        else if(str.equals("What is the time")){


                            editText.setText("The time is "+currentDateAndTime);
                            textToSpeech.speak("The time is "+currentDateAndTime, TextToSpeech.QUEUE_FLUSH,null,null);

                        }


                        else if(str.equals("Open commands")){
                           Intent intent = new Intent(getApplicationContext(),Command.class);

                           startActivity(intent);


                        }
                        else if(str.equals("battery")){
                            Intent intent = new Intent(getApplicationContext(),Battery.class);

                            startActivity(intent);
                        }


                        else if(str.equals("Turn on robot")){
                            Intent intent = new Intent(getApplicationContext(),RobotOn.class);

                            startActivity(intent);
                        }


                        else if(str.equals("exit")){

                            textToSpeech.speak("Exiting the app ", TextToSpeech.QUEUE_FLUSH,null,null);
                            vibrator.vibrate(100);
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                           System.exit(1);
                        }
                        //t1.speak(str, TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                },1000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }//onCreate bundle ends



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                   Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                editText.setText(
                        Objects.requireNonNull(result).get(0));
            }


        }

    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
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

                                   editText.setText("Listening");
                                  //vibrator.vibrate(100);
                                   Intent intent
                                           = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                   intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                           RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                   intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                                           Locale.getDefault());
                                   intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                                   vibrator.vibrate(100);
                                   try {
                                       startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                                   }
                                   catch (Exception e) {
                                       Toast
                                               .makeText(MainActivity.this, " " + e.getMessage(),
                                                       Toast.LENGTH_SHORT)
                                               .show();
                                   }



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


