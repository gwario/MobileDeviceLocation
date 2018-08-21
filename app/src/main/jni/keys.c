#include <jni.h>

JNIEXPORT jstring JNICALL
Java_at_ameise_devicelocation_service_GeolocateApiAccessService_getNativeMlsApiKey(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "dGVzdA==");
}