# GifRemoteView
[![](https://jitpack.io/v/rishab247/GifRemoteView.svg)](https://jitpack.io/#rishab247/GifRemoteView)

## what is GifRemoteView?
A Simple Android library made on top of Remoteview to show Gifs inside notifications.

## Sample

<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/690515dd-fbbc-48ea-a6e2-6acc96ad7cf8" width="240" height="500" />
<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/d2ee6867-d3b4-4dd8-8006-a53eb01eb7d9" width="240" height="500" />
<img src="https://github.com/rishab247/GifRemoteView/assets/47221639/cc3639d6-9248-4acb-86f0-5643982b0a3a" width="240" height="500" />

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
The project must have support for glide 4.11 to decode GIF files.
The size of individual Gifs should not exceed 1MB otherwise it will throw a OutOfMemoryError Try to keep it under 100KBs for better results.

## How do I use GifRemoteView?
1. Declare a ViewFlipper inside your view hierarchy to show Gif/Animations. Make sure autoStart is set to true otherwise Gif won't play

		<ViewFlipper
		android:id="@+id/frame_flipper1"
		android:layout_width="80dp"
		android:layout_height="80dp"
		android:autoStart="true" />
2. When Animation is needed in Notification RemoteView use GifRemoteview

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
   
## Additional Functionality
   - GifRemoteView Constructor Gives additional argument limitRemoteViewSize which is set to true by default limiting the max size of remoteview to 2000000 bytes. Because some OEMS limits the size of the remoteview allowed in the system tray but if your device is lower than Android 13 or has OEMs that allow default remoteview size then the size of remoteview can be increased to 5000000 bytes by the limitRemoteViewSize to false.
   - setMaxRemoteViewSize() function provides functionality to increase remoteview size beyond 5000000 bytes for the device below Android 12 but it takes a significant toll on performance and is not recommended.
   - addGif() Gives developer control over the max size a gif can take and help in further optimization of quality.
   - addGif() Gives developer control of how a Gif should be optimized using the gifOptimisationStrategy variable. There are three different types of optimization available which is applicable when a gif remoteview is consuming more memory than Allowed.
     -	OPTIMISE_LENGTH:- This will decrease the quality of frames to fit all the frames into remoteview.
     -	OPTIMISE_QUALITY:- This will decrease the number of frames to maintain the quality of all the remaining frames into remoteview.
     -	AUTOMATIC:- This will use a combination of OPTIMISE_LENGTH and OPTIMISE_QUALITY in different proportions to achieve the best result so this is set as default.
     


