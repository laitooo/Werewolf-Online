package zxc.laitooo.warewolfonline.objects.groupmessage;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class GroupMessage {

    private int id;
    private int userId;
    private String userName;
    private String content;
    private String time;
    private boolean infoMessage;

    public GroupMessage(int id, int userId, String userName, String content, String time,
                        boolean infoMessage) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.time = time;
        this.infoMessage = infoMessage;
    }

    public boolean isInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(boolean infoMessage) {
        this.infoMessage = infoMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserPicture(){
        return Links.USER_PICTURE + userId + ".png";
    }

}
