package xiaolu123.testhookapi;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 获取设备各种
 * Created by jalen on 2016/11/7.
 */

public class DeviceUtil {
    /**
     * 获取IMSI标识
     * @param context 上下文
     * @return
     */
    public static String getIMSI(Context context){
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSubscriberId();

    }

    /**
     * 手机号码
     * @param context
     * @return
     */
    public static String getNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getLine1Number();
    }

    /**
     * Sim Serial Number
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSimSerialNumber();
    }

    public static String getSimCountryIso(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSimCountryIso();
    }

    public static String getSimOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSimOperator();
    }

    public static String getSimOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSimOperatorName();
    }
    public static String getNetworkCountryIso(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getNetworkCountryIso();
    }
    public static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getNetworkOperator();
    }
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getNetworkOperatorName();
    }
    public static int getPhoneType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getPhoneType();
    }
    public static int getNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getNetworkType();
    }
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
    public static int getSimState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.getSimState();
    }
    public static boolean hasIccCard(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        return telephonyManager.hasIccCard();
    }
    public static String getRadioVersion(Context context){
        // 无线固件版本
        return android.os.Build.getRadioVersion();
    }
    public static String getAndroidID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
    }
    public static Location getLocaltion(Context context){
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return lastKnownLocation;
    }
    public static GsmCellLocation getGsmLocaltion(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
        return location;
    }
    public static CdmaCellLocation getCdmaLocation(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CdmaCellLocation location = (CdmaCellLocation) tm.getCellLocation();
        return location;
    }
    public static String getBSSID(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getBSSID();
    }
    public static String getSSID(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getSSID();
    }
    public static String getBlueName(Context context) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            //退出应用
            return null;
        }
        return defaultAdapter.getName();
    }
    public static String getBlueMac(){
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            //退出应用
            return null;
        }
        return defaultAdapter.getAddress();
    }

}
