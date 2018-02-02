package com.example.don.qrtm;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//implementing onclicklistener
    public class scan extends AppCompatActivity implements View.OnClickListener {

        //View Objects
        private Button buttonScan;
        private TextView textViewName;

        //qr code scanner object
        private IntentIntegrator qrScan;

        FirebaseDatabase database;
        DatabaseReference myRef;

    String date;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_scan);

             date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            database = FirebaseDatabase.getInstance();

            //View objects
            buttonScan = (Button) findViewById(R.id.buttonScan);
            textViewName = (TextView) findViewById(R.id.textView);


            //intializing scan object
            qrScan = new IntentIntegrator(this);

            //attaching onclick listener
            buttonScan.setOnClickListener(this);
        }

        //Getting the scan results
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                //if qrcode has nothing in it
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    //if qr contains data
                    try {
                        //converting the data to json
                        JSONObject obj = new JSONObject(result.getContents());
                        //setting values to textviews
                      //  textViewName.setText(obj.getString("name"));
                      //  textViewAddress.setText(obj.getString("address"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //if control comes here
                        //that means the encoded format not matches
                        //in this case you can display whatever data is available on the qrcode
                        //to a toast
                        textViewName.setText(result.getContents().toString());
                        String empId=result.getContents().toString();
                        addIntoDb(empId);
                        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

        private void addIntoDb(String empId) {

            myRef = database.getReference();

            myRef = myRef.child(date);
            myRef=myRef.child(empId);


            Date currentTime = Calendar.getInstance().getTime();
            String timeString= currentTime.toString();
            myRef.setValue(timeString);
        }

        @Override
        public void onClick(View view) {
            //initiating the qr code scan
            qrScan.initiateScan();
        }

    }