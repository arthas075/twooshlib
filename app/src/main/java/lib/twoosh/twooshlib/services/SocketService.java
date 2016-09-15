package lib.twoosh.twooshlib.services;

/**
 * Created by arthas on 14/9/16.
 */
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import lib.twoosh.twooshlib.constants.AppConstants;
import lib.twoosh.twooshlib.R;


public class SocketService {


    private static Socket socketobj = null;
    String host = AppConstants.socket_localhost;
    IO.Options opts = null;

    public SocketService(){



    }


    public Socket getSocketInstance(){
        return socketobj;
    }


    public void connectSocket(){

        if(socketobj==null){

            makeConnection();
        }
        else if(!socketobj.connected()){

            makeConnection();

        }

    }


    private void makeConnection(){

        try{



                opts = new IO.Options();
                opts.query = "auth_token=1234567890";
                socketobj = IO.socket(host, opts);
                socketobj.connect();
                socketobj.emit("get_online_count");
                try{

                    socketobj.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            int i =1;



                            //                   mSocket.emit("test", "awesome");
//                            JSONObject roomobj = new JSONObject();
//                            try
//                            {
//                                roomobj.put("id",0);
//                                socketobj.emit("joinroom",roomobj.toString());
//                            }
//                            catch(Exception e)
//                            {
//
//                            }

                        }

                    }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {

                            int k = 0;
                            socketobj.emit("get_online_count");

                        }

                    }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {

                            int i =0;

                        }

                    });
                }
                catch (Exception e){


                }


            }
            catch (Exception e){

                //try again
                try{

                    opts = new IO.Options();
                    opts.query = "auth_token=1234567890";
                    socketobj = IO.socket(host, opts);
                    socketobj = IO.socket(host);
                    socketobj.connect();
                }
                catch (Exception e1){

                    // debug
                }

            }
    }


//
//    public Socket getConnectedObject(){
//
//        String host = AppConstants.socket_localhost;
//        if(socketobj == null){
//
//
//
//        }
//        else if(!socketobj.connected()){
//
//
//
//        }
//
//        return socketobj;
//    }

}
