LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES += libandroidsupport
LOCAL_STATIC_JAVA_LIBRARIES += libconcurrent
LOCAL_STATIC_JAVA_LIBRARIES += libcodec
LOCAL_STATIC_JAVA_LIBRARIES += liblang
LOCAL_STATIC_JAVA_LIBRARIES += liblogging
LOCAL_STATIC_JAVA_LIBRARIES += libical4j

LOCAL_MODULE_TAGS := eng

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := LocalCalendar

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libandroidsupport:libs/android-support-v4.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libconcurrent:libs/backport-util-concurrent-3.1.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libcodec:libs/commons-codec-1.5.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += liblang:libs/commons-lang-2.6.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += liblogging:libs/commons-logging-1.1.1.jar
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += libical4j:libs/ical4j-1.0.4.jar

include $(BUILD_MULTI_PREBUILT)


# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH)) 
