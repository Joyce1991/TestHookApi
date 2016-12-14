package business;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import bean.MockTask;

/**
 * Created by wallace on 16/11/2.
 */

public class TaskManager {
    private static TaskManager instance = new TaskManager();
    private DateTaskQueue mTaskQueue;

    public static TaskManager getInstance() {
        return instance;
    }

    public static String getFileName(int year, int date) {
        return "data/" + year + "_" + date + ".json";
    }

    public synchronized MockTask getNextTask() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        // 以天为单位, 天变化了, 那么不再使用当前
        if (mTaskQueue == null || mTaskQueue.mDate != day || mTaskQueue.mYear != year) {
            mTaskQueue = load(year, day);
        }

        MockTask task;
        while (true) {
            // 内存中pop, 不要删除本地生成的列表
            task = mTaskQueue.mTaskQueue.poll();
            if (task == null) {
                break;
            }

            // 如果任务不是同一个小时的, 抛弃
            if (task.mHour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                return task;
            }
        }

        return null;
    }

    public synchronized DateTaskQueue buildTask(String apkFile, Calendar calendar) {
        DateTaskQueue queue = new DateTaskQueue();
        queue.mYear = calendar.get(Calendar.YEAR);
        queue.mDate = calendar.get(Calendar.DAY_OF_YEAR);
        queue.mWeek = calendar.get(Calendar.DAY_OF_WEEK);
        queue.build(apkFile);

        save(queue);
        return queue;
    }

    private DateTaskQueue load(int year, int day) {
        File file = new File(getFileName(year, day));
        try {
            FileReader reader = new FileReader(file);
            return new Gson().fromJson(reader, DateTaskQueue.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void save(DateTaskQueue queue) {
        if (queue == null) {
            return;
        }

        // 以年为文件夹, 日为文件名
        File file = new File(getFileName(queue.mYear, queue.mDate));
        file.getParentFile().mkdirs();

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(new Gson().toJson(queue));
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
