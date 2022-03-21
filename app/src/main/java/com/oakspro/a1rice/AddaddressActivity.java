package com.oakspro.a1rice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.Map;

public class AddaddressActivity extends AppCompatActivity {

    EditText addNameEd, addHnoed,addPincodeEd, addStateEd;
    Spinner addAreaEd, addCityEd;
    Button saveBtn;
    String[] city={"Hanamkonda", "Warangal"};
    private RequestQueue mRequestQueue;

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
        mRequestQueue = Volley.newRequestQueue(AddaddressActivity.this);
        

        ArrayAdapter<String> cityAdapter=new ArrayAdapter<String >(this, android.R.layout.simple_spinner_dropdown_item, city);
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
                    getPincodeDetails(api);
                  //  getDataFromPinCode(api);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void getDataFromPinCode(String api) {
        // clearing our cache of request queue.
        mRequestQueue.getCache().clear();

        // below line is use to initialize our request queue.
        RequestQueue queue = Volley.newRequestQueue(AddaddressActivity.this);

        // in below line we are creating a
        // object request using volley.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, api, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // inside this method we will get two methods
                // such as on response method
                // inside on response method we are extracting
                // data from the json format.
                try {
                    // we are getting data of post office
                    // in the form of JSON file.
                    JSONArray postOfficeArray = response.getJSONArray("PostOffice");
                    if (response.getString("Status").equals("Error")) {
                        // validating if the response status is success or failure.
                        // in this method the response status is having error and
                        // we are setting text to TextView as invalid pincode.

                    } else {
                        // if the status is success we are calling this method
                        // in which we are getting data from post office object
                        // here we are calling first object of our json array.
                       // JSONObject obj = postOfficeArray.getJSONObject(0);

                        // inside our json array we are getting district name,
                        JSONObject obj=postOfficeArray.getJSONObject(0);
                        Log.i("name", obj.getString("Name"));
                        // state and country from our data.
                        String[] area=new String[postOfficeArray.length()];
                        String state;
                        //area[0]="Select Area";
                        for (int i=0; i<postOfficeArray.length(); i++){
                            JSONObject object=postOfficeArray.getJSONObject(i);
                            area[i]=object.getString("Name");
                            state=object.getString("State");
                            Log.i("Name", object.getString("Name"));
                        }
                        ArrayAdapter<String> areaadapter=new ArrayAdapter<String>(AddaddressActivity.this, android.R.layout.simple_spinner_dropdown_item, area);
                        addAreaEd.setAdapter(areaadapter);


                    }
                } catch (JSONException e) {
                    // if we gets any error then it
                    // will be printed in log cat.
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // below method is called if we get
                // any error while fetching data from API.
                // below line is use to display an error message.
            }
        });
        // below line is use for adding object
        // request to our request queue.
        queue.add(objectRequest);

    }

    private void getPincodeDetails(String pincode) {
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, pincode,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("data", response.toString());
                    String status=response.getString("Status");

                    if (status.equals("Success")){

                        String state="";

                        JSONArray jsonArray=response.getJSONArray("PostOffice");
                        String[] area=new String[jsonArray.length()];
                        area[0]="Select Area";
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            area[i+1]=object.getString("Name");
                            state=object.getString("State");
                        }
                        ArrayAdapter<String> areaadapter=new ArrayAdapter<String>(AddaddressActivity.this, android.R.layout.simple_spinner_dropdown_item, area);
                        addAreaEd.setAdapter(areaadapter);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}