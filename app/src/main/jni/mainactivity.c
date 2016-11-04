#include <jni.h>
#include <stdlib.h>
#include <stdio.h>

#define JNI_FALSE 0
#define JNI_TRUE 1

JNIEXPORT jboolean JNICALL
Java_xiaolu123_testhookapi_MainActivity_isRootByNative(JNIEnv *env, jobject instance) {

    // TODO

    FILE *fp;
    fp = fopen("/system/app/Superuser.apk", "r");
    if (fp != NULL){
        return JNI_TRUE;
    }

    FILE *fsu;
    fsu = fopen("/system/xbin/su", "r");
    if (fsu != NULL){
        return JNI_TRUE;
    }

    return JNI_FALSE;
}

