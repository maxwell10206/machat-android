package machat.machat.parsing;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import machat.machat.conf.SocketData;

public class BroadcastCompose {

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
