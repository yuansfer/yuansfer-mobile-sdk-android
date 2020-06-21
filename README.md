## Yuansfer-Mobile-Pay-SDK-Android
Yuansfer-Mobile-Pay-SDK-Android is a project that aggregates WeChat and Alipay payments. It mainly provides apps to quickly access APIs for WeChat and Alipay payments. In addition, it includes some other API interfaces with payment. Full-featured and lightweight (lite) versions. The lightweight version mainly does not directly refer to WeChat and Alipay SDK. In some cases, the app has already relied on the WeChat and Alipay SDK Lite.

### Quick integration
* Full version, already dependent on payment platform SDK
```
dependencies {
        ...
        implementation 'com.fly.sdk:yuansfer-pay:0.9.0'
}
```
* The Lite version does not depend on the payment platform SDK, which can avoid the conflict problem when the app introduces WeChat to share the SDK. At this time, the app module needs to rely on WeChat or Alipay SDK, otherwise the program will crash
```
dependencies {
        ...
        implementation 'com.fly.sdk:yuansfer-pay-lite:0.9.0'
}
```

### How to use
1. Register and remove payment callbacks and receive order payment results
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    YSAppPay.registerPayResultCallback(callback);
}

@Override
protected void onDestroy() {
    ...
    YSAppPay.unregisterPayResultCallback(callback);
}
```
2. Initiate payment after calling the pre-order interface successfully
```
YSAppPay.getInstance().startPay(activity,payItem)
```

### Other instructions

* AppID registration is required when initiating WeChat payment

```
YSAppPay.getInstance().supportWxPay(appid)
```
* Since the minimum version requirement of Alipay SDK is 16, if the app module is lower than 16, you need to add the following statement in AndroidManifest.xml

```
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.fly.sdk"/>
```
* WeChat payment needs to determine the client appid, package name, package signature, and server parameters and signature to be the same to pull up the WeChat client


### Version log

#### 0.9.0
- Project initialization
- Simplify the launch of Alipay or WeChat Pay