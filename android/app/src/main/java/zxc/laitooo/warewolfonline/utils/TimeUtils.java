package zxc.laitooo.warewolfonline.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import zxc.laitooo.warewolfonline.constants.Integeres;

/**
 * Created by Laitooo San on 5/31/2020.
 */

public class TimeUtils {

    static String[] months = new String[]{"January","February","March","April","May","June","July","August",
    "September","October","November","December"};

    public static String getTimeNow(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        return df.format(Calendar.getInstance().getTime());
    }

    public static String getDisplayedText(String messageDate){
        String date = messageDate.substring(0,10);
        String time = messageDate.substring(11,16);

        if (messageDate.equals(getTimeNow()))
            return "Just Now";
        if (date.equals(getDate()))
            return whatTime(time);
        if (getYesterday().equals(date))
            return "Yesterday at " + time;
        if (date.substring(0,4).equals(getDate().substring(0,4))) {
            return date.substring(8,10) + "th" + months[Integer.parseInt(date.substring(5,7)) - 1]
                    + " at " + time;
        }
            //TODO: month display format by name
        return date + " , " + time;
    }

    private static String whatTime(String time) {
        DateFormat df = new SimpleDateFormat("HH:mm:ZZZZZ", Locale.ENGLISH);
        String nowTime = df.format(Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault()).getTime());
        int n1 = (Integer.parseInt(time.substring(0,2)) * 60) + Integer.parseInt(time.substring(3,5));
        int n2 = (Integer.parseInt(nowTime.substring(0,2)) * 60) + Integer.parseInt(nowTime.substring(3,5));
        int n3 = 0;
        int n4 = (Integer.parseInt(nowTime.substring(7,9)) * 60);
        int n5 = 0;
        if (nowTime.length() == 11){
            n5 = Integer.parseInt(nowTime.substring(9,11));
        }else {
            n5 = Integer.parseInt(nowTime.substring(10,12));
        }

        if (nowTime.charAt(6) == '+'){
            n3 += (n4 + n5);
        } else {
            n3 -= (n4 + n5);
        }
        n3 -= Integeres.TIME_ZONE;
        int n6 = (n2 - n1 - n3);
        Log.e("time diff","n3: " + n3 + " n1: " + n1 + " n2: " + n2 + " n6: " + n6);

        if (n6 < 1 && n6 > -3){
            return "Just now";
        }else if (n6 < -2 ){
            return nowTime.substring(0,5);
        }

        if (n6 > 59){
            return (n6 / 60) + " hours ago";
        }else {
            return n6 + " minutes ago";
        }
    }

    public static void printTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();

        DateFormat date = new SimpleDateFormat("ZZZZZ",Locale.getDefault());
        String localTime = date.format(currentLocalTime);
        Log.e("Timezone","z:" + localTime);
        if (localTime.charAt(0) == '+'){

        } else {

        }
    }

    public static int whatDay(String last){
        if (last.equals(getDate()))
            return 1;
        if (last.equals(getYesterday()))
            return 2;
        return 0;
    }

    public static String getYesterday(){
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static String getDate(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return df.format(Calendar.getInstance().getTime());
    }

}
