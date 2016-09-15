package lib.twoosh.twooshlib;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import lib.twoosh.twooshlib.adapters.ChatListAdapter;
import lib.twoosh.twooshlib.models.ChatListItem;
import lib.twoosh.twooshlib.models.User;

public class Chatbox extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatListAdapter chatboxAdapter;
    private LinearLayoutManager chatLayoutManager;
    private NestedScrollView nestedScrollView;
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

        inflateDummyDataAdapter();

    }

    public void inflateDummyDataAdapter(){

        ChatListItem dummychat; //= new ChatListItem("You are all dumbfucks...","Satyam : ","21:23 GMT");
        for(int i=0;i<15;i++){
            dummychat = new ChatListItem("Chat - "+i,"Satyam : ","21:23 GMT");
            chatboxAdapter.add(dummychat);
        }

        dummychat = new ChatListItem("Last Chat Item...","Satyam : ","21:23 GMT");
        chatboxAdapter.add(dummychat);
       // chatRecyclerView.scrollToPosition(11);
        chatboxAdapter.notifyDataSetChanged();
        chatLayoutManager.scrollToPosition(0);

       // chatLayoutManager.setStackFromEnd(true);
        //chatLayoutManager.scrollToPosition(9);
       // chatRecyclerView.smoothScrollToPosition(10);
//        nestedScrollView.scrollTo(0, nestedScrollView.getBottom());
//        nestedScrollView.fullScroll(View.FOCUS_DOWN);
       // chatRecyclerView.


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

        ChatListItem newchat = new ChatListItem(chat_text, User.name, "00:00 GMT");
        chatboxAdapter.add(newchat);



        chatboxAdapter.notifyDataSetChanged();
        int count = chatboxAdapter.getItemCount();


       // chatLayoutManager.scrollToPosition(chatboxAdapter.getItemCount());
        chatRecyclerView.scrollToPosition(chatboxAdapter.getItemCount() - 1);
        chatLayoutManager.setStackFromEnd(true);



    }
}
