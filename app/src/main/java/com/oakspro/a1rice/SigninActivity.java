package com.oakspro.a1rice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText mobileEd, otpEd;
    ImageButton nextBtn;
    //String api="";
    TextView timerTv, errorMsg, resendOtpTv;
    int nextB=0;
    Boolean isnumberValid;
    String randomOTP=null;
    String api_send_otp;
    String prof_status;
    String mobileF=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        api_send_otp=getResources().getString(R.string.core_api)+"/app_api/send_otp_sms.php";
        mobileEd=findViewById(R.id.mobile_ed);
        otpEd=findViewById(R.id.otp_ed);
        nextBtn=findViewById(R.id.next_btn);
        errorMsg=findViewById(R.id.error_msg);
        timerTv=findViewById(R.id.time_tv);
        resendOtpTv=findViewById(R.id.resend_tv);

        //progress
        progressDialog=new ProgressDialog(SigninActivity.this);
        //progressDialog.show();




        mobileEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



                /*
                        if(!isValidNumber(charSequence.toString())){
                                    isnumberValid=false;
                                    errorMsg.setVisibility(View.VISIBLE);
                                    errorMsg.setText("Not Valid");


                        }else{
                            isnumberValid=true;
                            errorMsg.setVisibility(View.VISIBLE);
                            errorMsg.setText("Valid");
                        }

                 */


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nextB==0){
                    String mob=mobileEd.getText().toString();
                    mobileF=mob;

                    if (!TextUtils.isEmpty(mob)){
                        if (mob.length()==10){

                            final String regexStr = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}$";
                            if (mob.matches(regexStr)){

                                errorMsg.setVisibility(View.INVISIBLE);
                                mobileEd.clearFocus();
                                otpEd.requestFocus();
                                nextB=1;
                                sendOTP(mob);
                            }else{
                                errorMsg.setVisibility(View.VISIBLE);
                                errorMsg.setText("Invalid Mobile Number");
                            }


                        }else{
                            Toast.makeText(SigninActivity.this, "Enter Valid 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else if (nextB==1){

                    String otp_res=otpEd.getText().toString();
                    if (!TextUtils.isEmpty(otp_res) && otp_res.length()==6 && otp_res.equals(randomOTP)){
                        mobileEd.setEnabled(false);
                        otpEd.setEnabled(false);
                        nextB=2;

                        Toast.makeText(SigninActivity.this, "Verified.......", Toast.LENGTH_SHORT).show();
                        if(prof_status.equals("0")){
                            Intent intent=new Intent(SigninActivity.this, SignupActivity.class);
                            intent.putExtra("mobile", mobileF);
                            startActivity(intent);
                            finish();
                        }else{

                            //open dashboard
                            Intent intent=new Intent(SigninActivity.this, DashActivity.class);
                            startActivity(intent);
                            finish();

                        }


                    }else {
                        Toast.makeText(SigninActivity.this, "Please Enter Valid  OTP", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        resendOtpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP(mobileF);
            }
        });


    }

    public void open_diag(View view) {

        progressDialog=new ProgressDialog(SigninActivity.this);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.loading_rice);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

    }
    private boolean isValidNumber(String phonenumber){
        String PHONE_PATTERN="^[6-9]\\d{9}$";
        //String PHONE_PATTERN="r'^[6-9]\\d{9}$'";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phonenumber);
        return matcher.matches();

    }
    private void sendOTP(String mobile_s) {

        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.loading_rice);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        Random random=new Random();
        int randNum=random.nextInt(999999);
        randomOTP=String.format("%6d", randNum);
        String message_temp="Dear Store Owner, \nYour OTP is "+randomOTP+" for Grocil Store registration. \n Thank you \n Grocil (Taxbees)";

        Log.i("OTP GEN", randomOTP);
        StringRequest request=new StringRequest(Request.Method.POST, api_send_otp, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    prof_status=jsonObject.getString("prof_status");
                    if (jsonObject.has("SENT")){

                        otpEd.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        mobileF=mobile_s;
                        Toast.makeText(SigninActivity.this, "OTP sent to your mobile", Toast.LENGTH_SHORT).show();

                        timerTv.setVisibility(View.VISIBLE);
                        new CountDownTimer(120000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                timerTv.setText(""+String.format("%d : %d",
                                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                            }

                            public void onFinish() {
                                resendOtpTv.setVisibility(View.VISIBLE);
                            }

                        }.start();

                    }else {

                        otpEd.setVisibility(View.INVISIBLE);
                        nextB=1;
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SigninActivity.this, "Volley Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> otpmap=new HashMap<>();
                otpmap.put("message", message_temp);
                otpmap.put("mobile", mobile_s);
                return otpmap;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}