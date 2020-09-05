package zxc.laitooo.warewolfonline.user;

import android.content.Context;

import zxc.laitooo.warewolfonline.utils.SecurePreferences;

/**
 * Created by Laitooo San on 4/25/2020.
 */

public class UserData {

    private SecurePreferences preferences;
    private Context c;

    private final String TAG = "warewolf_user_data.zxc";

    public UserData(Context c) {
        this.c = c;
        preferences = new SecurePreferences(c,TAG,c.getPackageName(),true);
    }

    public void saveUser(User user){
        preferences.put("id","" + user.getId());
        preferences.put("name",user.getUsername());
        preferences.put("email",user.getEmail());
        preferences.put("token",user.getToken());
        preferences.put("country","" + user.getCountry());
        preferences.put("pic",user.getPic());
        preferences.put("password",user.getPassword());
    }

    public User getUser(){
        return new User(
                Integer.parseInt(preferences.getString("id")),preferences.getString("email"),
                preferences.getString("name"),preferences.getString("password"),
                Integer.parseInt(preferences.getString("country")),
                preferences.getString("token")
        );
    }

    public void clearUser(){
        preferences.clear();
    }
}
