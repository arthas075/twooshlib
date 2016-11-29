package lib.twoosh.twooshlib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import lib.twoosh.twooshlib.adapters.RoomListAdapter;
import lib.twoosh.twooshlib.interfaces.Callbacker;
import lib.twoosh.twooshlib.models.Fref;
import lib.twoosh.twooshlib.models.Prefs;
import lib.twoosh.twooshlib.models.RoomListItem;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.notifs.Toasts;
import lib.twoosh.twooshlib.services.FService;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class TwooshDock extends AppCompatActivity implements Callbacker{




    Context c;
    Socket socket = null;
    JSONObject userdetails ;
    RoomListAdapter adapter ;
    String twoosher = "";
    public static Firebase.AuthResultHandler authResultHandler = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twoosh_dock);





        // initialise activity
        initActivity();






    }



    public void initActivity(){

        // touch user
        touchUser();


    }




    public void touchUser(){



        // check regular user or corp user
        Prefs prefs = new Prefs(getApplicationContext());
        if(prefs.prefExists()){

            prefs.setUserStatics();
            initApp();

            if(!FService.isRunning){

                FService.authcallback = this;
                Intent fservice = new Intent(getApplicationContext(), FService.class);
                fservice.putExtra("payload","1");
                startService(fservice);
            }

        }else{

            showLoginScreen();
        }
    }


    public void initApp(){


        // recognize user
        attachListeners();

        // renderAdpapter
        renderAdapter();

        //register listeners
        initFirebase();

    }



    @Override
    public void callback(String data){

        //new Toasts().showToastMsg(TwooshDock.this, "Init firebase listeners");


    }
    public void showLoginScreen(){

        Intent i = new Intent(this, Signup.class);
        startActivity(i);

    }


    public void initFirebase(){


        Firebase.setAndroidContext(getApplicationContext());
        try {
            Firebase.getDefaultConfig().setPersistenceEnabled(true);
        }catch (Exception e){}
        if(Fref.fref_base==null){
            Fref.fref_base = new Firebase("https://twooshapp-763a4.firebaseio.com");
        }

        Fref.fref_rooms = Fref.fref_base.child("rooms").child(User.appname);


        this.authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                System.out.println("Authentication successfull...");

            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError

                getFirebaseAuth();
            }
        };

        Fref.fref_base.authWithCustomToken(User.f_access_token, this.authResultHandler);
        Fref.fref_rooms.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                RoomListItem roomitem = snapshot.getValue(RoomListItem.class);
                adapter.add(roomitem);

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



        Fref.fref_rooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.PostBack(){

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){

                        User.f_access_token = roomlist_response.getString("response");
                        Prefs.saveUserStatics();
                        Fref.fref_base.authWithCustomToken(User.f_access_token, authResultHandler);

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        });


        String host = getResources().getString(R.string.local_host);
        String getfaccessapi = getResources().getString(R.string.getfaccessapi);
        String getfaccessurl = host+getfaccessapi;
        JSONObject getfaccess = new JSONObject();
        try{

            getfaccess.put("mobile",User.mobile);
            getfaccess.put("pwd",User.pwd);
        }
        catch (Exception e){}

        httpClient.Post(getApplicationContext(), getfaccessurl, getfaccess);

    }



    public void renderAdapter(){

        adapter = new RoomListAdapter();
        ListView list=(ListView)findViewById(R.id.roomList);
        list.setAdapter(adapter);

    }








    public void renderDock(){


        getRoomList();

    }




    public void getRoomList(){

        //Toast.makeText(TwooshDock.this,"Getting rooms list..."+User.name,Toast.LENGTH_SHORT).show();
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    //Toast.makeText(TwooshDock.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject roomlist_response = new JSONObject(response);
                    JSONArray roomlist = roomlist_response.getJSONArray("response");
                    addtoAdapter(roomlist);


                } catch (JSONException e) {
                    //Toast.makeText(TwooshDock.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });




        String host = getResources().getString(R.string.local_host);
        String getroomsapi = getResources().getString(R.string.getroomsapi);
        String getroomsurl = host+getroomsapi;

        String urlparams;
        if(User.user_type.equals("C")){
            urlparams = "{\"corpid\":\""+User.corpid+"\",\"type\":\"corp\"}";
        }else{
            urlparams = "{\"type\":\"community\"}";
        }

        httpClient.Get(this, getroomsurl, urlparams);

    }




    // add room objects to adapter and local firebase
    public void addtoAdapter(JSONArray roomlist){

        //wordlistref = fbaserootref.child(User.corpid).child("wordlist");
        JSONObject room = null;
        RoomListItem roomitem;
        for(int i=0;i<roomlist.length();i++){
            try {

                room = new JSONObject(roomlist.get(i).toString());
                String roomname = room.getString("mapname");
                String roomstrength = room.getString("users_count");
                String roomposts = "0";

                roomitem = new RoomListItem(roomname,roomname,roomstrength, roomposts);
                //wordlistref.push().setValue(roomitem);
                //wordlistref.child(roomname).setValue(roomitem);
                adapter.add(roomitem);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        adapter.notifyDataSetChanged();
    }




    public void attachListeners(){



        ListView list=(ListView) findViewById(R.id.roomList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RoomListItem m = (RoomListItem) parent.getAdapter().getItem(position);
                Intent intent = new Intent(TwooshDock.this, RoomDock.class);
                intent.putExtra("tag_id", m.tag_id);
                intent.putExtra("room", m.tag_name);
                User.current_room = m.tag_name;

                startActivity(intent);
            }


        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(User.user_type.equals("C")) {
            getMenuInflater().inflate(R.menu.menu_chatbox, menu);
        }
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_room) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){

        new Toasts().showToastMsg(this,"TwooshDock ondestroy1");
        stopService(new Intent(getApplicationContext(),FService.class));
        super.onDestroy();
        startService(new Intent(getApplicationContext(), FService.class));

    }

}
