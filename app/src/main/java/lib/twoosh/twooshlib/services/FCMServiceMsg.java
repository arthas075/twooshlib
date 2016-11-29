package lib.twoosh.twooshlib.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import lib.twoosh.twooshlib.notifs.Notifs;

/**
 * Created by satyamsurendra on 14/10/16.
 */
//import com.google.firebase.messaging.FirebaseMessagingService;
public class FCMServiceMsg extends FirebaseMessagingService {




    @Override
    public void onMessageReceived(RemoteMessage rmsg){


        // gather details from remote notification
        System.out.println("New FCM Notification received");
        JSONObject notification_payload = new JSONObject();
        try {
            notification_payload.put("head", "FCM Notification");
            notification_payload.put("body", "Remote message");
            notification_payload.put("room", "everything");
            notification_payload.put("type", "NP");
        } catch (Exception err) {
            System.out.print("Error in on message received "+err.toString());
        }
        Notifs notify = new Notifs();
        notify.notify(getApplicationContext(), notification_payload);
    }
}
