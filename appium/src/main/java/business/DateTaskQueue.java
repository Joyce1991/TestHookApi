package business;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import autotest.utils.HttpUtils;
import autotest.utils.PackageUtils;
import bean.DeviceBean;
import bean.MockTask;

/**
 * Created by wallace on 16/11/11.
 */

public class DateTaskQueue {
    public int mYear;
    public int mDate;
    public int mWeek;
    public Queue<MockTask> mTaskQueue = new LinkedList<MockTask>();
    private String DEVICE_URL = "http://test.api.kuaiyouxi.com/game/device.php";

    public void build(String apkFile) {
        try {
            PackageUtils packageUtils = new PackageUtils(new File(apkFile));

            // 一天的时间
            int totalTime = 24 * 3600;
            int time = 0;

            while (time < totalTime) {
                MockTask task = new MockTask();
                task.mAppLaunchActivity = packageUtils.getPackageName() + "/" + packageUtils.getLaunchActivity();
                task.mAppPackageName = packageUtils.getPackageName();
                task.mAppPath = apkFile;
                task.mHour = time / 3600;

                task.mStartupTime = (int) (Math.random() * 2 + 1);
                // 单位是秒, 区间是[10, 1500]
                task.mDuration = buildDuration(task.mHour);
                // 排除安装等时间, 平均按照1分钟
                time += task.mDuration * task.mStartupTime + 60;

                randomInsert(task, time);
            }

            buildDeviceList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据当前时间生成时长, 由于部分时段活跃少, 避免浪费手机, 特意弄长时间
     *
     * @param hour
     * @return
     */
    private int buildDuration(int hour) {
        if (hour < 2) {
            // [10, 20)
            return (int) (Math.random() * 10 + 10) * 60;
        } else if (hour < 6) {
            // [20, 40)
            return (int) (Math.random() * 20 + 20) * 60;
        } else if (hour < 9) {
            // [10, 20)
            return (int) (Math.random() * 10 + 10) * 60;
        }  else if (hour < 12) {
            // [5, 7)
            return (int) (Math.random() * 2 + 5) * 60;
        } else if (hour < 14) {
            // [3, 5)
            return (int) (Math.random() * 2 + 3) * 60;
        } else if (hour < 18) {
            // [10, 15)
            return (int) (Math.random() * 5 + 10) * 60;
        } else if (hour < 21) {
            // [1, 3)
            return (int) (Math.random() * 2 + 1) * 60;
        }  else if (hour < 23) {
            // [3, 5)
            return (int) (Math.random() * 2 + 3) * 60;
        } else {
            // [5, 10)
            return (int) (Math.random() * 5 + 5) * 60;
        }
    }

    private void buildDeviceList() throws Exception {
        // 生成今日随机用户
        List<DeviceBean> deviceBeanList = reqDevice(mTaskQueue.size());
        if(deviceBeanList != null && deviceBeanList.size() > 0 && deviceBeanList.size() < 200){
        	while(deviceBeanList.size() < 1000){
        		deviceBeanList.add(deviceBeanList.get(2));
        	}
        }
        if (deviceBeanList == null || deviceBeanList.size() < mTaskQueue.size()) {
            return;
        }

        // 生成昨日留存用户
        List<DeviceBean> yesterdayDeviceList = new LinkedList<DeviceBean>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        File file = new File(TaskManager.getFileName(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR)));
        if (file.exists()) {
            try {
                FileReader reader = new FileReader(file);
                DateTaskQueue list = new Gson().fromJson(reader, DateTaskQueue.class);
                if (list != null) {
                    for (MockTask task : list.mTaskQueue) {
                        yesterdayDeviceList.add(task.mDeviceBean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 生成历史回访用户数据
        List<DeviceBean> backDeviceList = new LinkedList<DeviceBean>();
        File[] fileList = new File("data").listFiles();
        for (File f : fileList) {
            // 抛弃今天和昨天的数据
            if (f.getPath().contains(TaskManager.getFileName(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_YEAR)))
                    || f.getPath().contains(TaskManager.getFileName(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.DAY_OF_YEAR)))) {
                continue;
            }

            try {
                FileReader reader = new FileReader(f);
                DateTaskQueue list = new Gson().fromJson(reader, DateTaskQueue.class);
                if (list != null) {
                    for (MockTask task : list.mTaskQueue) {
                        // 只留一日数据的量
                        if ((int)(Math.random() * (fileList.length - 2)) == 0) {
                            backDeviceList.add(task.mDeviceBean);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (MockTask task : mTaskQueue) {
            // 30%的概率进入昨日留存逻辑
            if (((int) (Math.random() * 3) == 1) && yesterdayDeviceList.size() > 0) {
                task.mDeviceBean = yesterdayDeviceList.get((int) (Math.random() * (yesterdayDeviceList.size() - 1)));
                continue;
            }

            // 10%的概率进入返场留存逻辑
            if (((int) (Math.random() * 10) == 1) && backDeviceList.size() > 0) {
                task.mDeviceBean = backDeviceList.get((int) (Math.random() * (backDeviceList.size() - 1)));
                continue;
            }

            // 获取新随机用户
            task.mDeviceBean = deviceBeanList.get((int) (Math.random() * (deviceBeanList.size() - 1)));
        }
    }

    /**
     * 支持24小时效应, 周末效应, 节假日效应
     *
     * @param task
     * @param time
     */
    private void randomInsert(MockTask task, int time) {
        // 周末效应
        boolean isValidHoliday = true;
        if (mWeek == 2) {
            // 概率是50%
            isValidHoliday = (int)(Math.random() * 2) == 1;
        } else if (mWeek == 3) {
            // 概率是50%
            isValidHoliday = (int)(Math.random() * 2) == 1;
        } else if (mWeek == 4) {
            // 概率是40%
            isValidHoliday = (int)(Math.random() * 3) == 1;
        } else if (mWeek == 5) {
            // 概率是50%
            isValidHoliday = (int)(Math.random() * 2) == 1;
        } else if (mWeek == 6) {
            // 概率是75%
            isValidHoliday = (int)(Math.random() * 3) != 1;
        } else if (mWeek == 7) {
            isValidHoliday = true;
        } else if (mWeek == 1) {
            isValidHoliday = true;
        }

        if (isValidHoliday) {
            mTaskQueue.add(task);
        }
    }

    /**
     * 服务器随机返回一批设备信息
     *
     * @return
     */
	private List<DeviceBean> reqDevice(int size) throws Exception {
        String url = HttpUtils.createGetUrl(DEVICE_URL, "action=info");
        return HttpUtils.getRequest(url, new TypeToken<List<DeviceBean>>(){}.getType());
    }
}
