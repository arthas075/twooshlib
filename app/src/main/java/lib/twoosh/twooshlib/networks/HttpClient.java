package lib.twoosh.twooshlib.networks;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arthas on 4/9/16.
 */
public class HttpClient {


    public interface GetBack{

        public void onResponse(String response);
    }

    public interface PostBack{

        public void onResponse(String response);
    }

    private GetBack getbacklistener;
    private PostBack postbacklistener;




    public HttpClient(GetBack getbacklistener){

        this.getbacklistener = getbacklistener;
    }

    public HttpClient(PostBack postbacklistener){

        this.postbacklistener = postbacklistener;
    }

    public void Get(final Context c, String url, JSONObject params){


        RequestQueue queue = Volley.newRequestQueue(c);
        //String urlparams = "{\"mode\":\"synctagpost\",\"tagid\":\""+tagid+"\",\"updated\":\""+twooshupdated+"\"}";
        String urlparams = "";

        try {
            urlparams = URLEncoder.encode(urlparams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {

                            getbacklistener.onResponse(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void Post(final Context c, String url, final JSONObject postparams){


        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(c, response,Toast.LENGTH_LONG).show();
//                        try {
//                            JSONObject jsonresp = new JSONObject(response);
//                            if(jsonresp.get("status").equals("success"))
//                            {
//                                JSONObject jsonrespdata = jsonresp.getJSONObject("data");
//                                if(jsonrespdata.get("otp_sent").equals("1"))
//                                {
//                                    Toast.makeText(Identity.this,"One Time Password sent to +91"+userphonenumber,Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                    Toast.makeText(Identity.this,"There seems to be an issue. Please try again.",Toast.LENGTH_SHORT).show();
//                                }
//                                String user_exists = "0";
//
//                                if(jsonrespdata.get("user_exists").equals("1"))
//                                {
//
//                                    user_exists = "1";
//
//                                }
//                                startRegistration(userphonenumber,user_exists);
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        try {

                            postbacklistener.onResponse(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(c,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
//                JSONObject jsonparams = new JSONObject();
//
//                try {
//                    jsonparams.put("mobile","9945325885");
//                    jsonparams.put("mode","register");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                params.put("params",postparams.toString());


                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(c);
        requestQueue.add(stringRequest);
    }


}
