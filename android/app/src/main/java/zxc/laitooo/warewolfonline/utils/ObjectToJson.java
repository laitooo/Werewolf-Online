package zxc.laitooo.warewolfonline.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import zxc.laitooo.warewolfonline.objects.game.Game;

/**
 * Created by Laitooo San on 5/7/2020.
 */

public class ObjectToJson {

    public static JSONObject gameToJson(Game game){
        try {
            JSONObject object = new JSONObject();
            object.put("id", "" + game.getId());
            object.put("num","" + game.getNumPlayers());
            object.put("max","" + game.getMaxPlayers());
            object.put("idOwner","" + game.getOwnerId());
            object.put("nameOwner","" + game.getOwnerName());
            return object;
        }catch (JSONException e){
            Log.e("Obj to Json","game e:" + e.getMessage());
            return  null;
        }
    }
}
