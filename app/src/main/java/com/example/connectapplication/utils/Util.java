package com.example.connectapplication.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Util {

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 2000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static String getTwoFloat(float r) {
        String right;
        DecimalFormat df = new DecimalFormat("0.00");
        right = df.format(r);
        return right;
    }

    public static ArrayList<String> getFileName(String fileAbsolutePaht, String type) {
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileAbsolutePaht);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (!files[i].isDirectory()) {
                String fileName = files[i].getName();
                if (fileName.trim().toLowerCase().endsWith(type)) {
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    public static String md5ForFile(File file) {
        int buffersize = 1024;
        FileInputStream fis = null;
        DigestInputStream dis = null;

        try {
            //创建MD5转换器和文件流
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, messageDigest);

            byte[] buffer = new byte[buffersize];
            //DigestInputStream实际上在流处理文件时就在内部就进行了一定的处理
            while (dis.read(buffer) > 0) ;

            //通过DigestInputStream对象得到一个最终的MessageDigest对象。
            messageDigest = dis.getMessageDigest();

            // 通过messageDigest拿到结果，也是字节数组，包含16个元素
            byte[] array = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            StringBuilder hex = new StringBuilder(array.length * 2);
            for (byte b : array) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void appendString(String filePath, String content) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param time   秒 北京时间=格林时间-8小时
     * @param format 格式 如yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getFormatTime(long time, String format) {
//        time = time - 8 * 3600;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time * 1000);
    }

    public static String getTodayStr() {
        return getFormatTime(System.currentTimeMillis() / 1000, "yyyy-MM-dd");
    }

}
