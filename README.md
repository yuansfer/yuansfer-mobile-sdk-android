## Language
English | [中文文档](README_zh.md)

## Introduction
This is a project that aggregates WeChat、Alipay or Braintree payments, It mainly provides apps to quickly access APIs for many payments, At the same time, it supports the convenient call of some online API interfaces. Each payment method is independent of each other, and corresponding dependencies are introduced if necessary.

## Quick integration
* Add jitpack and Alibaba Cloud mirror url to the build.gradle file under the project. If Alipay is integrated, specify the aar directory.
````
buildscript {
    repositories {
        google()
        // aliyun mirror
        maven { url 'https://maven.aliyun.com/repository/jcenter' }
    }
}
repositories {
    // jitpack url
    maven { url 'https://jitpack.io' }
    // aliyun mirror
    maven { url 'https://maven.aliyun.com/repository/jcenter' }
    // alipay arr location (optional)
    flatDir {
        dirs 'libs'
    }
}
````
* Add the following dependencies to the app’s build.gradle file. Payment is necessary. Other payment methods are optional. If you want to use Braintree’s Drop-in toolkit with UI functionality, add the following private server authentication.
````
dependencies {
        ... 
        // Required
        implementation 'com.github.yuansfer:yuansfer-payment-android:1.3.0'

        // Alipay (optional)
        implementation (name: 'alipaySdk-15.7.6-20200521195109', ext: 'aar')

        // Wechat Pay (optional)
        implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+'

        // Custom UI of Braintree (optional)
        implementation 'com.braintreepayments.api:braintree:3.14.2'

        // Drop-in UI of Braintree (optional)
        implementation 'com.braintreepayments.api:drop-in:4.6.0'

        // Google Pay of Braintree (optional)
        implementation 'com.google.android.gms:play-services-wallet:16.0.1'

        // Client Online API (optional)
        implementation 'com.ejlchina:okhttps-gson:3.2.0'
        
        // CashAppPay
        implementation 'app.cash.paykit:core:1.0.3'
}
android{
    repositories {
        // Add drop-in certificate
        maven {
            url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
            credentials {
                username 'braintree_team_sdk'
                password 'AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp'
            }
        }
    }
}
````
## How to use
* When WeChat or Alipay is integrated, register and remove unified monitoring of payment results.
````
IAliWxPay pay = YSAppPay.getAliWxPay()

@Override
protected void onCreate() {
    ...
    pay.registerAliWxPayCallback(callback);
}

@Override
protected void onDestroy() {
    ...
    pay.unregisterAliWxPayCallback(callback);
}
````
* Start payment after obtaining WeChat or Alipay data from the backend server
````
// Start Alipay
pay.requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
pay.registerWXAPP(Context context, String appId)

// Start Wechat Pay
pay.requestWechatPayment(WxPayItem wxPayItem)
````

* When using Braintree’s Drop-in UI, the activity needs to inherit BTDropInActivity. When using a custom UI, the activity needs to inherit BTCustomPayActivity, and implement the interface methods of IBTrepayCallback and IBTNonceCallback that need to be rewritten.
   
  - IBTPrepayCallback callback occurs after checking the payment environment.
   
````
    // Whether related services and configurations are available
    void onPaymentConfigurationFetched(Configuration configuration);

    void onPrepayCancel();

    void onPrepayError(ErrStatus errStatus);
````

  - IBTNonceCallback will be called back after obtaining the payment Nonce successfully, only the supported payment method needs to be implemented.

````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````

* The corresponding Braintree payment can be initiated by obtaining the customer token from the back-end server or using a constant merchant authorization code.
````
IBraintreePay pay = YSAppPay.getBraintreePay()

// Bind Braintree
pay.bindBrainTree(T activity, String authorization)

// Unbind Braintree
pay.unbindBrainTree(T activity)

// Start Drop-in UI Payment
pay.requestDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest)

// Start Google Pay
pay.requestGooglePayment(T activity, GooglePaymentRequest googlePayItem)

// Start PayPal，One-time
pay.requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest)

// Start PayPal, Save payment method
pay.requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest)

// Start Venmo
pay.requestVenmoPayment(T activity, boolean vault)

// Start Card Pay
pay.requestCardPayment(T activity, CardBuilder cardBuilder)

````
* Cash App Pay.
````
ICashAppPay pay = YSAppPay.getCashAppPay()

// Register event callback
pay.registerPayEventCallback()

// To create a payment request, you must use a thread to call
pay.requestCashAppPayInBackground()

// Authorized payment
pay.authorizeCashAppPay()

````
* Online payment API interface.
````
IClientAPI api = YSAppPay.getClientAPI()

// Instore api: Add
api.transAdd(request, new OnResponseListener<TransAddResponse>() {})

// Instore api: Prepay
api.transPrepay(request2, new OnResponseListener<TransPrepayResponse>() {})

````
* For detailed instructions, please refer to Demo usage examples.

## Other instructions
* Android 11 system policy update, adding WeChat software visibility adaptation
````
  // Add the following<queries>tag to the AndroidManifest.xml of the application
  <queries>
      <package android:name="com.tencent.mm" />   // Specify WeChat package name
  </queries>
  // After adding the above tags, developers need to upgrade the compilation tools, otherwise compilation errors will occur.
  // Android Studio needs to be upgraded to 3.3 or above, and it is recommended to upgrade to 4.0 or above;
  // Android SDK Build Tools needs to be upgraded to version 30 or above;
  // Com.android.tools.build.gradle needs to be upgraded to version 3.6.0. It is recommended to upgrade to the latest version 3.6.4.
````

* Since the minimum version requirement of Alipay SDK is 16, if the app module is lower than 16, you need to add the following statement in AndroidManifest.xml

````
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.yuansfer.pay"/>
````
* WeChat payment needs to determine the client appid, package name, package signature, and server parameters and signature to be the same to pull up the WeChat client

* After adding the CashAppPay dependency, when the error of Failed to transform moshi-1.13.0.jar appears, please add the AndroidX automatic conversion blacklist in the gradle.properties file to exclude it:
````
  android.jetifier.blacklist=moshi-1.13.0
````

* ErrorStatus Code of payment result:
  - Wechat Pay, Start with the character 'W'
  - Alipay, Start with the character 'A'
  - Google Pay, Start with the character 'G'
  - Braintree, Start with the character 'B'
  