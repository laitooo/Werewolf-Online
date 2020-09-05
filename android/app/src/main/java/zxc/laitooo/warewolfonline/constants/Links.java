package zxc.laitooo.warewolfonline.constants;

/**
 * Created by Laitooo San on 5/9/2020.
 */

public class Links {

    private final static String DOMAIN = "192.168.43.211";
    private final static String PORT = "3001";

    private final static String HOST = "http://" + DOMAIN + ":" + PORT + "/";
    //public final static String HOST = "http://411ace9aa6d6.ngrok.io/";

    public final static String REGISTER_URL = HOST + "addUser";
    public final static String LOGIN_URL = HOST + "isUser";
    public final static String ADD_FRIEND = HOST + "addFriend";

    public final static String MAIN_SOCKET = HOST + "main";
    public final static String ADD_REVIEW = HOST + "addReview";
    public final static String GAMES_SOCKET = HOST + "games";
    public final static String CHATS_SOCKET = HOST + "chats";
    public final static String GROUPS_SOCKET = HOST + "groups";
    public final static String GAME_SOCKET = GAMES_SOCKET + "/game";
    public final static String CHAT_SOCKET = CHATS_SOCKET + "/chat";
    public final static String GROUP_SOCKET = GROUPS_SOCKET + "/group";

    public final static String LOAD_GROUP_MEMBERS = HOST + "loadGroupMembers";
    public final static String LOAD_FRIENDS = HOST + "loadFriends";
    public final static String ADD_MEMBERS = HOST + "addMembers";

    public final static String USER_PICTURE = HOST + "images/image";

}
