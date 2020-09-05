package zxc.laitooo.warewolfonline.objects.group;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class Group {

    private int id;
    private String groupName;
    private int adminId;
    private String adminName;
    private int numMembers;

    public Group(int id, String groupName, int adminId, String adminName, int numMembers) {
        this.id = id;
        this.groupName = groupName;
        this.adminId = adminId;
        this.adminName = adminName;
        this.numMembers = numMembers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getNumMembers() {
        return numMembers;
    }

    public void setNumMembers(int numMembers) {
        this.numMembers = numMembers;
    }
}
