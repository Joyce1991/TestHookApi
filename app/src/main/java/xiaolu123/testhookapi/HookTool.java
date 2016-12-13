package xiaolu123.testhookapi;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.google.gson.Gson;
import com.saurik.substrate.MS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 基于Cydia Substrate框架的钩子工具类，通用写法
 * Created by jalen on 2016/11/5.
 */

public class HookTool {
    private static DeviceInfo deviceInfo;
    /**
     * 修改类clzz的方法method的返回值为retu <br/>
     * 只适合不需要条件判断的hook
     * @param clzz 类全路径
     * @param method 方法名称
     * @param retu 返回值
     * @param <T> 返回值类型
     * @param parameterTypes 方法method的参数
     */
    public static <T> void simpleHookMethod(final String clzz, final String method, final T retu, final Class... parameterTypes){
        MS.hookClassLoad(clzz, new MS.ClassLoadHook(){
            @Override
            public void classLoaded(Class<?> aClass) {
                if (deviceInfo == null){
                    deviceInfo = new Gson().fromJson(Main.testJNI(), DeviceInfo.class);
                }
                Method hookMethod;
                try {
                    hookMethod = aClass.getMethod(method, parameterTypes);
                } catch (Exception e) {
                    Log.i("joyce_method", clzz + "." + method + " not found.");
                    hookMethod = null;
                }
                if (hookMethod != null) {
                    final MS.MethodPointer oldMethodPointer = new MS.MethodPointer();
                    MS.hookMethod(aClass, hookMethod, new MS.MethodHook(){
                        @Override
                        public Object invoked(Object o, Object... objects) throws Throwable {
                            try {
                                Class<?> aClass1 = Class.forName(DeviceInfo.class.getName());
                                Method method = aClass1.getMethod((String) retu);
                                Object invoke = method.invoke(deviceInfo);
                                Log.i("joyce_method", clzz + "." + method + " hooked." + "return is -> " + invoke.toString());
                                return invoke;
                            }catch (Throwable e){
                                Log.i("joyce_method", e.getMessage());
                            }

                            return null;
                        }
                    }, oldMethodPointer);
                }else {
                    Log.i("joyce_method", clzz + "." + method + " not found.");
                }
            }
        });
    }

    /**
     * 修改类clzz的方法method的返回 <br/>
     * 带回调的方式
     * @param clzz 类全路径
     * @param method 方法名称
     * @param hookCallback callback
     * @param parameterTypes  方法method的参数
     * @param <T>
     * @param <R>
     */
    public static<T, R> void hookMethosCallBack(final String clzz, final String method, final MS.MethodHook<T, R> hookCallback, final Class... parameterTypes){
        MS.hookClassLoad(clzz, new MS.ClassLoadHook(){
            @Override
            public void classLoaded(Class<?> aClass) {
                Method hookMethod;
                try {
                    hookMethod = aClass.getMethod(method, parameterTypes);
                } catch (Exception e) {
                    Log.i("joyce_method", clzz + "." + method + " not found.");
                    hookMethod = null;
                }
                if (hookMethod != null){
                    Log.i("joyce_method", clzz + "." + method + " found.");
                    final MS.MethodPointer oldMethodPointer = new MS.MethodPointer();
                    MS.hookMethod(aClass, hookMethod, hookCallback, oldMethodPointer);
                }
            }
        });
    }

    /**
     * 修改clzz的field的值为value     <br/>
     * @param clzz 类全路径
     * @param fieldName 全局变量名称
     * @param value 要设置的值
     * @param <T> field的数据类型
     */
    public static <T> void simpleHookField(final String clzz, final String fieldName, final T value){
        MS.hookClassLoad(clzz, new MS.ClassLoadHook(){
            @Override
            public void classLoaded(Class<?> aClass) {
                if (deviceInfo == null){
                    deviceInfo = new Gson().fromJson(Main.testJNI(), DeviceInfo.class);
                }
                try {
                    Field field;
                    try {
                        field = aClass.getField(fieldName);
                    } catch (Exception e) {
                        Log.e("joyce_field","field not found -> " + fieldName);
                        field = null;
                    }
                    if (field != null){
                        Class<?> aClass1 = Class.forName(DeviceInfo.class.getName());
                        Method method = aClass1.getMethod((String) value);
                        Object invoke = method.invoke(deviceInfo);
                        field.setAccessible(true);
                        try {
                            field.set(null, invoke);
                            Log.i("joyce_field","modify field -> " + fieldName + ", value -> " + invoke);
                        } catch (IllegalAccessException e) {
                            Log.e("joyce_field","access illegal -> " + fieldName + ", exception:" + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.e("joyce_field", "exception:" + e.getMessage());
                }
            }
        });
    }

    public static void hookDisplay() {
        MS.hookClassLoad(Display.class.getName(), new MS.ClassLoadHook() {
            public void classLoaded(Class<?> resources) {
                if (deviceInfo == null){
                    deviceInfo = new Gson().fromJson(Main.testJNI(), DeviceInfo.class);
                }
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
    }

    public static void hookRootOnJava() {
        MS.hookClassLoad("java.io.File", new MS.ClassLoadHook() {
            @Override
            public void classLoaded(Class<?> aClass) {
                Log.i("joyce", "File cloadLoaded");
                Method method;
                try {
                    method = aClass.getMethod("exists");
                } catch (Exception e) {
                    method = null;
                    Log.e("joyce", e.getMessage());
                }
                if (method != null) {
                    Log.i("joyce", "File exists methodLoaded");
                    final MS.MethodPointer old = new MS.MethodPointer();
                    MS.hookMethod(aClass, method, new MS.MethodHook() {
                        @Override
                        public Object invoked(Object o, Object... objects) throws Throwable {
                            String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
                                    "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
                            File file = (File) o;
                            for (String path : paths) {
                                if (path.equals(file.getPath())) {
                                    return false;
                                }
                            }
                            return old.invoke(o, objects);
                        }
                    }, old);
                }
            }
        });
    }
}
