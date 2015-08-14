package machat.machat.socketIO;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 6/25/2015.
 */
public class ServiceCompose {

    public static JSONObject getAvatar(JSONObject jsonObject) {
        try {
            byte[] byteImage = (byte[]) jsonObject.get(SocketData.byteArray);
            String base64Image = Base64.encodeToString(byteImage, Base64.DEFAULT);
            jsonObject.remove(SocketData.byteArray);
            jsonObject.put(SocketData.byteArray, base64Image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
