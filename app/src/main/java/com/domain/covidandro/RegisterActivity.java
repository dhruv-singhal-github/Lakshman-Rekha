package com.domain.covidandro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText nameBox;
    EditText editText;
    EditText startText;
    EditText pinBox;
    EditText periodBox;
    Calendar myCalendar;
    Calendar myCalendar2;
    Button takePhoto;

    public static final String SHARED_PREF = "com.domain.covidandro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle extras = getIntent().getExtras();

        String newString= extras.getString("STRING_I_NEED");

         myCalendar = Calendar.getInstance();
         myCalendar2 = Calendar.getInstance();
         takePhoto = (Button)findViewById(R.id.complete_button);

        editText = (EditText) findViewById(R.id.dob_box);
        startText = (EditText) findViewById(R.id.startDate_box);
        nameBox = (EditText) findViewById(R.id.name_box);
        pinBox = (EditText) findViewById(R.id.pin_box);
        periodBox = (EditText) findViewById(R.id.period_box);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, month);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }
        };

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, startdate, myCalendar2.get(Calendar.YEAR),
                        myCalendar2.get(Calendar.MONTH), myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nameBox.getText().toString().trim().equalsIgnoreCase("")){
                    nameBox.setError("This field cannot be blank.");
                }
                else if(editText.getText().toString().trim().equalsIgnoreCase("")){
                    editText.setError("This field cannot be blank.");
                }
                else if(pinBox.getText().toString().trim().equalsIgnoreCase("")){
                    pinBox.setError("This field cannot be blank.");
                }
                else if(periodBox.getText().toString().trim().equalsIgnoreCase("")){
                    periodBox.setError("This field cannot be blank.");
                }
                else if(startText.getText().toString().trim().equalsIgnoreCase("")){
                    startText.setError("This field cannot be blank.");
                }
                else {
                    String url="http://"+newString+":8080/post";
                    SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("name_str", nameBox.getText().toString().trim()).apply();
                    prefsEditor.putString("dob_str", editText.getText().toString().trim()).apply();
                    prefsEditor.putString("pin_str", pinBox.getText().toString().trim()).apply();
                    prefsEditor.putString("period_str", periodBox.getText().toString().trim()).apply();
                    prefsEditor.putString("start_str", startText.getText().toString().trim()).apply();

                    new RegisterActivity.authenticate().execute(url);

//                    Intent intent = new Intent(RegisterActivity.this, CameraXLivePreviewActivity.class);
//                    startActivity(intent);

                }
            }
        });

    }

   public class authenticate extends AsyncTask<String,String,String> {
        String decodedString = "";
        int duplicate=0;
        ProgressDialog p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(RegisterActivity.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                duplicate=0;
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                JSONObject jsonParam = new JSONObject();

                jsonParam.put("name", mPrefs.getString("name_str", ""));
                jsonParam.put("pwd",mPrefs.getString("period_str", "") );
                jsonParam.put("email",mPrefs.getString("pin_str", ""));
                jsonParam.put("dob", mPrefs.getString("dob_str", ""));
                jsonParam.put("startdate", mPrefs.getString("start_str", ""));

                jsonParam.put("gpsloc1", "0");
                jsonParam.put("gpsloc2", "0");

                Log.i("JSON", jsonParam.toString());
                try {
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();
                }catch(Exception e){

                    Log.d("dupli", String.valueOf(duplicate));
                }


                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(),"UTF-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    System.out.println("poopy");
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("pooh"+response.toString());
                }


                Log.i("STATUSaa", String.valueOf(conn.getResponseMessage()));
                Log.i("MSG" , conn.getResponseMessage());

                conn.disconnect();
            } catch (Exception e) {
                duplicate=1;
                Log.d("dup", String.valueOf(duplicate));
                e.printStackTrace();
            }
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                try {
//                    BufferedReader in = new BufferedReader(
//                            new InputStreamReader(
//                                    urlConnection.getInputStream()));
//
//                    while ((decodedString = in.readLine()) != null) {
//                        validate=Integer.parseInt(decodedString);
//                        System.out.println("hello"+decodedString);
//                    }
//                    in.close();
//
//                } finally {
//                    urlConnection.disconnect();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return decodedString;


            return "abc" ;}

        @Override
        protected void onPostExecute(String s) {
                p.dismiss();
            if(duplicate==1){
                Toast.makeText(RegisterActivity.this,"Email already registered", Toast.LENGTH_LONG).show();
            }

            else {


                Intent intent = new Intent(RegisterActivity.this, CameraXLivePreviewActivity.class);
                startActivity(intent);
            }
        }
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateLabel2() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(startText.getWindowToken(), 0);

        startText.setText(sdf.format(myCalendar2.getTime()));
    }
}
