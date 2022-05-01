package com.example.finalver4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String uid= firebaseUser.getUid();
        Toast.makeText(this, "Id is "+uid, Toast.LENGTH_LONG).show();
    }
}