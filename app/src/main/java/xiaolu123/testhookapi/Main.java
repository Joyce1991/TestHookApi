package xiaolu123.testhookapi;

import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.saurik.substrate.MS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class Main {
    public static final String GET_INSTALLED_APPLICATIONS = "getInstalledApplications";

    static void initialize() {
        MS.hookClassLoad("android.os.Build", new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                Method method;

                try {
                    Field cpu = resources.getField("CPU_ABI");
                    cpu.setAccessible(true);
                    cpu.set(null, "A10");

                    Field manufacturer = resources.getField("MANUFACTURER");
                    manufacturer.setAccessible(true);
                    manufacturer.set(null, "Apple");

                    Field model = resources.getField("MODEL");
                    model.setAccessible(true);
                    model.set(null, "iPhone 7");

                    method = resources.getDeclaredMethod("getString", String.class);
                } catch (Exception e) {
                    method = null;
                }

                if (method != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();

                    MS.hookMethod(resources, method, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args) throws Throwable {
                            String param = (String) args[0];
                            if (param.equals("ro.build.version.release")) {
                                return "5.0";
                            }

                            return old.invoke(resources, args);
                        }
                    }, old);
                }
            }
        });

        MS.hookClassLoad("android.view.Display", new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                Method method;

                try {
                    method = resources.getMethod("getMetrics", DisplayMetrics.class);
                } catch (Exception e) {
                    method = null;
                }

                if (method != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();

                    MS.hookMethod(resources, method, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args) throws Throwable {
                            old.invoke(resources, args);
                            DisplayMetrics outMetrics = (DisplayMetrics) args[0];
                            outMetrics.densityDpi = 400;
                            outMetrics.heightPixels = 1920;
                            outMetrics.widthPixels = 1080;
                            return null;
                        }
                    }, old);
                }
            }
        });

        MS.hookClassLoad("android.net.wifi.WifiInfo", new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                Method method;

                try {
                    method = resources.getMethod("getMacAddress");
                } catch (Exception e) {
                    method = null;
                }

                if (method != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();

                    MS.hookMethod(resources, method, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args) throws Throwable {
                            return "1234567879";
                        }
                    }, old);
                }
            }
        });

        MS.hookClassLoad("android.telephony.TelephonyManager", new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                Log.i("joyce","TelephonyManager classLoaded");
                Method method;

                try {
                    method = resources.getMethod("getDeviceId");
                } catch (Exception e) {
                    method = null;
                }

                if (method != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();

                    MS.hookMethod(resources, method, new MS.MethodHook() {
                        public Object invoked(Object resources, Object... args) throws Throwable {
                            Log.i("joyce","TelephonyManager hookMethod");
                            return "0987654321";
                        }
                    }, old);
                }
            }
        });

        MS.hookClassLoad("com.android.server.pm.PackageManagerService", new MS.ClassLoadHook(){
            @Override
            public void classLoaded(Class<?> aClass) {
                Log.i("joyce","PackageManagerService classLoaded");
                Method method = null;

                try {
                    method = aClass.getMethod("getInstalledApplications", int.class, int.class);
                } catch (Exception e) {
                    method = null;
                    Log.i("joyce","getInstalledApplications not be found, " + e.getMessage());
                }

                if (method != null){
                    Log.i("joyce","PackageManagerService getInstalledApplications be found");
                    final MS.MethodPointer old = new MS.MethodPointer();
//                    MS.hookMethod(aClass, method, null, null);

                    MS.hookMethod(aClass, method, new MS.MethodHook() {
//                        public Object invoked(Object resources, Object... args) throws Throwable {
//                            Log.i("joyce","PackageManager hookMethod");
//                            ParceledListSlice<ApplicationInfo> info = (ApplicationInfo) old.invoke(resources, args);
//                            Log.i("joyce", "packagename: " + info.packageName);
//                          if(info.packageName.equals("xiaolu123.testhookapi")){
//                              info.packageName = "com.zhang.joyce";
//                          }
//                            return info;
//                        }
                    }, old);
                }
            }
        });

//        Method getInstalledApplications;
//        try {
//            Class class_PackageManager = Class.forName("android.content.pm.PackageManager");
//            getInstalledApplications = class_PackageManager.getMethod("getInstalledApplications", new Class[]{int.class});
//            MS.hookMethod(class_PackageManager, getInstalledApplications, new MS.MethodAlteration(){
//                @Override
//                public Object invoked(Object o, Object... objects) throws Throwable {
//                    Log.i("joyce", "getInstalledApplications invoked");
//                    return null;
//                }
//            });
//        } catch (Exception e) {
//            Log.e("joyce", e.getMessage());
//        }


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

        } catch (Exception e) {}
    }
}