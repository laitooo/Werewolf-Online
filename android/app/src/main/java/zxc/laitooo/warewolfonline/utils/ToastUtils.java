package zxc.laitooo.warewolfonline.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Laitooo San on 5/5/2020.
 */

public class ToastUtils {

    public static void networkError(Context c){
        Toast.makeText(c, "Network Connection Error", Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context c,String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    public static void shortToast(Context c,String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }

    public static void jsonException(Context c){
        Toast.makeText(c, "Json Exception", Toast.LENGTH_SHORT).show();
    }
}
