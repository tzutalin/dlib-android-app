## dlib-android-app

[![Build Status](https://travis-ci.org/tzutalin/dlib-android-app.png)](https://travis-ci.org/tzutalin/dlib-android-app)
[ ![Download](https://api.bintray.com/packages/tzutalin/maven/dlib-android-app/images/download.svg) ](https://bintray.com/tzutalin/maven/dlib-android-app/_latestVersion)

See http://dlib.net for the main project documentation.

See [dlib-android](https://github.com/tzutalin/dlib-android) for JNI lib. Refer to dlib-android/jni/jnilib_ex

### Grap the source

`$ git clone https://github.com/tzutalin/dlib-android-app.git`

### Features

* Support HOG detector

* HOG Face detection

* Facial Landmark/Expression

### Demo
![](demo/demo1.png)
![](demo/demo2.png)
![](demo/demo3.png)

[![Demo video](https://j.gifs.com/82n7Oo.gif)](https://www.youtube.com/watch?v=TbX3t7QNhvs)

### Build

#### Android app
* Open Android studio to build

* Use command line to build (Optional)

On Windows platforms, type this command:

`$ gradlew.bat assembleDebug`

On Mac OS and Linux platforms, type these commands:

```
$ ./gradlew assembleDebug

or

$ make ; make install

```

#### Update shared lib (Optional)
You can build shared library from [dlib-android](https://github.com/tzutalin/dlib-android)

Copy the shared libray to ./dlib/src/main/jniLibs/

### Try directly

Install the apk

`$ adb install demo/app-debug.apk`

Otherwise, import the library to your build.gradle

```
repositories {
    maven {
        url 'https://dl.bintray.com/tzutalin/maven'
    }
}

dependencies {
    compile 'com.tzutalin.dlib-android-app:dlib:1.0.4'
}

```

### Sample code

Facial landmark detection
```java
FaceDet faceDet = new FaceDet(Constants.getFaceShapeModelPath());
Bitmap bitmap = BitmapFactory.decodeFile("Image Path");
List<VisionDetRet> results = faceDet.detect(bitmap);
for (final VisionDetRet ret : results) {
    String label = ret.getLabel();
    int rectLeft = ret.getLeft();
    int rectTop = ret.getTop();
    int rectRight = ret.getRight();
    int rectBottom = ret.getBottom();
    // Get 68 landmark points
    ArrayList<Point> landmarks = ret.getFaceLandmarks();
    for (Point point : landmarks) {
        int pointX = point.x;
        int pointY = point.y;
    }
}
```

Pedestrian detection

```java
Pedestrian pedestrianDet = new PedestrianDet();
List<VisionDetRet> personList = pedestrianDet.detect(imgPath);
```


### License
[License](LICENSE.md)
