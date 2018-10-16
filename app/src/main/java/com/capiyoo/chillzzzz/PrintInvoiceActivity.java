package com.capiyoo.chillzzzz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoin.btsdk.BluetoothService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PrintInvoiceActivity extends AppCompatActivity {
    private String receiptNumberGenerated;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    ArrayList<Orders> orders;
    private static final int REQUEST_ENABLE_BT = 2;
    String formattedDate = "";
    private static final int REQUEST_CONNECT_DEVICE = 1;  //Get device message
    DatabaseReference cartDatabaseReference;
    ListView listView;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders=new ArrayList<>();
        sharedPref=new SharedPref(this);
        setContentView(R.layout.activity_print_invoice);
        listView=findViewById(R.id.listView);
        PrintInvoice printInvoice=new PrintInvoice(this,orders);
        listView.setAdapter(printInvoice);
        mService = new BluetoothService(PrintInvoiceActivity.this, mHandler);
        //Bluetooth is not available to exit the program
        if (!mService.isAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        cartDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constant.SHOPPE_CHECK_MATE).child("CART").child(sharedPref.getOrderId());
        cartDatabaseReference.keepSynced(true);

    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            // connectPrinter.setVisibility(View.GONE);
                            Toast.makeText(PrintInvoiceActivity.this, "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                            //  btnClose.setEnabled(true);
                            // btnSend.setEnabled(true);
                            // qrCodeBtnSend.setEnabled(true);
                            //  btnSendDraw.setEnabled(true);
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
                    Toast.makeText(PrintInvoiceActivity.this, "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    //connectPrinter.setVisibility(View.VISIBLE);
                    // btnClose.setEnabled(false);
                    //   btnSend.setEnabled(false);
                    //   qrCodeBtnSend.setEnabled(false);
                    //   btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(PrintInvoiceActivity.this, "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    long getNumberDays(String givenDateString) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            long currentTime = System.currentTimeMillis();
            long diff = (currentTime - timeInMilliseconds) / (1000 * 60 * 60 * 24);
            System.out.println("Date in milli :: " + diff);
            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                //Request to turn on Bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is turned on
                    Toast.makeText(PrintInvoiceActivity.this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
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

    void connectPrinter() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }


    void printInvoice()

    {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("hh:mm:ss");
        String timeFomatted = df.format(cal.getTime());


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormatf = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = dateFormatf.format(c);

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

                cmd[2] |= 0x10;
                mService.write(cmd);
                mService.sendMessage(formatString("Order Receipt"), "GBK");

                cmd[2] &= 0xEF;
                mService.write(cmd);
                mService.sendMessage(arrangeEndToEnd("Receipt No.", receiptNumberGenerated), "GBK");

                cmd[2] &= 0xEF;
                mService.write(cmd);
                mService.sendMessage(arrangeEndToEnd("Order Date.", formattedDate), "GBK");

                cmd[2] &= 0xEF;
                mService.write(cmd);
                mService.sendMessage(arrangeEndToEnd("Order Time", timeFomatted), "GBK");


                cmd[2] |= 0x10;
                mService.write(cmd);
                mService.sendMessage(formatString("--------------------------------"), "GBK");


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


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


