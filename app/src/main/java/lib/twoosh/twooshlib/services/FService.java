package lib.twoosh.twooshlib.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.constants.AppConstants;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.Notifs;

public class FService extends Service {


    String msg = "";
    final Handler handler = new Handler(Looper.getMainLooper());
    Firebase fref = null;
    Firebase.AuthResultHandler authResultHandler = null;
    public static Context c;


    public FService() {

        showToastMsg("FService constructor called....");
        //initFirebase();
    }


    public void onCreate(){
        super.onCreate();
        c= getApplicationContext();
        showToastMsg("FService oncreate called....");
        initFirebase();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        showToastMsg("FService on started....");
        return super.onStartCommand(intent, flags, startId);

    }

    public void initFirebase(){

//        c = getBaseContext();
        Firebase.setAndroidContext(c);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        fref = new Firebase("https://twooshapp-763a4.firebaseio.com");
        fref = fref.child("posts");
        fref.keepSynced(true);


        getFirebaseAuth();

        // set authentication handlers
        // Create a handler to handle the result of the authentication
        authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                showToastMsg("User authenticated...");
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
//                System.out.println("The read failed: " );
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                showToastMsg("User authenticated failed...");
                System.out.println("The read failed: " );
                getFirebaseAuth();
            }
        };
        //User.f_access_token = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ2IjowLCJkIjp7InVpZCI6ImprbWMifSwiaWF0IjoxNDc0OTE4NjAwfQ.njvr9yUzyaPBGIez2h1wdH-yPm_unyyhYXeYuVw43wo";
        showToastMsg("FAcess token - " + User.f_access_token);
        //fref.authWithCustomToken(User.f_access_token, authResultHandler);


        // set child event handlers
        fref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
               // System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
                //showToastMsg("Fservice addChildEventListener - ");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                System.out.println("The " + dataSnapshot.getKey() + " dinosaur's score is " + dataSnapshot.getValue());
                showToastMsg("Fservice addChildEventListener - ");
                JSONObject notification_payload = new JSONObject();
                try{
                    notification_payload.put("head","Twoosh - You are connected.");
                    notification_payload.put("body","Chat msg");
                }
                catch (Exception e){

                }
                Notifs notify = new Notifs();
                notify.notify(getApplicationContext(), notification_payload);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




        // set add value event handlers
        fref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
               // System.out.println(snapshot.getValue());

//                String key = snapshot.getKey();
//                DataSnapshot d = snapshot.child(key);
//                ChatListItem post = d.getValue(ChatListItem.class);
//
//                showToastMsg("Fservice onaddvalueEVent - ");
//                JSONObject notification_payload = new JSONObject();
//                try{
//                    notification_payload.put("head","Twoosh - You are connected.");
//                    notification_payload.put("body","Chat msg");
//                }
//                catch (Exception e){
//
//                }
//                Notifs notify = new Notifs();
//                notify.notify(getApplicationContext(), notification_payload);
            }
            //
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }


    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){
                        User.f_access_token = roomlist_response.getString("response");
                        fref.authWithCustomToken(User.f_access_token, authResultHandler);
                    }


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });



        String host = AppConstants.localhost;
        String getroomsapi = AppConstants.getfaccessapi;
        //String host = getResources().getString(R.string.local_host);
        //String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getroomsurl = host+getroomsapi;

        String urlparams;

        showToastMsg("User access token before auth - "+User.access_token);
        urlparams = "{\"access_token\":\""+User.access_token+"\"}";
        httpClient.Get(c, getroomsurl, urlparams);



    }
    public void showToastMsg(String msg)
    {
        final String toastmsg = msg;

        handler.post(new Runnable() {

            @Override
            public void run() {
                c = getApplicationContext();
                Toast.makeText(getApplicationContext(), toastmsg, Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
