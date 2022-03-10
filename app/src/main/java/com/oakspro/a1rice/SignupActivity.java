package com.oakspro.a1rice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText fnameEd, lnameEd, emailEd, cityEd;
    ImageButton signupBtn;
    String api="https://a1rice.in/app_api/signup_api.php";
    ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String fname,lname,email,city,name,mobile, useridS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        preferences=getSharedPreferences("MyLogin", MODE_PRIVATE);
        editor=preferences.edit();

        fnameEd=findViewById(R.id.fname_ed);
        lnameEd=findViewById(R.id.lname_ed);
        emailEd=findViewById(R.id.email_ed);
        cityEd=findViewById(R.id.city_ed);
        signupBtn=findViewById(R.id.sgn_btn);

        mobile=getIntent().getStringExtra("mobile");

        //progress
        progressDialog=new ProgressDialog(SignupActivity.this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fname=fnameEd.getText().toString();
                lname=lnameEd.getText().toString();
                email=emailEd.getText().toString();
                city=cityEd.getText().toString();
                name=fname+" "+lname;

                signupServer(name, email, city, mobile);


            }
        });


    }

    private void signupServer(String name, String email, String city, String mobile) {

        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.loading_rice);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        StringRequest request=new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String status=jsonObject.getString("status");
                    if (status.equals("SUCCESS")){

                        useridS=jsonObject.getString("userid");
                        //store data to sharedpreference

                        editor.putBoolean("islogged", true);
                        editor.putString("name", name);
                        editor.putString("mobile", mobile);
                        editor.putString("userid", useridS);
                        editor.putString("address", city);
                        editor.commit();

                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Signup Success", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SignupActivity.this, DashActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data=new HashMap<>();
                data.put("name", name);
                data.put("email", email);
                data.put("mobile", mobile);
                data.put("city", city);
                return data;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}