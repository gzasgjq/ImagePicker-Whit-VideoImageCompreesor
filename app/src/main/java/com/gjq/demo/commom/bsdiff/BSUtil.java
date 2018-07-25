package com.gjq.demo.commom.bsdiff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/07/25 11:38
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BSUtil {
    public static final int HEADER_SIZE = 32;
    public static final int BUFFER_SIZE = 8192;

    public BSUtil() {
    }

    public static final boolean readFromStream(InputStream in, byte[] buf, int offset, int len) throws IOException {
        int var5;
        for(int var4 = 0; var4 < len; var4 += var5) {
            var5 = in.read(buf, offset + var4, len - var4);
            if (var5 < 0) {
                return false;
            }
        }

        return true;
    }

    public static byte[] inputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        byte[] var2 = new byte[8192];
        boolean var3 = true;

        int var5;
        while((var5 = in.read(var2, 0, 8192)) != -1) {
            var1.write(var2, 0, var5);
        }

        Object var4 = null;
        return var1.toByteArray();
    }
}
