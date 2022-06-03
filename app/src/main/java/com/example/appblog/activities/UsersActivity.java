package com.example.appblog.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.appblog.adapter.UsersAdapter;


import com.example.appblog.databinding.ActivityUserBinding;
import com.example.appblog.utilities.Constants;
import com.example.appblog.utilities.PreferenceManager;
import com.example.appblog.entities.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    ActivityUserBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();

    }
    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name= queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email= queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image= queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token= queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);


                        }
                        if(users.size()>0){
                            UsersAdapter usersAdapter = new UsersAdapter(users);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else {
                            showErrorMessage();
                        }
                    }

                });

    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
