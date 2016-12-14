package business;

import com.google.gson.Gson;

import autotest.AdbDevice;
import autotest.AndroidKeyCode;
import bean.MockTask;

/**
 * Created by wallace on 16/11/1.
 */

public class MockAppBusiness {
    private static MockAppBusiness instance = new MockAppBusiness();

    public static MockAppBusiness getInstance() {
        return instance;
    }

    public void doEmulator() throws InterruptedException {
        System.out.println("doEmulator start");
        MockTask task;

        while (true) {
            task = TaskManager.getInstance().getNextTask();
            if (task == null) {
                Thread.sleep(5000);
                continue;
            }

            System.out.println("\r\ntask start:" + new Gson().toJson(task));

            long start = System.currentTimeMillis();
            emulatorOneTask(task);

            System.out.println("finish task " + (System.currentTimeMillis() - start) / 1000 + "s");
        }
    }

    private void emulatorOneTask(MockTask task) throws InterruptedException {
        // 获取空闲设备
        String uuid =  DeviceManager.getInstance().getIdleDevice();

        // 等待空闲设备
        while (uuid == null) {
            Thread.sleep(5000);
            uuid =  DeviceManager.getInstance().getIdleDevice();
        }
        System.out.println("task get device " + uuid);

        AdbDevice device = DeviceManager.getInstance().getDevice(uuid);

        // 安装测试app
        if (!device.isInstalled(task.mAppPackageName)) {
            device.installApp(task.mAppPath);

            // 等待安装完成
            while (!device.isInstalled(task.mAppPackageName)) {
                Thread.sleep(1000);
            }
        }
        System.out.println("task installed " + task.mAppPackageName);

        int i = task.mStartupTime;
        while (i > 0) {
            i--;
            // 启动程序
            device.startActivity(task.mAppLaunchActivity);
            // 等待程序启动完成
            while (!device.getCurrentPackageName().equals(task.mAppPackageName)) {
                Thread.sleep(5000);
            }
            System.out.println("task start " + task.mAppLaunchActivity);

            // 随机跑特定时间
            device.runMonkeyTest(task.mDuration, task.mAppPackageName);

            // 切换到后台
            device.sendKeyEvent(AndroidKeyCode.HOME);
            Thread.sleep(1000);

            // 退出程序
            device.quitCurrentApp();

            System.out.println("task end " + task.mAppLaunchActivity);
        }

        // 卸载app
        device.removeApp(task.mAppPackageName);
        Thread.sleep(1000);

        // 释放设备
        DeviceManager.getInstance().releaseDevice(uuid);
    }
}
