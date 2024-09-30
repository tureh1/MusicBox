package com.example.musibox;

import android.os.StrictMode;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
/*import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;*/

public class SignupActivity extends AppCompatActivity {

    private EditText email, password, confirm;
   // private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        Button signUpButton = findViewById(R.id.SignUpButton);


        /*client = new OkHttpClient();*/

        /*signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handleSignUp()) {
                    // Send email and password to the backend
                    sendSignUpRequest(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });*/


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private boolean handleSignUp() {
        String usernameInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmInput = confirm.getText().toString().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ((passwordInput.length() & confirmInput.length()) < 6) {
            Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordInput.equals(confirmInput)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

/*
    private void sendSignUpRequest(String email, String password) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        String json = "{ \"emailId\":\"" + email + "\", \"password\":\"" + password + "\" }";


        RequestBody body = RequestBody.create(JSON, json);


        Request request = new Request.Builder()
                .url("http://coms-3090-048.class.las.iastate.edu:8080/add")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {

                Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
            } else {

                Toast.makeText(SignUp.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SignUp.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
*/
