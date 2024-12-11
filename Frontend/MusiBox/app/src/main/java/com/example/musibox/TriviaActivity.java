package com.example.musibox;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private boolean answered = false;
    private int triviaId;

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

        // Generate trivia and store the trivia ID
        generateTrivia();

        // Set click listeners for choices
        View.OnClickListener choiceClickListener = view -> {
            if (!answered) {
                checkAnswer(((TextView) view).getText().toString());
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
                if (currentQuestionIndex < 5) { // End trivia after 5 questions
                    displayQuestion();
                } else {
                    showResultsDialog(); // Show dialog after 5 questions
                }
                answered = false;
            } else {
                Toast.makeText(this, "Please answer the question first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateTrivia() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/generate";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Log.d("TriviaActivity", "Response: " + response.toString());

                    fetchAllTrivia();  // Fetch all trivia after generating
                },
                error -> {
                    Toast.makeText(this, "Failed to generate trivia!", Toast.LENGTH_SHORT).show();
                    Log.e("TriviaActivity", "Error in response: " + error.getMessage(), error);
                }
        );

        queue.add(request);
    }

    private void fetchAllTrivia() {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/fetch";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("TriviaActivity", "Response: " + response.toString()); // Log the entire response for debugging

                    try {
                        JSONArray triviaArray = response.getJSONArray("trivia");
                        for (int i = 0; i < triviaArray.length(); i++) {
                            JSONObject triviaObj = triviaArray.getJSONObject(i);
                            int triviaId = triviaObj.getInt("id");
                            storeTriviaId(triviaId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch all trivia!", Toast.LENGTH_SHORT).show();
                    Log.e("TriviaActivity", "Error: " + error.getMessage(), error);
                }
        );

        queue.add(request);
    }

    private void storeTriviaId(int triviaId) {
        this.triviaId = triviaId;
        Log.d("TriviaActivity", "Stored trivia ID: " + triviaId);
        fetchQuestions(triviaId); // After storing, fetch the trivia questions
    }

    private void fetchQuestions(int triviaId) {
        String url = "http://coms-3090-048.class.las.iastate.edu:8080/trivia/" + triviaId;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("TriviaActivity", "Response: " + response.toString()); // Log the entire response for debugging

                    try {
                        JSONArray questions = response.getJSONArray("questions");
                        JSONArray answers = response.getJSONArray("answers");
                        JSONArray options = response.getJSONArray("options");

                        for (int i = 0; i < questions.length(); i++) {
                            JSONObject questionObj = new JSONObject();
                            String question = questions.getString(i);
                            String correctAnswer = answers.getString(i);
                            JSONArray questionOptions = options.getJSONArray(i);

                            ArrayList<String> allAnswers = new ArrayList<>();
                            for (int j = 0; j < questionOptions.length(); j++) {
                                allAnswers.add(questionOptions.getString(j));
                            }

                            Collections.shuffle(allAnswers);

                            questionObj.put("question", question);
                            questionObj.put("correct_answer", correctAnswer);
                            questionObj.put("answers", new JSONArray(allAnswers));

                            questionsList.add(questionObj);
                        }

                        Log.d("TriviaActivity", "Questions List Size: " + questionsList.size());

                        displayQuestion(); // Display the first question once the data is fetched
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch questions!", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questionsList.size()) {
            Toast.makeText(this, "No more questions!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject questionObj = questionsList.get(currentQuestionIndex);
            String question = questionObj.getString("question");
            String correctAnswer = questionObj.getString("correct_answer");
            JSONArray answers = questionObj.getJSONArray("answers");

            questionText.setText(question);
            counterText.setText((currentQuestionIndex + 1) + "/5");

            choice1.setText(answers.getString(0));
            choice2.setText(answers.getString(1));
            choice3.setText(answers.getString(2));
            choice4.setText(answers.getString(3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkAnswer(String selectedAnswer) {
        if (questionsList.isEmpty()) {
            Toast.makeText(this, "No questions available!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject questionObj = questionsList.get(currentQuestionIndex);
            String correctAnswer = questionObj.getString("correct_answer");

            if (selectedAnswer.equals(correctAnswer)) {
                correctAnswers++;
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wrong! Correct answer: " + correctAnswer, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showResultsDialog() {
        String resultMessage = "You got " + correctAnswers + " out of 5 correct!";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Trivia Results")
                .setMessage(resultMessage)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    // Reset for a new round of trivia
                    questionsList.clear();
                    currentQuestionIndex = 0;
                    correctAnswers = 0;
                    generateTrivia();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    Intent pageIntent = new Intent(getApplicationContext(), MainPage.class);
                    startActivity(pageIntent);
                })
                .setCancelable(false)
                .show();
    }

}
