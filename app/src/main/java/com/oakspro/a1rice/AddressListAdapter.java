package com.oakspro.a1rice;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddressListAdapter extends ArrayAdapter<AddressData> {

    private Context context;
    private  ArrayList<AddressData> listData;
    private int resource;

    public AddressListAdapter(Context context, ArrayList<AddressData> listData){
        super(context, R.layout.address_item, listData);
        this.context=context;
        this.listData=listData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.address_item, null, true);

        AddressData listData=getItem(position);
        TextView addressTitle=(TextView)convertView.findViewById(R.id.address_title_txt);
        TextView addressInfo=(TextView)convertView.findViewById(R.id.address_info_txt);
        ImageView addressImg=(ImageView)convertView.findViewById(R.id.drop_img);

       addressTitle.setText(listData.getAddress_name());
       addressInfo.setText(listData.getAddress_info());

       addressImg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               PopupMenu popupMenu=new PopupMenu(getContext(), addressImg);
               popupMenu.getMenuInflater().inflate(R.menu.address_menu, popupMenu.getMenu());
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   @Override
                   public boolean onMenuItemClick(MenuItem menuItem) {
                       switch (menuItem.getItemId()){
                           case R.id.edit:
                               Toast.makeText(context, "Edit Address", Toast.LENGTH_SHORT).show();
                               break;
                           case R.id.delete:
                               Toast.makeText(context, "Delete Address", Toast.LENGTH_SHORT).show();
                               break;
                       }
                       return true;
                   }
               });
               popupMenu.show();

           }
       });

        return convertView;
    }
}
