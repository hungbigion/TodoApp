package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.todoapp.UtilsService.SharedPreferenceClass;
import com.example.todoapp.UtilsService.UtilsService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin,btnSignup;
    private EditText edtEmail, edtPassword;
    ProgressBar progressBar;

    private String email, password;
    UtilsService utilsService;
    SharedPreferenceClass sharedPreferenceClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progress_bar);
        utilsService = new UtilsService();
        sharedPreferenceClass = new SharedPreferenceClass(this);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,Register.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilsService.hideKeyboard(view,LoginActivity.this);
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                if(validate(view)){
                    loginUser(view);
                }
            }
        });
    }

    private boolean validate(View view) {
        boolean isValid;
        if(!TextUtils.isEmpty(email)){
            if(!TextUtils.isEmpty(password)){
                isValid = true;
            } else {
                utilsService.showSnackBar(view,"Enter password pls...");
                isValid=false;
            }
        } else {
            utilsService.showSnackBar(view,"Enter email pls...");
            isValid=false;
        }

        return isValid;
    }

    private void loginUser (View view) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);

        String apiKey ="https://todo-app-with-node-js.vercel.app/api/todo/auth/login";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        String token = response.getString("token");
                        sharedPreferenceClass.setValue_string("token",token);
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response !=null){
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Toast.makeText(LoginActivity.this, obj.getString("msg"),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException | UnsupportedEncodingException js){
                        js.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return params;
            }
        };
        // set retry policy
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                ,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        // request add
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences todo_pref = getSharedPreferences("user_todo", MODE_PRIVATE);
        if(todo_pref.contains("token")){
            startActivity( new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}