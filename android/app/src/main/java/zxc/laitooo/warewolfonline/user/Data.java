package zxc.laitooo.warewolfonline.user;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class Data {

    private boolean isLogged;
    private boolean hasPicture;

    public Data() {
        isLogged = false;
        hasPicture = false;
    }

    public Data(boolean isLogged, boolean hasPicture) {
        this.isLogged = isLogged;
        this.hasPicture = hasPicture;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public boolean isHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }
}
