package com.example.quizbowl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView questionField, answerField, score, stats;
    Button correct, incorrect, buzz;
    boolean toRead, goNext;
    Question rq;
    String[] txt;
    int points;
    String TAG = "com.example.quizbowl";
    LifecycleData currentRun, lifeTime;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        questionField = findViewById(R.id.questionField);
        answerField = findViewById(R.id.answerField);
        score = findViewById(R.id.score);
        correct = findViewById(R.id.correct);
        incorrect = findViewById(R.id.incorrect);
        buzz = findViewById(R.id.buzz);
        stats = findViewById(R.id.stats);
        sharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String lifecycleDataAsString = sharedPreferences.getString("lifetime", "");

        if (lifecycleDataAsString.equals("")){
            lifeTime = new LifecycleData();
            lifeTime.duration = "Lifetime Data";
        } else {
            lifeTime = LifecycleData.parseJSON(lifecycleDataAsString);
        }

        String pointsString = sharedPreferences.getString("points", "");

        if (pointsString.equals("")){
            points = 0;
            stats.setText(Integer.toString(points));
        } else {
            points = Integer.parseInt(pointsString);
            stats.setText(pointsString);
        }

        print(lifeTime.toString());
        // Lifetime Data
        // onCreate 0
        // onDestroy 0

        toRead = true;
        goNext = false;

        // change to use shared preferences


        ArrayList<Question> Questions = importDatabase();
        rq = getRandomQuestion(Questions);
        txt = rq.getQuestionText().split(" ");

        int i = 0;
        read(txt, i);

        buzz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRead = false;

                answerField.setText(rq.getAnswerText());
            }
        });

        incorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                points += -10;
                score.setText(Integer.toString(points));
                int i = 0;
                toRead = true;
                questionField.setText("");
                answerField.setText("");
                rq = getRandomQuestion(Questions);
                txt = rq.getQuestionText().split(" ");
                storePoints();
                read(txt, i);
            }
        });

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                points += 10;
                score.setText(Integer.toString(points));
                int i = 0;
                toRead = true;
                questionField.setText("");
                answerField.setText("");
                rq = getRandomQuestion(Questions);
                txt = rq.getQuestionText().split(" ");
                storePoints();
                read(txt, i);
            }
        });

        String currentEnclosingMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();
        updateCount(currentEnclosingMethod);
    }
    protected void onDestroy(){
        super.onDestroy();
        String currentEnclosingMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();
        updateCount(currentEnclosingMethod);
    }
    public void storeData(){
        editor.putString("lifetime",lifeTime.toJSON()).apply();
    }
    public void storePoints() {
        editor.putString("points",Integer.toString(points)).apply();
    }
    public void read(String[] txt, int i) {
        Handler handler = new Handler();

        final int finalI = i;

        handler.postDelayed(new Runnable() {
            public void run() {
                if(toRead == true) {
                    showNextWord(txt[finalI]);

                    if(txt.length > (finalI+1)) {
                        int temp = finalI + 1;
                        read(txt, temp);
                    }
                }

            }
        }, 300);
    }
    public void showNextWord(String words) {
        questionField.setText(questionField.getText().toString() + words + " ");
    }
    public void updateCount(String currentEnclosingMethod){
        //pass name to LifecycleData to update count
        lifeTime.updateEvent(currentEnclosingMethod);
        storeData();
        displayData();
    }
    public void displayData(){
        stats.setText(lifeTime.toString());
    }
    public void print(String str) {
        System.out.println(str);
    }
    public ArrayList<Question> importDatabase() {
        ArrayList<Question> Questions = new ArrayList<>();

        InputStream is = getResources().openRawResource(R.raw.database);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line;
        try {
            while ( (line = reader.readLine()) != null) {
                // tokenize
                String[] tokens = line.split(",");
                // question + " " + answer
                String text = tokens[0] + " ANSWER: " + tokens[1];
                // import into arraylist
                Question temporaryQuestion = new Question(text);
                Questions.add(temporaryQuestion);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Questions;
    }
    public Question getRandomQuestion(ArrayList<Question> my_list) {
        int index = (int)(Math.random() * my_list.size());
        return my_list.get(index);
    }
}