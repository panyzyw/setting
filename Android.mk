LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := YYDRobotSettings
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_STATIC_JAVA_LIBRARIES:= android-support-v4 android-support-v7-recyclerview settingslib2 settingslib3
LOCAL_DEX_PREOPT := false
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_JNI_SHARED_LIBRARIES += libBreathLed
LOCAL_MULTILIB := 32
LOCAL_CERTIFICATE := platform

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	settingslib2:libs/core-3.0.0.jar \
	settingslib3:libs/my_gson-2.3.1.jar
	
include $(BUILD_MULTI_PREBUILT)
