package Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Kanet on 3/13/2016.
 */
public class TimeStamp {
    public static final int FULL_TIME = 1;
    public static final int CHARACTER_TIME=2;
    public static String getDistanceTime(long createdTime,int key){
        long distance=(System.currentTimeMillis())-(createdTime*1000);
        long[] disTime=calculatorTime(distance);
        String sTime=String.valueOf(disTime[0])+" "+getSuffix(key,disTime[1]);
        return sTime;
    }

    private static String getSuffix(int key,long suffix){
        switch (key){
            case FULL_TIME: return getFullTime(suffix);
            case CHARACTER_TIME:return getCharacterTime(suffix);
        }
        return null;
    }
    private static String getFullTime(long key){
        switch ((int)key){
            case 0: return "seconds ago";
            case 1: return "minutes ago";
            case 2: return "hours ago";
            case 3:return "days ago";
            case 4:return "weeks ago";
            case 5:return "months ago";
            case 6:return "years ago";
        }
        return null;
    }

    private static String getCharacterTime(long key){
        switch ((int)key){
            case 0: return "s";
            case 1: return "min";
            case 2: return "h";
            case 3:return "d";
            case 4:return "w";
            case 5:return "m";
            case 6:return "y";
        }
        return null;
    }

    private static long[] calculatorTime(long lTime)
    {
        final long SEC=1000;
        final long MIN=60*SEC;
        final long HOUR=60*MIN;
        final long DAY=24*HOUR;
        final long WEEK=7*DAY;
        final long MONTH=30*DAY;
        final long YEAR=365*DAY;
        long[] disTime=new long[2];
        if (lTime/YEAR>0){
            disTime[0]=lTime/YEAR;
            disTime[1]=6;
            return disTime;
        }

        if (lTime/MONTH>0){
            disTime[0]=lTime/MONTH;
            disTime[1]=5;
            return disTime;
        }
        if (lTime/WEEK>0){
            disTime[0]=lTime/WEEK;
            disTime[1]=4;
            return disTime;
        }
        if (lTime/(24*60*60*1000)>0){
            disTime[0]=lTime/(24*60*60*1000);
            disTime[1]=3;
            return disTime;
        }
        if (lTime/(60*60*1000)>0){
            disTime[0]=lTime/(60*60*1000);
            disTime[1]=2;
            return disTime;
        }
        if(lTime/(60*1000)>0){
            disTime[0]=lTime/(60*1000);
            disTime[1]=1;
            return disTime;
        }
        if(lTime/1000>0){
            disTime[0]=lTime/(60*1000);
            disTime[1]=0;
            return disTime;
        }
        return disTime;
    }
}
