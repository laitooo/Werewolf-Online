package zxc.laitooo.warewolfonline.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import zxc.laitooo.warewolfonline.objects.game.Game;

/**
 * Created by Laitooo San on 4/26/2020.
 */

public class AppData {

    private SharedPreferences preferences;
    private String TAG = "warewolf_app_data.zxc";
    private String IS_LOGGED = "is.logged";
    private String HAS_PICTURE = "has.picture";
    private String GOING_GAME1 = "GOiNG_Games.1";
    private String GOING_GAME2 = "GOiNG_Games.2";
    private String GOING_GAME3 = "GOiNG_Games.3";
    public int ERROR_ID_GAME = -1;

    private Context c;

    public AppData(Context c) {
        this.c = c;
        preferences = c.getSharedPreferences(TAG,Context.MODE_PRIVATE);
    }

    public void setData(Data data){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HAS_PICTURE,data.isHasPicture());
        editor.putBoolean(IS_LOGGED,data.isLogged());
        editor.putInt(GOING_GAME1,ERROR_ID_GAME);
        editor.putInt(GOING_GAME2,ERROR_ID_GAME);
        editor.putInt(GOING_GAME3,ERROR_ID_GAME);
        editor.commit();
    }

    public Data getData(){
        return new Data(
                preferences.getBoolean(IS_LOGGED,false),
                preferences.getBoolean(HAS_PICTURE,false)
        );
    }

    public boolean addGame(int id){
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getInt(GOING_GAME1,ERROR_ID_GAME) == ERROR_ID_GAME){
            editor.putInt(GOING_GAME1,id);
        } else if (preferences.getInt(GOING_GAME2,ERROR_ID_GAME) == ERROR_ID_GAME){
            editor.putInt(GOING_GAME2,id);
        } else if (preferences.getInt(GOING_GAME3,ERROR_ID_GAME) == ERROR_ID_GAME){
            editor.putInt(GOING_GAME3,id);
        }else {
            return false;
        }
        editor.commit();
        Log();
        return true;
    }

    public int getFirstGame(){
        return preferences.getInt(GOING_GAME1,ERROR_ID_GAME);
    }

    public int getSecondGame(){
        return preferences.getInt(GOING_GAME2,ERROR_ID_GAME);
    }

    public int getThirdGame(){
        return preferences.getInt(GOING_GAME3,ERROR_ID_GAME);
    }

    public boolean isGameGoing(int id){
        if (id == getFirstGame())
            return true;
        if (id == getSecondGame())
            return true;
        if (id == getThirdGame())
            return true;
        return false;
    }

    public boolean isGamesFull(){
        Log();
        if (preferences.getInt(GOING_GAME1,ERROR_ID_GAME) != ERROR_ID_GAME){
            if (preferences.getInt(GOING_GAME2,ERROR_ID_GAME) != ERROR_ID_GAME){
                if (preferences.getInt(GOING_GAME3,ERROR_ID_GAME) != ERROR_ID_GAME){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public void finishGame(int id){
        if (preferences.getInt(GOING_GAME1,ERROR_ID_GAME) != id){
            if (preferences.getInt(GOING_GAME2,ERROR_ID_GAME) != id){
                if (preferences.getInt(GOING_GAME3,ERROR_ID_GAME) == id){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(GOING_GAME3,ERROR_ID_GAME);
                    editor.commit();
                    Log.e("appdata","removed third id: " + id);
                }
            }else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(GOING_GAME2,ERROR_ID_GAME);
                editor.commit();
                Log.e("appdata","removed second id: " + id);
            }
        }else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(GOING_GAME1,ERROR_ID_GAME);
            editor.commit();
            Log.e("appdata","removed first id: " + id);
        }
        Log();
    }

    public boolean stillExists(ArrayList<Game> games,int id){
        for (Game g: games) {
            if (g.getId() == id)
                return true;
        }
        return false;
    }

    public void Log(){
        Log.e("Logging","game1: " + preferences.getInt(GOING_GAME1,ERROR_ID_GAME) +
                " game2: " + preferences.getInt(GOING_GAME2,ERROR_ID_GAME) +
                " game3: " + preferences.getInt(GOING_GAME3,ERROR_ID_GAME));
    }

}
