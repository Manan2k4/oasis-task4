package com.oasis.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView txtProgressIndicator, txtQuestionText;
    private Button btnOptionA, btnOptionB, btnOptionC, btnOptionD, btnNextQuestion;

    // Academic Mock Database Arrays
    private final String[] questionsPool = {
            "Which chemical element has the symbol Au?",
            "What is the capital city of Australia?",
            "How many bones are there in an adult human body?",
            "Which planet is known as the Red Planet?",
            "What is the primary gas found in the air we breathe?"
    };

    private final String[][] choicesMatrix = {
            {"Silver", "Gold", "Copper", "Iron"},
            {"Sydney", "Melbourne", "Canberra", "Brisbane"},
            {"106", "206", "306", "406"},
            {"Venus", "Mars", "Jupiter", "Saturn"},
            {"Oxygen", "Carbon Dioxide", "Hydrogen", "Nitrogen"}
    };

    private final int[] correctAnswersIndex = {1, 2, 1, 1, 3}; // Zero-indexed answers maps

    // Active Engine State Tracking Indicators
    private int currentQuestionIndex = 0;
    private int chosenAnswerIndex = -1;
    private int accumulativePointsScore = 0;
    private boolean isAnswerSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtProgressIndicator = findViewById(R.id.txtProgressIndicator);
        txtQuestionText = findViewById(R.id.txtQuestionText);

        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);

        btnOptionA.setOnClickListener(this);
        btnOptionB.setOnClickListener(this);
        btnOptionC.setOnClickListener(this);
        btnOptionD.setOnClickListener(this);
        btnNextQuestion.setOnClickListener(this);

        populateQuestionState();
    }

    private void populateQuestionState() {
        isAnswerSubmitted = false;
        chosenAnswerIndex = -1;
        btnNextQuestion.setText("Submit Answer");
        btnNextQuestion.setEnabled(false);

        // Reset option button visual styling back to defaults
        resetOptionButtonsStyle();

        // Update indicators
        txtProgressIndicator.setText(String.format(Locale.US, "Question: %d/%d", (currentQuestionIndex + 1), questionsPool.length));
        txtQuestionText.setText(questionsPool[currentQuestionIndex]);
        btnOptionA.setText(choicesMatrix[currentQuestionIndex][0]);
        btnOptionB.setText(choicesMatrix[currentQuestionIndex][1]);
        btnOptionC.setText(choicesMatrix[currentQuestionIndex][2]);
        btnOptionD.setText(choicesMatrix[currentQuestionIndex][3]);
    }

    private void resetOptionButtonsStyle() {
        Button[] options = {btnOptionA, btnOptionB, btnOptionC, btnOptionD};
        for (Button btn : options) {
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFEFEF")));
            btn.setTextColor(Color.parseColor("#333333"));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnOptionA || id == R.id.btnOptionB || id == R.id.btnOptionC || id == R.id.btnOptionD) {
            if (isAnswerSubmitted) return; // Freeze entry inputs after verification submissions

            resetOptionButtonsStyle();
            Button targetOption = (Button) view;
            targetOption.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#33B5E5"))); // Selected Blue
            targetOption.setTextColor(Color.WHITE);

            if (id == R.id.btnOptionA) chosenAnswerIndex = 0;
            else if (id == R.id.btnOptionB) chosenAnswerIndex = 1;
            else if (id == R.id.btnOptionC) chosenAnswerIndex = 2;
            else if (id == R.id.btnOptionD) chosenAnswerIndex = 3;

            btnNextQuestion.setEnabled(true); // Unlock main submission gate
        }
        else if (id == R.id.btnNextQuestion) {
            if (!isAnswerSubmitted) {
                verifySelectionResponse();
            } else {
                advanceEngineIndex();
            }
        }
    }

    private void verifySelectionResponse() {
        isAnswerSubmitted = true;
        int correctAnswer = correctAnswersIndex[currentQuestionIndex];
        Button[] options = {btnOptionA, btnOptionB, btnOptionC, btnOptionD};

        if (chosenAnswerIndex == correctAnswer) {
            accumulativePointsScore++;
            options[chosenAnswerIndex].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#66BB6A"))); // Green Success
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
        } else {
            options[chosenAnswerIndex].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EF5350"))); // Red Failure
            options[correctAnswer].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#66BB6A"))); // Highlight correct answer
            Toast.makeText(this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
        }

        if (currentQuestionIndex == questionsPool.length - 1) {
            btnNextQuestion.setText("Finish Quiz");
        } else {
            btnNextQuestion.setText("Next Question");
        }
    }

    private void advanceEngineIndex() {
        if (currentQuestionIndex < questionsPool.length - 1) {
            currentQuestionIndex++;
            populateQuestionState();
        } else {
            renderFinalScorecardDialog();
        }
    }

    private void renderFinalScorecardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Completed!");
        builder.setMessage(String.format(Locale.US, "Your Final Score: %d out of %d", accumulativePointsScore, questionsPool.length));
        builder.setCancelable(false);
        builder.setPositiveButton("Restart Quiz", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currentQuestionIndex = 0;
                accumulativePointsScore = 0;
                populateQuestionState();
            }
        });
        builder.show();
    }
}