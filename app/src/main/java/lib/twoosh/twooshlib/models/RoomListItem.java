package lib.twoosh.twooshlib.models;

/**
 * Created by arthas on 8/9/16.
 */
public class RoomListItem {


    public String tagname;
    public String tagdesc;
    public String hashid;
    //public int imgsrc;
    public int tagonlinecount ;
    public String hash_users;
    public String hash_posts;



    public RoomListItem(String hashtag,String hashid,String hash_users,String hash_posts )
    {
        this.tagname = hashtag;
        this.hashid = hashid;
        this.hash_users = hash_users;
        this.hash_posts = hash_posts;

    }

    public RoomListItem()
    {

    }
}