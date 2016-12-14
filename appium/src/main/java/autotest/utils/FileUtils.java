package autotest.utils;

import java.io.File;

/**
 * Created by wallace on 16/11/17.
 */

public class FileUtils {
    /**
     * 删除目录
     *
     * @param dirPath 目录路径(绝对路径)
     * @return
     */
    public static boolean deleteDir(String dirPath) {
        File delDir = new File(dirPath);

        if (delDir.isDirectory()) {
            for (String dirName : delDir.list()) {
                deleteDir(dirPath + "/" + dirName);
            }
        }
        return delDir.delete();
    }
}
