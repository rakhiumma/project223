package com.oakspro.a1rice.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.oakspro.a1rice.AddaddressActivity;
import com.oakspro.a1rice.AddressData;
import com.oakspro.a1rice.AddressListAdapter;
import com.oakspro.a1rice.CategoryAdapter;
import com.oakspro.a1rice.CategoryListData;
import com.oakspro.a1rice.DashActivity;
import com.oakspro.a1rice.R;
import com.oakspro.a1rice.SignupActivity;
import com.oakspro.a1rice.SliderImgAdapter;
import com.oakspro.a1rice.SliderItem;
import com.oakspro.a1rice.databinding.FragmentHomeBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<CategoryListData> list_data=new ArrayList<>();
    private ArrayList<AddressData> list_address=new ArrayList<>();
    private GridView gridView;
    AlertDialog builder2;
    String api=null;
    String api_address=null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    ArrayList<SliderItem> adsArray=new ArrayList<>();
    SliderView adSlider;
    double latitude;
    double longitude;
    final static int REQUEST_LOCATION = 199;

    TextView addressHead, addressText;
    LinearLayout homeLL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressDialog=new ProgressDialog(getContext());
        preferences=getContext().getSharedPreferences("MyLogin", Context.MODE_PRIVATE);

        api=getActivity().getResources().getString(R.string.core_api)+"app_api/category_api.php";
        api_address=getActivity().getResources().getString(R.string.core_api)+"app_api/address_list_api.php";

        adSlider=root.findViewById(R.id.ads_slider);
        gridView=root.findViewById(R.id.gridview_rice);
        addressHead=root.findViewById(R.id.text_head);
        addressText=root.findViewById(R.id.text_address);
        homeLL=root.findViewById(R.id.home_address_ll);

        getCategory_Ads();
        checkGps();
      //  alertCode();


        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddressBook();
            }
        });

        return root;
    }

    private void checkGps() {
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            /*
            new AlertDialog.Builder(getContext())
                    .setMessage("Turn on Location")
                    .setPositiveButton("Turn ON", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();

             */
            displayLocationSettingsRequest(getContext());
        }
    }

    private void alertCode() {

    }

    private void getCategory_Ads() {
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.loading_rice);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);

        StringRequest request=new StringRequest(Request.Method.POST, api, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    list_data.clear();
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("category");
                    JSONArray jsonArray2=jsonObject.getJSONArray("ads");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject object=jsonArray.getJSONObject(i);

                        CategoryListData listData=new CategoryListData();
                        listData.setCat_id(object.getString("cat_id"));
                        listData.setCat_name(object.getString("cat_name"));
                        listData.setCat_imageurl(object.getString("cat_pic"));

                        list_data.add(listData);
                    }
                    CategoryAdapter adapter=new CategoryAdapter(getContext(), list_data);
                    gridView.setAdapter(adapter);

                    //ads
                    adsArray.clear();

                    for (int i=0; i<jsonArray2.length(); i++){
                        JSONObject object2=jsonArray2.getJSONObject(i);

                        SliderItem sliderItem=new SliderItem();
                        sliderItem.setAd_id(object2.getString("ad_id"));
                        sliderItem.setAd_img(object2.getString("ad_img"));
                        sliderItem.setAd_link(object2.getString("ad_link"));

                        adsArray.add(sliderItem);
                    }
                    SliderImgAdapter adapter2 = new SliderImgAdapter(getContext(), adsArray);

                    adSlider.setSliderAdapter(adapter2);

                    adSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    adSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    adSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    adSlider.setIndicatorSelectedColor(Color.WHITE);
                    adSlider.setIndicatorUnselectedColor(Color.GRAY);
                    adSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
                    adSlider.startAutoCycle();

                    //end of ads


                    progressDialog.dismiss();

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
                data.put("package", getContext().getPackageName());
                return data;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }
    private void coderLoc() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(3000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.getFusedLocationProviderClient(getContext())
                                .requestLocationUpdates(locationRequest, new LocationCallback() {
                                    @Override
                                    public void onLocationResult(@NonNull LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(getContext())
                                                .removeLocationUpdates(this);
                                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                            Log.i("Location", "Lat: "+latitude+" Long: "+longitude);
                                        }
                                    }
                                }, Looper.getMainLooper());

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        PermissionListener dialogPermissionListener =
                                DialogOnDeniedPermissionListener.Builder
                                        .withContext(getContext())
                                        .withTitle("Location")
                                        .withMessage("Location permission needed to find nearest Rice Stores")
                                        .withButtonText(android.R.string.ok)
                                        .withIcon(R.mipmap.ic_launcher)
                                        .build();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private void displayLocationSettingsRequest(Context context) {

        View view=LayoutInflater.from(getContext()).inflate(R.layout.location_dialog, null, true);
        builder2=new AlertDialog.Builder(getContext()).create();
        builder2.setView(view);
        builder2.setCanceledOnTouchOutside(true);
        view.findViewById(R.id.enable_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder2.dismiss();
                builder2.cancel();
                coderLoc();

                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(LocationServices.API).build();
                googleApiClient.connect();

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(10000 / 2);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                Log.i("TAG", "All location settings are satisfied.");
                                builder2.dismiss();
                                builder2.cancel();
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result
                                    // in onActivityResult().
                                    status.startResolutionForResult(getActivity(),REQUEST_LOCATION);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.i("TAG", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                                break;
                        }
                    }
                });
            }
        });
        view.findViewById(R.id.select_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddressBook();
            }
        });
        builder2.show();
    }

    private void openAddressBook() {

        String profileID=preferences.getString("userid", null);
        getAddressBookServer(profileID);

    }

    private void getAddressBookServer(String profileID) {

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.address_book);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
        ListView addressListview;
        addressListview=bottomSheetDialog.findViewById(R.id.address_listview);
        TextView addAddress;
        addAddress=bottomSheetDialog.findViewById(R.id.add_address_txt);

        StringRequest request=new StringRequest(Request.Method.POST, api_address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    list_address.clear();
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("book");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject object=jsonArray.getJSONObject(i);

                        AddressData listData=new AddressData();
                        listData.setAddress_id(object.getString("address_id"));
                        listData.setAddress_area(object.getString("address_area"));
                        listData.setAddress_city(object.getString("address_city"));
                        listData.setAddress_info(object.getString("address_info"));
                        listData.setAddress_name(object.getString("address_name"));
                        listData.setAddress_pincode(object.getString("address_pincode"));
                        listData.setAddress_state(object.getString("address_state"));

                        list_address.add(listData);
                    }
                    AddressListAdapter adapter=new AddressListAdapter(getContext(), list_address);
                    addressListview.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
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
                Map<String, String> data2=new HashMap<>();
                data2.put("pack", getActivity().getPackageName());
                data2.put("userid", profileID);
                return data2;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(getContext());
        requestQueue.add(request);

        addressListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int position=i;
                addressHead.setText(list_address.get(position).getAddress_name());
                addressText.setText(list_address.get(position).getAddress_info());
                builder2.dismiss();
                bottomSheetDialog.dismiss();
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddaddressActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}