## Language
English | [中文文档](README_zh.md)

## Introduction
This is a project that aggregates WeChat、Alipay or Braintree payments, It mainly provides apps to quickly access APIs for many payments.

## Quick integration
* Add jitpack and Alibaba Cloud mirror url to the build.gradle file under the project. If Alipay is integrated, specify the aar directory.
````
buildscript {
    repositories {
        google()
        //aliyun mirror
        maven { url 'https://maven.aliyun.com/repository/jcenter' }
    }
}
repositories {
    //jitpack url
    maven { url 'https://jitpack.io' }
    //aliyun mirror
    maven { url 'https://maven.aliyun.com/repository/jcenter' }
    // alipay arr location
    flatDir {
        dirs 'libs'
    }
}
````
* Add the following dependencies to the app’s build.gradle file. Payment is necessary. Other payment methods are optional. If you want to use Braintree’s Drop-in toolkit with UI functionality, add the following private server authentication, For password, please visit[official website](https://developer.paypal.com/braintree/docs/guides/client-sdk/migration/android/v3#3d-secure).
````
android{
    repositories {
        //add drop-in certificate
        maven {
            url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
            credentials {
                username 'braintree_team_sdk'
                password 'xxx'
            }
        }
    }
}
dependencies {
        ... 
        // Required
        implementation 'com.github.yuansfer:yuansfer-payment-android:1.1.9'

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
}
````
## How to use
* When WeChat or Alipay is integrated, register and remove unified monitoring of payment results.
````
@Override
protected void onCreate() {
    ...
    YSAppPay.registerAliWxPayCallback(callback);
}

@Override
protected void onDestroy() {
    ...
    YSAppPay.unregisterAliWxPayCallback(callback);
}
````
* Start payment after obtaining WeChat or Alipay data from the backend server
````
// Start Alipay
YSAppPay.getInstance().requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
YSAppPay.getInstance().registerWXAPP(Context context, String appId)

// Start Wechat Pay
YSAppPay.getInstance().requestWechatPayment(WxPayItem wxPayItem)
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
// Bind Braintree
YSAppPay.getInstance().bindBrainTree(T activity, String authorization)

// Unbind Braintree
YSAppPay.getInstance().unbindBrainTree(T activity)

// Start Drop-in UI Payment
YSAppPay.getInstance().requestDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest)

// Start Google Pay
YSAppPay.getInstance().requestGooglePayment(T activity, GooglePaymentRequest googlePayItem)

// Start PayPal，One-time
YSAppPay.getInstance().requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest)

// Start PayPal, Save payment method
YSAppPay.getInstance().requestPayPalBillingAgreementPayment(T activity, PayPalRequest payPalRequest)

// Start Venmo
YSAppPay.getInstance().requestVenmoPayment(T activity, boolean vault)

// Start Card Pay
YSAppPay.getIntance().requestCardPayment(T activity, CardBuilder cardBuilder)

````
* For detailed instructions, please refer to Demo usage examples.

## Other instructions

* Since the minimum version requirement of Alipay SDK is 16, if the app module is lower than 16, you need to add the following statement in AndroidManifest.xml

````
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.yuansfer.pay"/>
````
* WeChat payment needs to determine the client appid, package name, package signature, and server parameters and signature to be the same to pull up the WeChat client

* ErrorStatus Code of payment result:
  - Wechat Pay, Start with the character 'W'
  - Alipay, Start with the character 'A'
  - Google Pay, Start with the character 'G'
  - Braintree, Start with the character 'B'