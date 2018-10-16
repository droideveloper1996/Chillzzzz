package com.capiyoo.chillzzzz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {
    TextView textCartItemCount;
    int mCartItemCount;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    NavigationView mNavigationView;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    Fragment fragment;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // mNavigationView = findViewById(R.id.navigationView);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new ChineseFragment()).commit();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // tabLayout.setupWithViewPager(viewPager);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.True, R.string.False);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        getSupportActionBar().setTitle("Chinese");
                        fragment = new ChineseFragment();
                        loadFragment(fragment);
                        //fragmentTransaction.replace(R.id.frame_container, new ChineseFragment()).commit();
                        return true;
                    case R.id.action_southIndian:
                        getSupportActionBar().setTitle("Classic South Indian");
                        fragment = new SouthIndianFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.action_burger:
                        getSupportActionBar().setTitle("Cheesy Burger");
                        fragment = new BurgerFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_ice_cream:
                        getSupportActionBar().setTitle("Beverages & IceCream");
                        fragment = new BeverageFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.action_northIndian:
                        getSupportActionBar().setTitle("Spicy North Indian");
                        fragment = new BlueToothFragment();
                        loadFragment(fragment);
                        return true;

                }

                return true;
            }
        });

        getItemCount();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cart:
                startActivity(new Intent(MenuActivity.this, PrintInvoiceActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void setupBadge() {


        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    void getItemCount() {

        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.getOrderId() != null) {
            DatabaseReference cartDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE).child("CART").child(sharedPref.getOrderId());
            cartDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        mCartItemCount = (int) dataSnapshot.getChildrenCount();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mCartItemCount = 0;
        }
    }
}
//