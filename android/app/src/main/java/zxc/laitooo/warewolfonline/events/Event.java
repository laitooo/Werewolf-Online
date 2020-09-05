package zxc.laitooo.warewolfonline.events;

/**
 * Created by Laitooo San on 5/18/2020.
 */

public class Event {

    private String text;
    private String image;
    private String username;
    private boolean showName;

    public Event(String text, String image, String username, boolean showName) {
        this.text = text;
        this.image = image;
        this.username = username;
        this.showName = showName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }
}
