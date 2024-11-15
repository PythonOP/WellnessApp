package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MiniEatToolActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_eat_tool);

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);

        // Initialize question list
        questionList = new ArrayList<>();
        setupQuestions();

        loadQuestion();

        nextButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                if (isCorrectAnswer(selectedRadio.getText().toString())) {
                    score++;
                }
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    loadQuestion();
                } else {
                    showRecommendation();
                }
            } else {
                Toast.makeText(MiniEatToolActivity.this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuestions() {
        questionList.add(new Question("How often do you eat vegetables?", new String[]{"Every day", "Few times a week", "Rarely"}));
        questionList.add(new Question("How much water do you drink daily?", new String[]{"2-3 liters", "1-2 liters", "Less than 1 liter"}));
        questionList.add(new Question("Do you consume sugary drinks?", new String[]{"Rarely", "Sometimes", "Frequently"}));
        questionList.add(new Question("How often do you eat processed foods?", new String[]{"Rarely", "Few times a week", "Almost daily"}));

    }

    private void loadQuestion() {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestionText());
        optionsGroup.clearCheck();

        // Set options
        ((RadioButton) optionsGroup.getChildAt(0)).setText(currentQuestion.getOptions()[0]);
        ((RadioButton) optionsGroup.getChildAt(1)).setText(currentQuestion.getOptions()[1]);
        ((RadioButton) optionsGroup.getChildAt(2)).setText(currentQuestion.getOptions()[2]);
    }

    private boolean isCorrectAnswer(String selectedAnswer) {

        return selectedAnswer.equals(questionList.get(currentQuestionIndex).getOptions()[0]);
    }

    private void showRecommendation() {
        // Shows recommendation based on the score
        if (score == questionList.size()) {
            Toast.makeText(this, "Great! You have a healthy diet!", Toast.LENGTH_LONG).show();
        } else if (score >= questionList.size() / 2) {
            Toast.makeText(this, "Good! You can improve your diet.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Consider revising your diet for better health.", Toast.LENGTH_LONG).show();
        }

        nextButton.setText("Finish");
        // navigate to Dashboard
        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(MiniEatToolActivity.this, Dashboard.class);
            startActivity(intent);
        });
    }
}

class Question {
    private String questionText;
    private String[] options;

    public Question(String questionText, String[] options) {
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }
}
