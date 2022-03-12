package com.oakspro.a1rice.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oakspro.a1rice.CategoryAdapter;
import com.oakspro.a1rice.CategoryListData;
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
    private GridView gridView;
    String api=null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    ArrayList<SliderItem> adsArray=new ArrayList<>();
    SliderView adSlider;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        progressDialog=new ProgressDialog(getContext());
        preferences=getContext().getSharedPreferences("MyLogin", Context.MODE_PRIVATE);

        api=getActivity().getResources().getString(R.string.core_api)+"app_api/category_api.php";

        adSlider=root.findViewById(R.id.ads_slider);
        gridView=root.findViewById(R.id.gridview_rice);

        getCategory_Ads();


        return root;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}