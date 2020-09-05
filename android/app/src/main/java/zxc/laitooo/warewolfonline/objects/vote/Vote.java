package zxc.laitooo.warewolfonline.objects.vote;

import zxc.laitooo.warewolfonline.constants.Roles;

/**
 * Created by Laitooo San on 5/12/2020.
 */

public class Vote {

    private int id;
    private String name;
    private String pic;
    private boolean selected;
    private int order;
    private int role;

    public Vote(int id, String name, String pic, boolean selected, int order,int role) {
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.selected = selected;
        this.order = order;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isWolf(){
        return Roles.isWolf(role);
    }
}
