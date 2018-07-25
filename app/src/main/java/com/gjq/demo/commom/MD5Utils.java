package com.gjq.demo.commom;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/07/25 11:41
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MD5Utils {
    private MD5Utils() {
    }

    public static String encode(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte[] messageDigest = md5.digest();
            StringBuilder hexString = new StringBuilder();
            byte[] var4 = messageDigest;
            int var5 = messageDigest.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte b = var4[var6];
                hexString.append(String.format("%02X", b));
            }

            return hexString.toString().toLowerCase();
        } catch (Exception var8) {
            var8.printStackTrace();
            return "";
        }
    }

    public static String encode(File file) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest);
            byte[] buffer = new byte[4096];

            while(digestInputStream.read(buffer) > -1) {
                ;
            }

            MessageDigest digest = digestInputStream.getMessageDigest();
            digestInputStream.close();
            byte[] md5 = digest.digest();
            StringBuilder sb = new StringBuilder();
            byte[] var8 = md5;
            int var9 = md5.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                byte b = var8[var10];
                sb.append(String.format("%02X", b));
            }

            return sb.toString().toLowerCase();
        } catch (Exception var12) {
            var12.printStackTrace();
            return null;
        }
    }
}
