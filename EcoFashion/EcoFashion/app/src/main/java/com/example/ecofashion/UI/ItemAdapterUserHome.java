package com.example.ecofashion.UI;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.example.ecofashion.Entities.Item;
import com.example.ecofashion.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Býr til "hlut" til þess að birta item snyrtilega í UserHome og Cart
 */
public class ItemAdapterUserHome extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> itemList = new ArrayList<>();

    public ItemAdapterUserHome(@NonNull Context context, @LayoutRes List<Item> list) {
        super(context, 0, list);
        mContext = context;
        itemList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_user_home,parent,false);

        Item currentItem = itemList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
        name.setText(currentItem.getItemName());

        TextView size = (TextView) listItem.findViewById(R.id.textView_size);
        size.setText(currentItem.getItemSize());

        TextView price = (TextView) listItem.findViewById(R.id.textView_price);
        price.setText(String.valueOf(currentItem.getItemPrice()) + " kr.");

        ImageView itemImage = (ImageView) listItem.findViewById(R.id.imageView_item);
        String imageId = currentItem.getImage1(); //Get image id of current property
        String url = "http://10.0.2.2:8090/Image/" + imageId;
        Picasso.get().load(url).into(itemImage); //Loadar mynd inn a imageView
        return listItem;
    }
}