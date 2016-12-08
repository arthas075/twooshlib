package lib.twoosh.twooshlib.models;

import java.util.Map;

/**
 * Created by arthas on 8/9/16.
 */
public class PostListItem {



    //public String twoosh_text;
    public String twoosh_id;
    public String user_id;
    public String user_name;
    public String hash_tags;
    public static String room;
    public String online_count;
    public String twoosh_time;


    // on contract
    public Map<String,String> ts ;
    public String p;
    public String p_id;
    public String from_id;
    public String from_name;
    public String following;
    public String replies;




    public PostListItem(String twoosh_text,String twoosh_id, String user_name,String user_id, String user_count,String replies_count, String online_count, String time )
    {
        this.p = twoosh_text;
        this.twoosh_id = twoosh_id;
        this.from_name = user_name;
        this.from_id = user_id;


        this.following = user_count;
        this.replies = replies_count;


        this.online_count = online_count;
        this.twoosh_time = time;




    }

    public PostListItem()
    {

    }
}
