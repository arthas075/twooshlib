package lib.twoosh.twooshlib.notifs;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by satyamsurendra on 12/10/16.
 */
public class Toasts {



    Context c;
    public void showToastMsg(Context c,String msg)
    {



        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();



    }


}
