package lib.twoosh.twooshlib.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.models.RoomListItem;
/**
 * Created by arthas on 8/9/16.
 */

public class RoomListAdapter extends BaseAdapter {


    //List<TagListItem> taglist = TagListItem.getTagListView();
    List<RoomListItem> taglist = new ArrayList<RoomListItem>();
    HashMap hm = new HashMap();

    Context context;

    @Override
    public int getCount() {
        return taglist.size();
    }

    @Override
    public Object getItem(int position) {
        return taglist.get(position);
    }


    public void updateTagList(JSONArray tagdata)
    {
        RoomListItem tagger;
        JSONObject tagjson;

        for(int i=0;i<tagdata.length();i++)
        {


            try {

                tagjson = new JSONObject(tagdata.get(i).toString());
                tagger = getItemfromKey(tagjson.getString("hashid"));
                tagger.hash_users = tagjson.getString("hash_users");
                tagger.hash_posts = tagjson.getString("hash_posts");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void emptyTaglist()
    {
        taglist = new ArrayList<>();

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void changetagonlinecount(int key, int count)
    {
        taglist.get(key).tagonlinecount = count;
    }

    public void add(RoomListItem hashtag)
    {

        taglist.add(hashtag);
        hm.put(hashtag.hashid,taglist.size()-1);

    }

    public RoomListItem getItemfromKey(String key)
    {

        int arrind = Integer.parseInt(hm.get(key).toString());
        RoomListItem tag = (RoomListItem)getItem(arrind);
        return tag;

    }
    public void add(JSONArray tagdata)
    {
        RoomListItem tagger ;
        JSONObject tagjson;
        for(int i=0;i<tagdata.length();i++)
        {
            tagger = new RoomListItem();
            try {

                tagjson = new JSONObject(tagdata.get(i).toString());
                tagger.tagname = tagjson.getString("hashtag");
                tagger.hashid = tagjson.getString("hashid");
                tagger.hash_users = tagjson.getString("hash_users");
                tagger.hash_posts = tagjson.getString("hash_posts");
                add(tagger);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {

            LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.room_list_item, parent,false);
        }

        TextView tagname = (TextView)convertView.findViewById(R.id.textView1);
        //TextView tagdesc = (TextView)convertView.findViewById(R.id.textView2);
        TextView tagonlinecount = (TextView)convertView.findViewById(R.id.tagonlinecount);
        // ImageView tagimg = (ImageView)convertView.findViewById(R.id.imageView1);
        //      RelativeLayout relativeLayout = (RelativeLayout)convertView.findViewById(R.layout.taglistitem);

        TextView tagusers = (TextView)convertView.findViewById(R.id.tagusers);
        TextView tagposts = (TextView)convertView.findViewById(R.id.tagposts);
        RoomListItem tagger = taglist.get(position);
        Button jointagbtn = (Button)convertView.findViewById(R.id.jointag);
        jointagbtn.setText("Join");



        tagusers.setText("Users : "+tagger.hash_users);
        tagposts.setText("Posts : "+tagger.hash_posts);
        tagname.setText(tagger.tagname);
        //tagdesc.setText(tagger.tagdesc);
        tagonlinecount.setText("Online : "+tagger.tagonlinecount);
        //tagimg.setImageResource(tagger.imgsrc);
        // tagimg.setImageResource(R.drawable.hashicon);



//        convertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                TagListItem m = (TagListItem) parent.getAdapter().getItem(position);
//                //TagListItem m = (TagListItem)view.getTag(R.id.tagList);
//                Toast.makeText(TwooshDock.this, m.tagdesc, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(TwooshDock.this, TagRoom.class);
//                intent.putExtra("tagtitle", m.tagname);
//                startActivity(intent);
//            }
//
//
//        });





        return convertView;
    }
}
