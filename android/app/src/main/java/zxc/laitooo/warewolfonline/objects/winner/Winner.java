package zxc.laitooo.warewolfonline.objects.winner;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class Winner {

    private int id;
    private String username;
    private int role;
    private boolean alive;
    private boolean winner;

    public Winner(int id, String username, int role, boolean alive, boolean winner) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.alive = alive;
        this.winner = winner;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public String getPicture(){
        return Links.USER_PICTURE + id + ".png";
    }

}
