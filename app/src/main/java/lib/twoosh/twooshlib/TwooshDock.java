package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import lib.twoosh.twooshlib.models.User;

import lib.twoosh.twooshlib.networks.HttpClient;

//import io.socket.client.IO.Options;
//import io.socket.client.IO;
//import io.socket.client.*;


//import com.github.nkzawa.*;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;


public class TwooshDock extends AppCompatActivity {


    Context c;
    Socket socket = null;
    JSONObject userdetails ;
   // public Socket socket=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoosh_dock);


        // recognize user
        recognizeUser();
           // Toast.makeText(this, User.name,Toast.LENGTH_SHORT).show();

        this.getSupportActionBar().setTitle("Twoosh Chat");
        this.getSupportActionBar().setSubtitle("Twoosh - You are connected.");

        try{

            socket = IO.socket("http://twoosh.info:8000");
            socket.connect();


        }
        catch (Exception e){

        }


    }


    public void recognizeUser(){
    // recognize user

        // sanitize user details
        boolean sanity = sanitizeUser();

        if(sanity){

            // check if new user
            boolean newuser = IfNewUser();
            if(newuser){

                Toast.makeText(this, "New User...Registering", Toast.LENGTH_SHORT).show();
                registerUser();
            }
            else{
                Toast.makeText(this, "Found old User...Entering Dock", Toast.LENGTH_SHORT).show();
                renderDock();

            }

        }
        //get user details
        //JSONObject userdetails = getUserDetails();


       // String application_name = getApplication().getClass().getCanonicalName();

        //Toast.makeText(this, "Making get request to twoosh.info", Toast.LENGTH_SHORT).show();
        //registerUser();

    }

    public boolean sanitizeUser(){




        // check if valid details present
        String name = User.name;
        String mobile = User.mobile;
        String email = User.email;
        String gender = User.gender;
        String dob = User.dob;
        String location = User.location;
        String tags = User.tags;
        String corp_referrer = User.corp_referrer;
        String corp_auth_token = User.corp_auth_token;

//        if(name == "" || mobile=="" || email == "" || gender == "" || dob == "" || location == "" || corp_referrer == "" || corp_auth_token == "" ){
//
//            Toast.makeText(this, "Invalid user details...", Toast.LENGTH_SHORT).show();
//            return false;
//
//        }

        try {
            userdetails = new JSONObject();
            userdetails.put("name",name);
            userdetails.put("mobile",name);
            userdetails.put("email",name);
            userdetails.put("gender",name);
            userdetails.put("dob",name);
            userdetails.put("location",name);
            userdetails.put("tags",name);
            userdetails.put("corp_referrer",name);
            userdetails.put("corp_auth_token",name);



        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;



    }


    public boolean IfNewUser()
    {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
        //
        String twoosher = prefs.getString("twoosher", null);
        if(twoosher==null){
            Toast.makeText(this, "twoosher null...", Toast.LENGTH_SHORT).show();
            return true;
        } else
        {
            Toast.makeText(this, "twoosher : "+twoosher, Toast.LENGTH_SHORT).show();
            return false;

        }


    }

    public void registerUser(){

            //HttpClient httpclient = new HttpClient();

        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {
                // Do Something after the request callback comes has finished

                Toast.makeText(TwooshDock.this,"POST RESPONSE : "+response,Toast.LENGTH_SHORT).show();
            }
        });

        httpclient.Post(this, "http://192.168.0.107:8001/registerUser", userdetails);

//        task.execute();

    }


    public void renderDock(){

        Toast.makeText(TwooshDock.this,"Rendering Dock...",Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twoosh_dock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
