Branchster Android (Instant App Enabled)
==================
## Introduction

This is a replica of [Branch Monster Factory](https://github.com/BranchMetrics/Branch-Example-Deep-Linking-Branchster-Android) project with Android Instant App support. This shows how easy it is to add Branch SDK to your Instant App supported Android app

**1. Initialize the Branch SDK**

Head to your _core library project_, where your Application class is defined and drop in the snippet of code to the onCreate() method as follows. If you plan on deep linking from your Android Instant App to your full Android app after its installed, you'll need to add the line `enablePlayStoreReferrer`. This adds a delay to the initialization to wait for the Google Play Referrer, which can take up to a second.

```java
public void onCreate() {
  super.onCreate();
  // This is needed to deferred deep link from an Android Instant App to a full app
  // It tells the Branch initialization to wait for the Google Play Referrer before proceeding.
  Branch.enablePlayStoreReferrer(1000L);

  // Initialize the Branch SDK
  Branch.getAutoInstance(this);
}
```
**2. Add your Branch keys and register for Install Referrer**

Instant Apps can be rather confusing as there are many different manifests, but you want to find the Manifest that contains your `application` tags. Make sure your Application class name is defined here, and then specify the Branch keys _inside_ the `application` element.

```xml
<application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        android:supportsRtl="true"
        android:name=".MyApplication">

  <meta-data android:name="io.branch.sdk.TestMode" android:value="false" /> <!-- Set to true to use Branch_Test_Key -->
  <meta-data android:name="io.branch.sdk.BranchKey" android:value="key_live_my_live_key" />
  <meta-data android:name="io.branch.sdk.BranchKey.test" android:value="key_test_my_test_key" />

  <receiver android:name="io.branch.referral.InstallListener" android:exported="true">
    <intent-filter>
       <action android:name="com.android.vending.INSTALL_REFERRER" />
    </intent-filter>
  </receiver>

</application>
```

**3. Configure your Branch links as Android App Links**

This guide presumes that you've already configured Branch for Android App Links in the past. If you haven't configured your full native app to use Branch as Android App Links, [please complete this guide](https://dev.branch.io/getting-started/universal-app-links/guide/android/) which will correctly configure the dashboard and manifest.

Now, you simply need to edit the above manifest and paste in the following snippet _inside_ the `application` element. Then you'll need to replace the `xxxx` with your own custom subdomain which will be visible on [the Branch link settings dashboard](https://dashboard.branch.io/link-settings) at the bottom of the page. If you're using a custom subdomain, you can find the advanced instructions in the above link regarding configuring Android App Links.

```xml
<application
  ......
  
  <intent-filter android:autoVerify="true">
      <action android:name="android.intent.action.VIEW" />
      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />
      <data android:scheme="https" android:host="xxxx.app.link" />
      <data android:scheme="https" android:host="xxxx-alternate.app.link" />
  </intent-filter>
  
</application>
```

**4. Retrieve Branch deep link data**

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
**5. Configure the deep linking from Instant App to your Full App**

Now, the user has arrived in your Instant App and you're ready to convert them to install your full native app. Don't worry, Branch as got your covered! We have overridden the default `showInstallPrompt` with a method that auto configures the Google Play prompt with all of the deep link data you need to carry context through install. Additionally, we can provide you the full set of attribution on how many users conver through this prompt.

Branch SDK provides convenient methods to check for app types and full app conversion. This eliminates the dependency on Google IA support SDK ('com.google.android.instantapp'). Here are some of the methods that makes life easy

- `Branch#isInstantApp()`

This convenience methods checks whether the current version of app running is Instant App or Full Android App to allow you convenience

- `Branch#showInstallPrompt()`
      
This methods shows an install prompt for the full Android app, allowing you an easy way to pass Branch referring deep data to the full app through the install process. Similar to how deferred deep linking works for Branch normally, the full app will receive the deep link params in the handle callback.

The below example shows how to create a custom Branch Universal Object, the associate it with the installation prompt that will be passed through to your full native Android app after the user installs.

```java
if (Branch.isInstantApp(this)) {
  myFullAppInstallButton.setVisibility(View.VISIBLE);
  myFullAppInstallButton.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
       BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
            .setCanonicalIdentifier("item/12345")
            .setTitle("My Content Title")
            .setContentDescription("My Content Description")
            .setContentImageUrl("https://example.com/mycontent-12345.png")
            .addContentMetadata("property1", "blue")
            .addContentMetadata("property2", "red");

      Branch.showInstallPrompt(myActivity, activity_ret_code, branchUniversalObject);
    }
  });
} else {
  myFullAppInstallButton.setVisibility(View.GONE);
}
```

## Building and Testing

1) Configure your device and tools to support Instant apps. Please follow the [Instant App guide from Google](https://developers.google.com/android/confidential/instant-apps/setup)

2) Move to project root directory and execute following command to build the Instant App zip bundle

    `./gradlew clean :branchster-instantapp:assembleDebug`
    
3) Execute the following command to run the Instant App (Make sure you have not installed the Branch Monster Factory app)

    `wh run -i branchster-instantapp/build/outputs/apk/branchster-instantapp-debug.zip -u https://branchster.app.link/3B8saD4SSC`
   
Where the `https://branchster.app.link/3B8saD4SSC` is the link used to simulate link click. You can change this link with any other link created from Branch Monster Factory application to test deeplinking with Instant Apps  

4) Just `Run` the `Branchster-apk` project from Android Studio to run the full application.

## Troubleshooting 

##### Gradle Build Errors 
1) Make sure you have added gradle 3.2 or above for your project. If there are many version added under your `.gradle` folder keep only the latest and remove everything else.

2) Make sure your build.gradle files are located under modules folder

3) Make sure gradle has sufficient permissions (chmod + X)

##### APK side loading failed

1) Go to settings-> applications and select `WH Dev Manager` and clear the cache

