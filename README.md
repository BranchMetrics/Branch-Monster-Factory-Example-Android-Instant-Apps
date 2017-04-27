Branchster Android (Instant App Enabled)
==================
## Introduction

This is a replica of [Branch Monster Factory](https://github.com/BranchMetrics/Branch-Example-Deep-Linking-Branchster-Android) project with Android Instant App support. This shows how easy it is to add Branch SDK to your Instant App supported Android app

### Integrate Branch SDK

1. Create your application class (in your main Library project) and add the below code in your Application class's onCreate() method
```
    public void onCreate() {
            super.onCreate();
            Branch.getAutoInstance(this);
    }
```
2. Add your application class in the manifest where you add `Application` element. This can be in your `base-lib/ manifest.xml` or `main-application/manifest.xml` depending on your implementation
 ```
    <application
            android:allowBackup="true"
            android:label="@string/app_name"
            android:theme="@style/AppTheme"
            android:supportsRtl="true"
            android:name=".MyApplication">
 ```
3. Add your Branch keys to the the manifest where the application element is added
```
     <meta-data android:name="io.branch.sdk.TestMode" android:value="true" /> <!-- Set to true to use Branch_Test_Key -->
     <meta-data android:name="io.branch.sdk.BranchKey" android:value="key_live_my_live_key" />
     <meta-data android:name="io.branch.sdk.BranchKey.test" android:value="key_my_test_key" />
```
4. Add Branch InitSession

Add Branch initSession in Activities which are configured to open from a link click in order to receive the deep link params. This will return the deep link data from the referring link.

```
  protected void onStart() {
        super.onStart();
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                Log.d("BranchSDK","onInitFinished()" +referringParams);
            }
        });
    }
```

### Full App conversion with deep linking
Branch SDK provides convenient methods to check for app types and full app conversion. This eliminates the need of having
Google IA support SDK ('com.google.android.instantapp'). Here are some of the methods that makes life easy

      1) Branch#isInstantApp()
      This methods checks whether the current version of app running is Instant app or Full app
      
      2)Branch#showInstallPrompt() 
      This methods shows an install prompt for the full app. This method will pass referrer info to the full app when it is installed.
      The full app will receive the same deep link params as the instant app.
  

Follow below step to add full app conversion with deferred deep linking support for your application
#### 1.Show Install Prompt
```
 if (Branch.isInstantApp(this)) {
            myFullAppInstallButton.setVisibility(View.VISIBLE);
            myFullAppInstallButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Branch.showInstallPrompt(myActivity, activity_ret_code);
                }
            });
        } else {
            myFullAppInstallButton.setVisibility(View.GONE);
        }       
```

#### 2. Add INSTALL_REFERRER
The referrer information is passed through play-store install referrer. So please add the following intent filter to your manifest
```
<receiver android:name="io.branch.referral.InstallListener" android:exported="true">
    <intent-filter>
        <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
</receiver>
```
If you are using custom install referrer please consider extending your receiver class with {@link io.branch.referral.InstallListener}

NOTE: Since install referrer broadcast from google play is few millisecond delayed, We recommend to call `Branch#enablePlayStoreReferrer(delay)` from `Application#onCreate()` method for
more accurate tracking and attribution. This will delay Branch init only the first time user open the app. Recommended delay is 1500ms to capture more than 90% of the install referrer 
cases per our testing as of 4/2017
```
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Branch.enablePlayStoreReferrer(1500L);
        Branch.getAutoInstance(this);
    }
}
```
## Building and Testing
1) Configure your device and tools to support Instant apps. Please follow the [Instant App guide from Google](https://developers.google.com/android/confidential/instant-apps/setup)

2) Move to project root directory and execute following command to build the Instant App zip bundle
    `./gradlew clean :branchster-instantapp:assembleDebug`
    
3) Execute the following command to run the Instant App (Make sure you have not installed the Branch Monster Factory app)
    `wh run -i branchster-instantapp/build/outputs/apk/branchster-instantapp-debug.zip -u https://bnc.lt/ALMc/ntzqudJf8B`
   Where the `https://bnc.lt/ALMc/ntzqudJf8B` is the link used to simulate link click. You can change this link with any other link created from Branch Monster Factory application to test deeplinking with Instant Apps  

4) Just `Run` the `Branchster-apk` project from Android Studio to run the full application.


## Troubleshooting 

##### Gradle Build Errors 
1) Make sure you have added gradle 3.2 or above for your project. If there are many version added under your `.gradle` folder keep only the latest and remove everything else.

2) Make sure your build.gradle files are located under modules folder

3) Make sure gradle has sufficient permissions (chmod + X)

##### APK side loading failed

1) Go to settings-> applications and select `WH Dev Manager` and clear the cache

