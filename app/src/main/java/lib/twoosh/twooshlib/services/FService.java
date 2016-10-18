package lib.twoosh.twooshlib.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
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

import java.util.Date;
import java.util.HashMap;

import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;
import lib.twoosh.twooshlib.TwooshDock;
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

//        showToastMsg("FService constructor called....");
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

    public void initFirebase() {

        if (authcallback == null) {
            try{
            Firebase.setAndroidContext(getApplicationContext());
            Firebase.getDefaultConfig().setPersistenceEnabled(true);}
            catch(Exception e){}

        }
        if (Fref.fref_base == null) {
            Fref.fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");
        }

        Fref.fref_notifs = Fref.fref_base.child("notifs");
        this.authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData

                if (authcallback != null) {
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
        final Query newnotif = Fref.fref_notifs.orderByChild("timestring").limitToLast(1);
        newnotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.print(dataSnapshot);
                NotifObjTs notif = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    notif = postSnapshot.getValue(NotifObjTs.class);
                    System.out.print(dataSnapshot);
                    //Log.e("Get Data", post.<YourMethod>());
                }
                switch (notif.notif_type) {
                    case "NP":
                        notifyNewPost(notif);
                        break;
                    case "NC":
                        notifyNewChat(notif);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }
    public void notifyNewPost(NotifObjTs notif){

        try{


            if(User.subscribed_rooms.getString(notif.room).equals("1")){
                if(!roomdockactive && (!User.current_room.equals(notif.room))){
                    JSONObject notification_payload = new JSONObject();
                    try {
                        notification_payload.put("head", notif.head);
                        notification_payload.put("body", notif.body);
                        notification_payload.put("room", notif.room);
                        notification_payload.put("type", notif.notif_type);
                    } catch (Exception err) {
                        showToastMsg(err.toString());
                    }
                    Notifs notify = new Notifs();
                    notify.notify(c, notification_payload);
                }


        }}
        catch (Exception e){}
    }
    public void notifyNewChat(NotifObjTs notif){

            try {
            if(User.subscribed_posts.getString(notif.twoosh_id).equals("1")){
                if(!User.chatboxactive && !User.current_post.equals(notif.twoosh_id)) {
                    JSONObject notification_payload = new JSONObject();

                    notification_payload.put("head", notif.twoosh_text);
                    notification_payload.put("body", notif.body);
                    notification_payload.put("room", notif.room);
                    notification_payload.put("type", notif.notif_type);
                    Notifs notify = new Notifs();
                    notify.notify(c, notification_payload);
                }
            }


            } catch (Exception err) {
                showToastMsg(err.toString());
            }


    }
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
        super.onDestroy();
        this.isRunning = false;
        showToastMsg("Restarting service...");

    }
}
