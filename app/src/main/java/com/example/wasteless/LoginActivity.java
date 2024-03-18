package com.example.wasteless;

import android.content.Intent;
import android.os.Bundle;
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
            GenericUtils.toast("E-Mail is empty.", LoginActivity.this);
            return;
        }

        if (password.isEmpty()) {
            GenericUtils.toast("Password is empty.", LoginActivity.this);
            return;
        }

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                                FirebaseUser user = auth.getCurrentUser();
                            GenericUtils.toast("WOOOOW", LoginActivity.this);
                            goToMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            GenericUtils.toast("Authentication failed.", LoginActivity.this);
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