## 语言
[English](README.md) | 中文文档

## 概述
这是一个可快速集成微信支付、支付宝、Braintree等第三方支付平台的SDK项目.

## 快速集成
* 在project下的build.gradle文件中添加jitpack和阿里云镜像url, 若集成支付宝，则指定aar目录.
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
* 在app的build.gradle文件中添加以下依赖, payment是必要的，其它支付方式均为可选，若要使用Braintree的带UI功能Drop-in工具包，则添加以下私服认证.
````
android{
    repositories {
        //add drop-in certificate
        maven {
            url "https://cardinalcommerceprod.jfrog.io/artifactory/android"
            credentials {
                username 'braintree_team_sdk'
                password 'AKCp8jQcoDy2hxSWhDAUQKXLDPDx6NYRkqrgFLRc3qDrayg6rrCbJpsKKyMwaykVL8FWusJpp'
            }
        }
    }
}
dependencies {
        ... 
        // Required
        //implementation project(':payment')
        implementation 'com.github.yuansfer:yuansfer-payment-android:1.1.8'

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
## 如何使用
* 当集成了微信或支付宝时，注册和移除统一监听付款结果.
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
* 从Yuansfer服务器获取预付款信息后发起支付宝或微信支付.
````
// Start Alipay
YSAppPay.getInstance().requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
YSAppPay.getInstance().registerWXAPP(Context context, String appId)

// Start Wechat Pay
YSAppPay.getInstance().requestWechatPayment(WxPayItem wxPayItem)
````

* 当使用Braintree的Drop-in UI，则Activity需继承BTDropInActivity，当使用自定义UI，则Activity需继承BTCustomPayActivity，并实现需要重写的IBTPrepayCallback和IBTNonceCallback的接口方法.
    
  - IBTPrepayCallback在检查支付环境后发生回调.
    
````
    // 相关服务和配置是否可用
    void onPaymentConfigurationFetched(Configuration configuration);

    void onPrepayCancel();

    void onPrepayError(ErrStatus errStatus);
````

  - IBTNonceCallback在获取支付Nonce成功后发生回调, 仅需要实现支持的支付方式即可.
   
````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````

* 可以通过从后端服务器获取客户令牌或使用恒定的商家授权码来发起相应的Braintree付款.
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
* 有关详细说明，请参阅演示用法示例.

## 其他说明

* 由于支付宝SDK的最低版本要求为16，如果应用模块低于16，则需要在AndroidManifest.xml中添加以下语句.

````
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.yuansfer.pay"/>
````
* 微信支付需要确定客户端appid，软件包名称，软件包签名以及服务器参数和签名相同才能拉起微信客户端.

* ErrorStatus付款结果代码:
  - 微信支付, 以字符W为开头的错误码
  - 支付宝, 以字符A为开头的错误码
  - Google Pay, 以字符G为开头的错误码
  - Braintree, 以字符B为开头的错误码
