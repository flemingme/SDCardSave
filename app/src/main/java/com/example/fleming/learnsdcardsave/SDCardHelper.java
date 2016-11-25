package com.example.fleming.learnsdcardsave;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * SDCardHelper
 * Created by Fleming on 2016/11/23.
 */

public class SDCardHelper {

    private static final String TAG = "SDCardhelper";

    //判断SDCard是否挂载
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    //获取SDCard的绝对路径
    public static String getSDCardPath() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    //获取SDCard的大小，单位MB
    public static long getSDCardSize() {
        if (isSDCardMounted()) {
            StatFs fs = new StatFs(getSDCardPath());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                long size = fs.getBlockSizeLong();
                long count = fs.getBlockCountLong();
                return size * count / 1024 / 1024;
            }
        }
        return 0;
    }

    //获取SDCard剩余的可用空间大小，单位MB
    public static long getSDCardFreeSize() {
        if (isSDCardMounted()) {
            StatFs fs = new StatFs(getSDCardPath());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                long size = fs.getBlockSizeLong();
                long count = fs.getAvailableBlocksLong();
                return size * count / 1024 / 1024;
            }
        }
        return 0;
    }

    /**
     * 将文件保存到SDCard中
     *
     * @param data     文件的字节数据
     * @param dir      要保存的相对目录
     * @param fileName 要保存的文件名字
     * @return boolean
     */
    public static boolean saveFile2SDCard(byte[] data, String dir, String fileName) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            Log.i(TAG, "SDCard absolute path: " + getSDCardPath());
            File file = new File(getSDCardPath() + File.separator + dir);
            Log.i(TAG, "file save path: " + file.getAbsolutePath());
            if (!file.exists()) {
                boolean flag = file.mkdirs();
                Log.i(TAG, "file's dir is made success: " + flag);
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(file, fileName)));
                bos.write(data, 0, data.length);
                bos.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从SDCard中加载文件到内存中
     * @param filePath 文件路径
     * @return byte[]
     */
    public static byte[] loadFileFromSDCard(String filePath) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        if (isSDCardMounted()) {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    bis = new BufferedInputStream(new FileInputStream(file));
                    baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8 * 1024];
                    int c = 0;
                    while ((c = bis.read(buffer)) != -1) {
                        baos.write(buffer, 0, c);
                        baos.flush();
                    }
                    return baos.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null && baos != null) {
                        try {
                            bis.close();
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }
}
