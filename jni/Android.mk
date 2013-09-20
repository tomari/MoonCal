LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := moonphase
LOCAL_SRC_FILES := jniif.c fillcal.c phases.c
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -Wall -g
include $(BUILD_SHARED_LIBRARY)
