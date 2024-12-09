package com.example.musibox;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class TriviaActivity extends AppCompatActivity {

    private TextView questionText, choice1, choice2, choice3, choice4, counterText;
    private Button nextButton;
    private ImageButton backButton;

    private ArrayList<JSONObject> questionsList = new ArrayList<>();
    private ArrayList<String> userAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int triviaId = -1; // To store the generated trivia ID
    private boolean answered = false;
    private int correctAnswersCount = 0; // Track correct answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);

        // Initialize views
        questionText = findViewById(R.id.question);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        counterText = findViewById(R.id.counter);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backArrow);

        // Generate trivia and fetch questions
        generateTrivia();

        // Set click listeners for choices
        View.OnClickListener choiceClickListener = view -> {
            if (!answered) {
                String selectedAnswer = ((TextView) view).getText().toString();
                userAnswers.add(selectedAnswer); // Record user's answer
                checkAnswer(selectedAnswer);
                answered = true;
            }
        };
        choice1.setOnClickListener(choiceClickListener);
        choice2.setOnClickListener(choiceClickListener);
        choice3.setOnClickListener(choiceClickListener);
        choice4.setOnClickListener(choiceClickListener);

        backButton.setOnClickListener(view -> {
            Intent pageIntent = new Intent(getApplicationContext(), MainPage.class);
            startActivity(pageIntent);
        });

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            if (answered) {
                currentQuestionIndex++;
                if (currentQuestionIndex < questionsList.size()) {
                    displayQuestion();
                } else {
                    submitAnswers(); // Submit answers when trivia is completed
                }
                answered = false;
            } else {
                Toast.makeText(this, "Please answer the question first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateTrivia() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/generate"; // Backend URL
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    try {
                        triviaId = response.getInt("id"); // Store the generated trivia ID
                        fetchQuestions(); // Fetch questions for the generated trivia
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to generate trivia!", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void fetchQuestions() {
        if (triviaId == -1) {
            Toast.makeText(this, "Trivia ID not found! Cannot fetch questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/" + triviaId; // Backend URL with trivia ID
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        questionsList.clear();
                        for (int i = 0; i < results.length(); i++) {
                            questionsList.add(results.getJSONObject(i));
                        }
                        displayQuestion(); // Display the first question
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch questions from backend!", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void displayQuestion() {
        try {
            JSONObject questionObj = questionsList.get(currentQuestionIndex);
            String question = questionObj.getString("question").replace("&quot;", "\"").replace("&#039;", "'");
            String correctAnswer = questionObj.getString("correct_answer").replace("&quot;", "\"").replace("&#039;", "'");
            JSONArray incorrectAnswers = questionObj.getJSONArray("incorrect_answers");

            ArrayList<String> allAnswers = new ArrayList<>();
            allAnswers.add(correctAnswer);
            for (int i = 0; i < incorrectAnswers.length(); i++) {
                allAnswers.add(incorrectAnswers.getString(i).replace("&quot;", "\"").replace("&#039;", "'"));
            }
            Collections.shuffle(allAnswers);

            // Update views
            questionText.setText(question);
            choice1.setText(allAnswers.get(0));
            choice2.setText(allAnswers.get(1));
            choice3.setText(allAnswers.get(2));
            choice4.setText(allAnswers.get(3));
            counterText.setText((currentQuestionIndex + 1) + "/5");

            // Reset choice colors
            choice1.setBackgroundColor(getResources().getColor(R.color.BabyBlue));
            choice2.setBackgroundColor(getResources().getColor(R.color.BabyBlue));
            choice3.setBackgroundColor(getResources().getColor(R.color.BabyBlue));
            choice4.setBackgroundColor(getResources().getColor(R.color.BabyBlue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkAnswer(String selectedAnswer) {
        try {
            String correctAnswer = questionsList.get(currentQuestionIndex).getString("correct_answer")
                    .replace("&quot;", "\"").replace("&#039;", "'");
            if (selectedAnswer.equals(correctAnswer)) {
                correctAnswersCount++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wrong! Correct answer: " + correctAnswer, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void submitAnswers() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/" + triviaId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userAnswers", new JSONArray(userAnswers));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                response -> {
                    try {
                        String scoreMessage = response.getString("message");
                        Toast.makeText(this, scoreMessage, Toast.LENGTH_LONG).show();
                        showResults(); // Show results after submission
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to submit answers!", Toast.LENGTH_SHORT).show()
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showResults() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Trivia Results");
        builder.setMessage("You completed the trivia!\nCorrect Answers: " + correctAnswersCount + "/" + questionsList.size());
        builder.setPositiveButton("Try Again", (dialog, which) -> {
            currentQuestionIndex = 0;
            correctAnswersCount = 0;
            userAnswers.clear();
            generateTrivia();
        });
        builder.setNegativeButton("Exit", (dialog, which) -> finish());
        builder.show();
    }
}
