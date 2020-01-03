#include <jni.h>
#include <sys/types.h>

#include "clipmgr.h"

#define RIRUCALL __attribute__((visibility("default")))

extern "C" {
RIRUCALL
void nativeForkAndSpecializePre(
        JNIEnv *env, jclass clazz, jint *_uid, jint *gid, jintArray *gids, jint *runtime_flags,
        jobjectArray *rlimits, jint *_mount_external, jstring *se_info, jstring *se_name,
        jintArray *fdsToClose, jintArray *fdsToIgnore, jboolean *is_child_zygote,
        jstring *instructionSet, jstring *appDataDir, jstring *packageName,
        jobjectArray *packagesForUID, jstring *sandboxId) {
    // packageName, packagesForUID, sandboxId added from Android Q beta 2, removed from beta 5
    clipmgr::onPrepareForkAndSpecialize(env, *appDataDir);
}

RIRUCALL
int nativeForkAndSpecializePost(JNIEnv *env, jclass clazz, jint res) {
    // if (res == 0) {
    //     // in app process
    // } else {
    //     // in zygote process, res is child pid
    //     // don't print log here, see https://github.com/RikkaApps/Riru/blob/77adfd6a4a6a81bfd20569c910bc4854f2f84f5e/riru-core/jni/main/jni_native_method.cpp#L55-L66
    // }
    return res == 0 ? clipmgr::onFinishForkAndSpecialize(env) : 0;
}

RIRUCALL
void nativeForkSystemServerPre(
        JNIEnv *env, jclass clazz, uid_t *uid, gid_t *gid, jintArray *gids, jint *debug_flags,
        jobjectArray *rlimits, jlong *permittedCapabilities, jlong *effectiveCapabilities) {
    clipmgr::onPrepareForkSystemServer(env);
}

RIRUCALL
int nativeForkSystemServerPost(JNIEnv *env, jclass clazz, jint res) {
//    if (res == 0) {
//        // in system server process
//    } else {
//        // in zygote process, res is child pid
//        // don't print log here, see https://github.com/RikkaApps/Riru/blob/77adfd6a4a6a81bfd20569c910bc4854f2f84f5e/riru-core/jni/main/jni_native_method.cpp#L55-L66
//    }
    return res == 0 ? clipmgr::onFinishForkSystemServer(env) : 0;
}

RIRUCALL
void specializeAppProcessPre(
        JNIEnv *env, jclass clazz, jint *_uid, jint *gid, jintArray *gids, jint *runtimeFlags,
        jobjectArray *rlimits, jint *mountExternal, jstring *seInfo, jstring *niceName,
        jboolean *startChildZygote, jstring *instructionSet, jstring *appDataDir,
        jstring *packageName, jobjectArray *packagesForUID, jstring *sandboxId) {
    // used by Android Q beta 3
    // a process in the "process pool", after specializeAppProcess is called, this process become a app process

    // packageName, packagesForUID, sandboxId existed from Android Q beta 2, removed from beta 5
    clipmgr::onPrepareForkAndSpecialize(env, *appDataDir);
}

RIRUCALL
int specializeAppProcessPost(
        JNIEnv *env, jclass clazz) {
    // used by Android Q beta 3
    // become an app process
    return clipmgr::onFinishForkAndSpecialize(env);
}
}
