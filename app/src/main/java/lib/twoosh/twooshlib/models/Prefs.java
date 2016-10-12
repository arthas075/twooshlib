package lib.twoosh.twooshlib.models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by arthas on 9/10/16.

 twoosher {
 userid
 name
 mobile
 pwd
 access_token
 f_access_token

 }
 */

public class Prefs {



    Context c;
    static String twoosher=null;
    static SharedPreferences prefs = null;

    public Prefs(Context c){

        this.c = c;
        this.prefs = c.getSharedPreferences("info.twoosh.TwooshUserPref", Context.MODE_PRIVATE);
        this.twoosher = prefs.getString("twoosher", null);
    }

    public boolean prefExists(){

        if(this.twoosher==null){return false;} else{return true;}
    }



    public void setUserStatics(){


        try {


            JSONObject user_data = new JSONObject(twoosher);
            User.twoosh_user_prefs = user_data;
            User.userid = user_data.getString("userid");
            User.name = user_data.getString("name");
            User.pwd = user_data.getString("pwd");
            User.mobile = user_data.getString("mobile");
            User.access_token = user_data.getString("access_token");
            User.f_access_token = user_data.getString("f_access_token");
            User.appname = "Twoosh";


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void saveUserStatics(){

        // go to dock
        JSONObject userdetails = new JSONObject();
        try{
            userdetails.put("mobile", User.mobile);
            userdetails.put("pwd",User.pwd);
            userdetails.put("name",User.name);
            userdetails.put("userid",User.userid);
            userdetails.put("otp_verified",User.otp_verified);
            userdetails.put("access_token", User.access_token);
            userdetails.put("f_access_token", User.f_access_token);
            userdetails.put("appname", User.appname);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("twoosher", userdetails.toString());
            editor.commit();

        }
        catch (Exception e){


        }
    }

}
