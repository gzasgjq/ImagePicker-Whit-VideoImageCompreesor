package com.gjq.demo.deltaupdatelib.bsdiff;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/07/25 11:39
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BSDiff {
    private static final byte[] MAGIC_BYTES = new byte[]{77, 105, 99, 114, 111, 77, 115, 103};

    public BSDiff() {
    }

    private static void split(int[] arrayI, int[] arrayV, int start, int len, int h) {
        int var5;
        int var6;
        int var7;
        int var8;
        int var9;
        if (len < 16) {
            for(var5 = start; var5 < start + len; var5 += var6) {
                var6 = 1;
                var7 = arrayV[arrayI[var5] + h];

                for(var8 = 1; var5 + var8 < start + len; ++var8) {
                    if (arrayV[arrayI[var5 + var8] + h] < var7) {
                        var7 = arrayV[arrayI[var5 + var8] + h];
                        var6 = 0;
                    }

                    if (arrayV[arrayI[var5 + var8] + h] == var7) {
                        var9 = arrayI[var5 + var6];
                        arrayI[var5 + var6] = arrayI[var5 + var8];
                        arrayI[var5 + var8] = var9;
                        ++var6;
                    }
                }

                for(var8 = 0; var8 < var6; ++var8) {
                    arrayV[arrayI[var5 + var8]] = var5 + var6 - 1;
                }

                if (var6 == 1) {
                    arrayI[var5] = -1;
                }
            }

        } else {
            var7 = arrayV[arrayI[start + len / 2] + h];
            int var10 = 0;
            int var11 = 0;

            for(var8 = start; var8 < start + len; ++var8) {
                if (arrayV[arrayI[var8] + h] < var7) {
                    ++var10;
                }

                if (arrayV[arrayI[var8] + h] == var7) {
                    ++var11;
                }
            }

            var10 += start;
            var11 += var10;
            var8 = start;
            var6 = 0;
            var5 = 0;

            while(var8 < var10) {
                if (arrayV[arrayI[var8] + h] < var7) {
                    ++var8;
                } else if (arrayV[arrayI[var8] + h] == var7) {
                    var9 = arrayI[var8];
                    arrayI[var8] = arrayI[var10 + var6];
                    arrayI[var10 + var6] = var9;
                    ++var6;
                } else {
                    var9 = arrayI[var8];
                    arrayI[var8] = arrayI[var11 + var5];
                    arrayI[var11 + var5] = var9;
                    ++var5;
                }
            }

            while(var10 + var6 < var11) {
                if (arrayV[arrayI[var10 + var6] + h] == var7) {
                    ++var6;
                } else {
                    var9 = arrayI[var10 + var6];
                    arrayI[var10 + var6] = arrayI[var11 + var5];
                    arrayI[var11 + var5] = var9;
                    ++var5;
                }
            }

            if (var10 > start) {
                split(arrayI, arrayV, start, var10 - start, h);
            }

            for(var8 = 0; var8 < var11 - var10; ++var8) {
                arrayV[arrayI[var10 + var8]] = var11 - 1;
            }

            if (var10 == var11 - 1) {
                arrayI[var10] = -1;
            }

            if (start + len > var11) {
                split(arrayI, arrayV, var11, start + len - var11, h);
            }

        }
    }

    private static void qsufsort(int[] arrayI, int[] arrayV, byte[] oldBuf, int oldsize) {
        int[] var4 = new int[256];

        int var5;
        for(var5 = 0; var5 < oldsize; ++var5) {
            ++var4[oldBuf[var5] & 255];
        }

        for(var5 = 1; var5 < 256; ++var5) {
            var4[var5] += var4[var5 - 1];
        }

        for(var5 = 255; var5 > 0; --var5) {
            var4[var5] = var4[var5 - 1];
        }

        var4[0] = 0;

        for(var5 = 0; var5 < oldsize; arrayI[++var4[oldBuf[var5] & 255]] = var5++) {
            ;
        }

        arrayI[0] = oldsize;

        for(var5 = 0; var5 < oldsize; ++var5) {
            arrayV[var5] = var4[oldBuf[var5] & 255];
        }

        arrayV[oldsize] = 0;

        for(var5 = 1; var5 < 256; ++var5) {
            if (var4[var5] == var4[var5 - 1] + 1) {
                arrayI[var4[var5]] = -1;
            }
        }

        arrayI[0] = -1;

        for(var5 = 1; arrayI[0] != -(oldsize + 1); var5 += var5) {
            int var6 = 0;
            int var7 = 0;

            while(var7 < oldsize + 1) {
                if (arrayI[var7] < 0) {
                    var6 -= arrayI[var7];
                    var7 -= arrayI[var7];
                } else {
                    if (var6 != 0) {
                        arrayI[var7 - var6] = -var6;
                    }

                    var6 = arrayV[arrayI[var7]] + 1 - var7;
                    split(arrayI, arrayV, var7, var6, var5);
                    var7 += var6;
                    var6 = 0;
                }
            }

            if (var6 != 0) {
                arrayI[var7 - var6] = -var6;
            }
        }

        for(var5 = 0; var5 < oldsize + 1; arrayI[arrayV[var5]] = var5++) {
            ;
        }

    }

    private static int search(int[] arrayI, byte[] oldBuf, int oldSize, byte[] newBuf, int newSize, int newBufOffset, int start, int end, BSDiff.IntByRef pos) {
        int var9;
        if (end - start < 2) {
            var9 = matchlen(oldBuf, oldSize, arrayI[start], newBuf, newSize, newBufOffset);
            int var10 = matchlen(oldBuf, oldSize, arrayI[end], newBuf, newSize, newBufOffset);
            if (var9 > var10) {
                pos.value = arrayI[start];
                return var9;
            } else {
                pos.value = arrayI[end];
                return var10;
            }
        } else {
            var9 = start + (end - start) / 2;
            return memcmp(oldBuf, oldSize, arrayI[var9], newBuf, newSize, newBufOffset) < 0 ? search(arrayI, oldBuf, oldSize, newBuf, newSize, newBufOffset, var9, end, pos) : search(arrayI, oldBuf, oldSize, newBuf, newSize, newBufOffset, start, var9, pos);
        }
    }

    private static int matchlen(byte[] oldBuf, int oldSize, int oldOffset, byte[] newBuf, int newSize, int newOffset) {
        int var6 = Math.min(oldSize - oldOffset, newSize - newOffset);

        for(int var7 = 0; var7 < var6; ++var7) {
            if (oldBuf[oldOffset + var7] != newBuf[newOffset + var7]) {
                return var7;
            }
        }

        return var6;
    }

    private static int memcmp(byte[] s1, int s1Size, int s1offset, byte[] s2, int s2Size, int s2offset) {
        int var6 = s1Size - s1offset;
        if (var6 > s2Size - s2offset) {
            var6 = s2Size - s2offset;
        }

        for(int var7 = 0; var7 < var6; ++var7) {
            if (s1[var7 + s1offset] != s2[var7 + s2offset]) {
                return s1[var7 + s1offset] < s2[var7 + s2offset] ? -1 : 1;
            }
        }

        return 0;
    }

    public static void bsdiff(File oldFile, File newFile, File diffFile) throws IOException {
        BufferedInputStream var3 = new BufferedInputStream(new FileInputStream(oldFile));
        BufferedInputStream var4 = new BufferedInputStream(new FileInputStream(newFile));
        FileOutputStream var5 = new FileOutputStream(diffFile);

        try {
            byte[] var6 = bsdiff((InputStream)var3, (int)oldFile.length(), (InputStream)var4, (int)newFile.length());
            var5.write(var6);
        } finally {
            var5.close();
        }

    }

    public static byte[] bsdiff(InputStream oldInputStream, int oldsize, InputStream newInputStream, int newsize) throws IOException {
        byte[] var4 = new byte[oldsize];
        BSUtil.readFromStream(oldInputStream, var4, 0, oldsize);
        oldInputStream.close();
        byte[] var5 = new byte[newsize];
        BSUtil.readFromStream(newInputStream, var5, 0, newsize);
        newInputStream.close();
        return bsdiff(var4, oldsize, var5, newsize);
    }

    public static byte[] bsdiff(byte[] oldBuf, int oldsize, byte[] newBuf, int newsize) throws IOException {
        int[] var4 = new int[oldsize + 1];
        qsufsort(var4, new int[oldsize + 1], oldBuf, oldsize);
        int var5 = 0;
        byte[] var6 = new byte[newsize];
        int var7 = 0;
        byte[] var8 = new byte[newsize];
        ByteArrayOutputStream var9 = new ByteArrayOutputStream();
        DataOutputStream var10 = new DataOutputStream(var9);
        var10.write(MAGIC_BYTES);
        var10.writeLong(-1L);
        var10.writeLong(-1L);
        var10.writeLong((long)newsize);
        var10.flush();
        GZIPOutputStream var11 = new GZIPOutputStream(var10);
        DataOutputStream var12 = new DataOutputStream(var11);
        int var13 = 0;
        int var14 = 0;
        int var15 = 0;
        int var16 = 0;
        int var17 = 0;
        BSDiff.IntByRef var18 = new BSDiff.IntByRef();

        while(true) {
            int var19;
            int var21;
            int var22;
            do {
                if (var13 >= newsize) {
                    var12.flush();
                    var11.finish();
                    var21 = var10.size() - 32;
                    var11 = new GZIPOutputStream(var10);
                    var11.write(var6, 0, var5);
                    var11.finish();
                    var11.flush();
                    var22 = var10.size() - var21 - 32;
                    var11 = new GZIPOutputStream(var10);
                    var11.write(var8, 0, var7);
                    var11.finish();
                    var11.flush();
                    var10.close();
                    ByteArrayOutputStream var31 = new ByteArrayOutputStream(32);
                    DataOutputStream var32 = new DataOutputStream(var31);
                    var32.write(MAGIC_BYTES);
                    var32.writeLong((long)var21);
                    var32.writeLong((long)var22);
                    var32.writeLong((long)newsize);
                    var32.close();
                    byte[] var33 = var9.toByteArray();
                    byte[] var30 = var31.toByteArray();
                    System.arraycopy(var30, 0, var33, 0, var30.length);
                    return var33;
                }

                var19 = 0;

                for(int var20 = var13 += var14; var13 < newsize; ++var13) {
                    for(var14 = search(var4, oldBuf, oldsize, newBuf, newsize, var13, 0, oldsize, var18); var20 < var13 + var14; ++var20) {
                        if (var20 + var17 < oldsize && oldBuf[var20 + var17] == newBuf[var20]) {
                            ++var19;
                        }
                    }

                    if (var14 == var19 && var14 != 0 || var14 > var19 + 8) {
                        break;
                    }

                    if (var13 + var17 < oldsize && oldBuf[var13 + var17] == newBuf[var13]) {
                        --var19;
                    }
                }
            } while(var14 == var19 && var13 != newsize);

            var21 = 0;
            var22 = 0;
            int var23 = 0;
            int var24 = 0;

            while(var15 + var24 < var13 && var16 + var24 < oldsize) {
                if (oldBuf[var16 + var24] == newBuf[var15 + var24]) {
                    ++var21;
                }

                ++var24;
                if (var21 * 2 - var24 > var22 * 2 - var23) {
                    var22 = var21;
                    var23 = var24;
                }
            }

            int var25 = 0;
            if (var13 < newsize) {
                var21 = 0;
                int var26 = 0;

                for(var24 = 1; var13 >= var15 + var24 && var18.value >= var24; ++var24) {
                    if (oldBuf[var18.value - var24] == newBuf[var13 - var24]) {
                        ++var21;
                    }

                    if (var21 * 2 - var24 > var26 * 2 - var25) {
                        var26 = var21;
                        var25 = var24;
                    }
                }
            }

            if (var15 + var23 > var13 - var25) {
                int var27 = var15 + var23 - (var13 - var25);
                var21 = 0;
                int var28 = 0;
                int var29 = 0;

                for(var24 = 0; var24 < var27; ++var24) {
                    if (newBuf[var15 + var23 - var27 + var24] == oldBuf[var16 + var23 - var27 + var24]) {
                        ++var21;
                    }

                    if (newBuf[var13 - var25 + var24] == oldBuf[var18.value - var25 + var24]) {
                        --var21;
                    }

                    if (var21 > var28) {
                        var28 = var21;
                        var29 = var24 + 1;
                    }
                }

                var23 += var29 - var27;
                var25 -= var29;
            }

            for(var24 = 0; var24 < var23; ++var24) {
                var6[var5 + var24] = (byte)(newBuf[var15 + var24] - oldBuf[var16 + var24]);
            }

            for(var24 = 0; var24 < var13 - var25 - (var15 + var23); ++var24) {
                var8[var7 + var24] = newBuf[var15 + var23 + var24];
            }

            var5 += var23;
            var7 += var13 - var25 - (var15 + var23);
            var12.writeInt(var23);
            var12.writeInt(var13 - var25 - (var15 + var23));
            var12.writeInt(var18.value - var25 - (var16 + var23));
            var15 = var13 - var25;
            var16 = var18.value - var25;
            var17 = var18.value - var13;
        }
    }

    private static class IntByRef {
        private int value;

        private IntByRef() {
        }
    }
}
