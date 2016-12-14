
import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


import autotest.utils.FileUtils;
import business.MockAppBusiness;
import business.TaskManager;


/**
 * Created by Administrator on 2016-8-25.
 */
public class Main {

    public static void main(String args[]) {
        FileUtils.deleteDir("data");
        // 生成今天的任务
        Calendar calendar  = Calendar.getInstance();
        if (!new File(TaskManager.getFileName(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR))).exists()) {
            TaskManager.getInstance().buildTask("F:/com.mojang.minecraftpe.apk", calendar);
        }

        // 生成明天的任务, 3小时检测一次
        new Timer().schedule(new TimerTask() {
            public void run() {
                Calendar calendar  = Calendar.getInstance();
                calendar.add(Calendar.DATE, 1);
                if (!new File(TaskManager.getFileName(calendar.get(Calendar.YEAR), calendar.get(Calendar.DAY_OF_YEAR))).exists()) {
                    TaskManager.getInstance().buildTask("‪F:/com.mojang.minecraftpe.apk", calendar);
                }
            }
        }, 0, 3 * 3600000);

        // 执行任务
        new Thread(new Runnable() {
            public void run() {
                try {
                    MockAppBusiness.getInstance().doEmulator();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }
}
