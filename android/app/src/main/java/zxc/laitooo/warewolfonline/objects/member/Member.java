package zxc.laitooo.warewolfonline.objects.member;

import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.objects.selectfriend.SelectFriend;

/**
 * Created by Laitooo San on 6/4/2020.
 */

public class Member {

    private int id;
    private int idUser;
    private String nameUser;
    private boolean isAdmin;

    public Member(int id, int idUser, String nameUser, boolean isAdmin) {
        this.id = id;
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.isAdmin = isAdmin;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPicture(){
        return Links.USER_PICTURE + idUser + ".png";
    }

}
