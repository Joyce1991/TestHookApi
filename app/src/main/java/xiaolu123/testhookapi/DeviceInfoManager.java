package xiaolu123.testhookapi;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by jalen on 2016/12/8.
 */
public class DeviceInfoManager {
    private static DeviceInfoManager ourInstance = new DeviceInfoManager();

    public static DeviceInfoManager getInstance() {
        return ourInstance;
    }

    private DeviceInfoManager() {
    }

    public DeviceInfo getInfo() {
        if (info == null){
            File device = new File("/data/local/tmp" + File.separator + "deviceinfo.json");
            Gson gson = new Gson();
            try {
                FileReader reader = new FileReader(device);
                info = gson.fromJson(reader, DeviceInfo.class);
                reader.close();
                Log.i("joyce_invoked", "配置文件获取成功：" + device.getPath());
            } catch (Throwable e) {
                Log.e("joyce_invoked_init", "获取配置失败，" + e.getMessage());
            }
        }
        return info;
    }

    private DeviceInfo info;


}
