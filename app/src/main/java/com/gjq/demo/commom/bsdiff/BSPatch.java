package com.gjq.demo.commom.bsdiff;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/07/25 11:37
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BSPatch {
    public static final int RETURN_SUCCESS = 1;
    public static final int RETURN_DIFF_FILE_ERR = 2;
    public static final int RETURN_OLD_FILE_ERR = 3;
    public static final int RETURN_NEW_FILE_ERR = 4;

    public BSPatch() {
    }

    public static int patchLessMemory(RandomAccessFile oldFile, File newFile, File diffFile, int extLen) throws IOException {
        if (oldFile != null && oldFile.length() > 0L) {
            if (newFile == null) {
                return 4;
            } else if (diffFile != null && diffFile.length() > 0L) {
                byte[] var4 = new byte[(int)diffFile.length()];
                FileInputStream var5 = new FileInputStream(diffFile);

                try {
                    BSUtil.readFromStream(var5, var4, 0, var4.length);
                } finally {
                    var5.close();
                }

                return patchLessMemory(oldFile, (int)oldFile.length(), var4, var4.length, newFile, extLen);
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }

    public static int patchLessMemory(RandomAccessFile oldFile, int oldsize, byte[] diffBuf, int diffSize, File newFile, int extLen) throws IOException {
        if (oldFile != null && oldsize > 0) {
            if (newFile == null) {
                return 4;
            } else if (diffBuf != null && diffSize > 0) {
                DataInputStream var6 = new DataInputStream(new ByteArrayInputStream(diffBuf, 0, diffSize));
                var6.skip(8L);
                long var7 = var6.readLong();
                long var9 = var6.readLong();
                int var11 = (int)var6.readLong();
                var6.close();
                ByteArrayInputStream var12 = new ByteArrayInputStream(diffBuf, 0, diffSize);
                var12.skip(32L);
                DataInputStream var13 = new DataInputStream(new GZIPInputStream(var12));
                var12 = new ByteArrayInputStream(diffBuf, 0, diffSize);
                var12.skip(var7 + 32L);
                GZIPInputStream var14 = new GZIPInputStream(var12);
                var12 = new ByteArrayInputStream(diffBuf, 0, diffSize);
                var12.skip(var9 + var7 + 32L);
                GZIPInputStream var15 = new GZIPInputStream(var12);
                FileOutputStream var16 = new FileOutputStream(newFile);

                try {
                    int var17 = 0;
                    int var18 = 0;
                    int[] var19 = new int[3];

                    while(var18 < var11) {
                        for(int var20 = 0; var20 <= 2; ++var20) {
                            var19[var20] = var13.readInt();
                        }

                        if (var18 + var19[0] > var11) {
                            var16.close();
                            byte var27 = 2;
                            return var27;
                        }

                        byte[] var26 = new byte[var19[0]];
                        if (!BSUtil.readFromStream(var14, var26, 0, var19[0])) {
                            var16.close();
                            byte var28 = 2;
                            return var28;
                        }

                        byte[] var21 = new byte[var19[0]];
                        byte var29;
                        if (oldFile.read(var21, 0, var19[0]) < var19[0]) {
                            var16.close();
                            var29 = 2;
                            return var29;
                        }

                        for(int var22 = 0; var22 < var19[0]; ++var22) {
                            if (var17 + var22 >= 0 && var17 + var22 < oldsize) {
                                var26[var22] += var21[var22];
                            }
                        }

                        var16.write(var26);
                        var18 += var19[0];
                        var17 += var19[0];
                        if (var18 + var19[1] > var11) {
                            var16.close();
                            var29 = 2;
                            return var29;
                        }

                        var26 = new byte[var19[1]];
                        if (!BSUtil.readFromStream(var15, var26, 0, var19[1])) {
                            var16.close();
                            var29 = 2;
                            return var29;
                        }

                        var16.write(var26);
                        var16.flush();
                        var18 += var19[1];
                        var17 += var19[2];
                        oldFile.seek((long)var17);
                    }

                    var13.close();
                    var14.close();
                    var15.close();
                    return 1;
                } finally {
                    oldFile.close();
                    var16.close();
                }
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }

    public static int patchFast(File oldFile, File newFile, File diffFile, int extLen) throws IOException {
        if (oldFile != null && oldFile.length() > 0L) {
            if (newFile == null) {
                return 4;
            } else if (diffFile != null && diffFile.length() > 0L) {
                BufferedInputStream var4 = new BufferedInputStream(new FileInputStream(oldFile));
                byte[] var5 = new byte[(int)diffFile.length()];
                FileInputStream var6 = new FileInputStream(diffFile);

                try {
                    BSUtil.readFromStream(var6, var5, 0, var5.length);
                } finally {
                    var6.close();
                }

                byte[] var7 = patchFast(var4, (int)oldFile.length(), var5, extLen);
                FileOutputStream var8 = new FileOutputStream(newFile);

                try {
                    var8.write(var7);
                } finally {
                    var8.close();
                }

                return 1;
            } else {
                return 2;
            }
        } else {
            return 3;
        }
    }

    public static int patchFast(InputStream oldInputStream, InputStream diffInputStream, File newFile) throws IOException {
        if (oldInputStream == null) {
            return 3;
        } else if (newFile == null) {
            return 4;
        } else if (diffInputStream == null) {
            return 2;
        } else {
            byte[] var3 = BSUtil.inputStreamToByte(oldInputStream);
            byte[] var4 = BSUtil.inputStreamToByte(diffInputStream);
            byte[] var5 = patchFast(var3, var3.length, var4, var4.length, 0);
            FileOutputStream var6 = new FileOutputStream(newFile);

            try {
                var6.write(var5);
            } finally {
                var6.close();
            }

            return 1;
        }
    }

    public static byte[] patchFast(InputStream oldInputStream, InputStream diffInputStream) throws IOException {
        if (oldInputStream == null) {
            return null;
        } else if (diffInputStream == null) {
            return null;
        } else {
            byte[] var2 = BSUtil.inputStreamToByte(oldInputStream);
            byte[] var3 = BSUtil.inputStreamToByte(diffInputStream);
            byte[] var4 = patchFast(var2, var2.length, var3, var3.length, 0);
            return var4;
        }
    }

    public static byte[] patchFast(InputStream oldInputStream, int oldsize, byte[] diffBytes, int extLen) throws IOException {
        byte[] var4 = new byte[oldsize];
        BSUtil.readFromStream(oldInputStream, var4, 0, oldsize);
        oldInputStream.close();
        return patchFast(var4, oldsize, diffBytes, diffBytes.length, extLen);
    }

    public static byte[] patchFast(byte[] oldBuf, int oldsize, byte[] diffBuf, int diffSize, int extLen) throws IOException {
        DataInputStream var5 = new DataInputStream(new ByteArrayInputStream(diffBuf, 0, diffSize));
        var5.skip(8L);
        long var6 = var5.readLong();
        long var8 = var5.readLong();
        int var10 = (int)var5.readLong();
        var5.close();
        ByteArrayInputStream var11 = new ByteArrayInputStream(diffBuf, 0, diffSize);
        var11.skip(32L);
        DataInputStream var12 = new DataInputStream(new GZIPInputStream(var11));
        var11 = new ByteArrayInputStream(diffBuf, 0, diffSize);
        var11.skip(var6 + 32L);
        GZIPInputStream var13 = new GZIPInputStream(var11);
        var11 = new ByteArrayInputStream(diffBuf, 0, diffSize);
        var11.skip(var8 + var6 + 32L);
        GZIPInputStream var14 = new GZIPInputStream(var11);
        byte[] var15 = new byte[var10];
        int var16 = 0;
        int var17 = 0;

        for(int[] var18 = new int[3]; var17 < var10; var16 += var18[2]) {
            int var19;
            for(var19 = 0; var19 <= 2; ++var19) {
                var18[var19] = var12.readInt();
            }

            if (var17 + var18[0] > var10) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            if (!BSUtil.readFromStream(var13, var15, var17, var18[0])) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            for(var19 = 0; var19 < var18[0]; ++var19) {
                if (var16 + var19 >= 0 && var16 + var19 < oldsize) {
                    var15[var17 + var19] += oldBuf[var16 + var19];
                }
            }

            var17 += var18[0];
            var16 += var18[0];
            if (var17 + var18[1] > var10) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            if (!BSUtil.readFromStream(var14, var15, var17, var18[1])) {
                throw new IOException("Corrupt by wrong patch file.");
            }

            var17 += var18[1];
        }

        var12.close();
        var13.close();
        var14.close();
        return var15;
    }
}
