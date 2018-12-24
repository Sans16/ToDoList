package com.gmail.edowilliams.todoapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.edowilliams.todoapp.R;
import com.gmail.edowilliams.todoapp.utils.NetworkUtilis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthenticationPage extends AppCompatActivity {
    @BindView(R.id.emailET)
    EditText emailET;
    @BindView(R.id.passwordET)
    EditText passwordET;

    ProgressDialog progressDialog;
    private String emailStr,passowrdStr;
    private Unbinder unbinder;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.loginBtn)
    public void loginMethod(){
        if(!validation()){return;}
        if(!NetworkUtilis.isNetworkAvailable(getApplicationContext())){
            Snackbar.make(findViewById(android.R.id.content),"No Internet Connection",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(emailStr,passowrdStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            changeIntent(currentUser);
//                            if(currentUser.isEmailVerified()){
//                                changeIntent(currentUser);
//                            }
//                            else {
//                                Toast.makeText(getApplicationContext(),"Email is yet to be verified," +
//                                        " please verify your email",Toast.LENGTH_LONG).show();
//                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"You are yet to register",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick(R.id.regBtn)
    public void regMethod(){
        if(!validation()){return;}
        if(!NetworkUtilis.isNetworkAvailable(getApplicationContext())){
            Snackbar.make(findViewById(android.R.id.content),"No Internet Connection",Snackbar.LENGTH_SHORT).show();
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailStr,passowrdStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        changeIntent(currentUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void changeIntent( FirebaseUser currentUser){
        if (currentUser!=null){
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);
        }
    }

    public void assignment(){
        emailStr = emailET.getText().toString().trim();
        passowrdStr = passwordET.getText().toString().trim();
    }

    public boolean validation(){
        assignment();
        if (TextUtils.isEmpty(emailStr)|| !Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()){
            emailET.setError("Invalid Email");
            emailET.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(passowrdStr)){
            passwordET.setError("Field Required");
           passwordET.requestFocus();
            return false;
        }

        if (TextUtils.getTrimmedLength(passowrdStr)<6){
            passwordET.setError("Minimum of six characters");
           passwordET.requestFocus();
            return false;
        }

        return true;
    }
}
