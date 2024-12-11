package com.example.musibox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add animation to the text views
        TextView splashText = findViewById(R.id.SplashText);
        TextView splashSubtitle = findViewById(R.id.SplashText1);

        // Load the fade-in animation from the anim folder
        @SuppressLint("ResourceType") Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.transition.fade_in);

        // Apply the animation to the text views
        splashText.startAnimation(fadeInAnimation);
        splashSubtitle.startAnimation(fadeInAnimation);

        // Delay for 3 seconds and transition to LoginActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000); // 3000ms = 3 seconds
    }
}