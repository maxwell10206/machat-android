package machat.machat;

/**
 * Created by Admin on 7/6/2015.
 */
public class MissedMessage {

    private int houseId;

    private String houseName;

    private String name;

    private String message;

    private int missedMessages;

    public MissedMessage(int houseId, String houseName, String name, String message, int missedMessages) {
        this.houseId = houseId;
        this.houseName = houseName;
        this.name = name;
        this.message = message;
        this.missedMessages = missedMessages;
    }

    public int getHouseId() {
        return houseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMissedMessages() {
        return missedMessages;
    }

    public void setMissedMessages(int missedMessages) {
        this.missedMessages = missedMessages;
    }
}
