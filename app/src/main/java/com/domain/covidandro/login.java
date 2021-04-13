package com.domain.covidandro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.StatusLine;

public class login extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle extras = getIntent().getExtras();

        String newString= extras.getString("STRING_I_NEED");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        System.out.println(ipAddress+"ipaddress");
        Button login_btn=findViewById(R.id.login_btn);
        EditText username=(EditText)findViewById(R.id.username);
        EditText password=(EditText)findViewById(R.id.pwd);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String uss= String.valueOf(username.getText());
               String pw= String.valueOf(password.getText());
               Log.d("username", String.valueOf(username.getText()));
               String url="http://"+newString+":8080/authenticate/"+uss+"/"+pw;
               new authenticate().execute(url);



            }
        });
    }

    private class authenticate extends AsyncTask<String,String,String> {
        String decodedString = "";
        int validate=1;
        ProgressDialog p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(login.this);
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    urlConnection.getInputStream()));

                    while ((decodedString = in.readLine()) != null) {
                        validate=Integer.parseInt(decodedString);
                        System.out.println("hello"+decodedString);
                    }
                    in.close();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return decodedString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(validate==0){
                Intent intent=new Intent(getApplicationContext(),trackPost.class);
                startActivity(intent);
            }
            else {

                p.dismiss();
                Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_LONG).show();
            }
        }
    }
}