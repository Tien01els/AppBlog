package com.example.finalver4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {


    EditText username, fullname, email, password;
    Button registerBtn;
    TextView txt_register;
    FirebaseAuth auth;
    DatabaseReference reference;
    LoadingDialogBar loadingDialogBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);





        username = (EditText) findViewById(R.id.usernameRegis);
        fullname = (EditText) findViewById(R.id.fullnameRegis);
        email = (EditText) findViewById(R.id.emailRegis);
        password = (EditText) findViewById(R.id.passwordRegis);
        registerBtn = (Button) findViewById(R.id.register);
        txt_register= (TextView) findViewById(R.id.txt_login);
        loadingDialogBar = new LoadingDialogBar(this);

        auth= FirebaseAuth.getInstance();




        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                pd= new ProgressDialog(RegisterActivity.this);
//                pd.setMessage("Please wait...");
//                pd.show();
//                setDialog(true);
//                loadingDialogBar.ShowDialog("Waiting");
                txt_register.setVisibility(View.GONE);

                String str_username= username.getText().toString();
                String str_fullname= fullname.getText().toString();
                String str_email= email.getText().toString();
                String str_password= password.getText().toString();

                if(TextUtils.isEmpty(str_username)||TextUtils.isEmpty(str_fullname)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are require !!!", Toast.LENGTH_SHORT).show();

                }else if(str_password.length()< 6){
                    Toast.makeText(RegisterActivity.this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
                }else{
                        register(str_username, str_fullname,str_email,str_password);

                }

            }
        });

    }

    private void register(String username, String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Waiting for response", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/final-ver4.appspot.com/o/depositphotos_137014128-stock-illustration-user-profile-icon.jpg?alt=media&token=4f917273-0812-417e-bbe0-7ddff6619c8d");


                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Cant save to database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
//                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}