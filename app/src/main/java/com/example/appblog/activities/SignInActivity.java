package com.example.appblog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appblog.databinding.ActivitySignInBinding;
import com.example.appblog.MainActivity;

import com.example.appblog.utilities.Constants;
import com.example.appblog.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), com.example.appblog.activities.SignUpActivity.class)));
       binding.buttonSignIn.setOnClickListener(v -> {
           if(isValidSignInDetails()) {
            signIn();
           }

       });
    }

//    private void addDatatoFirestore(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String,Object> data = new HashMap<>();
//        data.put("first_name","Minh");
//        data.put("last_name","Phuong");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(),"Data Inserted",Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
//                });
//
//
//    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
//sign in

    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                        && task.getResult().getDocuments().size() >0){

                        DocumentSnapshot documentSnapshot =task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), HomeChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        loading(false);
                        showToast("unable to sign in");
                    }
                });


    }

    private Boolean isValidSignInDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid Email");
            return false;
        } else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else {
            return true;
        }
    }

    //loading
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {

            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);

        }
    }
}