package com.capiyoo.chillzzzz;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

public class SharedPref {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "mydata";
    public static final String DAILY_ORDER_NUMBER = "daily_order_number";
    public static final String RANDOM_ORDER_ID = "order_id";
    SharedPreferences.Editor editor;
    int OrderNumber = 0;


    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        OrderNumber= sharedPreferences.getInt(DAILY_ORDER_NUMBER, 0);
    }

    void generateOrder(String orderId) {
        editor.putString(RANDOM_ORDER_ID, orderId);
        editor.commit();
    }

    String getOrderId() {
        return sharedPreferences.getString(RANDOM_ORDER_ID, null);
    }

    void getOrderNUmber() {
        ++OrderNumber;
        editor.putInt(DAILY_ORDER_NUMBER, OrderNumber);
        editor.commit();
    }

    int getOrderNumber() {
        getOrderNUmber();
        return sharedPreferences.getInt(DAILY_ORDER_NUMBER, -1);

    }


}



