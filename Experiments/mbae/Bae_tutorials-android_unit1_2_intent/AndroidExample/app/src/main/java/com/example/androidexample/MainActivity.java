package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;     // define message textview variable
    private Button counterButton;     // define counter button variable
    private int counter = 0; //Define counter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        counterButton = findViewById(R.id.main_counter_btn);// link to counter button in the Main activity XML
         counterButton.setText("Clicked 0 times");
        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Intent Example");

        } else {
            String number = extras.getString("NUM");  // this will come from LoginActivity
            messageText.setText("The number was " + number);
        }

        /* click listener on counter button pressed */
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++; //Incerment counter on button click
                counterButton.setText("Clicked " + counter + "times");
                /*  when counter button is pressed 4 times, use intent to switch to Counter Activity */
                if(counter == 4){
                    Toast.makeText(MainActivity.this, "Switching to counter Acivity", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
}