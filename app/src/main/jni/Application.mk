APP_ABI:=armeabi x86
APP_PLATFORM:=android-14
APP_STL:=gnustl_static
APP_CFLAGS:=-DSWIG=1 -fexceptions -DLIQUIDFUN_EXTERNAL_LANGUAGE_API
APP_PROJECT_PATH := $(abspath $(call my-dir)/..)
$(info $(APP_PROJECT_PATH))
LIQUIDFUN_SRC_PATH:=$(APP_PROJECT_PATH)/../../../liquidfun-1.1.0/liquidfun/Box2D
NDK_PROJECT_PATH:=$(APP_PROJECT_PATH)/../../../android-ndk-r10e
$(info $(NDK_PROJECT_PATH))
NDK_MODULE_PATH+=$(abspath $(NDK_PROJECT_PATH))
SWIG_BIN:=/usr/local/bin/swig

$(info "Hello Application")
