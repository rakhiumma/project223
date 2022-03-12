package com.oakspro.a1rice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.net.ContentHandler;
import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryListData> {
    ArrayList<CategoryListData> listData;
    Context context;
    String pic_address=null;

    public CategoryAdapter(Context context, ArrayList<CategoryListData> listData){
        super(context, R.layout.grid_item, listData);
        this.context=context;
        this.listData=listData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.grid_item, null, true);

        CategoryListData listData=getItem(position);
        ImageView img=(ImageView)convertView.findViewById(R.id.cat_image);
        TextView name=(TextView)convertView.findViewById(R.id.cat_name);

        pic_address=getContext().getResources().getString(R.string.core_api)+"app_api/category_1203/"+listData.getCat_imageurl();

        Picasso.get().load(pic_address).into(img);
        name.setText(listData.getCat_name());
        return convertView;
    }
}
