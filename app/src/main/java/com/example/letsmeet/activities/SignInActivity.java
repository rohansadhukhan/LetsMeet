package com.example.letsmeet.activities;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private EditText email, password;
    private Button signIn_button;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        setUpViews();

        if(preferenceManager.getBoolean(PreferenceModel.IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.signup_text).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        signIn_button.setOnClickListener(v -> {
            if(email.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Email", Toast.LENGTH_SHORT).show();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                Toast.makeText(getApplicationContext(), "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else if(password.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            } else {
//                signIn();
                mainSignIn();
            }
        });
    }

    private void signIn() {

        progressBar.setVisibility(View.VISIBLE);
        signIn_button.setVisibility(View.INVISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(PreferenceModel.USER)
                .whereEqualTo(PreferenceModel.EMAIL, email.getText().toString())
                .whereEqualTo(PreferenceModel.PASSWORD, password.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(PreferenceModel.IS_SIGNED_IN, true);
                        preferenceManager.putString(PreferenceModel.FIRST_NAME, documentSnapshot.getString(PreferenceModel.FIRST_NAME));
                        preferenceManager.putString(PreferenceModel.LAST_NAME, documentSnapshot.getString(PreferenceModel.LAST_NAME));
                        preferenceManager.putString(PreferenceModel.EMAIL, documentSnapshot.getString(PreferenceModel.EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        signIn_button.setVisibility(View.VISIBLE);
                    }
                });

    }

    private void mainSignIn() {

        String newEmail, newPassword;
        newEmail = email.getText().toString().trim();
        newPassword = password.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        signIn_button.setVisibility(View.INVISIBLE);

        mAuth.signInWithEmailAndPassword(newEmail, newPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                signIn_button.setVisibility(View.VISIBLE);
                Toast.makeText(SignInActivity.this, "Something went wrong!", Toast.LENGTH_SHORT);
            }
        });
    }


    private void setUpViews() {
        email = findViewById(R.id.email_signin);
        password = findViewById(R.id.password_signin);
        signIn_button = findViewById(R.id.signin_button);
        progressBar = findViewById(R.id.progress_signin);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }
}