package com.idotalmor.triviaapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements SpinningWheelView.OnRotationListener<String>{

    RelativeLayout relativeLayout;
    GridView gridView;
    LinearLayout scoreLayout;
    SpinningWheelView wheelView;
    ParticleSystem blueConfetti,yellowConfetti,redConfetti,greenConfetti;
    MediaPlayer wheelSound, successSound,failedSound;
    TextView questionTTL,scoreSum;
    ImageView starIcon;
    Handler handler;
    TextToSpeech textToSpeech;
    Runnable speakingRunnable;
    SQLiteDatabase db;
    int score;
    SharedPreferences prefs;
    List topicList;
    int[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBManager dbManager = DBManager.GetDBHelper(this);
        db = dbManager.getWritableDatabase();

        relativeLayout = findViewById(R.id.relativeLayout);
        gridView = findViewById(R.id.gridView);
        scoreLayout = findViewById(R.id.scoreLayout);
        wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        wheelSound =MediaPlayer.create(getApplicationContext(),R.raw.spinningwheelshortsound);
        successSound =MediaPlayer.create(getApplicationContext(),R.raw.bar_chimes_sound);
        failedSound = MediaPlayer.create(getApplicationContext(),R.raw.failed);
        questionTTL = findViewById(R.id.questionTTL);
        scoreSum = findViewById(R.id.scoreSum);
        starIcon = findViewById(R.id.starIcon);
        handler = new Handler();
        prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        score = prefs.getInt("score",0);//try to get score from shared preferences - default value zero
        scoreSum.setText(Integer.toString(score));//display the score in textview
        colors = new int[6];

        //initiate colors array for topics
        int [] wheelColors = wheelView.getColors();
        for(int i=0;i<wheelColors.length;i++){
            colors[i] = wheelColors[i];
        }
        //workaround known random color
        colors[colors.length-1] = wheelColors[2];


        // Can be array string or topicList of topics
        String [] topics = {"Animal Trivia","Art Trivia","Computer Trivia","Food Trivia","Movie Trivia","Music Trivia"};

        topicList = new ArrayList(asList(topics));//convert string array to arraylist for wheel third library

        wheelView.setItems(topicList);
        wheelView.setOnRotationListener(this);
        wheelView.setEnabled(false);

        //initializing text to speech object

        textToSpeech=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {//success initialize text to speech object
                }
            }
        });

        //movie trivia is always the starting topic selected in wheel view
        inflateQuestion("Movie Trivia");

    }


    @Override
    protected void onPause() {
        stopTTS();
        prefs.edit().putInt("score",score).apply();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //kill text to speech service when app destroyed
        if(textToSpeech!=null){
        textToSpeech.stop();
        textToSpeech.shutdown();}

        super.onDestroy();

    }

    public void rotate(View v){

        //disappear question title with animation
        questionTTL.animate().alpha(0).setDuration(300);
        removeAnswers();

        wheelView.rotate(70, 3000, 50);

    }

    @Override
    public void onRotation() {

        //stop text to speech voice
        stopTTS();

        //stop all music and seek wheeling sound to start
        if(wheelSound.isPlaying()){
            wheelSound.seekTo(0);}
        wheelSound.start();
    }

    @Override
    public void onStopRotation(String item) {

        //Display chosen topic with TextView

       final TextView textView = new TextView(this);

       textView.setText(item);
        textView.setTypeface(Typeface.create("amiko_bold", Typeface.BOLD));
        textView.setTextSize(30);

        //add the TextView to parent at last index
       relativeLayout.addView(textView,relativeLayout.getChildCount());
       textView.bringToFront();

       //Position the TextView to the center of the wheel
        RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams)textView.getLayoutParams();
        layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        textView.setY(wheelView.getY()+wheelView.getHeight()/2-70);
        textView.setLayoutParams(layoutparams);

        //Scale animation
        textView.animate().scaleXBy(2).scaleYBy(2).setDuration(1000);

        //removing the textview from parent when finish animate

        final Runnable removeFromParentRun = new Runnable() {
            @Override
            public void run() {
                relativeLayout.removeView(textView);
            }
        };
        Runnable fadeRun = new Runnable() {
            @Override
            public void run() {
                if(Build.VERSION.SDK_INT>15){
                textView.animate().alpha(0).setDuration(200).withEndAction(removeFromParentRun);}
                else{
                    textView.animate().alpha(0).setDuration(200);
                    handler.postDelayed(removeFromParentRun,200);
                    //runOnUiThread(removeFromParentRun);
                }
            }
        };
        handler.postDelayed(fadeRun,800);

        //get question
        inflateQuestion(item);

    }



    public void inflateQuestion(final String item){

        //query for unanswered questions
        String query = "SELECT * FROM '"+item+"' WHERE chosen_answer IS NULL ";
        Cursor cursor = db.rawQuery(query, null);

        //if the user answered every question in the category
        if(cursor.getCount()==0){

            questionTTL.setText("You have already answered all the questions in this category!");
            //TODO - Music
            questionTTL.setAlpha(1);
            removeAnswers();

            makeConfetti();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopConfetti();
                }
            },2000);

            return;

        }

        //get a question from db - question constructor takes the first result from the cursor
        final Question question = new Question(cursor);

        questionTTL.setText(question.question);
        questionTTL.setScaleX(2);
        questionTTL.setScaleY(2);
        questionTTL.animate().scaleY(1).scaleX(1).alpha(1).setDuration(1000);

        //set color for question
        int colorIndex = topicList.indexOf(item);//get index by topic string
        question.color = colors[colorIndex];//get the color by index from colors array

        //display answers with adapter
        gridView.setAdapter(new AnswersAdapter(this,question));

        //saving the runnable in class scope to kill it if user spin the wheel again - delayed post.
        speakingRunnable = new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak(question.question, TextToSpeech.QUEUE_FLUSH, null);
            }};

        handler.postDelayed(speakingRunnable,1600);


        //set on item click listener

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                //set the onclicklistener to be the view listener instead of onitemclicklisterer which comes from parent(grid) - workaround to disable clickable after answering the question
                for(int j=0;j<gridView.getChildCount();j++){
                    gridView.getChildAt(j).setClickable(true);
                }

                //update the question with the user answer
                db.execSQL("UPDATE '"+item+"' SET chosen_answer="+(i+1)+" WHERE _id="+question.questionID+";");

                view.getBackground().setColorFilter(question.color, PorterDuff.Mode.SRC);
                stopTTS();

                if(question.CheckAnswer(i)){

                    //if answer is correct

                    score++;
                    successSound.start();
                    //speak correct with flush queue in case the question is still being read
                    textToSpeech.speak("Correct",TextToSpeech.QUEUE_FLUSH,null);

                    //star coming out btn effect
                    new ParticleSystem(MainActivity.this, 100, R.drawable.star_icon, 1000)
                            .setSpeedRange(0.2f, 0.5f)
                            .oneShot(view, 50);

                    //main star added
                    final ImageView star = new ImageView(getApplicationContext());
                    star.setImageResource(R.drawable.star_icon_big);
                    star.setVisibility(View.GONE);
                    relativeLayout.addView(star);

                    //set star size to 1 for scaling purposes
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)star.getLayoutParams();
                    layoutParams.height = 1;
                    layoutParams.width = 1;

                    //set margins
                    final float scale = getResources().getDisplayMetrics().density;//convert px to dp
                    layoutParams.setMargins((int) (5 * scale + 0.5f),0,(int) (5 * scale + 0.5f),0);
                    star.setLayoutParams(layoutParams);

                    //position the star at the center of the wheel
                    star.setY(wheelView.getY()+wheelView.getHeight()/2);
                    star.setX(relativeLayout.getWidth()/2);
                    star.bringToFront();

                    //make it visible
                    star.setVisibility(View.VISIBLE);

                    //animate the star to fit wheel size
                    star.animate().scaleY(wheelView.getHeight()).scaleX(wheelView.getWidth()).rotationYBy(720).setDuration(800);

                    //animate the star to the same size and location as the star icon - where the score is written
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //animate the star to fit score star icon size
                            star.animate().scaleY(starIcon.getHeight()).scaleX(starIcon.getWidth()).rotationYBy(720).setDuration(300);

                            //after 100 milliseconds move the star to the icon position
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //animate transition to score star point
                                  star.animate().x(starIcon.getX()+starIcon.getWidth()/2)
                                          .y(starIcon.getY()+scoreLayout.getY()+starIcon.getHeight()/2)
                                          .setDuration(300);

                                  //after star merging with the star icon set the current score
                                  handler.postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          scoreSum.setText(Integer.toString(score));
                                          //removing the view
                                          relativeLayout.removeView(star);
                                      }
                                  },300);
                                }
                            },100);

                        }
                    },850);


                }else{
                    //if answer is incorrect

                    //remove point from user if score greater then zero and display the current score
                    if(score>0){score --;
                    scoreSum.setText(Integer.toString(score));}

                    failedSound.start();

                    //get correct answer view
                    final View correctAnswer = gridView.getChildAt(question.correctAnswer-1);

                    //set flicker green color for the correct answer
                    new CountDownTimer(14000,200){
                        boolean originalColor = true;
                        @Override
                        public void onTick(long l) {
                            if(originalColor){
                        correctAnswer.getBackground().setColorFilter(Color.parseColor("#00cc00"), PorterDuff.Mode.SRC);
                            }else{
                                correctAnswer.getBackground().setColorFilter(question.color, PorterDuff.Mode.SRC);
                            }
                            originalColor = !originalColor;
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();

                    //star coming from score to wheel animation
                    final ImageView star = new ImageView(getApplicationContext());
                    star.setImageResource(R.drawable.star_icon_big);
                    relativeLayout.addView(star);

                    //set star size to 1 for scaling purposes
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)star.getLayoutParams();
                    layoutParams.height = 1;
                    layoutParams.width = 1;

                    //set margins
                    final float scale = getResources().getDisplayMetrics().density;//convert px to dp
                    layoutParams.setMargins((int) (5 * scale + 0.5f),0,(int) (5 * scale + 0.5f),0);
                    star.setLayoutParams(layoutParams);

                    //scale to star icon size
                    star.animate().scaleY(starIcon.getHeight()).scaleX(starIcon.getWidth()).setDuration(0);

                    //set position as star icon
                    star.setX(starIcon.getX()-scoreLayout.getPaddingLeft()+starIcon.getWidth()/2);
                    star.setY(starIcon.getY()+scoreLayout.getY()+starIcon.getHeight()/2);

                    //animate star to the center of the wheel
                    star.animate().x(relativeLayout.getWidth()/2)
                            .y(wheelView.getY()+wheelView.getHeight()/2).setDuration(300);

                    //at the same time animate scaling to wheel size as well
                    star.animate().scaleX(wheelView.getWidth()).scaleY(wheelView.getHeight()).setDuration(300);

                    //after animation complete - star is in the center of the wheel and with the same size
                    //animate the star getting smaller into the wheel while rotating
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            star.animate().scaleX(1).scaleY(1).rotationYBy(720).setDuration(800);

                            //after star is disappear remove from parent
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                  relativeLayout.removeView(star);
                                }
                            },800);
                        }
                    }, 300);

                }

                //after each animation complete - inflate the next question
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                     inflateQuestion(item);
                    }
                }, 1400);
            }
        });

        //set touch listener in cases the user press answer and move-up with finger - to restore to view original color
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()==motionEvent.ACTION_UP){

                for (int i=0;i<gridView.getChildCount();i++){
                    gridView.getChildAt(i).getBackground().setColorFilter(question.color, PorterDuff.Mode.SRC);
                }}

                return false;
            }
        });


    }

    public void removeAnswers(){

        //remove answers view with animation for api 16 and above
        for(int i=0;i<gridView.getChildCount();i++){
            final View child = gridView.getChildAt(i);
            if(Build.VERSION.SDK_INT>15){
                child.animate().alpha(0).setDuration(300).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        child.setVisibility(View.GONE);
                    }
                });}else{ child.setAlpha(0);
                child.setVisibility(View.GONE);}
        }

    }

    public void stopTTS(){
        //flush text to speech queue and remove handler post delay runnable for speaking
        if(textToSpeech.isSpeaking()){textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null);}
        handler.removeCallbacks(speakingRunnable);
    }

    public void makeConfetti(){

        int displayWidthPixel = getResources().getDisplayMetrics().widthPixels;

        blueConfetti = new ParticleSystem(this, 80, R.drawable.confetti1, 10000);
        blueConfetti.setSpeedModuleAndAngleRange(0f, 0.3f, 100, 180)
                .setRotationSpeed(60)
                .setAcceleration(0.0005f, 90).emit(displayWidthPixel,0,8);

        yellowConfetti = new ParticleSystem(this, 80, R.drawable.confetti2, 10000);
        yellowConfetti.setSpeedModuleAndAngleRange(0f, 0.3f, 360, 360)
                .setRotationSpeed(144)
                .setAcceleration(0.0005f, 90).emit(0,0,8);

        redConfetti = new ParticleSystem(this, 80, R.drawable.confetti3, 10000);
        redConfetti.setSpeedModuleAndAngleRange(0f, 0.3f, 360, 360)
                .setRotationSpeed(60)
                .setAcceleration(0.0005f, 90).emit(displayWidthPixel/4,0,8);

        greenConfetti = new ParticleSystem(this, 80, R.drawable.confetti4, 10000);
        greenConfetti.setSpeedModuleAndAngleRange(0f, 0.3f, 100, 180)
                .setRotationSpeed(60)
                .setAcceleration(0.0005f, 90).emit(displayWidthPixel-displayWidthPixel/4,0,8);

    }

    public void stopConfetti(){
        blueConfetti.stopEmitting();
        yellowConfetti.stopEmitting();
        redConfetti.stopEmitting();
        greenConfetti.stopEmitting();
    }

    //change to darker given color
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }
}
