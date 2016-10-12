package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;

public class Signup2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        attachListeners();
    }


    public void attachListeners(){


        Button enter_user_details = (Button)findViewById(R.id.enter_user_details);
        final EditText enter_name = (EditText)findViewById(R.id.twoosh_user_name);
        final EditText enter_pwd = (EditText)findViewById(R.id.twoosh_user_password);


        enter_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String name = enter_name.getText().toString();
                String pwd = enter_pwd.getText().toString();
                JSONObject signup = new JSONObject();
                if(name.equals("")){

                    Snackbar.make(v, "Please enter name...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }else if(pwd.equals("") || pwd.length()<6){

                    Snackbar.make(v, "PLease choose a password minimum 6 characters...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{

                    try{

                        User.name = name;
                        User.pwd = pwd;
                        signup.put("name",name);
                        signup.put("pwd",pwd);
                        signup.put("mobile",User.mobile);


                    }
                    catch (Exception e){

                    }
                }

                signupUser(signup);

            }
        });
    }
    public void signupUser(final JSONObject signupobj){


        // POST - publish twoosh remmote
        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject publishtwoosh_resp = new JSONObject(response);
                    JSONObject response_data = publishtwoosh_resp.getJSONObject("response");


                    String inserted = response_data.getString("inserted");
                    String id = response_data.getString("id");
                    String mobile = response_data.getString("mobile");


                    //int matched = response_data.getInt("matched");
                    String otp_verified = response_data.getString("otp_verified");
                    if(  (publishtwoosh_resp.get("status").equals("Success") && inserted.equals("1")) || otp_verified.equals("0")){

                            User.userid = id;
                            Intent i = new Intent(Signup2.this, VerifyOTP.class);
                            startActivity(i);

                        //renderDock();
                        // verifyotp screen
                       // startVerifyOTP(mobile);


                    }else{

                        Toast.makeText(Signup2.this, "In else part", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Toast.makeText(Signup2.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        String host = getResources().getString(R.string.local_host);
        String createpostapi = getResources().getString(R.string.signupapi);

        String createposturl = host + createpostapi;



        httpclient.Post(this, createposturl, signupobj);


    }
}
