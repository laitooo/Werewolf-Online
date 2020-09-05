package zxc.laitooo.warewolfonline.objects.player;

import android.util.Log;

import zxc.laitooo.warewolfonline.constants.Links;
import zxc.laitooo.warewolfonline.constants.Roles;

/**
 * Created by Laitooo San on 4/27/2020.
 */

public class Player {

    private int id;
    private String name;
    private boolean online;
    private int order;
    private int role;
    private boolean alive;
    private boolean revealed;

    public Player(int id, String name, boolean online, int order, int role, boolean alive,
                  boolean revealed) {
        this.id = id;
        this.name = name;
        this.online = online;
        this.order = order;
        this.role = role;
        this.alive = alive;
        this.revealed = revealed;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture(){
        return Links.USER_PICTURE + id + ".png";
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void printPlayer(){
        Log.e("player","id: " + id + " name: " + name + " role: " + Roles.RolesNames[role] +
                (revealed ? " revealed " : " not revealed ") + " pic: " + getPicture() +
                        (online ? " online" : " not online"));
    }
}
