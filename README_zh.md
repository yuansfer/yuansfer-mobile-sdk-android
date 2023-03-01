## 语言
[English](README.md) | 中文文档

## 概述
这是一个可快速集成微信支付、支付宝、Braintree等第三方支付平台的SDK项目, 同时支持部分在线api接口的便捷调用。每种支付方式相互独立，需要则引入相应依赖.

## 快速集成
* 在project下的build.gradle文件中添加jitpack和阿里云镜像url, 若集成支付宝，则需指定aar目录.
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
* 在app的build.gradle文件中添加以下依赖, payment是必要的，其它支付方式均为可选，若要使用Braintree的带UI功能Drop-in工具包，则添加以下私服认证.
````
dependencies {
        ... 
        // Required
        implementation 'com.github.yuansfer:yuansfer-payment-android:1.2.0'

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
        // Add drop-in certificate (optional)
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
## 如何使用
* 当集成了微信或支付宝时，注册和移除统一监听付款结果.
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
* 从Yuansfer服务器获取预付款信息后发起支付宝或微信支付.
````
// Start Alipay
pay.requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
pay.registerWXAPP(Context context, String appId)

// Start Wechat Pay
pay.requestWechatPayment(WxPayItem wxPayItem)
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

// 注册支付回调
pay.registerPayEventCallback()

// 创建支付请求，必须在工作线程调用
pay.requestCashAppPayInBackground()

// 授权支付
pay.authorizeCashAppPay()

````

* 在线支付API接口.
````
IClientAPI api = YSAppPay.getClientAPI()

// Instore api: Add
api.transAdd(request, new OnResponseListener<TransAddResponse>() {})

// Instore api: Prepay
api.transPrepay(request2, new OnResponseListener<TransPrepayResponse>() {})

````
* 有关详细说明，请参阅演示用法示例.

## 其他说明
* Android 11 系统策略更新, 添加微信的软件可见性适配，
````
  // 在应用的AndroidManifest.xml添加如下<queries>标签
  <queries>
      <package android:name="com.tencent.mm" />   // 指定微信包名
  </queries>
  // 添加以上标签之后，需要开发者升级编译工具，否则会出现编译错误。
  // Android Studio 需要升级至 3.3 及以上，建议升级至 4.0 及以上版本；
  // Android SDK Build-Tools 需要升级至 30 及以上版本；
  // com.android.tools.build:gradle 需要升级至 3.6.0 版本，建议升级至最新的 3.6.4 版本。
````

* 由于支付宝SDK的最低版本要求为16，如果应用模块低于16，则需要在AndroidManifest.xml中添加以下语句.

````
<uses-sdk tools:overrideLibrary="com.alipay.sdk,com.yuansfer.pay"/>
````
* 微信支付需要确定客户端appid，软件包名称，软件包签名以及服务器参数和签名相同才能拉起微信客户端.

* 添加了CashAppPay依赖后出现Failed to transform moshi-1.13.0.jar报错时，请在gradle.properties文件添加AndroidX自动转换黑名单排除在外:
````
  android.jetifier.blacklist=moshi-1.13.0
````

* ErrorStatus付款结果代码:
  - 微信支付, 以字符W为开头的错误码
  - 支付宝, 以字符A为开头的错误码
  - Google Pay, 以字符G为开头的错误码
  - Braintree, 以字符B为开头的错误码
  
