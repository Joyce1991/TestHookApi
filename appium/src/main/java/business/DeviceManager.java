package business;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import autotest.AdbDevice;
import autotest.utils.ShellUtils;

/**
 * Created by wallace on 16/11/1.
 */

public class DeviceManager {
    private static DeviceManager instance;
    private List<String> mDevicesName;
    private Map<String, AdbDevice> mDevices = new HashMap<String, AdbDevice>();
    private Map<String, Boolean> mDeviceOnOff = new HashMap<String, Boolean>();

    public synchronized static DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
            instance.init();

            // 检测设备是否有效
//            instance.startCheck();
        }

        return instance;
    }

    private List<String> getConnectedDevices() {
        LinkedList devices = new LinkedList();
        String raw = ShellUtils.getShellOut(ShellUtils.cmd("devices"));
        for(String line : raw.split("\n")){
            if(line.endsWith("device")) devices.add(line.replace("device","").trim());
        }
        return devices;
    }

    private void init() {
        mDevicesName = getConnectedDevices();
        mDevices.clear();
        mDeviceOnOff.clear();
        if (mDevicesName != null && mDevicesName.size() > 0) {
            for (String name : mDevicesName) {
                try {
                    mDevices.put(name, new AdbDevice(name));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    if (!checkDevice()) {
                        init();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).run();
    }

    private boolean checkDevice() {
        List list = getConnectedDevices();
        if (list.size() != mDevicesName.size()) {
            return false;
        }

        if (!list.toString().equals(mDevicesName.toString())) {
            return false;
        }

        return true;
    }

    public List<String> getDevicesName() {
        return mDevicesName;
    }

    public AdbDevice getDevice(String uuid) {
        if (uuid == null)
            return null;

        return mDevices.get(uuid);
    }

    public String getIdleDevice() {
        List<String> list = DeviceManager.getInstance().getDevicesName();
        for (String uuid : list) {
            if (!mDeviceOnOff.containsKey(uuid) || !mDeviceOnOff.get(uuid)) {
                mDeviceOnOff.put(uuid, true);
                return uuid;
            }
        }

        return null;
    }

    public void releaseDevice(String uuid) {
        mDeviceOnOff.remove(uuid);
    }
}
