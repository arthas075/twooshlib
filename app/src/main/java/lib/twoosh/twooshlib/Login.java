package lib.twoosh.twooshlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.Toasts;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Prefs prefs = new Prefs(getApplicationContext());
        if(prefs.prefExists()){
            Intent todock = new Intent(this,TwooshDock.class);
            startActivity(todock);
        }


        Button loginbtn = (Button) findViewById(R.id.enter_pwd_btn);
        final EditText enter_pwd = (EditText)findViewById(R.id.twoosh_user_password);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pwd = enter_pwd.getText().toString();
                if(pwd.equals("") || pwd.length()<6){

                    Snackbar.make(view, "Password is minimum 6 characters...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{

                    User.pwd = pwd;
                    performLogin(pwd);

                }


            }
        });

    }


    public void performLogin(final String pwwd){

        // POST - publish twoosh remmote
        HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject publishtwoosh_resp = new JSONObject(response);
                    JSONObject response_data = publishtwoosh_resp.getJSONObject("response");

                    String id = response_data.getString("_id");
                    String f_access_token = response_data.getString("f_access_token");
                    String name = response_data.getString("name");
                    String otp_verified= response_data.getString("otp_verified");

                    if(publishtwoosh_resp.get("status").equals("Success")){


                        if(otp_verified.equals("1")){

                            // set user statics

                            User.userid = id;
                            User.f_access_token = f_access_token;
                            User.name = name;
                            User.pwd = pwwd;
                            Prefs.subscribeRoom("everything");

                            Prefs.saveUserStatics();
                            Intent i = new Intent(Login.this, TwooshDock.class);
                            startActivity(i);

                        }else{

                          new Toasts().showToastMsg(getApplicationContext(), "Please signup again...");
                        }


                    }else{

                        Toast.makeText(Login.this, "In else part", Toast.LENGTH_SHORT).show();
                    }



                } catch (JSONException e) {
                    Toast.makeText(Login.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        String host = getResources().getString(R.string.local_host);
        String loginapi = getResources().getString(R.string.loginapi);

        String loginurl = host + loginapi;

        JSONObject loginobj = new JSONObject();
        try{

            loginobj.put("mobile",User.mobile);
            loginobj.put("pwd",pwwd);

        }catch (Exception err){}

        httpclient.Post(this, loginurl, loginobj);
    }


}
