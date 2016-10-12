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
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;
import lib.twoosh.twooshlib.constants.AppConstants;
import lib.twoosh.twooshlib.interfaces.Callbacker;
import lib.twoosh.twooshlib.models.Fref;
import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.NotifObj;
import lib.twoosh.twooshlib.notifs.NotifObjTs;
import lib.twoosh.twooshlib.notifs.Notifs;

public class FService extends Service {


    String msg = "";
    final Handler handler = new Handler(Looper.getMainLooper());
    Firebase serviceref = null;
    public static Context c;
    public static boolean isRunning = false;

    public static Callbacker caller = null;
    public static Callbacker authcallback = null;
    public static Callbacker roomscallback = null;
    public static boolean roomdockactive = false;
    public static Firebase.AuthResultHandler authResultHandler = null;


    public FService() {

        showToastMsg("FService constructor called....");
        //initFirebase();
    }


    public void onCreate(){
        super.onCreate();
        c= getApplicationContext();
        Prefs prefs = new Prefs(c);
        if(prefs.prefExists()){
            prefs.setUserStatics();
            initFirebase();
        }
        showToastMsg("FService oncreate called....");


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        showToastMsg("FService on started....");
        this.isRunning = true;
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);

    }

    public void initFirebase(){

        if(authcallback==null){
            Firebase.setAndroidContext(getApplicationContext());
            Firebase.getDefaultConfig().setPersistenceEnabled(true);

        }
        if(Fref.fref_base==null){
            Fref.fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");
        }

        Fref.fref_notifs = Fref.fref_base.child("notifs");
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
                if(authcallback!=null){
                    authcallback.callback("success");
                }

                System.out.println("The read failed: ");
                //showToastMsg("User authenticated success...");

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                showToastMsg("User authenticated failed...");
                //showToastMsg("FAccessToken - "+User.f_access_token);
                getFirebaseAuth();
            }
        };

        Fref.fref_base.authWithCustomToken(User.f_access_token, this.authResultHandler);
//        frefs.authenticate(c);


        //User.f_access_token = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJ2IjowLCJkIjp7InVpZCI6ImprbWMifSwiaWF0IjoxNDc0OTE4NjAwfQ.njvr9yUzyaPBGIez2h1wdH-yPm_unyyhYXeYuVw43wo";
       // showToastMsg("FAcess token - " + User.f_access_token);
        final Query newnotif = Fref.fref_notifs.orderByChild("timestring").limitToLast(1);
        newnotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.print(dataSnapshot);
                NotifObjTs notif = null;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                     notif= postSnapshot.getValue(NotifObjTs.class);
                    System.out.print(dataSnapshot);
                    //Log.e("Get Data", post.<YourMethod>());
                }

                if(!roomdockactive){



                    JSONObject notification_payload = new JSONObject();
                    try {
                        notification_payload.put("head", notif.head);
                        notification_payload.put("body", notif.body);
                        notification_payload.put("room", notif.room);
                    } catch (Exception err) {
                        showToastMsg(err.toString());
                    }
                    Notifs notify = new Notifs();
                    notify.notify(c, notification_payload);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        Fref.fref_notifs.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {


//                postcount = postcount+1;

//                //Toast.makeText(getActivity(), "Total local objects - " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//                adapter.add(post_local);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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



        Fref.fref_notifs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());

                if(!roomdockactive){

//                    String key = snapshot.getKey();
//
//                    Object e = snapshot.getValue();
//                    HashMap<String, String> map = (HashMap<String, String>) e;
//                    String head = map.get("head");
//                    String body = map.get("body");
//                    String room = map.get("room");
//                    //NotifObj notif_local = snapshot.getValue(NotifObj.class);
//
//
//                    JSONObject notification_payload = new JSONObject();
//                    try {
//                        notification_payload.put("head", head);
//                        notification_payload.put("body", body);
//                        notification_payload.put("room", room);
//                    } catch (Exception err) {
//                        showToastMsg(err.toString());
//                    }
//                    Notifs notify = new Notifs();
//                    notify.notify(c, notification_payload);
                }

               // notify.notify(c, notification_payload);
//                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
//                JSONObject notification_payload = new JSONObject();
//                try {
//                    notification_payload.put("head", "Twoosh - You are connected");
//                    notification_payload.put("body", "Addvalueevent notification listener");
//                } catch (Exception e) {
//
//                }
//                Notifs notify = new Notifs();
//                notify.notify(c, notification_payload);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        //fref.authWithCustomToken(User.f_access_token, authResultHandler);


//        // set child event handlers
//        fref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//               // System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
//                //showToastMsg("Fservice addChildEventListener - ");
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                System.out.println("The " + dataSnapshot.getKey() + " dinosaur's score is " + dataSnapshot.getValue());
//                showToastMsg("Fservice addChildEventListener - ");
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
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//
//
//
//
//        // set add value event handlers
//        fref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//               // System.out.println(snapshot.getValue());
//
////                String key = snapshot.getKey();
////                DataSnapshot d = snapshot.child(key);
////                ChatListItem post = d.getValue(ChatListItem.class);
////
////                showToastMsg("Fservice onaddvalueEVent - ");
////                JSONObject notification_payload = new JSONObject();
////                try{
////                    notification_payload.put("head","Twoosh - You are connected.");
////                    notification_payload.put("body","Chat msg");
////                }
////                catch (Exception e){
////
////                }
////                Notifs notify = new Notifs();
////                notify.notify(getApplicationContext(), notification_payload);
//            }
//            //
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });

    }

//    public void authenticateFirebase(){
//
//            if(User.f_access_token!=""){
//                fref.authWithCustomToken(User.f_access_token, authResultHandler);
//            }else{
//                getFirebaseAuth();
//            }
//    }


    public static void registerRoomListeners(){


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

                        Prefs.saveUserStatics();
                        Fref.fref_base.authWithCustomToken(User.f_access_token, authResultHandler);
                        // dock.putExtra("work","getaccess");
//                        Intent dock = new Intent(VerifyOTP.this, RoomDock.class);
//                        startActivity(dock);

                    }


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        String host = getResources().getString(R.string.local_host);
        String getfaccessapi = getResources().getString(R.string.getfaccessapi);

        //String host = getResources().getString(R.string.local_host);
        //String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getfaccessurl = host+getfaccessapi;
        JSONObject getfaccess = new JSONObject();
        try{

            getfaccess.put("mobile",User.mobile);
            getfaccess.put("pwd",User.pwd);
        }
        catch (Exception e){}

        httpClient.Post(getApplicationContext(), getfaccessurl, getfaccess);



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

    @Override
    public void onDestroy(){

        this.isRunning = false;
        showToastMsg("Fservice on destroy called...");
    }
}
