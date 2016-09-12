package lib.twoosh.twooshlib.models;

/**
 * Created by arthas on 8/9/16.
 */
public class PostListItem {



    public String twoosh_text;
    public static String twoosh_id;
    public String user_id;
    public String user_name;
    public String hash_tags;
    public static String room;
    public String online_count;
    public String twoosh_time;
    public String users_count = "0";
    public String replies_count = "0";




    public PostListItem(String twoosh_text,String twoosh_id, String user_name,String user_count,String replies_count, String online_count, String time )
    {
        this.twoosh_text = twoosh_text;
        this.twoosh_id = twoosh_id;
        this.user_name = user_name;
        this.users_count = user_count;
        this.online_count = online_count;
        this.twoosh_time = time;
        this.users_count = users_count;
        this.replies_count = replies_count;


    }

    public PostListItem()
    {

    }
}
