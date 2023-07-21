# GifRemoteView
[![](https://jitpack.io/v/rishab247/GifRemoteView.svg)](https://jitpack.io/#rishab247/GifRemoteView)
## what is GifRemoteView?
A Simple Android libaray made on top of Remoteview to show Gifs inside a notifiations.

## Installation
Step 1. Add the JitPack repository to your build file

gradle
maven
sbt
leiningen
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.rishab247:GifRemoteView:ReleaseTag'
	}

## Requirements

Android device Must be on  Android 7.1+ (API level 24).
Project must have support for glide 4.11 to decode GIF File.
Size of Indivisual Gifs should not exceed 1MB otherwise it will throw a OutOfMemoryError Try to keep it under 100KBs for better results.

## How do I use GifRemoteView?
1. Declare a ViewFlipper inside your view hierarchy to show Gif/Animations. Make sure autoStart is set to true otherwise Gif won't play

		<ViewFlipper
		android:id="@+id/frame_flipper1"
		android:layout_width="80dp"
		android:layout_height="80dp"
		android:autoStart="true" />
2. When Animation is need in Notification RemoteView use GifRemoteview

   		gifRemoteView = GifRemoteView(
                applicationContext.packageName,
                R.layout.notify_12,
                applicationContext,
                limitRemoteViewSize = true
            )

3. Call addGif() for adding gifs in remoteview.
   	
   		gifRemoteView.addGif(R.id.frame_flipper1, gifDataInBytes)
   
4. Must Call publishGifs() for Post Processing.
   	
   		gifRemoteView.publishGifs()
   
5. GifRemoteview provides following special functions.
   - addGif()
     This function is used to add Gifs inside remote view this can be called multiple times for adding multiple gifs.
     .
   - publishGifs()
     When all Gifs are added call this function for post procesing on the gif .
   - setMaxRemoteViewSize()
     
## Sample
### Single Gif
<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/690515dd-fbbc-48ea-a6e2-6acc96ad7cf8" width="240" height="500" />
<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/d2ee6867-d3b4-4dd8-8006-a53eb01eb7d9" width="240" height="500" />
<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/cc3639d6-9248-4acb-86f0-5643982b0a3a" width="240" height="500" />

