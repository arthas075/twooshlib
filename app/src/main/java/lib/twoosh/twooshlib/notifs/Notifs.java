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

import lib.twoosh.twooshlib.Chatbox;
import lib.twoosh.twooshlib.R;
import lib.twoosh.twooshlib.RoomDock;

/**
 * Created by arthas on 22/9/16.
 */
public class Notifs {

    String msghead ;
    String module = "posts" ;
    String msg;
    JSONObject payload;
    public Notifs()
    {
        msghead = "Twoosh";
        msg = "You are connected.";
    }


    public void notify(Context c,JSONObject payload)
    {

        msghead = "Twoosh";
        try {

            msg=payload.getString("chatfromname")+" : "+payload.getString("chatmsg");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.curiosity)
                        .setContentTitle(msghead)
                        .setContentText(msg);




        Intent resultIntent = new Intent(c, Chatbox.class);
//        resultIntent.putExtra("tagtitle","#Everything");
        try {
            resultIntent.putExtra("twooshid",payload.getString("twooshid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        resultIntent.putExtra("chatmsg","hello");


        Intent backIntent = new Intent(c, RoomDock.class);
        PendingIntent pintent = PendingIntent.getActivity(c, 0,
                resultIntent, 0);

        //PendingIntent pendingIntent =
//                TaskStackBuilder.create(c)
//                        // add all of DetailsActivity's parents to the stack,
//                        // followed by DetailsActivity itself
//                        .addNextIntentWithParentStack(resultIntent)
//                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


//                            PendingIntent resultPendingIntent =
//                                    stackBuilder.getPendingIntent(
//                                            0,
//                                            PendingIntent.FLAG_UPDATE_CURRENT
//                                    );
        mBuilder.setContentIntent(pintent);
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mBuilder.setDefaults(defaults);
        NotificationManager mNotificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        Notification newmsgnotif = mBuilder.build();
        newmsgnotif.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1,newmsgnotif);
    }
}
