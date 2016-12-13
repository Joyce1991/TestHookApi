#include <jni.h>
#include <stdio.h>
#include <malloc.h>
#include <android/log.h>

#define LOG_TAG "joyce_native"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_xiaolu123_testhookapi_Main_testJNI(JNIEnv *env, jclass type) {

    FILE *fp;
    fp = fopen("/data/local/tmp/deviceinfo.json", "rb");
    fseek(fp,0L,SEEK_END); // 定位到文件末尾
    long flen = ftell(fp);  // 得到文件大小
    char *str = (char*)malloc(flen + 1);    // 根据文件大小动态分配内存空间
    // 判断文件读取是否正常
    if(str==NULL){
        fclose(fp);
        LOGE("read file failed");
        return 0;
    }
    fseek(fp, 0L, SEEK_SET);    // 定位到文件开头
    fread(str, flen, 1, fp);    // 读取所有内容
    str[flen] = 0;  // 字符串结束标志符
    fclose(fp); // 关闭文件柄

    return (*env)->NewStringUTF(env, str);
}

