package machat.machat;

import android.text.format.DateUtils;

import java.io.Serializable;

/**
 * Created by Admin on 6/13/2015.
 */
@SuppressWarnings("serial")
public class FavoriteItem extends User implements Serializable {

    private boolean isMute;

    private boolean header = false;

    private boolean read = true;

    private boolean block;

    private Message message;

    public FavoriteItem(int id, String username, String name, boolean isMute, Message message, boolean read, boolean block){
        super(id, username, name);
        this.message = message;
        this.isMute = isMute;
        this.read = read;
        this.block = block;
    }

    public boolean isBlock(){ return block; }

    public void setBlock(boolean block){
        this.block = block;
    }

    public boolean isRead(){ return read; }

    public void setRead(boolean read){
        this.read = read;
    }

    public Message getMessage(){ return message; }

    public void setMessage(Message newMessage){ message = newMessage; }

    public void setHeader(boolean header){
        this.header = header;
    }

    public boolean isHeader(){ return header; }

    public boolean isMute(){ return isMute; }

    public void setMute(boolean isMute){
        this.isMute = isMute;
    }

    public String getTimeString(){
        long now = System.currentTimeMillis();
        if(message.getTime() == 0) {
            return "";
        }else if((now - message.getTime()*1000) < 60000) {
            return "just now";
        }else{
            return DateUtils.getRelativeTimeSpanString(message.getTime() * 1000, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        }
    }

}
