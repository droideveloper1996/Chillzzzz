package com.capiyoo.chillzzzz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BurgerRecyclerAdapter extends RecyclerView.Adapter<BurgerRecyclerAdapter.RecylerVieHolder> {


    private Context mctx;
    private ArrayList<Items> itemsArrayList;


    public BurgerRecyclerAdapter(Context mCtx, ArrayList<Items> items) {
        this.itemsArrayList = items;
        this.mctx = mCtx;
    }

    @NonNull
    @Override
    public RecylerVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecylerVieHolder(LayoutInflater.from(mctx).inflate(R.layout.list_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerVieHolder holder, int position) {

        Picasso.with(mctx).load(itemsArrayList.get(position).getmItemImageUrl()).into(holder.foodImage);
        holder.foodNAme.setText(itemsArrayList.get(position).getmItemName());
        holder.priceFull.setText("₹"+itemsArrayList.get(position).getmFoodFullprice());
        holder.foodDes.setText(itemsArrayList.get(position).getmItemDescription());
        holder.pricehalf.setVisibility(View.GONE);
        holder.titlefull.setText("Price");
        holder.titlehalf.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    class RecylerVieHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodNAme;
        TextView priceFull;
        TextView pricehalf;
        TextView foodDes;
        TextView titlefull;
        TextView titlehalf;


        public RecylerVieHolder(View itemView) {

            super(itemView);
            titlehalf=itemView.findViewById(R.id.title_half);

            titlefull=itemView.findViewById(R.id.title_full);
            foodImage=itemView.findViewById(R.id.food_icon);
            foodNAme=itemView.findViewById(R.id.foodName);
            priceFull=itemView.findViewById(R.id.food_price_full);
            pricehalf=itemView.findViewById(R.id.food_price_half);
            foodDes=itemView.findViewById(R.id.foodDesc);
        }
    }
}
