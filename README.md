## Introduction

![jitpack](https://img.shields.io/badge/jitpack-v2.0.8-blue)  
This is a payment sdk that supports mainstream payment methods such as WeChat Pay, Alipay, Cash App and Braintree etc.

## Getting Started

- Before integrating the payment, please contact the Pockyt team to create an account. We may require you to submit documentation files.
- For Wechat Pay, configure the package name and signature in the WeChat Open Platform. [official signature apk](https://res.wx.qq.com/wxdoc/dist/assets/media/Gen_Signature_Android.e481f889.zip)
- For Braintree, please contact the Pockyt team to confirm the payment mode. When integrating with Braintree, you will need to create an account and configure it on the official platform.

## Configuration

* Configure the jitpack repository URL in the project-level dependencies file. Additionally, if you need to add the Drop-in or 3D Secure dependency library, please add the following Maven repository and (non-sensitive) credentials to the gradle. After Android Gradle 7.x, add it in the `settings.gradle` file. Before Android Gradle 7.x, please add it in the `build.gradle` file.
```gradle
repositories {
    ...
    // Required
    maven { url 'https://jitpack.io' }
    // Optional (required for drop-in or three-d-secure library)
    maven {
        url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
        credentials {
            username 'braintree_team_sdk'
            password 'AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp'
        }
    }
}
```

* Please add the dependency in the `build.gradle` file of the app-level gradle. Add the corresponding dependencies selectively based on the payments you want to support.
```gradle
dependencies {
    ...
    // Required
    implementation 'com.github.yuansfer:yuansfer-mobile-sdk-android:2.0.8' 
    // Optional, Alipay dependency
    implementation 'com.alipay.sdk:alipaysdk-android:15.8.14@aar' 
    // Optional, Wechat Pay dependency
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0'  
    // Optional, Include all other dependencies
    implementation 'com.braintreepayments.api:drop-in:6.13.0' 
    // Optional, The following dependencies are independent of each other.
    implementation 'com.braintreepayments.api:card:4.39.0' 
    implementation 'com.braintreepayments.api:three-d-secure:4.39.0' 
    implementation 'com.braintreepayments.api:paypal:4.39.0' 
    implementation 'com.braintreepayments.api:venmo:4.39.0'
    implementation 'com.braintreepayments.api:google-pay:4.39.0'
    implementation 'com.braintreepayments.api:data-collector:4.39.0'
    // Optional, Required for Cash App
    implementation "app.cash.paykit:core:2.3.0"
}
```

* For CashApp, in order to return to your app after the payment flow is completed, this can be achieved by declaring an incoming intent filter in your app's Android Manifest and passing a corresponding redirect URI that utilizes the SDK when creating a Customer Request. Please note that the launch mode of the Activity must be one of singleTop, singleTask, or singleInstance. The following is an example of how to declare the intent filter in the Android Manifest.
```AndroidManifest.xml
<activity
    android:name=".YourActivity"
    android:launchMode="singleTop">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!-- When the value of the data attribute is not modified, the redirectUrl value can be omitted when creating a payment request. -->
        <data
            android:scheme="cashapppay"
            android:host="checkout" />
    </intent-filter>
</activity>
```

## How to use

* For WeChat Pay, first, you need to register the WeChat API.
```
WechatPayStrategy.registerApi(context, appid)
```

* After calling the Pockyt prepayment API(/micropay/v3/prepay or /online/v3/secure-pay), create a payment object and call the Pockyt.requestPay method.
```
// For Alipay
val request = AlipayReq(activity, payInfo)
Pockyt.createAlipay().requestPay(AlipayReq(activity, payInfo)) {
    vLog.log("Paid:${it.isSuccessful}, cancelled:${it.isCancelled}, $it")
}

// For WeChat Pay
val request = WechatPayReq(appId, partnerId, prepayId, packageValue, nonceStr, timeStamp, sign)
Pockyt.createWechatPay().requestPay(request) {
    vLog.log("Paid:${it.isSuccessful}, cancelled:${it.isCancelled}, $it")
}

// For Braintree Drop-in
val dropInRequest = DropInRequest()
val request = DropInReq(activity, authorization, dropInRequest)
Pockyt.createDropIn().requestPay(request) {
    vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, vendor:${it.dropInResult?.paymentMethodType}, nonce:${it.dropInResult?.paymentMethodNonce?.string}, deviceData:${it.dropInResult?.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer(jsonObject.optJSONObject("result").optString("transactionNo")
            , it.dropInResult?.paymentMethodNonce?.string ?: ""
            , it.dropInResult?.deviceData)
    }
}

// For Card
val request = CardReq(activity, authorization, card, true)
Pockyt.createCardPay().requestPay(request) {
    vLog.log("Obtained nonce:${it.isSuccessful}, desc:${it.respMsg}, nonce:${it.cardNonce?.string}, deviceData:${it.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer("Your transactionNo", it.cardNonce!!.string, it.deviceData)
    }
}

// For PayPal
val checkoutRequest = PayPalCheckoutRequest("0.01")
checkoutRequest.currencyCode = "USD"
val paypalRequest = PayPalReq(this, authorization, checkoutRequest, true)
Pockyt.createPaypal().requestPay(paypalRequest) {
    vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.paypalNonce?.string}, deviceData:${it.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer("Your transactionNo", it.paypalNonce!!.string, it.deviceData)
    }
}

// For Venmo
val request = VenmoReq(this, authorization, VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE), true)
Pockyt.createVenmo().requestPay(request) {
    vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.venmoNonce?.string}, deviceData:${it.deviceData}")
    if (it.isSuccessful) {
        submitNonceToServer("Your transactionNo", it.venmoNonce!!.string, it.deviceData)
    }
}

// For Google Pay
val googlePayRequest = GooglePayRequest()
googlePayRequest.transactionInfo = TransactionInfo.newBuilder()
    .setTotalPrice("1.00")
    .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
    .setCurrencyCode("USD")
    .build()
googlePayRequest.googleMerchantId = "merchant-id-from-google";
googlePayRequest.isBillingAddressRequired = true
val request = GooglePayReq(this, authorization, googlePayRequest)
Pockyt.createGooglePay().requestPay(request) {
    vLog.log("Obtained nonce:${it.isSuccessful}, cancelled:${it.isCancelled}, desc:${it.respMsg}, nonce:${it.paymentNonce?.string}")
    if (it.isSuccessful) {
        submitNonceToServer("Your transactionNo", it.paymentNonce!!.string)
    }
}

// For CashApp
val requestData = CashAppRequest.OneTimeRequest(scopeId, amount)
val request = CashAppReq(clientId, requestData)
Pockyt.createCashApp().requestPay(request) {
    if (it.isSuccessful) {
        // Payment Approved
        vLog.log("Payment approved")
        queryTransactionResult(transactionNo)
    } else {
        vLog.log("Payment failed: ${it.respMsg}")
    }
}
```

* For Braintree, After obtaining the nonce from the payment result, call the Pockyt process API (`/creditpay/v3/process`) to complete the payment.

## Note

* Drop-in UI of Braintree is a complete, ready-made payment UI that offers a quick and easy way to securely accept payments. The UI includes a card entry form and, if enabled, PayPal, Venmo and Google Pay.
* For Braintree payment, it is recommended to retrieve and upload deviceData to reduce the rejection rate. For card payments, you can choose to include threeDSecure verification based on your requirements.
* Due to the refactoring of version 2.x.x using Kotlin, it is not compatible with version 1.x.x, when the android project is pure java language, recommended configuration for supporting Kotlin.
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
