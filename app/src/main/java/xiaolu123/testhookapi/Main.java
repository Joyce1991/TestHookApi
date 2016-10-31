package xiaolu123.testhookapi;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.saurik.substrate.MS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Main {
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
                            return "0987654321";
                        }
                    }, old);
                }
            }
        });

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