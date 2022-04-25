package com.oakspro.a1rice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.oakspro.a1rice.ui.home.HomeFragment;
import com.oakspro.a1rice.ui.profile.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    Context context;
    ArrayList<StoreListData> arrayList;
    private String img_link="";

    public StoreListAdapter(Context context, ArrayList<StoreListData> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        img_link=context.getApplicationContext().getResources().getString(R.string.core_api)+"app_api/stores_1203/";
        return new StoreListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreListData model=arrayList.get(position);
        holder.storeName.setText(model.getName());
        holder.storeCaption.setText(model.getCaption());
        holder.storePrice.setText("₹ "+model.getMin_price());
        holder.storeRating.setText(model.getRating());
        Picasso.get().load(img_link+model.getPic()).into(holder.storeImg);

        /*
        holder.storeItemLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductsFragment productsFragment=new ProductsFragment();
                FragmentManager fragmentManager=((FragmentActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_dash, productsFragment, "ProductFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

         */

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                Fragment fragment = new ProductsFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView storeImg;
        TextView storeName, storeCaption, storePrice, storeRating;
        LinearLayout storeItemLL;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storeName=itemView.findViewById(R.id.store_name_txt);
            storeCaption=itemView.findViewById(R.id.store_caption_txt);
            storePrice=itemView.findViewById(R.id.store_price_txt);
            storeRating=itemView.findViewById(R.id.store_rating_txt);
            storeImg=itemView.findViewById(R.id.store_img);
            storeItemLL=itemView.findViewById(R.id.linearll);
            cardView=itemView.findViewById(R.id.card11);


        }
    }

}
