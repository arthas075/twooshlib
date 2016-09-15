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

    public ChatListItem(String from,String msg)
    {
        chatfrom = from;
        chatmsg = msg;

    }

    public ChatListItem(String chatmsg, String chatfrom,String chattime)
    {

        this.chatmsg = chatmsg;
        this.chatfrom = chatfrom;
        this.chattime = chattime;

    }

    public void setChatHead(String topic)
    {
        chathead = topic;
    }
}
