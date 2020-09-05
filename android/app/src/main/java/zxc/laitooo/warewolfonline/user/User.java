package zxc.laitooo.warewolfonline.user;

import zxc.laitooo.warewolfonline.constants.Links;

/**
 * Created by Laitooo San on 4/25/2020.
 */

public class User {

    private int id;
    private String Email;
    private String Username;
    private String Password;
    private int Country;
    private String Token;
    private String Pic;

    public User(int id, String email, String username, String password, int country, String token) {
        this.id = id;
        Email = email;
        Username = username;
        Password = password;
        Country = country;
        Token = token;
        Pic = Links.USER_PICTURE + id + ".png";
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPic() {
        return Pic;
    }

    public void setPic(String pic) {
        Pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getCountry() {
        return Country;
    }

    public void setCountry(int country) {
        Country = country;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
