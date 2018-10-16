package com.capiyoo.chillzzzz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PrintInvoice extends ArrayAdapter<Orders> {
    public PrintInvoice(@NonNull Context context, @NonNull List<Orders> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_list_view, parent, false);
        }

        Orders orders = getItem(position);

        TextView item_name = listItemView.findViewById(R.id.item_name);
        TextView item_price = listItemView.findViewById(R.id.item_price);
        TextView item_quantity = listItemView.findViewById(R.id.item_quantity);

        item_name.setText(orders.getProduct_name());
        item_price.setText(orders.getGrandtotal());
        item_quantity.setText(orders.getQuantity());


        return listItemView;
    }
}
