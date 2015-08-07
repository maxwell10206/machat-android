package machat.machat.socketIO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 5/28/2015.
 */
public class SocketCompose {

    public static JSONObject login(String username, String password){
        JSONObject data = new JSONObject();
        try {
            data.put(SocketData.username, username);
            data.put(SocketData.password, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject changePassword(String oldPassword, String newPassword){
        JSONObject data = new JSONObject();
        try{
            data.put(SocketData.oldPassword, oldPassword);
            data.put(SocketData.newPassword, newPassword);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject changeEmail(String password, String email){
        JSONObject data = new JSONObject();
        try{
            data.put(SocketData.password, password);
            data.put(SocketData.email, email);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject updateAvatar(int id, long time){
        JSONObject data = new JSONObject();
        try{
            data.put(SocketData.time, time);
            data.put(SocketData.id, id);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject getOldMessages(int houseId, int oldestMessageId){
        JSONObject data = new JSONObject();
        try {
            data.put(machat.machat.socketIO.SocketData.id, houseId);
            data.put(SocketData.oldestMessageId, oldestMessageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject getNewMessages(int houseId, int newestMessageId){
        JSONObject data = new JSONObject();
        try{
            data.put(SocketData.id, houseId).put(SocketData.newestMessageId, newestMessageId);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject blockUser(int id, boolean block){
        JSONObject data = new JSONObject();
        try{
            data.put(machat.machat.socketIO.SocketData.block, block);
            data.put(machat.machat.socketIO.SocketData.id, id);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject registerAccount(String username, String email, String password){
        JSONObject data = new JSONObject();
        try{
            data.put(SocketData.username, username);
            data.put(SocketData.email, email);
            data.put(SocketData.password, password);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject muteHouse(int id, boolean mute){
        JSONObject data = new JSONObject();
        try{
            data.put(machat.machat.socketIO.SocketData.id, id);
            data.put(machat.machat.socketIO.SocketData.mute, mute);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject favoriteHouse(int id, boolean favorite){
        JSONObject data = new JSONObject();
        try{
            data.put(machat.machat.socketIO.SocketData.id, id);
            data.put(machat.machat.socketIO.SocketData.favorite, favorite);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return data;
    }

    public static JSONObject newMessage(int houseId, int localId, String message){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject();
            jsonObject.put(SocketData.id, houseId);
            jsonObject.put(SocketData.localId, localId);
            jsonObject.put(SocketData.message, message);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
