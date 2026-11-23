# 2048

Classic 2048 game in Kotlin for Android. Swipe to slide the tiles, merge
equal numbers, and reach the 2048 tile.

## Features

- 4x4 grid with the classic 2048 look and tile colours
- Swipe controls
- Score and best score (best score is saved on the device)
- New Game button
- Win message at 2048 — and you can keep playing for a higher score
- Game-over detection, and the game survives screen rotation

## Build

Open the project in Android Studio (JDK 17, Android SDK 34) and run it, or
build a debug APK from the command line:

```sh
./gradlew assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`. Sideload it
onto your phone by tapping the file and allowing install from that source
once.

## Notes

- Package: `com.charles.game2048`, version 1.0, minSdk 24, targetSdk 34.
- The APK itself is not stored in this repo (it is a binary); release APKs
  belong on a GitHub Release.
