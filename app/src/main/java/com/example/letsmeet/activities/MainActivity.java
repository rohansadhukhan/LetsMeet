package com.example.letsmeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.letsmeet.PreferenceManager;
import com.example.letsmeet.PreferenceModel;
import com.example.letsmeet.R;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button signout_button;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);
        signout_button = findViewById(R.id.signout_button);
        preferenceManager = new PreferenceManager(this);

        textView.setText(preferenceManager.getString(PreferenceModel.FIRST_NAME) + " " + preferenceManager.getString(PreferenceModel.LAST_NAME));
        signout_button.setOnClickListener(v -> {
            preferenceManager.putBoolean(PreferenceModel.IS_SIGNED_IN, false);
            preferenceManager.putString(PreferenceModel.FIRST_NAME, null);
            preferenceManager.putString(PreferenceModel.LAST_NAME, null);
            preferenceManager.putString(PreferenceModel.EMAIL, null);
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

    }
}