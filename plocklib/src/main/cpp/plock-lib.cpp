//
// Created by pqpo on 2018/4/28.
//
#include <jni.h>
#include "includes/PLock.h"

static const char* const kClassDocScanner = "me/pqpo/plocklib/PLock";

static jlong initPLockNative(JNIEnv *env, jobject instance, jstring file_) {
    const char *file = env->GetStringUTFChars(file_, 0);
    PLock* pLock = new PLock(const_cast<char*>(file));
    env->ReleaseStringUTFChars(file_, file);
    return reinterpret_cast<long>(pLock);
}

static jboolean readLockNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    return (jboolean) pLock->readLock();
}

static jboolean writeLockNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    return (jboolean) pLock->writeLock();
}

static jboolean tryReadLockNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    return (jboolean) pLock->tryReadLock();
}

static jboolean tryWriteLockNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    return (jboolean) pLock->tryWriteLock();
}

static jboolean unlockNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    return (jboolean) pLock->unlock();
}

static void releaseNative(JNIEnv *env, jobject instance, jlong ptr_) {
    PLock* pLock = reinterpret_cast<PLock*>(ptr_);
    delete pLock;
}

static JNINativeMethod gMethods[] = {

        {
                "initPLockNative",
                "(Ljava/lang/String;)J",
                (void*)initPLockNative
        },

        {
                "readLockNative",
                "(J)Z",
                (void*)readLockNative
        },

        {
                "writeLockNative",
                "(J)Z",
                (void*)writeLockNative
        },

        {
                "tryReadLockNative",
                "(J)Z",
                (void*)tryReadLockNative
        },

        {
                "tryWriteLockNative",
                "(J)Z",
                (void*)tryWriteLockNative
        },

        {
                "unlockNative",
                "(J)Z",
                (void*)unlockNative
        },

        {
                "releaseNative",
                "(J)V",
                (void*)releaseNative
        },

};

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_FALSE;
    }
    jclass classDocScanner = env->FindClass(kClassDocScanner);
    if(env -> RegisterNatives(classDocScanner, gMethods, sizeof(gMethods)/ sizeof(gMethods[0])) < 0) {
        return JNI_FALSE;
    }
    return JNI_VERSION_1_4;
}