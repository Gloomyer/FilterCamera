package com.gloomyer.camera.camera.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Classname FileUtils
 * @Description 工具类
 * @Date 2019-09-29 09:37
 * @Created by gloomy
 */
public class FileUtils {
    /**
     * 保存数据成为文件
     *
     * @param buffer 要保存的数据
     * @param path   路径
     * @return 是否成功
     */
    public static boolean save(byte[] buffer, String path) {
        File mImageFile = new File(path);
        return save(buffer, mImageFile);
    }

    /**
     * 保存数据成为文件
     *
     * @param buffer 要保存的数据
     * @param path   路径
     * @return 是否成功
     */
    public static boolean save(byte[] buffer, File path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(buffer, 0, buffer.length);
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
