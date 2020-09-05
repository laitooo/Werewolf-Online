package zxc.laitooo.warewolfonline.objects.friend;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class Friend {

    private int id;
    private int userId;
    private String userName;

    public Friend(int id, int userId, String userName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
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

    public String getPicture(){
        return Links.USER_PICTURE + userId + ".png";
    }

}
