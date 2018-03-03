# MoonCal

[![CircleCI](https://circleci.com/gh/tomari/MoonCal.svg?style=svg)](https://circleci.com/gh/tomari/MoonCal)

An application for Android that shows calendar with moon phase.

<img src="http://davy.nyacom.net/mooncal/mooncal_2.png" alt="screenshot" />

## Supported devices
Android 3.0 and later

Verified on an Android 4.1.2 device and an Android 4.4 emulator

## Changes

Version 8

* Improve support for Android N (7.0)

Version 6

* Support Android 5.0 library behavior (DatePickerDialog)
* The new Material theme on Android 5.0

Version 5

* Supported years extended to 1923-2037 on 32-bit platforms.
* Added a dialog to pick a month/year
* Labels for day of week are localized using system settings.

Version 4

* includes binary modules for arm64-v8a, x86_64, mips64, armeabi-v7a, armeabi, x86 and mips (NDK R10 in use)
* Removed next/prev month menu items. Use gestures instead.
* A week can start on either Sunday or Monday. Use Settings to switch this.
* Use best representation of Year/Month based on locale settings (only on API>=19).

## How to compile
Requires NDK R13

* compile using Android Studio. Works with built-in NDK support that uses CMake.

## License
GPLv3. See COPYING.

