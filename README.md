## yuansfer-payment-android
yuansfer-payment-android is a project that aggregates WeChat、Alipay or Braintree payments, It mainly provides apps to quickly access APIs for many payments. In addition, it includes some other API interfaces with payment.

### Quick integration
* Add the following dependencies in the app's build.gradle file, the payment method is optional
````
dependencies {
        ... 
        // Necessary
        implementation 'com.yuansfer.pay:payment:1.1.0'

        // Alipay (optional)
        implementation (name: 'alipaySdk-15.7.6-20200521195109', ext: 'aar')

        // Wechat Pay (optional)
        implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+'

        // Google Pay of Braintree (optional)
        implementation 'com.google.android.gms:play-services-wallet:16.0.1'
        implementation 'com.braintreepayments.api:braintree:3.14.2'

        // Drop-in UI of Braintree (optional)
        implementation 'com.braintreepayments.api:drop-in:4.6.0'
}
````
* When using the Drop-in UI payment method, please add the following code to app's build.gradle file
````
repositories {
    //add drop-in certificate
    maven {
        url  "https://cardinalcommerce.bintray.com/android"
        credentials {
            username 'braintree-team-sdk@cardinalcommerce'
            password '220cc9476025679c4e5c843666c27d97cfb0f951'
        }
    }
}
````
* If you want to add Alipay payment SDK, please copy the Alipay SDK aar file to the app/libs directory and declare the location of aar in project build.gradle
````
allprojects {
    repositories {

        // alipay arr location
        flatDir {
            dirs 'libs'
        }

        // ... jcenter() 
    }
}
````
### How to use
* Register and remove payment callbacks and receive payment results
````
@Override
protected void onStart() {
    ...
    YSAppPay.registerPayResultCallback(callback);
}

@Override
protected void onStop() {
    ...
    YSAppPay.unregisterPayResultCallback(callback);
}
````
* Start payment after obtaining WeChat or Alipay data from the backend server
````
// Alipay
YSAppPay.getInstance().startAlipay(Activity activity, AlipayItem alipayItem)

// Wechat Pay
YSAppPay.getInstance().startWechatPay(Activity activity, WxPayItem wxPayItem)
````

* If you want to integrate Braintree’s Drop-in UI, Your Activity must inherit YSDropinPayActivity and implement the methods of IBrainTreeCallback that need to be overridden.
````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````
* If you need to add a separate Google Pay, you should check whether the Google Pay service and configuration are available. If it is available, the Google Pay button is usually displayed, Your Activity must inherit YSGooglePayActivity and implement the methods of it.
````
    public void onReadyToPay() {
        //Google Pay service is available, usually showing the payment button
    }

    public void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData) {
        //Upload the nonce and deviceData to the server
    }
````
* Braintree payment can be initiated by obtaining the client token from the backend server or using a constant merchant authorization code，But before google pay payment is started, it needs to check whether google pay service is available
````
// Bind Google Pay, check whether google pay service is available
YSAppPay.getInstance().bindGooglePay(T activity, String authorization)

// Unbind Google Pay
YSAppPay.getInstance().unbindGooglePay(T activity)

// Start Google Pay
YSAppPay.getInstance().startGooglePay(T activity, YSGooglePayItem googlePayItem)

// Start Drop-in UI Payment
YSAppPay.getInstance().startDropInPayment(T activity, String authorization
            , DropInRequest dropInRequest)

````
* For detailed instructions, please refer to Demo usage examples.

### Other instructions

* Since the minimum version requirement of Alipay SDK is 16, if the app module is lower than 16, you need to add the following statement in AndroidManifest.xml

````
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.yuansfer.pay"/>
````
* WeChat payment needs to determine the client appid, package name, package signature, and server parameters and signature to be the same to pull up the WeChat client

* ErrorStatus Code of payment result:
  - Wechat Pay, Start with the character 'W'
  - Alipay, Start with the character 'A'
  - Google Pay, Start with the character 'G'
  - Dropin Pay, Start with the character 'D'
  
### Version log

#### 1.1.0
- Add Braintree Drop-in Payment
- Add Braintree Google Pay Payment

#### 1.0.1
- Add multi-currency secure-pay api

#### 1.0.0
- Project initialization
- Simplify the launch of Alipay or WeChat Pay