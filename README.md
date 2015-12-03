## dlib-android-app


See http://dlib.net for the main project documentation.

See [dlib-android](https://github.com/tzutalin/dlib-android) for JNI lib. Refer to dlib-android/jni/jnilib_ex


###Grap the source

`$ git clone https://github.com/tzutalin/dlib-android-app`

`$ git submodule update --init --recursive`

### Features
* Support dlib HOG detector

* Facial Landmark

### Demo
![](demo/demo1.png)
![](demo/demo2.png)

### Build jniLibs
* Command line to build

You can build shared library from [dlib-android](https://github.com/tzutalin/dlib-android)

Copy the shared libray to ./dlib/src/main/jniLibs/

* Use IDE to build

If you want to build it using Andriod studio, you need to specify your NDK path in dlib/build.gradle

`commandLine "/home/darrenl/tools/android-ndk-r10e/ndk-build"`

### Try directly
`$ adb install demo/app-debug.apk`

###License
`Copyright 2015 TzuTa Lin`
