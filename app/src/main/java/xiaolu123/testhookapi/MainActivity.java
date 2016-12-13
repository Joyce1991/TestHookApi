package xiaolu123.testhookapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.telephony.TelephonyManager.PHONE_TYPE_CDMA;
import static android.telephony.TelephonyManager.PHONE_TYPE_GSM;

public class MainActivity extends Activity {
    private final String mPageName = "AnalyticsHome";
    private TextView uid;
    private TextView model;
    private TextView manufacturer;
    private TextView systemver;
    private TextView density;
    private TextView screen_height;
    private TextView screen_width;
    private TextView mac;
    private TextView imei;
    private TextView cpu;
    private TextView language;
    private TextView network;
    private TextView root;
    private TextView appList;
    private TextView any;
    private TextView location;
    private TextView gsmLocation;
    private TextView build;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = this.getFilesDir();
        String filePath = file.getPath();
        Log.i("joyce", filePath);

/*        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);*/
        setContentView(R.layout.activity_main);

        uid = (TextView) findViewById(R.id.uuid);
        model = (TextView) findViewById(R.id.model);
        manufacturer = (TextView) findViewById(R.id.manufacturer);
        systemver = (TextView) findViewById(R.id.systemver);
        density = (TextView) findViewById(R.id.densitytext);
        screen_height = (TextView) findViewById(R.id.screen_height);
        screen_width = (TextView) findViewById(R.id.screen_width);
        mac = (TextView) findViewById(R.id.mac);
        imei = (TextView) findViewById(R.id.imei);
        cpu = (TextView) findViewById(R.id.cpu);
        language = (TextView) findViewById(R.id.language);
        network = (TextView) findViewById(R.id.network);
        root = (TextView) findViewById(R.id.isroot);
        appList = (TextView) findViewById(R.id.appList);
        any = (TextView) findViewById(R.id.any);
        location = (TextView) findViewById(R.id.location);
        gsmLocation = (TextView) findViewById(R.id.gsmorcdmalocation);
        build = (TextView) findViewById(R.id.build);

        Log.e("zyr", "onCreate");

        init();
    }

    private void init() {
        uid.setText(UUID.randomUUID().toString().replace("-", ""));
        model.setText(Build.MODEL);
        manufacturer.setText(Build.MANUFACTURER);
        systemver.setText(Build.VERSION.RELEASE);
        density.setText(String.valueOf(getResolution().densityDpi));
        screen_height.setText(String.valueOf(getResolution().heightPixels));
        screen_width.setText(String.valueOf(getResolution().widthPixels));
        mac.setText(getWifiMac());
        imei.setText(getDeviceIMEI());
        cpu.setText(Build.CPU_ABI);
        language.setText(getLanguage());
        network.setText(getNetworkType());
        root.setText(String.valueOf(isRoot()));
        any.setText("\n" + "imsi: " + DeviceUtil.getIMSI(this) + "\n\n"
                + "number: " + DeviceUtil.getNumber(this) + "\n\n"
                + "Sim Serial Number: " + DeviceUtil.getSimSerialNumber(this) + "\n\n"
                + "Sim Country Iso: " + DeviceUtil.getSimCountryIso(this) + "\n\n"
                + "Sim Operator: " + DeviceUtil.getSimOperator(this) + "\n\n"
                + "Sim Operator Name: " + DeviceUtil.getSimOperatorName(this) + "\n\n"
                + "Network Country Iso: " + DeviceUtil.getNetworkCountryIso(this) + "\n\n"
                + "Network Operator: " + DeviceUtil.getNetworkOperator(this) + "\n\n"
                + "Network Operator Name: " + DeviceUtil.getNetworkOperatorName(this) + "\n\n"
                + "Phone Type: " + DeviceUtil.getPhoneType(this) + "\n\n"
                + "Network Type: " + DeviceUtil.getNetworkType(this) + "\n\n"
                + "Mac Address: " + DeviceUtil.getMacAddress(this) + "\n\n"
                + "BSSID: " + DeviceUtil.getBSSID(this) + "\n\n"
                + "SSID: " + DeviceUtil.getSSID(this) + "\n\n"
                + "Blue Name: " + DeviceUtil.getBlueName(this) + "\n\n"
                + "Blue Mac: " + DeviceUtil.getBlueMac() + "\n\n"
                + "Sim State: " + DeviceUtil.getSimState(this) + "\n\n"
                + "has Icc Card: " + DeviceUtil.hasIccCard(this) + "\n\n"
                + "Radio Version: " + DeviceUtil.getRadioVersion(this) + "\n\n"
                + "Android ID: " + DeviceUtil.getAndroidID(this) );
//                + "Cdma Location: " + DeviceUtil.getCdmaLocation(this).getBaseStationLatitude() + "-" + DeviceUtil.getCdmaLocation(this).getBaseStationLongitude());
        if (DeviceUtil.getLocaltion(this) != null) {
            location.setText("Localtion: " + DeviceUtil.getLocaltion(this).getLatitude() + "-" + DeviceUtil.getLocaltion(this).getLongitude() + "\n\n");
        }
        if (DeviceUtil.getPhoneType(this) == PHONE_TYPE_GSM){
            gsmLocation.setText("gsmLocaltion: " + DeviceUtil.getGsmLocaltion(this).getLac() + "-" + DeviceUtil.getGsmLocaltion(this).getCid());
        }
        if (DeviceUtil.getPhoneType(this) == PHONE_TYPE_CDMA && DeviceUtil.getCdmaLocation(this) != null){
            gsmLocation.setText("Cdma Location: " + DeviceUtil.getCdmaLocation(this).getBaseStationLatitude() + "-" + DeviceUtil.getCdmaLocation(this).getBaseStationLongitude());
        }

        build.setText("apilevel: " + Build.VERSION.SDK_INT + "\n\n"
                + "version: " + Build.VERSION.RELEASE + "\n\n"
                + "fingerprint: " + Build.FINGERPRINT + "\n\n"
                + "display: " + Build.DISPLAY + "\n\n"
                + "product: " + Build.PRODUCT + "\n\n"
                + "id: " + Build.ID + "\n\n"
                + "device: " + Build.DEVICE + "\n\n"
                + "serial: " + Build.SERIAL + "\n\n"
                + "board: " + Build.BOARD + "\n\n"
                + "brand: " + Build.BRAND + "\n\n"
                + "manufacturer: " + Build.MANUFACTURER + "\n\n"
                + "model: " + Build.MODEL + "\n\n"
                + "hardware: " + Build.HARDWARE + "\n\n"
        );
        appList.setText(getInstalledApps().toString());
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }*/

    /** 判断是否具有ROOT权限 ,此方法对有些手机无效，比如小米系列 */
    private boolean isRoot() {
        boolean res = false;
        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                res = false;
            } else {
                res = true;
            }
        } catch (Exception e) {
            res = false;
        }
        return res;
    }

    private String getWifiMac() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wi = wifiManager.getConnectionInfo();
            String result = wi.getMacAddress();
            if (result == null)
                result = "";
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    private String getDeviceIMEI() {
        String result = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) (getSystemService(Context.TELEPHONY_SERVICE));
            result = telephonyManager.getDeviceId();
            if (result == null)
                result = "";
        } catch (Exception e) {
        }
        return result;
    }

    public String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (language == null)
            return "";
        return language;
    }


    public DisplayMetrics getResolution() {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    public String getNetworkType() {

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            String type = ni.getTypeName().toLowerCase(Locale.US);
            if (!type.equals("wifi")) {
                type = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getExtraInfo();
            }
            return type;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取用户安装的app列表，不包含
     * @return 包名列表
     */
    public List<String> getInstalledApps(){
        try {
            Class class_package = Class.forName("android.content.pm.PackageParser$Package");
            class_package.getMethod("setPackageName", String.class);
        } catch (Exception e) {
            Log.e("joyce", e.getMessage());
        }
        ArrayList<String> appNameList = new ArrayList<String>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for(ApplicationInfo app : apps) {
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                appNameList.add(app.packageName);
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // 系统app
            } else {
                appNameList.add(app.packageName);
            }
        }
        return appNameList;
    }
}
