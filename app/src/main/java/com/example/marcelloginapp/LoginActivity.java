package com.example.marcelloginapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private final String LOGIN_URL = BuildConfig.BASE_URL + "login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignupLink = findViewById(R.id.tvSignupLink);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        tvSignupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void loginUser(String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            String result = ApiClient.makePostRequest(LOGIN_URL, params);

            handler.post(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (status.equals("success")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", jsonObject.optString("username", "User"));
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}