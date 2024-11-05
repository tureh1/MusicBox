    package com.example.musibox;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.android.volley.Request;
    import com.android.volley.toolbox.JsonArrayRequest;
    import com.android.volley.toolbox.JsonObjectRequest;
    import org.json.JSONException;
    import org.json.JSONObject;
    import java.util.ArrayList;
    import java.util.List;

    public class FriendsActivity extends AppCompatActivity {

        private ImageButton house;
        private ImageButton message;
        private ImageButton user;
        private ImageButton adduser;

        @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_friends);

            house = findViewById(R.id.home);
            message = findViewById(R.id.message);
            user = findViewById(R.id.user);
            adduser = findViewById(R.id.adduser);




            house.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, MainPage.class);
                startActivity(intent);
            });

            user.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, UserProfileActivity.class);
                startActivity(intent);
            });

            adduser.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, FriendsActivity.class);
                startActivity(intent);
            });
            message.setOnClickListener(v -> {
                Intent intent = new Intent(FriendsActivity.this, MessageActivity.class);
                startActivity(intent);
            });

        }
    }