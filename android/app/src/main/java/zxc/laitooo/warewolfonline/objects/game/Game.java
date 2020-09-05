package zxc.laitooo.warewolfonline.objects.game;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class Game {

    private int id;
    private int numPlayers;
    private int maxPlayers;
    private int ownerId;
    private String ownerName;

    public Game(int id, int numPlayers, int maxPlayers, int ownerId, String ownerName) {
        this.id = id;
        this.numPlayers = numPlayers;
        this.maxPlayers = maxPlayers;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
