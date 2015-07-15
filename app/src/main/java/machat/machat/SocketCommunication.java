package machat.machat;

import android.graphics.Bitmap;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;
import machat.machat.socketIO.OnCallbackMessageStatus;
import machat.machat.socketIO.OnChangeEmail;
import machat.machat.socketIO.OnChangeName;
import machat.machat.socketIO.ServiceCompose;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketCompose;
import machat.machat.socketIO.SocketParse;
import machat.machat.socketIO.SocketValid;

/**
 * Created by Admin on 5/23/2015.
 */
public class SocketCommunication implements OnCallbackAvatar{

    private Socket socket;

    private SocketService service;

    public SocketCommunication(final SocketService service, Socket socket) {
        this.socket = socket;
        this.service = service;

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(Socket.EVENT_CONNECT, null);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(Socket.EVENT_DISCONNECT, null);
            }
        }).on(SocketCommand.NEW_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.NEW_MESSAGE, args[0].toString());
            }
        }).on(SocketCommand.DELIVERED_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.DELIVERED_MESSAGE, args[0].toString());
            }
        }).on(SocketCommand.READ_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.READ_MESSAGE, args[0].toString());
            }
        }).on(SocketCommand.SEND_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.SEND_MESSAGE, args[0].toString());
                Ack ack = (Ack) args[args.length - 1];
                ack.call();
            }
        }).on(SocketCommand.BLOCKED_BY_USER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.BLOCKED_BY_USER, args[0].toString());
            }
        });
    }

    public void deliveredMessage(int id){
        socket.emit(SocketCommand.DELIVERED_MESSAGE, id);
    }

    public void readHouse(int houseId){
        socket.emit(SocketCommand.READ_HOUSE, houseId);
        service.sendBroadcast(SocketCommand.READ_HOUSE, Integer.toString(houseId));
    }

    public void readMessage(int id){
        socket.emit(SocketCommand.READ_MESSAGE, id);
        service.sendBroadcast(SocketCommand.READ_MESSAGE, Integer.toString(id));
    }

    public void search(String text){
        socket.emit(SocketCommand.SEARCH, text, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.SEARCH, args[0].toString());
            }
        });
    }

    public void login(String username, String password) {
        socket.emit(SocketCommand.LOGIN, SocketCompose.login(username, password), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.LOGIN, args[0].toString());
            }
        });
    }

    public void sendMessage(int localId, int houseId, String messageString) {
        socket.emit(SocketCommand.SEND_MESSAGE, SocketCompose.newMessage(houseId, localId, messageString), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.SEND_MESSAGE, args[0].toString());
            }
        });
    }

    public void getFavoriteList() {
        socket.emit(SocketCommand.GET_FAVORITE_LIST, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_FAVORITE_LIST, args[0].toString());
            }
        });
    }

    public void getHouse(int id) {
        socket.emit(SocketCommand.GET_HOUSE, id, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_HOUSE, args[0].toString());
            }
        });
    }

    public void getOldMessages(int id, int oldestMessageId) {
        socket.emit(SocketCommand.GET_OLD_MESSAGES, SocketCompose.getOldMessages(id, oldestMessageId), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_OLD_MESSAGES, args[0].toString());
            }
        });
    }

    public void joinHouse(int houseId){
        socket.emit(SocketCommand.JOIN_HOUSE, houseId, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.JOIN_HOUSE, args[0].toString());
            }
        });
    }

    public void getBlockList(){
        socket.emit(SocketCommand.GET_BLOCK_LIST, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_BLOCK_LIST, args[0].toString());
            }
        });
    }

    public void leaveHouse(int houseId){
        socket.emit(SocketCommand.LEAVE_HOUSE, houseId, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.LEAVE_HOUSE, args[0].toString());
            }
        });
    }

    public void getNewMessages(int id, int newestMessageId){
        socket.emit(SocketCommand.GET_NEW_MESSAGES, SocketCompose.getNewMessages(id, newestMessageId), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_NEW_MESSAGES, args[0].toString());
            }
        });
    }

    public void getProfile(int id) {
        socket.emit(SocketCommand.GET_PROFILE, id, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_PROFILE, args[0].toString());
            }
        });
    }

    public void blockUser(final int id,final boolean block) {
        socket.emit(SocketCommand.BLOCK_USER, SocketCompose.blockUser(id, block), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.BLOCK_USER, args[0].toString());
            }
        });
    }

    public void favoriteHouse(int id, boolean favorite) {
        socket.emit(SocketCommand.FAVORITE_HOUSE, SocketCompose.favoriteHouse(id, favorite), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.FAVORITE_HOUSE, args[0].toString());
            }
        });
    }

    public void logout(String sessionId){
        socket.emit(SocketCommand.LOGOUT, sessionId);
        service.sendBroadcast(SocketCommand.LOGOUT, "");
        socket.disconnect();
        socket.connect();
    }

    public void registerAccount(String username, String email, String password){
        socket.emit(SocketCommand.REGISTER, SocketCompose.registerAccount(username, email, password), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.REGISTER, args[0].toString());
            }
        });
    }

    public void muteHouse(int id, boolean mute) {
        service.user.setFavoriteMute(id, mute);
        socket.emit(SocketCommand.MUTE_HOUSE, SocketCompose.muteHouse(id, mute), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.MUTE_HOUSE, args[0].toString());
            }
        });
    }

    public void changeName(final String name, OnChangeName listener) {
        boolean valid = SocketValid.checkName(name, listener);
        if(valid)
            socket.emit(SocketCommand.CHANGE_NAME, name, new Ack() {
                @Override
                public void call(Object... args) {
                    service.sendBroadcast(SocketCommand.CHANGE_NAME, args[0].toString());
                }
            });
    }

    public void getMessageStatus(int id){
        socket.emit(SocketCommand.GET_MESSAGE_STATUS, id, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_MESSAGE_STATUS, args[0].toString());
            }
        });
    }

    public void changeEmail(String password, final String email, OnChangeEmail listener) {
        boolean valid = SocketValid.checkEmail(email, listener);
        if (valid)
            socket.emit(SocketCommand.CHANGE_EMAIL, SocketCompose.changeEmail(password, email), new Ack() {
                @Override
                public void call(Object... args) {
                    service.sendBroadcast(SocketCommand.CHANGE_EMAIL, args[0].toString());
                }
            });
    }

    public void changePassword(String oldPassword, String newPassword) {
        socket.emit(SocketCommand.CHANGE_PASSWORD, SocketCompose.changePassword(oldPassword, newPassword), new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.CHANGE_PASSWORD, args[0].toString());
            }
        });
    }

    public void loginSession(String sessionId) {
        socket.emit(SocketCommand.LOGIN_SESSION, sessionId, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.LOGIN, args[0].toString());
            }
        });
    }

    public void getAvatar(final int id) {
        socket.emit(SocketCommand.GET_AVATAR, id, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = ServiceCompose.getAvatar((JSONObject) args[0]);
                service.sendBroadcast(SocketCommand.GET_AVATAR, jsonObject.toString());
                SocketParse.parseGetAvatar(jsonObject.toString(), SocketCommunication.this);
            }
        });
    }

    public void sendAvatar(byte[] avatar){
        socket.emit(SocketCommand.SEND_AVATAR, avatar, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.SEND_AVATAR, args[0].toString());
            }
        });
    }

    public void getUndeliveredMessages(){
        socket.emit(SocketCommand.GET_UNDELIVERED_MESSAGES, new Ack() {
            @Override
            public void call(Object... args) {
                service.sendBroadcast(SocketCommand.GET_UNDELIVERED_MESSAGES, args[0].toString());
            }
        });
    }

    @Override
    public void newAvatar(int id, Bitmap bitmap) {
        AvatarManager.newAvatar(id, bitmap);
    }

}
