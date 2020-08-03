package com.example.letsmeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.letsmeet.PreferenceManager;
import com.example.letsmeet.PreferenceModel;
import com.example.letsmeet.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText first_name, last_name, email, password, confirm_password;
    private Button signUp_button;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setUpViews();

        findViewById(R.id.signin_text).setOnClickListener(v -> onBackPressed());
        signUp_button.setOnClickListener(v -> {
            if(first_name.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter First Name", Toast.LENGTH_SHORT).show();
            } else if(last_name.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Last Name", Toast.LENGTH_SHORT).show();
            } else if(email.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                Toast.makeText(getApplicationContext(), "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else if(password.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            } else if(confirm_password.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Confirm Password", Toast.LENGTH_SHORT).show();
            } else if(!password.getText().toString().equals(confirm_password.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Password and Confirm Password are different", Toast.LENGTH_SHORT).show();
            } else {
                signUp();
            }
        });
    }

    private void signUp() {

        progressBar.setVisibility(View.VISIBLE);
        signUp_button.setVisibility(View.INVISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> user_data = new HashMap<>();
        user_data.put(PreferenceModel.FIRST_NAME, first_name.getText().toString());
        user_data.put(PreferenceModel.LAST_NAME, last_name.getText().toString());
        user_data.put(PreferenceModel.EMAIL, email.getText().toString());
        user_data.put(PreferenceModel.PASSWORD, password.getText().toString());

        database.collection(PreferenceModel.USER)
                .add(user_data)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(PreferenceModel.IS_SIGNED_IN, true);
                    preferenceManager.putString(PreferenceModel.FIRST_NAME, first_name.getText().toString());
                    preferenceManager.putString(PreferenceModel.LAST_NAME, last_name.getText().toString());
                    preferenceManager.putString(PreferenceModel.EMAIL, email.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    signUp_button.setVisibility(View.VISIBLE);
                });

    }

    private void setUpViews() {
        first_name = findViewById(R.id.first_name_signup);
        last_name = findViewById(R.id.last_name_signup);
        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        confirm_password = findViewById(R.id.confirm_password_signup);
        signUp_button = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progress_signup);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}