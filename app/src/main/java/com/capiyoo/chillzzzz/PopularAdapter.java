package com.capiyoo.chillzzzz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    Context mCtx;
    int arr[]={R.drawable.south_indian,R.drawable.burgur,R.drawable.beverages,R.drawable.appetiser};

    public PopularAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCtx).inflate(R.layout.popular_recyler_adapter,parent,false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularViewHolder holder, int position) {
        Picasso.with(mCtx).load(arr[position]).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PopularViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.popularImage);
        }
    }
}
