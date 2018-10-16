package com.capiyoo.chillzzzz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecylerVieHolder> {


    private Context mctx;
    private ArrayList<Items> itemsArrayList;
    private FoodItemClickListner foodItemClickListner;


    public RecyclerViewAdapter(Context mCtx, ArrayList<Items> items,FoodItemClickListner foodItemClickListner) {
        this.itemsArrayList = items;
        this.mctx = mCtx;
        this.foodItemClickListner=foodItemClickListner;
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
        holder.priceHalf.setText("₹"+itemsArrayList.get(position).getmFoodHalfprice());
        holder.foodDes.setText(itemsArrayList.get(position).getmItemDescription());



        holder.priceHalf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mctx,"Clicked Half Price",Toast.LENGTH_LONG).show();
            }
        });
        holder.priceFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mctx,"Clicked Full Price",Toast.LENGTH_LONG).show();

            }
        });
    }

    interface FoodItemClickListner{
        void onFoodItemClick(int id);
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    class RecylerVieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView foodImage;
        TextView foodNAme;
        TextView priceFull;
        TextView priceHalf;
        TextView foodDes;

        public RecylerVieHolder(View itemView) {

            super(itemView);
            foodImage=itemView.findViewById(R.id.food_icon);
            foodNAme=itemView.findViewById(R.id.foodName);
            priceFull=itemView.findViewById(R.id.food_price_full);
            priceHalf=itemView.findViewById(R.id.food_price_half);
            foodDes=itemView.findViewById(R.id.foodDesc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            foodItemClickListner.onFoodItemClick(getAdapterPosition());
        }
    }
}
