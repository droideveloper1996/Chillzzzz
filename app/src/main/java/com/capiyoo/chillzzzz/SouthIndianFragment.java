package com.capiyoo.chillzzzz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SouthIndianFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Items> itemsArrayList;
    BurgerRecyclerAdapter recyclerViewAdapter;
    RecyclerView foodList;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    DatabaseReference firebaseDatabase;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.south_indian_food, container, false);
        itemsArrayList = new ArrayList<>();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        linearLayout = view.findViewById(R.id.sampleLayout);
        recyclerViewAdapter = new BurgerRecyclerAdapter(getContext(), itemsArrayList);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE);
        foodList = view.findViewById(R.id.southfoodList);
        foodList.setHasFixedSize(true);
        foodList.setLayoutManager(new LinearLayoutManager(getContext()));
        foodList.setAdapter(recyclerViewAdapter);
        if (recyclerViewAdapter.getItemCount() > 0) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }

        getMenu();


        return view;
    }


    void getMenu() {

        FirebaseDatabase checkmate_menu_database = FirebaseDatabase.getInstance();
        checkmate_menu_database.getReference().child(Constant.SHOPPE_CHECK_MATE).child(Constant.MENU).child(Constant.SOUTH_INDIAN).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    itemsArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            Items items = dataSnapshot1.getValue(Items.class);
                            itemsArrayList.add(items);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    recyclerViewAdapter = new BurgerRecyclerAdapter(getContext(), itemsArrayList);
                    foodList.setAdapter(recyclerViewAdapter);
                    if (recyclerViewAdapter.getItemCount() > 0) {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Null Return", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
