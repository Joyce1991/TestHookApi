#include <android/log.h>
#include <substrate.h>

#define LOG_TAG "joyce_native"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 1. http://www.cnblogs.com/baizx/p/4254359.html
 2. http://blog.csdn.net/jianwei824/article/details/6091387
 3. http://blog.csdn.net/hu3167343/article/details/50792153
 4. https://github.com/myCodeHurts/Plum---Android-Native-Cydia-Substrate
 5. http://www.cydiasubstrate.com/inject/android/
 6. http://bbs.pediy.com/showthread.php?t=213043&highlight=substrate
 7. https://github.com/ele7enxxh/Android-Inline-Hook
 8. http://androidxref.com/6.0.1_r10/xref/bionic/libc/bionic/open.cpp
**/

// The name (last path component) of a library the developer is attempting to hook. As an example, to hook __android_log, specify "liblog.so".
MSConfig(MSFilterLibrary, "libc.so")

int (*open)(const char*, int, ...); // 保留原来的地址（就是MSHookFunction调用后会把原来的地址保存在了这里）
int hookopen(const char* pathname, int flags, ...){
    LOGI("method hooked");
    char *su_1 = "/system/app/Superuser.apk";
    char *su_2 = "/sbin/su";
    char *su_3 = "/system/bin/su";
    char *su_4 = "/system/xbin/su";
    char *su_5 = "/data/local/xbin/su";
    char *su_6 = "/data/local/bin/su";
    char *su_7 = "/system/sd/xbin/su";
    char *su_8 = "/system/bin/failsafe/su";
    char *su_9 = "/data/local/su";
    char *su_0 = "/su/bin/su";
    if (strcasecmp(pathname, su_0) || strcasecmp(pathname, su_1) || strcasecmp(pathname, su_2) || strcasecmp(pathname, su_3)
            || strcasecmp(pathname, su_4) || strcasecmp(pathname, su_5) || strcasecmp(pathname, su_6)
            || strcasecmp(pathname, su_7) || strcasecmp(pathname, su_8) || strcasecmp(pathname, su_9)){
        LOGI("match condition, return -1");
        return -1;
    }
    return open(pathname,flags);
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
            LOGI("find open from libc.so");
            MSHookFunction(openload, (void*)&hookopen,(void**)&open);
        }
    } else{
        LOGE("error find libc.so");
    }
}


