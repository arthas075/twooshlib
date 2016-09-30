package lib.twoosh.twooshlib.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.twoosh.twooshlib.PeopleChat;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.adapters.PeopleListAdapter;
import lib.twoosh.twooshlib.models.PeopleListItem;
import lib.twoosh.twooshlib.models.User;
import lib.twoosh.twooshlib.networks.HttpClient;

public class PeopleFragment extends Fragment {

    private RecyclerView peopleRecyclerView;
    private PeopleListAdapter peopleAdapter;
    private LinearLayoutManager peopleLayoutManager;
    Firebase peopleref = null;
    int people_count = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_people_fragment, container, false);
    }


    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        initActivity();
        attachListeners();
    }


    public void initActivity(){


        peopleRecyclerView = (RecyclerView)getView().findViewById(R.id.people_recycler_view);
        peopleRecyclerView.setNestedScrollingEnabled(false);



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        peopleRecyclerView.setHasFixedSize(true);

        //use a linear layout manager
        peopleAdapter = new PeopleListAdapter();
        peopleLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        peopleLayoutManager.setStackFromEnd(true);

        peopleRecyclerView.setLayoutManager(peopleLayoutManager);
        peopleRecyclerView.setAdapter(peopleAdapter);

        setFireBase();
        getPeopleListRemote();
    }

    public void setFireBase(){


//        peopleref = new Firebase("https://twooshapp-763a4.firebaseio.com");
//
//        peopleref = peopleref.child(User.corpid).child(User.current_room).child("people");
//
//        peopleref.keepSynced(true);
//
//
//        peopleref.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
//                people_count = people_count+1;
//                PeopleListItem post_local = snapshot.getValue(PeopleListItem.class);
//                Toast.makeText(getActivity(), "Total local objects - " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//                peopleAdapter.add(post_local);
//                if (people_count==1){
////                    ViewFlipper vf = (ViewFlipper)getView().findViewById(R.id.postviewflipper);
////                    vf.showNext();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
////
////        wordlistref.addListenerForSingleValueEvent(new ValueEventListener() {
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                System.out.println("We're done loading the initial " + dataSnapshot.getChildrenCount() + " items");
////                adapter.notifyDataSetChanged();
////                Toast.makeText(getApplicationContext(), "Add data change called ", Toast.LENGTH_SHORT).show();
////            }
////
////            public void onCancelled(FirebaseError firebaseError) {
////            }
////        });
//
//        peopleref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                System.out.println(snapshot.getValue());
//                peopleAdapter.notifyDataSetChanged();
////                if(adapter.getCount()>0){
////                    ViewFlipper vf = (ViewFlipper)getView().findViewById(R.id.postviewflipper);
////                    vf.showNext();
////                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });

    }

    public void getPeopleListRemote(){

        Toast.makeText(getActivity(), "getting users", Toast.LENGTH_SHORT).show();
        HttpClient httpClient = new HttpClient(new HttpClient.GetBack(){

            @Override
            public void onResponse(String response) {

                try {

                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                    JSONObject ppllist_response = new JSONObject(response);
                    JSONArray ppllist = ppllist_response.getJSONArray("response");
                    addtoAdapter(ppllist);


                } catch (JSONException e) {

                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }
        });

        String host = getResources().getString(R.string.local_host);
        String getpplapi = getResources().getString(R.string.getpeopleapi);
        String getpplurl = host+getpplapi;

        String urlparams = "{\"roomid\":\""+User.current_room+"\",\"corpid\":\""+User.corpid+"\",\"userid\":\""+User.userid+"\"}";
        httpClient.Get(getActivity(), getpplurl, urlparams);



    }

    public void addtoAdapter(JSONArray ppl_list){



        Toast.makeText(getActivity(), "Inside add peopleadapter...", Toast.LENGTH_SHORT).show();

        JSONObject userobj = null;
        PeopleListItem pplitem;

        for(int i=0;i<ppl_list.length();i++){
            try {

                userobj = new JSONObject(ppl_list.get(i).toString());
                String user_name = userobj.getString("name");
                String user_id = userobj.getString("_id");
                String user_dp = userobj.getString("pic");
                String user_email = userobj.getString("email");
                String user_mobile = userobj.getString("mobile");
                String user_gender = userobj.getString("gender");
                String user_dob = userobj.getString("dob");
                String user_city = userobj.getString("city");
                String user_corpid = userobj.getString("corp_id");
                String user_appname = userobj.getString("corp_appname");

                //String name,String pic, String email,String mobile, String gender,String dob, String corp_id, String corp_appname, String taglist

                pplitem = new PeopleListItem(user_name, user_dp, user_email, user_mobile, user_gender, user_dob, user_corpid, user_appname,User.current_room, user_id);
                peopleAdapter.add(pplitem);
                 //peopleref.child(user_id).setValue(pplitem);
            } catch (JSONException e) {
                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

        peopleAdapter.notifyDataSetChanged();

    }


    public void attachListeners(){



        RecyclerView ppl_list=(RecyclerView)getView().findViewById(R.id.people_recycler_view);
        peopleAdapter.SetOnItemClickListener(new PeopleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                Toast.makeText(getActivity(),"Someone clicked me", Toast.LENGTH_SHORT).show();
                PeopleListItem ppl_clicked = peopleAdapter.getItem(position);
                Toast.makeText(getActivity(),"user clicked"+ppl_clicked.userid, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), PeopleChat.class);
                startActivity(i);
            }
        });
//        ppl_list.setOnItemClickListener(new RecyclerView.Adapter<>().OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                PostListItem m = (PostListItem) parent.getAdapter().getItem(position);
//                //TagListItem m = (TagListItem)view.getTag(R.id.tagList);
//                //Toast.makeText(TwooshDock.this, m.tagdesc, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), Chatbox.class);
//                intent.putExtra("twoosh_text",m.twoosh_text);
//                intent.putExtra("twoosh_id", m.twoosh_id);
//
//                // intent.putExtra("tagname", m.tagname);
//                startActivity(intent);
//            }
//
//
//        });
    }
}
