package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;

public class VerifyOTP extends AppCompatActivity {

    String tomobile = "";
    String topwd = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Intent i = getIntent();
       this.tomobile = i.getStringExtra("mobile");
        this.topwd = i.getStringExtra("pwd");


        final Button verifyotp = (Button) findViewById(R.id.VerifyOTP);
        Button resendotp = (Button) findViewById(R.id.ResendOTP);
        final EditText twoosh_otp = (EditText)findViewById(R.id.twoosh_user_otp);

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // call verifyotp
                String otp_to = twoosh_otp.getText().toString();
                // POST - publish twoosh remmote
                HttpClient httpclient = new HttpClient(new HttpClient.PostBack() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject publishtwoosh_resp = new JSONObject(response);
//                            JSONObject response_data = publishtwoosh_resp.getJSONObject("response");
//
//
//                            String inserted = response_data.getString("inserted");
//                            String id = response_data.getString("id");
//                            String mobile = response_data.getString("mobile");
//
//
//                            //int matched = response_data.getInt("matched");
//                            String otp_verified = response_data.getString("otp_verified");
                            if(publishtwoosh_resp.get("status").equals("Success")){


                               // go to dock
                                JSONObject userdetails = new JSONObject();
                                try{
                                    userdetails.put("mobile", tomobile);
                                    userdetails.put("pwd",topwd);
                                    userdetails.put("access_token", "");
                                    userdetails.put("otp_verified","1");
                                }
                                catch (Exception e){}
                                Intent dock = new Intent(VerifyOTP.this, TwooshDock.class);
                                String twoosher = userdetails.toString();

                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                                        "info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("twoosher",userdetails.toString());
                                editor.commit();

                                // dock.putExtra("work","getaccess");
                                startActivity(dock);

                            }else{

                                Toast.makeText(VerifyOTP.this, "OTP Verificaion failed...Please try again", Toast.LENGTH_SHORT).show();
                            }



                        } catch (JSONException e) {
                            Toast.makeText(VerifyOTP.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
                String host = getResources().getString(R.string.local_host);
                String createpostapi = getResources().getString(R.string.verifyotpapi);

                String createposturl = host + createpostapi;
                JSONObject verifyotpobj =  new JSONObject();
                try{
                    verifyotpobj.put("mobile",tomobile);
                    verifyotpobj.put("otp",otp_to);
                }
                catch (Exception e){}


                httpclient.Post(VerifyOTP.this, createposturl, verifyotpobj);



            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                // resend otp

                //Toast.makeText(TwooshDock.this,"Getting rooms list..."+User.name,Toast.LENGTH_SHORT).show();
                HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

                    @Override
                    public void onResponse(String response) {

                        try {

                            //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                            JSONObject roomlist_response = new JSONObject(response);
                            JSONArray roomlist = roomlist_response.getJSONArray("response");

                            if(roomlist_response.get("status").equals("Success")){
                                Snackbar.make(view, "OTP sent successfully...", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                           // addtoAdapter(roomlist);


                        } catch (JSONException e) {
                            //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });




                String host = getResources().getString(R.string.local_host);
                String resendotpapi = getResources().getString(R.string.resendotpapi);
                String resendotpurl = host+resendotpapi;

                String urlparams;
                urlparams = "{\"mobile\":\""+tomobile+"\"}";


                httpClient.Get(VerifyOTP.this, resendotpurl, urlparams);



            }
        });
//        Snackbar.make(, "Verify OTP for mobile"+mobile, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
    }

}
