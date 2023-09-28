## Language
English | [中文文档](README_zh.md)

## Introduction
This project supports multiple payment methods, including WeChat Pay, Alipay, credit card, PayPal, Venmo, Google Pay, CashApp, and more. These payment methods are independent of each other, and you only need to introduce the corresponding dependencies when needed.
The project consists of an SDK and a demo. The SDK provides interfaces and encapsulations for various payment methods, making it easy for you to integrate payment functions quickly. The demo provides specific code examples and explanations, making it easy for you to learn how to use the SDK for payment integration.
By using the SDK, you can quickly integrate multiple payment methods, provide users with more convenient payment options, and improve their payment experience and transaction efficiency.
## Quick integration
* In the build.gradle file of your project, you can add JitPack and Alibaba Cloud mirror URLs. If you need to integrate Alipay, you also need to specify the AAR directory.
````
buildscript {
    repositories {
        google()
        maven { url 'https://maven.aliyun.com/repository/jcenter' }
    }
}
repositories {
    maven { url 'https://jitpack.io' }
    maven { url 'https://maven.aliyun.com/repository/jcenter' }
    // alipay arr location (optional)
    flatDir {
        dirs 'libs'
    }
}
````
* Add the following dependencies to the build.gradle file of your application. Payment is required, while other payment methods are optional. If you want to use the Drop-in tool package with UI functionality, you need to add the following private service authentication.
````
dependencies {
        ... 
        // Required
        implementation 'com.github.yuansfer:yuansfer-payment-android:1.3.2'

        // Alipay (optional)
        implementation (name: 'alipaySdk-15.7.6-20200521195109', ext: 'aar')

        // Wechat Pay (optional)
        implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+'

        // Custom UI (optional)
        implementation 'com.braintreepayments.api:braintree:3.14.2'

        // Drop-in UI (optional)
        implementation 'com.braintreepayments.api:drop-in:4.6.0'

        // Google Pay (optional)
        implementation 'com.google.android.gms:play-services-wallet:16.0.1'

        // Client Online API (optional)
        implementation 'com.ejlchina:okhttps-gson:3.2.0'
        
        // CashAppPay (optional)
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
* If you want to integrate WeChat Pay or Alipay, please register a unified listener to handle payment results first.
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
* After obtaining the pre-payment data from the Pockyt server, you can use the requestXXX function to initiate Alipay or WeChat Pay.
````
// Start Alipay
pay.requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
pay.registerWXAPP(Context context, String appId)

// Start Wechat Pay
pay.requestWechatPayment(WxPayItem wxPayItem)
````

* If you want to integrate the Drop-in UI, you need to make the Activity inherit from BTDropInActivity. If you want to use a custom UI, you need to make the Activity inherit from BTCustomPayActivity and implement the interface methods that need to be overwritten, including IBTPrepayCallback and IBTNonceCallback.

>  When the payment environment is not allowed, the user cancels the payment, or an error occurs, the IBTPrepayCallback callback will be triggered. Please implement the following method and provide feedback to the user.
   
````
    void onPaymentConfigurationFetched(Configuration configuration);

    void onPrepayCancel();

    void onPrepayError(ErrStatus errStatus);
````

> The IBTNonceCallback triggers a callback after successfully obtaining a payment Nonce. Only the supported payment methods need to be implemented, for example, for credit cards, only the callback method with a parameter of CardNonce instance needs to be implemented.

````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````

* You can initiate credit card, Paypal, Venmo, and Google Pay payments by obtaining a customer token from the backend server.
````
IBraintreePay pay = YSAppPay.getBraintreePay()

pay.bindBrainTree(T activity, String authorization)

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

* Saving payment methods such as credit cards and PayPal

  Save payment methods such as credit cards and PayPal. To facilitate customers using the same payment method for future payments, saving the most recent payment method can avoid the need to repeatedly enter account information and complete payment. The client integration process is as follows:

  1. Register a customer before the first payment, including information such as email, phone, and country. The customer information can be retrieved or updated as needed.
  2. Call the /online/v3/secure-pay interface and pass in the customerNo field associated with the customer from the previous step.
  3. Call the /creditpay/v3/process interface to complete the payment.

  **Drop-in method:**

  Following the above steps, the Drop-in method will automatically save and display the previously used payment methods such as Credit Card and PayPal for the customer in the Drop-in display panel. After selecting the payment method, the customer can proceed to complete the payment without entering any information.

  **Custom UI method:**

  - Call the /online/v3/secure-pay interface to obtain the authorization and bind the fragment.
  - Call the PaymentMethod.getPaymentMethodNonces() method to retrieve the list of recently used payment methods, and implement the PaymentMethodNoncesUpdatedListener interface to display the list data, including payment type and the last four digits of the card.

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
* Please refer to the usage examples in the demo for detailed instructions.

## Other instructions
* Android 11 system policy update, adding WeChat software visibility adaptation.
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

* WeChat payment needs to determine the client appid, package name, package signature, and server parameters and signature to be the same to pull up the WeChat client

* After adding the CashAppPay dependency, when the error of Failed to transform moshi-1.13.0.jar appears, please add the AndroidX automatic conversion blacklist in the gradle.properties file to exclude it:
````
  android.jetifier.blacklist=moshi-1.13.0
````