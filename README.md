# Twittasteroid
Android test for farandwide.pl

Build ready to deploy on your device available here: https://github.com/vgarshyn/Twittasteroid/blob/master/sample_build/twittasteroid_build.apk

![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-11-43-27.png "Screen 1")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-11-56-37.png "Screen 2")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-11-57-16.png "Screen 3")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-11-57-27.png "Screen 4")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-12-03-13.png "Screen 5")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-11-43-10.png "Screen 6")
![Alt text](https://github.com/vgarshyn/Twittasteroid/blob/master/readme_imgs/Screenshot_2015-07-31-12-03-24.png "Screen 7")





Build notices:

1) Developed with Android Studio 1.2.2

2) Gradle config: 

    compileSdkVerision 22
  
    buildToolsVersion 22.0.1
  
    targetSdkVersion 22

  
3) You must use provided default keystore/custom.keystore for sign yours debug builds.

Now gradle config can do it automatically and you shouldn't care about that.

But this information might be useful:

Its required because app use google services (Youtube and Maps) and depends on public keys.
If you will use your own keystore, such funcionality as show maps and play youtube video will be unavailable.
More information: https://developers.google.com/maps/documentation/android/signup

      Keystore name: "custom.keystore"
      
      Keystore password: "android"
      
      Key alias: "androidgoogleapikey"
      
      Key password: "android"

4) Twitter api is not returning video entities for now, so it's functionality is anavailable in this app.

5) Device or emulator what you will use to test must have installed google play services, for playing youtube video and showing maps location.

6) Used 3rd part libraries:

- Official android google libraries

    YouTubeAndroidPlayerApi.jar: https://developers.google.com/youtube/android/player/
    
    com.android.support:appcompat-v7:22.2.+
    
    com.google.android.gms:play-services:7.5.+
    
    com.android.support:recyclerview-v7:+
    
    com.android.support:cardview-v7:21.0.+
    

- Official twitter kit: https://fabric.io/kits/android/twitterkit/summary

- Picasso Image loader: http://square.github.io/picasso/

- Photo View:  https://github.com/chrisbanes/PhotoView

- Dir Chooser: https://github.com/passy/Android-DirectoryChooser

7) Tested on devices:
      Samsung Galaxy Nexus
      
      LG Nexus 5
      
      Samsung S4
      
      Asus MemoPad 7
      
      Asus Nexus 7
      
      Virtual device GenyMotion: https://www.genymotion.com/#!/
      

P.S.> If you have any questions you can always ask me by mail: v.garshyn@gmail.com or skype: blackstream5
