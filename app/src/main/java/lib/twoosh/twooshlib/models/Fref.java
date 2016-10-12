package lib.twoosh.twooshlib.models;

import android.content.Context;
import android.content.Intent;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;
import lib.twoosh.twooshlib.interfaces.Callbacker;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.Notifs;
import lib.twoosh.twooshlib.services.FService;

/**
 * Created by arthas on 9/10/16.
 */
public class Fref {


    public static Firebase.AuthResultHandler authResultHandler = null;
    public static Context c = null;
    public static Firebase fref_post = null;
    public static Firebase fref_base = null;
    public static Firebase fref_notifs = null;
    public static Firebase fref_rooms = null;
    public Callbacker cb;

    // init frefs
    public Fref(Context c){

        this.c = c;
//        Firebase.setAndroidContext(c);
//        Firebase.getDefaultConfig().setPersistenceEnabled(true);
//        fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");





    }

    public void authenticate(Callbacker cbf){
        this.cb = cbf;

        // set authentication handlers
        // Create a handler to handle the result of the authentication
        this.authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                //showToastMsg("User authenticated...");
//                JSONObject notification_payload = new JSONObject();
//                try{
//                    notification_payload.put("head","Twoosh - You are connected");
//                    notification_payload.put("body","Firebase authentication successfull");
//                }
//                catch (Exception e){
//
//                }
//                Notifs notify = new Notifs();
//                notify.notify(getApplicationContext(), notification_payload);
                cb.callback("success");
                System.out.println("The read failed: ");

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                //showToastMsg("User authenticated failed...");
                System.out.println("The read failed: " );
                //getFirebaseAuth();
            }
        };

        fref_base.authWithCustomToken(User.f_access_token, this.authResultHandler);


    }


    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.PostBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){
                        User.f_access_token = roomlist_response.getString("response");
                        //  fref.authWithCustomToken(User.f_access_token, authResultHandler);
                        Prefs pfs = new Prefs(c);
                        Prefs.saveUserStatics();
                        // dock.putExtra("work","getaccess");


                    }


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        String host = c.getResources().getString(R.string.local_host);
        String getfaccessapi = c.getResources().getString(R.string.getfaccessapi);

        //String host = getResources().getString(R.string.local_host);
        //String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getfaccessurl = host+getfaccessapi;
        JSONObject getfaccess = new JSONObject();
        try{

            getfaccess.put("mobile",User.mobile);
            getfaccess.put("pwd",User.pwd);
        }
        catch (Exception e){}

        httpClient.Post(c, getfaccessurl, getfaccess);



    }
}
