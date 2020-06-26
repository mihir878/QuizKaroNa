package com.example.triviatest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviatest.controller.AppController;
import com.example.triviatest.data.AnswerListAsyncResponse;
import com.example.triviatest.data.QuestionBank;
import com.example.triviatest.model.Question;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MESSAGE_OK ="message_prefs" ;
    private TextView questionText;
    private TextView questionCounterText;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private int currentQuestionIndex=0;
    private TextView yourScore;
    private TextView heightestScore;
    private int score=0,high=0;
    private List<Question> questionList;
    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText=findViewById(R.id.question_text);
        questionCounterText=findViewById(R.id.counter_text_view);
        trueButton=findViewById(R.id.true_button);
        falseButton=findViewById(R.id.false_button);
        prevButton=findViewById(R.id.prev_button);
        nextButton=findViewById(R.id.next_button);
        yourScore=findViewById(R.id.score_text_view);
        heightestScore=findViewById(R.id.heighest_score_text_view);
        shareButton=findViewById(R.id.button_share);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        SharedPreferences getSharedPrefs=getSharedPreferences(MESSAGE_OK,MODE_PRIVATE);
        high=getSharedPrefs.getInt("message",0);
        heightestScore.setText(String.valueOf(high));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                //questionList=questionArrayList;
                questionCounterText.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex+1, questionArrayList.size()));
                questionText.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                Log.d("Main", "onCreate: "+questionArrayList);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prev_button:
                if(currentQuestionIndex>0){
                currentQuestionIndex=(currentQuestionIndex-1)%questionList.size();
                updateQuestion();
                }
                break;
            case R.id.next_button:
                currentQuestionIndex=(currentQuestionIndex+1)%questionList.size();
                updateQuestion();
                break;
            case R.id.true_button:
                checkAnswer(true);
                currentQuestionIndex=(currentQuestionIndex+1)%questionList.size();
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                    }
                },1000);
                break;
            case R.id.false_button:
                checkAnswer(false);
                currentQuestionIndex=(currentQuestionIndex+1)%questionList.size();
                Handler handler1=new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                    }
                },1000);
                break;
            case R.id.button_share:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"My current score is "+yourScore.getText()+" and My highest score is "
                                             +heightestScore.getText()+" What's about yours?");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Hello! I am playing QuizKaroNa App");
                startActivity(intent);
                break;
        }
    }

    private void checkAnswer(boolean b) {
        boolean actualAnswer=questionList.get(currentQuestionIndex).isAnswerTrue();
        if(b==actualAnswer){
            fadeView();
            score++;
            yourScore.setText(String.valueOf(score));
            Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();}
        else{
            shakeAnimation();
            Toast.makeText(MainActivity.this,"Wrong!",Toast.LENGTH_SHORT).show();}
        int h1=Integer.valueOf(heightestScore.getText().toString().trim());
        if(score>h1){
           heightestScore.setText(String.valueOf(score));
        }
    }

    private void updateQuestion() {
        String question=questionList.get(currentQuestionIndex).getAnswer();
        questionCounterText.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
        questionText.setText(question);
    }

    private void fadeView(){
        final CardView cardView1=findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView1.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView1.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                 cardView1.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation(){
        updateQuestion();
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPreferences=getSharedPreferences(MESSAGE_OK,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("message",max(high,score));
        editor.apply();
    }
}
