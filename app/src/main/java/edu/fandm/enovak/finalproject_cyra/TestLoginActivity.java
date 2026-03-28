package edu.fandm.enovak.finalproject_cyra;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// WE CAN DELETE THIS LATER

public class TestLoginActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USERNAME = "extra_username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.tv_user_info);

        String userId = getIntent().getStringExtra(EXTRA_USER_ID);
        String username = getIntent().getStringExtra(EXTRA_USERNAME);

        tv.setText("Logged in!\nUserID: " + userId + "\nUsername: " + username);
    }
}