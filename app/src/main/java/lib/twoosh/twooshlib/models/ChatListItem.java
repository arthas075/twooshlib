package lib.twoosh.twooshlib.models;

/**
 * Created by arthas on 13/9/16.
 */
public class ChatListItem {


    public String chatmsg;
    public String chatfrom;
    public String chatid;
    public String chathead;
    public String chattime;

    public String chat_unixtime = "";
    public String chat_fromuserid="";
    public String chat_postid ="";
    public String chat_id = "";






    public ChatListItem(String from,String msg)
    {
        chatfrom = from;
        chatmsg = msg;

    }

    public ChatListItem(String chatid, String chatmsg, String chatfrom,String chattime, String twooshpostid)
    {

        this.chat_id =  chatid;
        this.chatmsg = chatmsg;
        this.chat_fromuserid = chatfrom;
        this.chat_unixtime = chattime;
        this.chat_postid = twooshpostid;


    }

    public void setChatHead(String topic)
    {
        chathead = topic;
    }
}
