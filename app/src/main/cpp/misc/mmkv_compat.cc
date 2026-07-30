//
// MMKV Compatibility Stubs
//
// The MMKV version used in this project (cinit/MMKV fork) may not include
// the enableDisableProcessMode native method that newer MMKV Java classes expect.
// This file provides a no-op implementation to prevent UnsatisfiedLinkError.
//

#include <jni.h>

extern "C" {

JNIEXPORT void JNICALL
Java_com_tencent_mmkv_MMKV_enableDisableProcessMode(JNIEnv*, jclass, jboolean) {
    // no-op: process mode is handled by MMKV's native initialization
}

JNIEXPORT void JNICALL
Java_com_tencent_mmkv_MMKV_enableDisableProcessMode__Z(JNIEnv*, jclass, jboolean) {
    // no-op: alternative mangled name for boolean parameter
}

} // extern "C"
