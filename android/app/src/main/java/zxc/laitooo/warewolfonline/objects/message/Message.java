package zxc.laitooo.warewolfonline.objects.message;

/**
 * Created by Laitooo San on 5/1/2020.
 */

public class Message {

    private int id;
    private boolean isFromServer;
    private String userName;
    private int userId;
    private String text;
    private int state;
    private boolean fromYou;

    public static final String CONVERSION_MESSAGE = "You are now a warewolf";
    public static final int STATE_SENDING = 1;
    public static final int STATE_SENT = 2;
    public static final int STATE_ERROR = 3;

    public Message(String userName, String text,boolean isFromYou,int state,int userId) {
        this.userName = userName;
        this.text = text;
        this.isFromServer = false;
        this.fromYou = isFromYou;
        this.state = state;
        this.userId = userId;
        this.id = 0;
    }

    public Message(String userName, String text,boolean isFromYou,int state,int userId,int id) {
        this.userName = userName;
        this.text = text;
        this.isFromServer = false;
        this.fromYou = isFromYou;
        this.state = state;
        this.userId = userId;
        this.id = id;
    }

    public Message(boolean isFromServer, String text, int state) {
        this.isFromServer = isFromServer;
        this.text = text;
        this.fromYou = false;
        this.state = state;
        this.userId = 0;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isFromServer() {
        return isFromServer;
    }

    public void setFromServer(boolean fromServer) {
        isFromServer = fromServer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFromYou() {
        return fromYou;
    }

    public void setFromYou(boolean fromYou) {
        this.fromYou = fromYou;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isConvertedMessage() {
        return text.equals(CONVERSION_MESSAGE);
    }

    public String log() {
        return (isFromServer() ? "Server : " : (fromYou ? "You : " : userName + " : ")) + text;
    }
}
