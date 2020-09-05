package zxc.laitooo.warewolfonline.objects.selectfriend;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 6/5/2020.
 */

public class SelectFriend {

    private int id;
    private int userId;
    private String userName;
    private boolean selected;

    public SelectFriend(int id, int userId, String userName, boolean selected) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.selected = selected;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPicture(){
        return Links.USER_PICTURE + userId + ".png";
    }

}
