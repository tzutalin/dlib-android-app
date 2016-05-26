## dlib-android-app

[![Build Status](https://travis-ci.org/tzutalin/dlib-android-app.png)](https://travis-ci.org/tzutalin/dlib-android-app)

See http://dlib.net for the main project documentation.

See [dlib-android](https://github.com/tzutalin/dlib-android) for JNI lib. Refer to dlib-android/jni/jnilib_ex

###Grap the source

`$ git clone https://github.com/tzutalin/dlib-android-app.git`

### Features

* Support HOG detector

* HOG Face detection

* Facial Landmark/Expression

### Demo
![](demo/demo1.png)
![](demo/demo2.png)
![](demo/demo3.png)

[![Demo video](https://gifs.com/gif/n5P3GD)](https://www.youtube.com/watch?v=5mqVzKdexzw&feature=youtu.be)

### Build

#### Android app
* Open Android studio to build

* Use command line to build (Optional)

On Windows platforms, type this command:

`$ gradlew.bat assembleDebug`

On Mac OS and Linux platforms, type these commands:

`$ chmod +x gradlew`

`$ ./gradlew assembleDebug`

#### Update shared lib (Optional)
You can build shared library from [dlib-android](https://github.com/tzutalin/dlib-android)

Copy the shared libray to ./dlib/src/main/jniLibs/

### Try directly

`$ adb install demo/app-debug.apk`

or 

Download and import dlib-debug.aar to AndroidStudio

###License
`Copyright 2015 TzuTa Lin`
