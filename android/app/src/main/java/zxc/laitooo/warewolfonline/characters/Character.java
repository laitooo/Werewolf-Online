package zxc.laitooo.warewolfonline.characters;

/**
 * Created by Laitooo San on 2/28/2020.
 */

public class Character {

    private int characterId;
    private String name;
    private int id;
    private int team;

    public Character(int characterId, String name, int id, int team) {
        this.characterId = characterId;
        this.name = name;
        this.id = id;
        this.team = team;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
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

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }
}
