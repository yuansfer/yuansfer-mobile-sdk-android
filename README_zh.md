## 语言
[English](README.md) | 中文文档

## 概述
该项目支持多种支付方式，包括微信支付、支付宝、信用卡、PayPal、Venmo、Google Pay、CashApp等。这些支付方式相互独立，您只需要在需要时引入相应依赖即可。
该项目由SDK和Demo组成，SDK提供了各种支付方式的接口和封装，方便您快速集成支付功能。Demo则提供了具体的代码示例和说明，方便您了解如何使用SDK进行支付集成。
通过使用SDK，您可以快速集成多种支付方式，为用户提供更加便捷的支付方式，提高用户的支付体验和交易效率。

## 快速集成
* 在项目的build.gradle文件中，您可以添加JitPack和阿里云镜像URL。如果您需要集成支付宝，还需要指定AAR目录。
````
buildscript {
    repositories {
        google()
        // aliyun url
        maven { url 'https://maven.aliyun.com/repository/jcenter' }
    }
}
repositories {
    // jitpack url
    maven { url 'https://jitpack.io' }
    // aliyun url
    maven { url 'https://maven.aliyun.com/repository/jcenter' }
    // alipay arr location (optional)
    flatDir {
        dirs 'libs'
    }
}
````
* 在您的应用程序的build.gradle文件中添加以下依赖项。其中，payment是必需的，而其他支付方式是可选的。如果您想要使用带有UI功能的Drop-in工具包，则需要添加以下私服认证。
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
* 如果您想要集成微信或支付宝支付，首先请注册统一监听器以处理付款结果。
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
* 在从Pockyt服务器获取预支付数据后，您可以使用requestXXX函数发起支付宝或微信支付。
````
// Start Alipay
pay.requestAliPayment(Activity activity, String orderInfo)

// Register App to Wechat
pay.registerWXAPP(Context context, String appId)

// Start Wechat Pay
pay.requestWechatPayment(WxPayItem wxPayItem)
````

* 如果您要集成Drop-in UI，您需要让Activity继承BTDropInActivity。如果您要使用自定义UI，则需要让Activity继承BTCustomPayActivity，并实现需要重写的IBTPrepayCallback和IBTNonceCallback接口方法。
  - 当支付环境不允许、用户取消支付或出现错误时，将触发IBTPrepayCallback回调，请实现以下方法并向用户提供反馈。
````
    void onPaymentConfigurationFetched(Configuration configuration);

    void onPrepayCancel();

    void onPrepayError(ErrStatus errStatus);
````
  - IBTNonceCallback在获取支付Nonce成功后触发回调, 仅需要实现支持的支付方式即可，比如信用卡只需实现参数为CardNonce实例的回调方法。
````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````

* 您可以通过从后端服务器获取客户令牌，然后发起相应的信用卡、Paypal、Venmo、Google Pay支付。
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
* 请参阅演示用法示例以获取详细说明。

## 其他说明
* Android 11 系统策略更新, 添加微信的软件可见性适配。
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

* 微信支付需要确定客户端appid，软件包名称，软件包签名以及服务器参数和签名相同才能拉起微信客户端.

* 添加了CashAppPay依赖后出现Failed to transform moshi-1.13.0.jar报错时，请在gradle.properties文件添加AndroidX自动转换黑名单排除在外:
````
  android.jetifier.blacklist=moshi-1.13.0
````

* 保存信用卡PayPal等付款方式。为方便同一客户再次使用相同的支付方式进行付款，保存最近的付款方式可避免重复输入账号等信息来完成支付。客户端流程如下：
  1. 首次支付前注册一个客户，内容包括邮箱、电话、国家等信息。必要时可检索或更新该客户信息。 
  2. 调用/online/v3/secure-pay接口传入上一步的customerNo字段关联客户。 
  3. 调用/creditpay/v3/process接口继续完成支付。

   **Drop-in方式**

    按照以上步骤Drop-in方式将自动把该客户之前付款过的Credit Card、PayPal等支付方式保存并显示在Drop-in显示面板，客户选择支付方式后免录入继续完成支付。
   
   **Custom UI方式**

  - 调用/online/v3/secure-pay接口获取authorization绑定braintree fragment。
  - 调用查找最近支付方式列表接口方法PaymentMethod.getPaymentMethodNonces()，同时实现监听PaymentMethodNoncesUpdatedListener接口并展示列表数据，包含支付类型、卡后4位等信息。
  
