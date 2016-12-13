package xiaolu123.testhookapi;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.saurik.substrate.MS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static native String testJNI();
    public static final String GET_INSTALLED_APPLICATIONS = "getInstalledApplications";

    static void initialize() {
        System.load("/data/app-lib/xiaolu123.testhookapi-1/libtest-hook.cy.so");

        HookTool.hookRootOnJava();
        HookTool.hookDisplay();

        // 修改API等级
        HookTool.simpleHookField(Build.VERSION.class.getName(), "SDK_INT", "getApiLevel"); // （会导致多dex的app崩溃，因为MultiDex是从Android5.0才开始支持的）
        // 修改系统版本号
        HookTool.simpleHookField(Build.VERSION.class.getName(), "RELEASE", "getVersion");
        // 指纹
        HookTool.simpleHookField(Build.class.getName(), "FINGERPRINT", "getFingerprint");
        // display的编译版本号
        HookTool.simpleHookField(Build.class.getName(), "DISPLAY", "getDisplay");
        HookTool.simpleHookField(Build.class.getName(), "PRODUCT", "getProduct");
        HookTool.simpleHookField(Build.class.getName(), "ID", "getId");
        HookTool.simpleHookField(Build.class.getName(), "DEVICE", "getDevice");
        HookTool.simpleHookField(Build.class.getName(), "SERIAL", "getSerial");
        HookTool.simpleHookField(Build.class.getName(), "BOARD", "getBoard");
        HookTool.simpleHookField(Build.class.getName(), "BRAND", "getBrand");
        HookTool.simpleHookField(Build.class.getName(), "MANUFACTURER", "getManufacturer");
        HookTool.simpleHookField(Build.class.getName(), "MODEL", "getModel");




        HookTool.simpleHookMethod(WifiInfo.class.getName(), "getMacAddress", "getWifiMac");   // 这个值不能够随便设置，会导致不能够链接wifi
        // imei
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getDeviceId", "getImei");
        HookTool.simpleHookMethod("com.android.internal.telephony.PhoneSubInfo", "getDeviceId", "getImei");
        // imsi
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSubscriberId", "getImsi");
        // number
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getLine1Number", "getPhoneNumber");
        // Sim Serial Number
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSimSerialNumber", "getSimSerialNumber");
        // Sim Country Iso（国家标识）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSimCountryIso", "getSimCountryIso");
        // Sim Operator（国家代码+网络代码）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSimOperator", "getSimOperator");
        // Sim Operator Name（运营商名称）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSimOperatorName", "getSimOperatorName");
        // Network Country Iso（国家标识）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getNetworkCountryIso", "getNetCountryIso");
        // Network Operator（国家代码+网络代码）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getNetworkOperator", "getNetOperator");
        // Network Operator Name（运营商名称）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getNetworkOperatorName", "getNetOperatorName");

        /*
        // Phone Type（PHONE_TYPE_NONE、PHONE_TYPE_GSM、PHONE_TYPE_CDMA、PHONE_TYPE_SIP）这应该是指手机制式
//        HookTool.simpleHookMethod("android.telephony.TelephonyManager", "getPhoneType", 1);
        // Network Type（当前网络类型 13是指LTE）
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getNetworkType", info.getNetType());
        // Sim state （sim卡状态: SIM_STATE_UNKNOWN、 SIM_STATE_ABSENT、SIM_STATE_PIN_REQUIRED、SIM_STATE_PUK_REQUIRED
        //   、SIM_STATE_NETWORK_LOCKED、SIM_STATE_READY、ERROR(/#SIM_STATE_NOT_READY)、ERROR(/#SIM_STATE_PERM_DISABLED)
        //   、ERROR(/#SIM_STATE_CARD_IO_ERROR)） SIM_STATE_READY=5
        HookTool.simpleHookMethod(TelephonyManager.class.getName(), "getSimState", info.getSimState());
        // 是否有sim卡 hasIccCard（true or false）
//        HookTool.simpleHookMethod("android.telephony.TelephonyManager", "hasIccCard", true);
        // radio version （无线固件版本）
        HookTool.simpleHookMethod(Build.class.getName(), "getRadioVersion", info.getRadioVersion());
        // android id
        HookTool.simpleHookMethod("Settings.Secure", "getString", info.getAndroidId(), ContentResolver.class, String.class);*/


//        HookTool.simpleHookMethod(Locale.class.getName(), "getLanguage", "zh"); // 这个还需要验证（改动这个值出现系统不断闪屏现象）
//        // 经度
//        HookTool.simpleHookMethod(Location.class.getName(), "getLatitude", "jingdu");
//        // 纬度
//        HookTool.simpleHookMethod(Location.class.getName(), "getLongitude", "weidu");
//        // gsm lac（基站的区域代码）
//        HookTool.simpleHookMethod(GsmCellLocation.class.getName(), "getLac", "ji_zhan_area_code");
//        // gsm cell id（基站的唯一标识id）
//        HookTool.simpleHookMethod(GsmCellLocation.class.getName(), "getCid", "ji_zhan_cell_id");
//        // cdma 经度
//        HookTool.simpleHookMethod(CdmaCellLocation.class.getName(), "getBaseStationLatitude", "cdma_latitude");
//        // cdma 纬度
//        HookTool.simpleHookMethod(CdmaCellLocation.class.getName(), "getBaseStationLongitude", "cdma_longitude");
//        // cdma 基站识别码 getBaseStationId
//        HookTool.simpleHookMethod(CdmaCellLocation.class.getName(), "getBaseStationId", "cdma_station_id");
//        // cdma 网络代码 getNetworkId
//        HookTool.simpleHookMethod(CdmaCellLocation.class.getName(), "getNetworkId", "cdma_network_id");

        // ***********************************这一块的网络信息也要相对应，不能够连着wifi网络hook信息却是MOBILE， 会导致手机上不了网的**********
//        HookTool.simpleHookMethod(NetworkInfo.class.getName(), "getType", ConnectivityManager.TYPE_MOBILE);
//        HookTool.simpleHookMethod(NetworkInfo.class.getName(), "getTypeName", "MOBILE");
//        HookTool.simpleHookMethod(NetworkInfo.class.getName(), "getSubtype", TelephonyManager.NETWORK_TYPE_LTE);
//        HookTool.simpleHookMethod(NetworkInfo.class.getName(), "getSubtypeName ", "联通LTE");
//        HookTool.simpleHookMethod(NetworkInfo.class.getName(), "getExtraInfo ", "uninet");
        // ************************************************************************************************************************************
/*



        MS.hookClassLoad("android.content.pm.PackageParser", new MS.ClassLoadHook() {
            @Override
            public void classLoaded(Class<?> aClass) {
                Log.i("joyce", "PackageParser classLoaded");
                Method method = null;
                try {
                    Class class_package = Class.forName("android.content.pm.PackageParser$Package");
                    Class class_PackageUserState = Class.forName("android.content.pm.PackageUserState");
                    method = aClass.getMethod("generateApplicationInfo", class_package, int.class, class_PackageUserState, int.class);
                } catch (Exception e) {
                    method = null;
                    Log.i("joyce", "generateApplicationInfo not be found, " + e.getMessage());
                }
                if (method != null) {
                    Log.i("joyce", "PackageManager generateApplicationInfo be found");
                    final MS.MethodPointer old = new MS.MethodPointer();
                    MS.hookMethod(aClass, method, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args) throws Throwable {
                            Log.i("joyce", "PackageManager hookMethod");
                            ApplicationInfo info = (ApplicationInfo) old.invoke(resources, args);
                            Log.i("joyce", "packagename: " + info.packageName);
                            if (info.packageName.equals("com.jojo.readtopactivity")) {
                                info.packageName = "com.zhang.joyce";
                            }
                            return info;
                        }
                    }, old);
                }
            }
        });
*/


/*
        Method subType;
        Method subtypeName;
        Method extraInfo;
        Method typeName;
        try {
            Class _class = Class.forName("android.net.NetworkInfo");

            subType = _class.getMethod("getSubtype");
            subtypeName = _class.getMethod("getSubtypeName");
            extraInfo = _class.getMethod("getExtraInfo");
            typeName = _class.getMethod("getTypeName");

            MS.hookMethod(_class, subType, new MS.MethodAlteration() {
                public Object invoked(Object _this, Object... args) throws Throwable {
                    return TelephonyManager.NETWORK_TYPE_CDMA;
                }
            });

            MS.hookMethod(_class, subtypeName, new MS.MethodAlteration() {
                public Object invoked(Object _this, Object... args) throws Throwable {
                    return "电信2g";
                }
            });

            MS.hookMethod(_class, extraInfo, new MS.MethodAlteration() {
                public Object invoked(Object _this, Object... args) throws Throwable {
                    return "电信2g";
                }
            });

            MS.hookMethod(_class, typeName, new MS.MethodAlteration() {
                public Object invoked(Object _this, Object... args) throws Throwable {
                    return "MOBILE"; // "WIFI" or "MOBILE"
                }
            });


            /////////////
            Class conClass = Class.forName("android.net.ConnectivityManager");
            Method networkInfo = conClass.getMethod("getNetworkInfo", Integer.TYPE);
            MS.hookMethod(_class, networkInfo, new MS.MethodAlteration() {
                public Object invoked(Object _this, Object... args) throws Throwable {
                    return invoke(_this, ConnectivityManager.TYPE_WIFI);
                }
            });

        } catch (Exception e) {
        }
*/
    }
}