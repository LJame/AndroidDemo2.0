#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_hardrubic_activity_NdkActivity_sayHello(JNIEnv *env, jobject instance) {

    // TODO


    return (*env)->NewStringUTF(env, returnValue);
}