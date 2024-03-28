package com.example.wasteless;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wasteless.utils.GenericUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText tvEmail, tvPassword;
    Button btnLogin, btnSignUp;
    TextView errorText;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEmail = findViewById(R.id.emailEt);
        tvPassword = findViewById(R.id.passwordEt);
        btnLogin = findViewById(R.id.btnContinue);
        btnSignUp = findViewById(R.id.btnSignUp);
        errorText = findViewById(R.id.errorTextView);

        btnLogin.setOnClickListener(view -> {
            setLoginOnClickFlow();
        });

        btnSignUp.setOnClickListener(view -> {
            setSignUpOnClickFlow();
        });
    }

    private void setSignUpOnClickFlow() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void setLoginOnClickFlow() {
        String email, password;
        email = tvEmail.getText().toString();
        password = tvPassword.getText().toString();

        if (email.isEmpty()) {
            errorText.setText("E-Mail is empty.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (password.isEmpty()) {
            errorText.setText("Password is empty.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            goToMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            GenericUtils.toast("Authentication failed.", LoginActivity.this);
                            errorText.setText("Authentication failed.");
                            errorText.setVisibility(View.VISIBLE);
                            Animation shakeAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_failed);
                            btnLogin.startAnimation(shakeAnimation);
                        }
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}