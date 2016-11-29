package lib.twoosh.twooshlib.notifs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import lib.twoosh.twooshlib.Chatbox;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;
import lib.twoosh.twooshlib.TwooshDock;

/**
 * Created by arthas on 22/9/16.
 */
public class Notifs {

    String msghead ;
    String module = "posts" ;
    String msg;
    String room;
    Long time;
    String type;
    JSONObject payload;
    public Notification newmsgnotif = null;
    NotificationManager mNotificationManager = null;
    public Notifs()
    {
        msghead = "Twoosh - You are connected";
        msg = "New notifications";
        room = "everything";
    }

    public void build(Context c, JSONObject payload){





    }
    public void notify(Context c,JSONObject payload)
    {

        try {

            msghead=payload.getString("head");
            msg=payload.getString("body");
            room = payload.getString("room");
            type = payload.getString("type");
            time = payload.getLong("time");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(c)
                            .setSmallIcon(R.drawable.twoosh_icon)
                            .setContentTitle(msghead)
                            .setContentText(msg);
            Intent resultIntent = null;
            switch(type){
                case "NP":
                    resultIntent = new Intent(c, RoomDock.class);
                    break;
                case "NC":
                    resultIntent = new Intent(c, Chatbox.class);
                    String twoosh_id = payload.getString("twoosh_id");
                    String twoosh_text = payload.getString("head");
                    resultIntent.putExtra("twoosh_id",twoosh_id);
                    resultIntent.putExtra("twoosh_text",twoosh_text);
                    resultIntent.putExtra("twoosh_id",twoosh_id);
                    resultIntent.putExtra("user_name",payload.getString("user_name"));
                    resultIntent.putExtra("user_id", payload.getString("user_id"));
                    resultIntent.putExtra("twoosh_time", payload.getString("twoosh_time"));
                    resultIntent.putExtra("replies","1");
                    resultIntent.putExtra("following", "1");
                    break;
                case "NR":
                    resultIntent = new Intent(c, TwooshDock.class);
                    break;
                default:
                    break;
            }

            resultIntent.putExtra("room", room);
            resultIntent.putExtra("notifed","1");
            resultIntent.putExtra("time",time);
            Date utildate = new Date();
            String twoosh_ts =  Long.toString(utildate.getTime());

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            resultIntent.setAction(twoosh_ts);

            PendingIntent pintent = PendingIntent.getActivity(c, 0,
                    resultIntent, 0);
            mBuilder.setContentIntent(pintent);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mBuilder.setDefaults(defaults);
            mNotificationManager =
                    (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
//          mId allows you to update the notification later on.
            newmsgnotif = mBuilder.build();
            newmsgnotif.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(1,newmsgnotif);


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
}
