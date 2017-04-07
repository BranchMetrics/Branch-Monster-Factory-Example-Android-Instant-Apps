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

