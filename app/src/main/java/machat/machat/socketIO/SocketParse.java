package machat.machat.socketIO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import machat.machat.FavoriteItem;
import machat.machat.House;
import machat.machat.Message;
import machat.machat.MissedMessage;
import machat.machat.MyProfile;
import machat.machat.Profile;
import machat.machat.SearchItem;
import machat.machat.User;

/**
 * Created by Admin on 5/28/2015.
 */
public class SocketParse {

    public static void parseSearchResults(String string, OnSearchResults listener){
        try {
            ArrayList<SearchItem> users = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                boolean blocked = (0 != jsonObject.getInt(SocketData.block));
                User user = parseUser(jsonObject);
                SearchItem searchItem = new SearchItem();
                searchItem.setBlock(blocked);
                searchItem.setUser(user);
                users.add(searchItem);
            }
            listener.newSearchResults(users);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseJoinHouse(String string, OnJoinHouse listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ){
                listener.joinedHouseSuccess();
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.joinedHouseFailed(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseGetMessageStatus(String string, OnCallbackMessageStatus listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            int id = jsonObject.getInt(SocketData.id);
            int status = jsonObject.getInt(SocketData.status);
            listener.updateMessageStatus(id, status);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseUserReadMessage(String string, OnUserReadMessage listener){
        listener.userReadMessage(Integer.parseInt(string));
    }

    public static void parseReadHouse(String string, OnReadHouse listener){
        listener.readHouse(Integer.parseInt(string));
    }

    public static void parseDeliveredMessage(String string, OnDeliveredMessage listener){
        listener.deliveredMessage(Integer.parseInt(string));
    }

    public static void parseSendAvatar(String string, OnCallbackSendAvatar listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ){
                listener.avatarUploadSuccess();
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.avatarUploadFailed(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseGetAvatar(String string, OnCallbackAvatar listener){
        try {
            JSONObject jsonObject = new JSONObject(string);
            int id = jsonObject.getInt(SocketData.id);
            String base64Image = jsonObject.getString(SocketData.byteArray);
            byte[] bytesImage = Base64.decode(base64Image, Base64.DEFAULT);
            listener.newAvatar(id, bytesImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseLogin(String string, OnLoginListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if (succ) {
                String sessionId = jsonObject.getString(SocketData.sessionId);
                String email = jsonObject.getString(SocketData.email);
                String username = jsonObject.getString(SocketData.username);
                String name = jsonObject.getString(SocketData.name);
                int id = jsonObject.getInt(SocketData.id);

                listener.onLoginSuccess(new MyProfile(id, username, name, email, sessionId));
            } else {
                String err = jsonObject.getString(SocketData.err);
                listener.onLoginFailed(err);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static FavoriteItem parseFavoriteItem(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt(SocketData.id);
        String username = jsonObject.getString(SocketData.username);
        String name = jsonObject.getString(SocketData.name);
        String messageUsername = jsonObject.getString(SocketData.m_username);
        int messageId = jsonObject.getInt(SocketData.m_id);
        boolean mute = (1 == jsonObject.getInt(SocketData.mute));
        boolean read = (1 == jsonObject.getInt(SocketData.seen));

        int userId = jsonObject.getInt(SocketData.m_user_id);
        String messageName = jsonObject.getString(SocketData.m_name);
        int status = jsonObject.getInt(SocketData.status);
        int messageTime = jsonObject.getInt(SocketData.time);
        String messageString = jsonObject.getString(SocketData.message);
        boolean block = (jsonObject.getInt(SocketData.block) > 0);

        User user1 = new User();
        user1.setId(userId);
        user1.setUsername(messageUsername);
        user1.setName(messageName);
        Message message = new Message();
        message.setHouseId(id);
        message.setId(messageId);
        message.setHouseName(name);
        message.setStatus(status);
        message.setTime(messageTime);
        message.setMessage(messageString);
        message.setUser(user1);
        User user2 = new User();
        user2.setName(name);
        user2.setUsername(username);
        user2.setId(id);
        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setMessage(message);
        favoriteItem.setBlock(block);
        favoriteItem.setMute(mute);
        favoriteItem.setRead(read);
        favoriteItem.setUser(user2);
        favoriteItem.setPrimaryKey(id);
        return favoriteItem;
    }

    public static void parseChangeEmail(String string, OnChangeEmail listener){
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ){
                listener.changeEmailSuccess(jsonObject.getString(SocketData.email));
            }else{
                listener.changeEmailFailed(jsonObject.getString(SocketData.err));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseBlockList(String string, OnCallbackBlockList listener){
        try{
            ArrayList<User> blockList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(string);
            for(int i = 0; i < jsonArray.length(); i++){
                blockList.add(parseUser(jsonArray.getJSONObject(i)));
            }
            listener.newBlockList(blockList);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private static User parseUser(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt(SocketData.id);
        String username = jsonObject.getString(SocketData.username);
        String name = jsonObject.getString(SocketData.name);
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setUsername(username);
        return user;
    }

    public static void parseChangeName(String string, OnChangeName listener){
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ){
                listener.changeNameSuccess(jsonObject.getString(SocketData.name));
            }else{
                listener.changeNameFailed(jsonObject.getString(SocketData.err));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseChangePassword(String string, OnChangePassword listener){
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if (succ) {
                listener.passwordChangeSuccess();
            } else {
                String err = jsonObject.getString(SocketData.err);
                listener.passwordChangeFailed(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseFavoriteList(String string, OnNewFavoriteList listener) {
        try {
            ArrayList<FavoriteItem> favoriteItems = new ArrayList();
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ) {
                JSONArray jsonArray = jsonObject.getJSONArray(SocketData.favoriteList);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject houseJSON = jsonArray.getJSONObject(i);
                    favoriteItems.add(parseFavoriteItem(houseJSON));
                }
                listener.newFavoriteList(favoriteItems);
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.getFavoriteListFailed(err);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseMessageList(String command, String string, OnNewMessageList listener) {
        try {
            ArrayList<Message> messageList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ) {
                JSONArray jsonArray = jsonObject.getJSONArray(SocketData.messageList);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject messageJSON = jsonArray.getJSONObject(i);
                    messageList.add(parseMessage(messageJSON));
                }
                if (command.equals(SocketCommand.GET_NEW_MESSAGES)) {
                    listener.addNewMessages(messageList);
                } else {
                    listener.addOldMessages(messageList);
                }
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.getMessageListFailed(err);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseRegister(String string, OnCallbackRegister listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ){
                listener.registerSuccess();
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.registerFailed(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseHouse(String string, OnNewHouse listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            boolean isFavorite = jsonObject.getBoolean(SocketData.isFavorite);
            boolean isMute = (1 == jsonObject.getInt(SocketData.isMute));
            House house = new House();
            User user = parseUser(jsonObject);
            house.setMute(isMute);
            house.setFavorite(isFavorite);
            house.setUser(user);
            listener.newHouse(house);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseProfile(String string, OnNewProfile listener) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean isBlocked = (0 != jsonObject.getInt(SocketData.block_id));

            User user = parseUser(jsonObject);
            Profile profile = new Profile();
            profile.setBlocked(isBlocked);
            profile.setUser(user);
            listener.newProfile(profile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseMessage(String string, OnNewMessage listener) {
        try {
            listener.newMessage(parseMessage(new JSONObject(string)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseUndeliveredMessages(String string, OnUndeliveredMessages listener){
        try{
            ArrayList<MissedMessage> messages = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(string);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int houseId = jsonObject.getInt(SocketData.houseId);
                String name = jsonObject.getString(SocketData.name);
                String houseName = jsonObject.getString(SocketData.houseName);
                String lastMessage = jsonObject.getString(SocketData.message);
                int missedMessages = jsonObject.getInt(SocketData.missed_messages);
                messages.add(new MissedMessage(houseId, houseName, name, lastMessage, missedMessages));
            }
            listener.undeliveredMessages(messages);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private static Message parseMessage(JSONObject jsonObject) throws JSONException {
        int userId = jsonObject.getInt(SocketData.userId);
        int houseId = jsonObject.getInt(SocketData.houseId);
        int id = jsonObject.getInt(SocketData.id);
        String username = jsonObject.getString(SocketData.username);
        String name = jsonObject.getString(SocketData.name);
        long messageTime = jsonObject.getLong(SocketData.time);
        String lastMessage = jsonObject.getString(SocketData.message);
        int status = jsonObject.getInt(SocketData.status);
        String houseName = jsonObject.getString(SocketData.houseName);
        //Message message = new Message(houseId, userId, id, username, name, lastMessage, messageTime, status, houseName);
        Message message = new Message();
        User user = new User();
        user.setUsername(username);
        user.setId(userId);
        user.setName(name);
        message.setHouseName(houseName);
        message.setStatus(status);
        message.setId(id);
        message.setHouseId(houseId);
        message.setMessage(lastMessage);
        message.setTime(messageTime);
        message.setUser(user);
        return message;
    }

    public static void parseSendMessage(String string, OnCallbackSendMessage listener){
        try {
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ) {
                int localId = jsonObject.getInt(SocketData.localId);
                Message message = parseMessage(jsonObject);
                message.setLocalId(localId);
                listener.sendMessageSuccess(message);
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.sendMessageFailed(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseBlockedByUser(String string, OnBlockedByUser listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            int id = jsonObject.getInt(SocketData.id);
            boolean block = jsonObject.getBoolean(SocketData.block);
            if(block){
                listener.blockedBy(id);
            }else{
                listener.unBlockedBy(id);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public static void parseBlockUser(String string, OnCallbackBlock listener) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            int id = jsonObject.getInt(SocketData.id);
            boolean block = jsonObject.getBoolean(SocketData.block);
            listener.callbackBlock(id, block);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseFavoriteHouse(String string, OnCallbackFavorite listener){
        try{
            JSONObject jsonObject = new JSONObject(string);
            boolean succ = jsonObject.getBoolean(SocketData.succ);
            if(succ) {
                int id = jsonObject.getInt(SocketData.id);
                boolean favorite = jsonObject.getBoolean(SocketData.favorite);
                if (!favorite) {
                    listener.removeFavorite(id);
                } else {
                    listener.newFavorite(parseFavoriteItem(jsonObject));
                }
            }else{
                String err = jsonObject.getString(SocketData.err);
                listener.favoriteError(err);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
