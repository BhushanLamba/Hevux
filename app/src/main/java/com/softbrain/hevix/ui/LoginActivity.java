package com.softbrain.hevix.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.softbrain.hevix.R;
import com.softbrain.hevix.databinding.ActivityLoginBinding;
import com.softbrain.hevix.network.RetrofitClient;
import com.softbrain.hevix.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    String userName, password;
    Context context;
    Activity activity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        activity = this;

        userName=SharedPref.getString(context,SharedPref.USER_NAME);
        password=SharedPref.getString(context,SharedPref.PASSWORD);

        binding.etUserId.setText(userName);
        binding.etPassword.setText(password);



        handleClicks();
    }

    private void handleClicks() {
        binding.btnLogin.setOnClickListener(v ->
        {
            userName = binding.etUserId.getText().toString();
            password = binding.etPassword.getText().toString();
            if (TextUtils.isEmpty(userName)) {
                binding.etUserId.setError("Required");
            } else if (TextUtils.isEmpty(password)) {
                binding.etPassword.setError("Required");
            } else {
                login();
            }
        });
    }

    private void login() {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        RetrofitClient.getInstance().getApi().login(userName, password)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                                String responseCode = responseObject.getString("response_code");
                                String message = responseObject.getString("response_msg");
                                if (responseCode.equalsIgnoreCase("TXN")) {
                                    JSONArray transactionsArray = responseObject.getJSONArray("transactions");
                                    JSONObject transactionObject = transactionsArray.getJSONObject(0);
                                    String userId = transactionObject.getString("ID");
                                    String name = transactionObject.getString("Name");

                                    SharedPref.setString(context, SharedPref.LOGIN_DATA_KEY, transactionObject.toString());
                                    SharedPref.setString(context, SharedPref.USER_ID, userId);
                                    SharedPref.setString(context, SharedPref.NAME, name);
                                    SharedPref.setString(context, SharedPref.PASSWORD, password);
                                    SharedPref.setString(context, SharedPref.USER_NAME, userName);

                                    startActivity(new Intent(activity, MainActivity.class));
                                    finish();
                                } else {
                                    new AlertDialog.Builder(context)
                                            .setMessage(message)
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }
}