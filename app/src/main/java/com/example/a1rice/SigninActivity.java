package com.example.a1rice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigninActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText mobileEd, otpEd;
    ImageButton nextBtn;
    String api="";
    TextView timerTv, errorMsg, resendOtpTv;
    Boolean nextB=false;
    Boolean isnumberValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mobileEd=findViewById(R.id.mobile_ed);
        otpEd=findViewById(R.id.otp_ed);
        nextBtn=findViewById(R.id.next_btn);

        mobileEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        if(!isValidNumber(charSequence.toString())){
                                    isnumberValid=false;
                                    mobileEd.setText("");

                        }else{
                            isnumberValid=true;
                        }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nextB==false){
                    String mob=mobileEd.getText().toString();

                    if (!TextUtils.isEmpty(mob)){
                        if (mob.length()==10){
                            
                        }else{
                            Toast.makeText(SigninActivity.this, "Enter Valid 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                        }
                    }
                }



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
        String PHONE_PATTERN="^(9|8|7|6)\\d{9}$";
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phonenumber);
        return matcher.matches();
    }
}