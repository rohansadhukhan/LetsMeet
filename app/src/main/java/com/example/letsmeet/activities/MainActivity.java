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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

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


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



//        textView.setText(preferenceManager.getString(PreferenceModel.FIRST_NAME) + " " + preferenceManager.getString(PreferenceModel.LAST_NAME));
        signout_button.setOnClickListener(v -> {
//            preferenceManager.putBoolean(PreferenceModel.IS_SIGNED_IN, false);
//            preferenceManager.putString(PreferenceModel.FIRST_NAME, null);
//            preferenceManager.putString(PreferenceModel.LAST_NAME, null);
//            preferenceManager.putString(PreferenceModel.EMAIL, null);
//            Intent intent = new Intent(this, SignInActivity.class);
//            startActivity(intent);
//            finish();

            mAuth.signOut();
            sendToStart();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            sendToStart();
        }
        else {
            FirebaseUser mUser = mAuth.getCurrentUser();
            String uid = mUser.getUid();

            DocumentReference docRef = db.collection(PreferenceModel.USER).document(uid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()) {
                        String firstName, lastName;
                        Map<String, Object> user_data = documentSnapshot.getData();
                        firstName = user_data.get(PreferenceModel.FIRST_NAME).toString();
                        lastName = user_data.get(PreferenceModel.LAST_NAME).toString();

                        textView.setText(firstName + " " + lastName);
                    }
                }
            });
        }
    }

    private void sendToStart() {
        Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }
}