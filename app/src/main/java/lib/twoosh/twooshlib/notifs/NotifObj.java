package lib.twoosh.twooshlib.notifs;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by arthas on 9/10/16.
 */
public class NotifObj {

    public String head = "";
    public String body = "";


    public String twoosh_text = "";
    public String twoosh_id = "";
    public String user_name = "";
    public String room = "";
    public String notif_type = "";

    public JSONObject timestamp = null;
    public Map<String,String> timestring ;
    public NotifObj(){


    }
}
