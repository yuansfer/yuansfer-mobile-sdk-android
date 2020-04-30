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
* The Lite version does not depend on the payment platform SDK, which can avoid the conflict problem when the app introduces WeChat to share the SDK. At this time, the app module needs to rely on WeChat and Alipay SDK, otherwise the program will crash
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
- Update test token
- Change a few places

#### 0.8.0
- Remove SDK interface function

#### 0.7.7
- Test mode settings merge

#### 0.7.6
- WeChat pre-order return field format adjustment

#### 0.7.3
- WeChat pre-orders without appid

#### 0.7.2
- Annotation English

#### 0.7.0
- Demo adds serial number input box
- Add note tips

#### 0.6.6
- Support sdk text prompt and demo display in Chinese and English

#### 0.6.5
- Add order query and refund API interface
- Registered payment callback adjusted to static call

#### 0.6.1
- Add test environment for Alipay sandbox binding
- WeChat payment demo calls public test data

#### 0.5.0
- Support Alipay to initiate payment
- WeChat payment calling package name, signature is consistent with public test

#### 0.2.0
- Support Alipay arr using jcenter dependent form

#### 0.1.0
- Project initialization
- Simplify the launch of Alipay and WeChat Pay