package zxc.laitooo.warewolfonline.objects.request;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 5/22/2020.
 */

public class Request {

    private int id;
    private String username;

    public Request(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture(){
        return Links.USER_PICTURE + id + ".png";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
