package lib.twoosh.twooshlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import lib.twoosh.twooshlib.adapters.ChatListAdapter;
import lib.twoosh.twooshlib.models.ChatListItem;
import lib.twoosh.twooshlib.models.PeopleListItem;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;
import lib.twoosh.twooshlib.notifs.Notifs;

public class Chatbox extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatListAdapter chatboxAdapter;
    private LinearLayoutManager chatLayoutManager;
    private NestedScrollView nestedScrollView;
    static String twoosh_id = "";
    Firebase postchatref = null;
    Firebase.AuthResultHandler authResultHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbox);


        // init activity
        initActivity();


        // attach listeners
        attachListeners();


    }

    public void initActivity(){
        getSupportActionBar().setTitle("Twoosh Chatbox");

        Intent i = getIntent();

        String twooshtext = i.getStringExtra("twoosh_text");
        String twooshid = i.getStringExtra("twoosh_id");
        this.twoosh_id = twooshid;
        User.current_post = twooshid;
        String username = i.getStringExtra("username");
        String userid = i.getStringExtra("user_id");
        String replies = i.getStringExtra("replies");
        String following = i.getStringExtra("following");
        String twoosh_time = i.getStringExtra("twoosh_time");

        TextView chathead_twooshtext = (TextView)findViewById(R.id.chathead_twooshtext);
        TextView chathead_username = (TextView)findViewById(R.id.chathead_username);
        TextView chathead_replies = (TextView)findViewById(R.id.chathead_replies);
        TextView chathead_following = (TextView)findViewById(R.id.chathead_following);
        TextView chathead_twooshtime = (TextView)findViewById(R.id.chathead_postTime);


        chathead_twooshtext.setText(twooshtext);
        chathead_username.setText(username);
        chathead_replies.setText("Replies : "+replies);
        chathead_following.setText("Following : "+following);
        chathead_twooshtime.setText(twoosh_time);



        chatRecyclerView = (RecyclerView) findViewById(R.id.chats_recycler_view);
        chatRecyclerView.setNestedScrollingEnabled(false);



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        chatRecyclerView.setHasFixedSize(true);

         //use a linear layout manager
        chatboxAdapter = new ChatListAdapter();
        chatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatLayoutManager.setStackFromEnd(true);

        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.setAdapter(chatboxAdapter);
        //nestedScrollView = (NestedScrollView)findViewById(R.id.chat_nested_scroll);

        // specify an adapter (see also next example)

       // inflateDummyDataAdapter();

        setFirebaseForChat();

    }


    public void setFirebaseForChat(){

         postchatref = new Firebase("https://twooshapp-763a4.firebaseio.com");
         postchatref = postchatref.child("posts").child(this.twoosh_id);
//
         postchatref.keepSynced(true);
//
         getFirebaseAuth();
        postchatref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());

                String key = snapshot.getKey();


                Object e = snapshot.getValue();
                HashMap<String, String> map = (HashMap<String, String>) e;
                String chat_id = map.get("chat_id");
                String chat_msg = map.get("chatmsg");


                String from = map.get("chat_fromuserid");
                String chat_unix = map.get("chat_unixtime");


                //public ChatListItem(String chatid, String chatmsg, String chatfrom,String chattime, String twooshpostid)
                ChatListItem m = new ChatListItem(chat_id, chat_msg, from, chat_unix, "123213");
                chatboxAdapter.add(m);

//

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
//


        postchatref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());


                chatboxAdapter.notifyDataSetChanged();
//                chatLayoutManager.setStackFromEnd(true);
                chatRecyclerView.scrollToPosition(chatboxAdapter.getItemCount() - 1);
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
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        // Create a handler to handle the result of the authentication
        authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                System.out.println("The read failed: " );
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                System.out.println("The read failed: " );
                getFirebaseAuth();
            }
        };

    }


    public void getFirebaseAuth(){
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {


                    JSONObject roomlist_response = new JSONObject(response);
                    if(roomlist_response.getString("status").equals("Success") && (roomlist_response.getString("response").length()>0)){
                        User.f_access_token = roomlist_response.getString("response");
                        postchatref.authWithCustomToken(User.f_access_token, authResultHandler);
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        });




        String host = getResources().getString(R.string.local_host);
        String getroomsapi = getResources().getString(R.string.getfaccessapi);
        String getroomsurl = host+getroomsapi;

        String urlparams;

        urlparams = "{\"access_token\":\""+User.access_token+"\"}";
        httpClient.Get(this, getroomsurl, urlparams);



    }


    public void attachListeners(){


        Button send_chat_tbn = (Button)findViewById(R.id.btn_send_chat);
        final EditText chat_msg = (EditText)findViewById(R.id.chat_msg);
        send_chat_tbn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){


                String chat_text = chat_msg.getText().toString().trim();
                if (chat_text!=""){

                    sendChatMsg(chat_text);
                    chat_msg.setText("");

                }


            }

        });


    }

    public void sendChatMsg(String chat_text){


        long unixTime = System.currentTimeMillis() / 1000L;
        String chatid = this.twoosh_id+unixTime;
        ChatListItem newchat = new ChatListItem(chatid, chat_text, User.name, "00:00 GMT", this.twoosh_id);

        int count = chatboxAdapter.getItemCount();
        chatLayoutManager.setStackFromEnd(true);
        chatRecyclerView.scrollToPosition(chatboxAdapter.getItemCount() - 1);
        postchatref.child(chatid).setValue(newchat);

        JSONObject notification_payload = new JSONObject();
        try{
            notification_payload.put("head","New chat msg...");
            notification_payload.put("body","Twoosh - You are connected.");
        }
        catch (Exception e){

        }
        Notifs notify = new Notifs();
        notify.notify(getApplicationContext(),notification_payload);

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
