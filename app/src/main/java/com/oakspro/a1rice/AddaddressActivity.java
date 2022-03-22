package com.oakspro.a1rice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddaddressActivity extends AppCompatActivity {

    EditText addNameEd, addHnoed,addPincodeEd, addStateEd;
    Spinner addAreaEd, addCityEd;
    Button saveBtn;
    String[] city={"Select City","Hanamkonda", "Warangal"};
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String api="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaddress);

        addNameEd=findViewById(R.id.addName_ed);
        addHnoed=findViewById(R.id.adHadd_ed);
        addAreaEd=findViewById(R.id.adArea_ed);
        addPincodeEd=findViewById(R.id.adPincode_ed);
        addCityEd=findViewById(R.id.adCity_ed);
        addStateEd=findViewById(R.id.adState_ed);
        saveBtn=findViewById(R.id.save_btn);
        preferences=getSharedPreferences("MyLogin", Context.MODE_PRIVATE);
        String profileID=preferences.getString("userid", null);
        api=getResources().getString(R.string.core_api)+"app_api/add_address_api.php";
        

        ArrayAdapter<String> cityAdapter=new ArrayAdapter<String >(this, R.layout.spinner_item, city){

            @Override
            public boolean isEnabled(int position) {
                if (position==0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        addCityEd.setAdapter(cityAdapter);

        addPincodeEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()==6){
                    String pincode=addPincodeEd.getText().toString();
                    String api="https://api.postalpincode.in/pincode/"+pincode;
                    //getPincodeDetails(api);
                    getDataFromPinCode(api);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adN=addNameEd.getText().toString();
                String adH=addHnoed.getText().toString();
                String adC=addCityEd.getSelectedItem().toString();
                String adP=addPincodeEd.getText().toString();
                String adA=addAreaEd.getSelectedItem().toString();
                String adS=addStateEd.getText().toString();
                if(!TextUtils.isEmpty(adN) && !TextUtils.isEmpty(adH) && !TextUtils.isEmpty(adC) && !TextUtils.isEmpty(adP) && !TextUtils.isEmpty(adA) &&
                        !TextUtils.isEmpty(adS)){

                    saveAddressServer(adN, adH, adC, adP, adA, adS, profileID);
                }else{
                    Toast.makeText(AddaddressActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void saveAddressServer(String adN, String adH, String adC, String adP, String adA, String adS, String profileID) {
        StringRequest request=new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")){
                    Toast.makeText(AddaddressActivity.this, "Address Saved", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(AddaddressActivity.this, DashActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(AddaddressActivity.this, "Failed to Save Address", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data=new HashMap<>();
                data.put("pack", getPackageName());
                data.put("ad_name", adN);
                data.put("ad_info", adH);
                data.put("ad_city", adC);
                data.put("ad_pincode", adP);
                data.put("ad_area", adA);
                data.put("ad_state", adS);
                data.put("userid", profileID);
                return data;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void getDataFromPinCode(String api) {
        StringRequest request=new StringRequest(Request.Method.GET, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray=new JSONArray(response);
                    String status=jsonArray.getJSONObject(0).getString("Status");

                    if (status.equals("Success")){
                        JSONArray jsonArray1=jsonArray.getJSONObject(0).getJSONArray("PostOffice");
                        String state="";
                        String[] area=new String[jsonArray1.length()+1];
                        area[0]="Select Area";
                        for (int i=0; i<jsonArray1.length(); i++){
                            JSONObject object=jsonArray1.getJSONObject(i);
                            area[i+1]=object.getString("Name");
                            state=object.getString("State");
                        }
                        ArrayAdapter<String> areaadapter=new ArrayAdapter<String>(AddaddressActivity.this, R.layout.spinner_item, area)
                        {

                            @Override
                            public boolean isEnabled(int position) {
                                if (position==0){
                                    return false;
                                }else{
                                    return true;
                                }
                            }
                        };
                        addAreaEd.setAdapter(areaadapter);
                        addStateEd.setText(state);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(request);

    }
}