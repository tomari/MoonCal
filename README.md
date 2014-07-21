# MoonCal

This is an application for Android that shows calendar with moon phase.

<img src="http://davy.nyacom.net/mooncal/mooncal_2.png" alt="screenshot" />

## Supported devices
Android 3.0 and later

Verified on an Android 4.1.2 device and an Android 4.4 emulator

## Changes

Version 4

* includes binary modules for arm64-v8a, x86_64, mips64, armeabi-v7a, armeabi, x86 and mips (NDK R10 in use)
* Removed next/prev month menu items. Use gestures instead.
* A week can start on either Sunday or Monday. Use Settings to switch this.
* Use best representation of Year/Month based on locale settings (only on API>=19).

## How to compile
Requires NDK R10.

* run ndk-build script included in the NDK in jni directory
* compile using Eclipse

## License
GPLv3. See COPYING.

