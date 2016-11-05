package xiaolu123.testhookapi;

import android.util.Log;

import com.saurik.substrate.MS;

import java.lang.reflect.Method;

/**
 * 基于Cydia Substrate框架的钩子工具类，通用写法
 * Created by jalen on 2016/11/5.
 */

public class HookTool {
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
                Log.i("joyce", clzz + " class loaded.");
                Method hookMethod = null;
                try {
                    hookMethod = aClass.getMethod(method, parameterTypes);
                } catch (Exception e) {
                    Log.i("joyce", clzz + "." + method + " not found.");
                    hookMethod = null;
                }
                if (hookMethod != null) {
                    Log.i("joyce", clzz + "." + method + " found.");
                    final MS.MethodPointer oldMethodPointer = new MS.MethodPointer();
                    MS.hookMethod(aClass, hookMethod, new MS.MethodHook(){
                        @Override
                        public Object invoked(Object o, Object... objects) throws Throwable {
                            Log.i("joyce", clzz + "." + method + " hooked.");
                            return retu;
                        }
                    }, oldMethodPointer);
                }
            }
        });
    }
}
