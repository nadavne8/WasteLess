package com.example.wasteless;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wasteless.utils.GenericUtils;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etPasswordReEntry, etUserName;
    private TextView errorTextView;
    private Button btnRegister, btnLogin;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get all views from the design file
        etEmail = findViewById(R.id.emailEt);
        etPassword = findViewById(R.id.passwordEt);
        etPasswordReEntry = findViewById(R.id.passwordReEnterEt);
        etUserName = findViewById(R.id.userNameEt);
        btnRegister = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogIn);
        errorTextView = findViewById(R.id.errorTv);

        // Add an on click listener to the sign up button
        addRegisterButtonOnClickListener();

        // Add an on click listener to the login button
        addLoginButtonOnClickListener();
    }

    private void addLoginButtonOnClickListener() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }) ;
    }

    private void addRegisterButtonOnClickListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Define variables for all fields
                String email, passwordReEnter, userName, password;
                email = etEmail.getText().toString();
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                passwordReEnter = etPasswordReEntry.getText().toString();


                // Initiate firebase auth instance
                auth = FirebaseAuth.getInstance();

                // Check valid fields
                if (email.isEmpty()) {
                    setErrorTextView("Please enter your E-mail address");
                    return;
                }

                if (userName.isEmpty()) {
                    setErrorTextView("Please enter a user name");
                    return;
                }

                if (password.isEmpty()) {
                    setErrorTextView("Please enter a password");
                    return;
                }

                if (passwordReEnter.isEmpty()) {
                    setErrorTextView("Please re-enter your password");
                    return;
                }

                if (!password.equals(passwordReEnter)) {
                    setErrorTextView("Passwords are not the same");
                    return;
                }

                // If all fields are valid, create the user
                progressBar.setVisibility(View.VISIBLE);
                createUser(email, password, userName);
            }


        });
    }

    public void setErrorTextView(String error) {
        errorTextView.setText(error);
    }

    public void createUser(String email, String password, String userName) {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        setErrorTextView("New user created successfully.");

                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
                            user.updateProfile(profileChangeRequest);
                        } else {
                            setErrorTextView("Failed to get user information");
                        }
                    } else {
                        setErrorTextView("Account creation failed");

                    }
                }).addOnFailureListener(e -> {
                    // Handle the failure here and log the error
                    Log.e(TAG, "Failed to create user account", e);
                    setErrorTextView(e.getMessage());
                    GenericUtils.toast("Failed to create user account: ", SignUpActivity.this);
                });
    }
}