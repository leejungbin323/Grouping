package com.chlwhdtn.grouping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chlwhdtn.grouping.Data.CommonResult;
import com.chlwhdtn.grouping.Data.LoginObject;
import com.chlwhdtn.grouping.Retrofit.GroupingRetrofit;
import com.chlwhdtn.grouping.Retrofit.GroupingService;
import com.chlwhdtn.grouping.Util.LoadingBox;
import com.chlwhdtn.grouping.Util.MessageBox;
import com.chlwhdtn.grouping.Util.MessageType;
import com.chlwhdtn.grouping.Util.UserManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button loginbtn, regbtn;

    EditText id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regbtn = findViewById(R.id.login_regBtn);
        loginbtn = findViewById(R.id.login_loginBtn);

        id = findViewById(R.id.login_id);
        pw = findViewById(R.id.login_pw);

        if(UserManager.hasAccount(this)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginbtn.setOnClickListener(v -> {
            GroupingService retrofit = GroupingRetrofit.getInstance(getBaseContext()).getGroupingService();
            Call<CommonResult> response = retrofit.Login(new LoginObject(id.getText().toString(),pw.getText().toString(),"0"));

            response.enqueue(new Callback<CommonResult>() {
                @Override
                public void onResponse(Call<CommonResult> call, Response<CommonResult> res) {
                    CommonResult result = res.body();

                    if(result == null) {
                        try { result = new Gson().fromJson(res.errorBody().string(), CommonResult.class); }
                        catch (IOException e) { e.printStackTrace(); }
                    }

                    if(result.isSuccess()) {
                        UserManager.saveAccount(getBaseContext(), id.getText().toString(), pw.getText().toString(), result.getAccessToken());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CommonResult> call, Throwable t) {

                }
            });

        });

        regbtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}