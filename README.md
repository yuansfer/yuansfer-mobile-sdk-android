## Introduction

![jitpack](https://img.shields.io/badge/jitpack-v2.0.0-blue)  
`pockyt_pay` is a mobile payment sdk that supports popular channels such as WeChat Pay, Alipay, Braintree (Credit Card, PayPal, Venmo, Google Pay).

## Getting Started

- Before integrating the payment, please contact the Pockyt team to create an account. We may require you to submit documentation files.
- For Wechat Pay, configure the package name and signature in the WeChat Open Platform. [official signature apk](https://res.wx.qq.com/wxdoc/dist/assets/media/Gen_Signature_Android.e481f889.zip)
- For Braintree, please contact the Pockyt team to confirm the payment mode. When integrating with Braintree, you will need to create an account and configure it on the official platform.

## Configuration

* Configure the jitpack repository URL in the project-level dependencies file. Additionally, if you need to add the Drop-in or 3D Secure dependency library, please add the following Maven repository and (non-sensitive) credentials to the gradle.
> After Android Gradle 7.x, please add it in the `settings.gradle` file.
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        // Required
        maven { url 'https://jitpack.io' }
        // Optional(Required for drop-in or three-d-secure libraries)
        maven {
            url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
            credentials {
                username 'braintree_team_sdk'
                password 'AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp'
            }
        }
    }
}
```
> Before Android Gradle 7.x, please add it in the `build.gradle` file.
```gradle
allprojects {
    repositories {
        ...
        // Required
        maven { url 'https://jitpack.io' }
        // Optional(Required for drop-in or three-d-secure libraries)
        maven {
            url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
            credentials {
                username 'braintree_team_sdk'
                password 'AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp'
            }
        }
    }
}
```
* Please add the `pocket_pay` dependency in the `build.gradle` file of the app-level gradle. Add the corresponding dependencies selectively based on the payments you want to support.
```gradle
dependencies {
    ...
    // Required
	implementation 'com.github.yuansfer:yuansfer-mobile-sdk-android:^${lastest version}' 
	// Optional, Alipay dependency
	implementation 'com.alipay.sdk:alipaysdk-android:15.8.14@aar' 
	// Optional, Wechat Pay dependency
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0'  
    // Optional, Include all other dependencies
    implementation 'com.braintreepayments.api:drop-in:6.13.0' 
    // Optional, The following dependencies are independent of each other, so you can add them as needed.
    implementation 'com.braintreepayments.api:card:4.39.0'  
    implementation 'com.braintreepayments.api:paypal:4.39.0' 
    implementation 'com.braintreepayments.api:venmo:4.39.0'
    implementation 'com.braintreepayments.api:google-pay:4.39.0'
    implementation 'com.braintreepayments.api:data-collector:4.39.0'
    implementation 'com.braintreepayments.api:three-d-secure:4.39.0'
}
```

## How to use

* Only a few steps are required to initiate the payment.
1. For WeChat Pay, registering WeChat API.
```
WechatPayStrategy.registerApi(context, wechatAppId)
```
2. After calling the Pockyt prepayment API(`/micropay/v3/prepay` or `/online/v3/secure-pay`), create a payment request.  
```
val request = AlipayReq(activity, payInfo)
val request = WechatPayReq(appId, partnerId, prepayId, packageValue, nonceStr, timeStamp, sign)
val request = DropInReq(activity, authorization, dropInRequest)
val request = CardReq(activity, authorization, card, true)
...
```
3. The payment request is then passed to the `PockytPay` class to initiate the payment.
```
// For WeChat Pay
PockytPay.alipayPay.requestPay(AlipayReq(activity, payInfo)) {
    vLog.log("Paid:${it.isSuccessful}, cancelled:${it.isCancelled}, $it")
}

// For WeChat Pay
PockytPay.wechatPay.requestPay(
    WechatPayReq(
    appId = result.optString("appid"),
    partnerId = result.optString("partnerid"),
    prepayId = result.optString("prepayid"),
    packageValue = result.optString("package"),
    nonceStr = result.optString("noncestr"),
    timeStamp = result.optString("timestamp"),
    sign = result.optString("sign")
)) {
    vLog.log("Paid:${it.isSuccessful}, cancelled:${it.isCancelled}, $it")
}

// For Braintree Drop-in
val dropInRequest = DropInRequest()
PockytPay.dropInPay.requestPay(DropInReq(DropInActivity@this, authorization, dropInRequest)) {
    vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, vendor:${it.dropInResult?.paymentMethodType}, nonce:${it.dropInResult?.paymentMethodNonce?.string}, deviceData:${it.dropInResult?.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer(jsonObject.optJSONObject("result").optString("transactionNo")
            , it.dropInResult?.paymentMethodNonce?.string ?: ""
            , it.dropInResult?.deviceData)
    }
}

// For Braintree Custom UI, Taking card payment as an example, similarly for other payment methods.
PockytPay.cardPay.requestPay(request) {
    vLog.log("Obtained nonce:${it.isSuccessful}, desc:${it.respMsg}, 3d secure nonce:${it.cardNonce?.string}, deviceData:${it.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer("Your transactionNo", it.cardNonce!!.string, it.deviceData)
    }
}
```
4. For Braintree, After obtaining the nonce from the payment result, call the Pockyt process API (`/creditpay/v3/process`) to complete the payment.

## Note

* Drop-in UI of Braintree is a complete, ready-made payment UI that offers a quick and easy way to securely accept payments. The UI includes a card entry form and, if enabled, PayPal, Venmo and Google Pay.
* For Braintree payment, it is recommended to retrieve and upload deviceData to reduce the rejection rate. For card payments, you can choose to include threeDSecure verification based on your requirements.
* If you are using WeChat Pay and need to obfuscate your code, please add the following code to ensure the proper functionality of the SDK.
```
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}
```
* Due to the refactoring of version 2.x.x using Kotlin, it is not compatible with version 1.x.x, when the android project is pure java language, please modify the project configuration to support Kotlin.
* Please refer to the example code for detailed usage instructions. 