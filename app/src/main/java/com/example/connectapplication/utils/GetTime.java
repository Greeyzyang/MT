package com.example.connectapplication.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTime {
    static long hours;
    static long minutes;
    static long seconds;
    public static String getShowTime(long startTime,long endTime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = formatter.format(endTime);
        String date2 = formatter.format(startTime);
                // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
                // 计算的时间差
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(date1);
            Date d2 = df.parse(date2);
            // 这样得到的差值是微秒级别
            long diff = d1.getTime() - d2.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24))
                    / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
                    * (1000 * 60 * 60))
                    / (1000 * 60);
            seconds = minutes/60;
            Log.i("viewDataFill", "会员剩余: " + days + "天" + hours + "小时" + minutes + "分");
        }catch (Exception e){
            e.getMessage();
        }
                return hours + ":" + minutes + ":" + seconds;
        }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime_Today() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(new java.util.Date());
    }


    public static String getDateToString(int milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     * @param times 时间戳
     * @return
     */
    public static String getStandardDate(long times) {
        Log.d("yj","times----"+times);
        StringBuffer sb = new StringBuffer();

        //long t = Long.parseLong(timeStr);
        //long time = System.currentTimeMillis() - (times*1000);
        long mill = (long) times /1000;//秒前

        long minute = (long)  (times/60/1000.0f);// 分钟前

        long hour = (long)  (times/60/60/1000.0f);// 小时

        long day = (long)   (times/24/60/60/1000.0f);// 天前

//        if (day - 1 > 0) {
//            sb.append(day + "天");
//        } else if (hour - 1 > 0) {
//            if (hour >= 24) {
//                sb.append("1天");
//            } else {
//                sb.append(hour + "小时");
//            }
//        } else if (minute - 1 > 0) {
//            if (minute == 60) {
//                sb.append("1小时");
//            } else {
//                sb.append(minute + "分钟");
//            }
//        } else if (mill - 1 > 0) {
//            if (mill == 60) {
//                sb.append("1分钟");
//            } else {
//                sb.append(mill + "秒");
//            }
//        } else {
//            sb.append("刚刚");
//        }
//        if (!sb.toString().equals("刚刚")) {
//            sb.append("前");
//        }
        BigDecimal bigDecimal = new BigDecimal(minute);
         sb.append(String.format("%.2f",bigDecimal));
        return sb.toString();
    }

}
