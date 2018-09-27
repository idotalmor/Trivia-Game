package com.idotalmor.triviaapp;

import android.database.Cursor;

public class Question {

    String question;
    public String[] answers;
    public int color,questionID,correctAnswer;

    Question(Cursor cursor){
        if (cursor == null)return;

        cursor.moveToFirst();
        question=cursor.getString(cursor.getColumnIndexOrThrow("question"));

        answers = new String[4];
        for(int i=0;i<4;i++){
            answers[i]= cursor.getString(cursor.getColumnIndexOrThrow("answer"+(i+1)));}

            questionID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            correctAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("correct_answer"));

    }

    public boolean CheckAnswer(int Answer){
        if((Answer+1)== correctAnswer)return true;
        return false;}

    @Override
    public String toString() {
        return "question: "+question+", answers by order:"+answers[0]+", "+answers[1]+", "+answers[2]+", "+answers[3]+", " +
                " questionId "+String.valueOf(questionID)+" , Correct Answer Number "+ String.valueOf(correctAnswer);
    }
}
