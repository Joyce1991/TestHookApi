#include <android/log.h>
#include "substrate.h"

#define LOG_TAG "joyce_native"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

MSConfig(MSFilterLibrary, "/system/lib/libc.so")

static jint (*_Resources$getColor)(JNIEnv *jni, jobject _this, ...);

static jint $Resources$getColor(JNIEnv *jni, jobject _this, jint rid) {
    jint color = _Resources$getColor(jni, _this, rid);
    return color & ~0x0000ff00 | 0x00ff0000;
}

int (*open)(const char*, int, ...); // 保留原来的地址（就是MSHookFunction调用后会把原来的地址保存在了这里）
int hookopen(const char* pathname, int flags, ...){
    char* su_1 = "/system/app/Superuser.apk";
    char* su_2 = "/sbin/su";
    char* su_3 = "/system/bin/su";
    char* su_4 = "/system/xbin/su";
    char* su_5 = "/data/local/xbin/su";
    char* su_6 = "/data/local/bin/su";
    char* su_7 = "/system/sd/xbin/su";
    char* su_8 = "/system/bin/failsafe/su";
    char* su_9 = "/data/local/su";
    char* su_0 = "/su/bin/su";
    if (strcasecmp(pathname, su_0)){
        return -1;
    }
    return open(pathname,flags);
}

static void OnResources(JNIEnv *jni, jclass resources, void *data) {
    jmethodID method = jni->GetMethodID(resources, "getColor", "(I)I");
    if (method != NULL)
        MSJavaHookMethod(jni, resources, method,
            &$Resources$getColor, &_Resources$getColor);
}
MSInitialize {
    LOGI("Substrate initialized.");
    MSImageRef image;
    image = MSGetImageByName("/system/lib/libc.so"); //载入lib
    if (image != NULL){
        //注意这个是个c++函数，可以通过objdump来获取
        void * openload=MSFindSymbol(image,"open");
        if(openload==NULL){
            LOGE("error find open ");
        } else{
            MSHookFunction(openload, (void*)&hookopen,(void**)&open);
        }
    } else{
        LOGE("error find libc.so");
    }
}


