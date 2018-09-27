package com.idotalmor.triviaapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AnswersAdapter extends BaseAdapter {

    Context context;
    Question question;

    public AnswersAdapter(Context context,Question question){
        this.context = context;
        this.question = question;

    }

    @Override
    public int getCount() {
        return question.answers.length;
    }

    @Override
    public String getItem(int i) {
        return question.answers[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final String answer = question.answers[i];

        // create a new view if not created
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.answer_layout, null);
        }

        //get view subviews
        final TextView answerNum = (TextView)view.findViewById(R.id.answerNum);
        final TextView answerText =(TextView) view.findViewById(R.id.answerText);

        //change subviews
        answerNum.setTextColor(Color.WHITE);
        answerText.setTextColor(Color.BLACK);
        answerNum.setText(String.valueOf(i+1));
        answerText.setText(answer);

        //change drawable bg color to match category color - only works for 16 api and above

        if(Build.VERSION.SDK_INT>15){
            Drawable bg = view.getBackground();
            bg.setColorFilter(question.color, PorterDuff.Mode.SRC);
            view.setBackground(bg);}

        //set height and dimensions
        view.setMinimumHeight(viewGroup.getHeight()/2 -viewGroup.getPaddingBottom()-viewGroup.getPaddingTop()-20);

        //bring back the view with animation
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1).setDuration((i+1)*100);

        //on click will be applied after the onitemclick - which changes the view click listener to the on click instead of the on item click.
        //this is a work around to prevent the onitemclick run more than once - and still change the view color to the original color
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getBackground().setColorFilter(question.color, PorterDuff.Mode.SRC);

            }
        });

        view.setClickable(false);//the click will be consume by the parent view - on item click listener, after the onitemclicklistener will executed,
        // the clickable will be true and the normal onclick will consume the click


        //change color when user touchdown on the button
        // the onclick/itemclick method will be executed when the user stop touching the view but in touch down hierarchy(basically on-click executed when touch up event occurs)
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Drawable drawable = view.getBackground();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {//the only action that takes place because true is not returned.

                    drawable.setColorFilter(MainActivity.manipulateColor(question.color,0.8f), PorterDuff.Mode.SRC);
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) { }

                return false;
            }
        });

        return view;
    }
}
