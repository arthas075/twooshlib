package lib.twoosh.twooshlib.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by satyamsurendra on 14/10/16.
 */
public class FCMService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh(){

        // Get updated InstanceID token.
        System.out.println("ON TOKEN REFRESH CALLED...");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("GCM TOKEN - "+refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }

    public void sendRegistrationToServer(String token){


    }
}
