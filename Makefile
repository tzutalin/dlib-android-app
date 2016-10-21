all:
	./gradlew assembleDebug

clean:
	./gradlew clean

install:
	adb install ./app/build/outputs/apk/app-debug.apk
