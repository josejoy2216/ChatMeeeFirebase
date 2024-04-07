package com.example.chatmeeefirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatmeeefirebase.MainActivity;
import com.example.chatmeeefirebase.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button btn_login;
    TextView forgotPasswordButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forgotPasswordButton = findViewById(R.id.forgot_password);

        btn_login.setOnClickListener(view -> {
            String txt_email = email.getText().toString().trim();
            String txt_password = password.getText().toString().trim();

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(LoginActivity.this, "Both email and password are required", Toast.LENGTH_SHORT).show();
            } else {
                // Authenticate user
                auth.signInWithEmailAndPassword(txt_email, txt_password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle authentication failure
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    } //onCreate

    private void showForgotPasswordDialog() {
        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View dialogView = inflater.inflate(R.layout.popup_forgot_password, null);

        // Find views in the dialog
        EditText emailInput = dialogView.findViewById(R.id.editTextEmail);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialogView);
        builder.setTitle("Forgot Password");
        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Get the email entered by the user
            String email = emailInput.getText().toString().trim();

            // Validate email
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send password reset email
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        builder.setNegativeButton("Cancel", null);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
