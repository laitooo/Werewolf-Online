package zxc.laitooo.warewolfonline.objects.chatmessage;

/**
 * Created by Laitooo San on 5/29/2020.
 */

public class ChatMessage {

    private int id;
    private int idChat;
    private boolean fromMe;
    private String content;
    private String date;

    public ChatMessage(int id, int idChat, boolean fromMe, String content, String date) {
        this.id = id;
        this.idChat = idChat;
        this.fromMe = fromMe;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
