package zxc.laitooo.warewolfonline.objects.rule;

/**
 * Created by Laitooo San on 7/23/2020.
 */

public class Rule {

    private int role;
    private String name;
    private int icon;

    public Rule(int role, String name, int icon) {
        this.role = role;
        this.name = name;
        this.icon = icon;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
