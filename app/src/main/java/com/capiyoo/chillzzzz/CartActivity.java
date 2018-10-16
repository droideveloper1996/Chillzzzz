package com.capiyoo.chillzzzz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hoin.btsdk.BluetoothService;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    String timeFomatted;
    String formattedDate;
    private CheckBox zomatoCheckBox;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;  //Get device message
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    RadioButton full;
    RadioButton half;
    ImageView imageView;
    TextView finalPrice;
    Button addMore;
    Button checkOut;
    Button increment;
    Button decrement;
    EditText customerID;
    EditText customerNAme;
    EditText customerAddress;
    SharedPref sharedPref;
    String _customerID;
    String _customerName;
    String _customerAddress;

    EditText quantity;
    TextView price;
    TextView itemName;
    int _quantity;
    Bundle bundle;
    String key;
    String category;
    DatabaseReference databaseReference;
    Items item;
    Button checkout;
    int _finalprice = 0;
    DatabaseReference salesDatabaseReference;
    DatabaseReference cartDatabaseReference;
    Button connectPrinter;


    String receiptNumber = "CHKMTE" + Long.toString(System.currentTimeMillis() / 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        customerAddress = findViewById(R.id.customerAddress);
        customerID = findViewById(R.id.customerId);
        customerNAme = findViewById(R.id.customerName);

        zomatoCheckBox = findViewById(R.id.zomato_bill);
        zomatoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zomatoCheckBox.isChecked()) {
                    customerAddress.setVisibility(View.VISIBLE);
                    customerID.setVisibility(View.VISIBLE);
                    customerNAme.setVisibility(View.VISIBLE);
                } else {
                    customerAddress.setVisibility(View.GONE);
                    customerID.setVisibility(View.GONE);
                    customerNAme.setVisibility(View.GONE);
                }
            }
        });
        connectPrinter = findViewById(R.id.connectPrinter);
        checkout = findViewById(R.id.checkout);
        mService = new BluetoothService(CartActivity.this, mHandler);
        //Bluetooth is not available to exit the program
        if (!mService.isAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        imageView = findViewById(R.id.image_item);
        bundle = new Bundle();
        full = findViewById(R.id.full);
        half = findViewById(R.id.half);
        Intent intent = getIntent();
        if (intent != null) {
            bundle = intent.getExtras();
            key = bundle.getString("ITEM_KEY");
            category = bundle.getString("ITEM_CATEGORY");
        }
        finalPrice = findViewById(R.id.final_price);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE).child(Constant.MENU).child(Constant.CHINESE);
        databaseReference.keepSynced(true);
        addMore = findViewById(R.id.addMore);
        checkOut = findViewById(R.id.checkout);
        price = findViewById(R.id.item_price);
        itemName = findViewById(R.id.item_name);
        increment = findViewById(R.id.increment);
        decrement = findViewById(R.id.decrement);
        quantity = findViewById(R.id.item_quantity);
        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE).child("Sales").child(getCurrentDate());
        salesDatabaseReference.keepSynced(true);
        String quan = quantity.getText().toString();
        try {
            _quantity = Integer.parseInt(quan);
        } catch (Exception e) {
            e.printStackTrace();
        }
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++_quantity;
                quantity.setText(Integer.toString(_quantity));
                updatePrice();

            }
        });
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_quantity <= 1) {
                    _quantity = 1;
                }
                --_quantity;

                quantity.setText(Integer.toString(_quantity));
                updatePrice();
            }
        });

        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addmore();
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (full.isChecked()) {
                    updatePrice();
                }
            }
        });
        half.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (half.isChecked()) {
                    updatePrice();
                }
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    connectPrinter();
                    checkOut.setVisibility(View.GONE);
                    connectPrinter.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        timeFomatted = df.format(cal.getTime());
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = dateFormat.format(c);
        sharedPref = new SharedPref(CartActivity.this);
        if (sharedPref.getOrderId() == null||sharedPref.getOrderId().equals("null")) {
            sharedPref.generateOrder(receiptNumber);
        }
        cartDatabaseReference=FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE).child("CART").child(sharedPref.getOrderId());
        cartDatabaseReference.keepSynced(true);

        connectPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _customerID = customerID.getText().toString();
                _customerName = customerNAme.getText().toString();
                _customerAddress = customerAddress.getText().toString();

                if (zomatoCheckBox.isChecked()) {
                    if (TextUtils.isEmpty(_customerID) || TextUtils.isEmpty(_customerName) || TextUtils.isEmpty(_customerAddress)) {
                        Toast.makeText(CartActivity.this, "Enter Zomato Details", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                try {
                    String lang = getString(R.string.bluetooth_strLang);

                    byte[] cmd = new byte[3];
                    cmd[0] = 0x1b;
                    cmd[1] = 0x21;
                    if ((lang.compareTo("en")) == 0) {

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Checkmate Restaurant"), "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Keshavpuram Awasvikas 1" + " Kalyanpur" + " Kanpur Nagar"
                                ) + '\n' + arrangeEndToEnd("Contact No.", "9453043163") + '\n' +
                                        arrangeEndToEnd("Person", "Sanjay Singh")
                                , "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        if (zomatoCheckBox.isChecked()) {

                            cmd[2] |= 0x10;
                            mService.write(cmd);
                            mService.sendMessage(formatString("Order For Zomato"), "GBK");

                            cmd[2] &= 0xEF;

                            mService.write(cmd);
                            mService.sendMessage(arrangeEndToEnd("Order For", _customerName), "GBK");

                            cmd[2] &= 0xEF;
                            mService.write(cmd);
                            mService.sendMessage(arrangeEndToEnd("Order Id", _customerID), "GBK");

                            cmd[2] |= 0x10;
                            mService.write(cmd);
                            mService.sendMessage(formatString("--------------------------------"), "GBK");

                        }

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Order Receipt"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Receipt No.", receiptNumber), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Order Date.", formattedDate), "GBK");


                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Order Time", timeFomatted), "GBK");

                        //ORDER NUMBER WHICH IS UNIQUE STARTING FROM DAY.
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Order Number", Integer.toString(sharedPref.getOrderNumber())), "GBK");


                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeThreeWay("ITEM", "QUANTITY", "PRICE"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeThreeWay(item.getmItemName(), Integer.toString(_quantity), Integer.toString(_finalprice)), "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Receipt Amount is Inclusive of   all taxes."), "GBK");


                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Have a great Day ahead :)\n"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("CapiYoo Infotech Pvt Ltd."), "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("droid.developer1996@gmail.com\n"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("VISIT AGAIN\n\n"), "GBK");

                        Map<String, String> map = new HashMap<>();
                        map.put("TokenGenerated", receiptNumber);
                        map.put("Time", timeFomatted);
                        map.put("Date", formattedDate);
                        map.put("Amount", Integer.toString(_finalprice));
                        map.put("Quantity", Integer.toString(_quantity));

                        updateSales(map);
                        sharedPref.generateOrder(null);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        fetchItem();

    }

    void updatePrice() {
        if (half.isChecked()) {
            price.setText("₹" + item.getmFoodHalfprice());
            _finalprice = Integer.parseInt(item.getmFoodHalfprice()) * _quantity;

        } else if (full.isChecked()) {
            price.setText("₹" + item.getmFoodFullprice());
            _finalprice = Integer.parseInt(item.getmFoodFullprice()) * _quantity;

        }
        setFinalPrice();

    }

    void setFinalPrice() {

        finalPrice.setText("₹" + Integer.toString(_finalprice));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mService.isAvailable()) {
            if (!mService.isBTopen()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }


    void fetchItem() {
        item = new Items();
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    item = dataSnapshot.getValue(Items.class);
                    Toast.makeText(getApplicationContext(), Long.toString(dataSnapshot.getChildrenCount()), Toast.LENGTH_LONG).show();
                    Picasso.with(CartActivity.this).load(item.getmItemImageUrl()).into(imageView);
                    itemName.setText(item.getmItemName());
                    updatePrice();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                //Request to turn on Bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is turned on
                    Toast.makeText(CartActivity.this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //Request to connect to a Bluetooth device
                if (resultCode == Activity.RESULT_OK) {
                    //	A device item in the search list has been clicked

                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //Get the mac address of the device in the list item
                    con_dev = mService.getDevByMac(address);
                    mService.connect(con_dev);

                }
                break;
        }
    }

    void connectPrinter() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);      //ÔËÐÐÁíÍâÒ»¸öÀàµÄ»î¶¯
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }


    String formatString(String str) {

        String spacedCha = "";
        int length = str.length();
        int maxSpaces = 32;
        int leftSpaces = maxSpaces - length;
        int remainingSpaces = leftSpaces / 2;

        for (int i = 0; i < remainingSpaces; i++) {
            spacedCha += " ";
        }
        spacedCha += str;
        return spacedCha;

    }

    String arrangeEndToEnd(String str, String lead) {
        String spacedCha = "";
        String myString = "";
        int length1 = str.trim().length();
        int length2 = lead.trim().length();
        int maxSpaces = 32;
        int rem1 = maxSpaces - length1;
        int rem2 = rem1 - length2;
        for (int i = 0; i < rem2; i++) {
            myString += " ";
        }
        spacedCha = str + myString + lead;
        return spacedCha;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            checkout.setVisibility(View.GONE);
                            connectPrinter.setVisibility(View.VISIBLE);

                            Toast.makeText(CartActivity.this, "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:  //ÕýÔÚÁ¬½Ó
                            Log.d("À¶ÑÀµ÷ÊÔ", "ÕýÔÚÁ¬½Ó.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //¼àÌýÁ¬½ÓµÄµ½À´
                        case BluetoothService.STATE_NONE:
                            Log.d("À¶ÑÀµ÷ÊÔ", "µÈ´ýÁ¬½Ó.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //À¶ÑÀÒÑ¶Ï¿ªÁ¬½Ó
                    Toast.makeText(CartActivity.this, "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    checkout.setVisibility(View.VISIBLE);

                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(CartActivity.this, "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    void updateSales(Map<String, String> map) {

    }


    public String getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = dateFormat.format(cal.getTime());
        Log.d("Formateddate", formattedDate);
        return formattedDate;
    }

    public String arrangeThreeWay(String str1, String str2, String str3) {
        String finalArrangedString = "";
        int MaxSpace = 32;
        int firstSpace = str1.length();
        int seconfSpace = str2.length();
        int thrirdSpace = str3.length();


        int reminingSpace1 = MaxSpace - firstSpace;

        finalArrangedString = str1;

        int totallengthoftwoString = seconfSpace + thrirdSpace;
        int leftoverSpace = reminingSpace1 - totallengthoftwoString;
        int firstSpacedChar = leftoverSpace / 2;

        for (int i = 0; i < firstSpacedChar; i++) {

            finalArrangedString += " ";
        }
        finalArrangedString += str2;

        for (int i = 0; i < firstSpacedChar; i++) {

            finalArrangedString += " ";
        }
        finalArrangedString += str3;
        Log.d("FInalArranged", finalArrangedString);
        return finalArrangedString;
    }

    void addmore()
    {
        Map<String, Object> items=new HashMap<>();
        items.put("product_key",key);
        items.put("product_name",item.getmItemName());
        items.put("grandtotal",Integer.toString(_finalprice));
        items.put("quantity",Integer.toString(_quantity));
        cartDatabaseReference.push().updateChildren(items).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete())
                {
                    finish();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


}


