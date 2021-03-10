## 语言
[English](README.md) | 中文文档

## 概述
yuansfer-payment-android 是一个可快速集成微信支付、支付宝、Braintree等第三方支付平台的SDK项目，项目中还包含有关Yuansfer平台的与支付相关的API接口例子.

## 快速集成
* 在app的build.gradle文件中添加以下依赖, payment是必要的，其它第三方支付是可选的.
````
dependencies {
        ... 
        // Required
        implementation 'com.yuansfer.pay:payment:1.1.5'

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
* 如果要使用Braintree的带UI功能Drop-in工具包，需在app下的build.gradle添加以下认证信息.
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
* 如果要添加支付宝支付SDK，请复制支付宝aar文件到app/libs目录，并在项目build.gradle中声明aar的位置.
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
## 如何使用
* 注册并移除支付、支付宝等付款监听并接收付款结果.
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
* 从Yuansfer服务器获取预付款信息后发起支付宝或微信支付.
````
// Alipay
YSAppPay.getInstance().requestAliPayment(Activity activity, AlipayItem alipayItem)

// Wechat Pay
YSAppPay.getInstance().requestWechatPayment(Activity activity, WxPayItem wxPayItem)
````

* 如果使用Braintree的Drop-in UI，则您的Activity必须继承YSDropinPayActivity并实现需要重写的IBrainTreeCallback方法.
````
    void onPaymentMethodResult(CardNonce cardNonce, String deviceData){}

    void onPaymentMethodResult(PayPalAccountNonce payPalAccountNonce, String deviceData){}

    void onPaymentMethodResult(GooglePaymentCardNonce googlePaymentCardNonce, String deviceData){}

    void onPaymentMethodResult(VisaCheckoutNonce visaCheckoutNonce, String deviceData){}

    void onPaymentMethodResult(VenmoAccountNonce venmoAccountNonce, String deviceData){}

    void onPaymentMethodResult(LocalPaymentResult localPaymentResult, String deviceData){}
````
* 如果需要添加单独的Braintree的支付，则应检查相关服务和配置是否可用。 如果可用，通常会显示“付款”按钮，您的活动必须继承YSBrainTreePayActivity并实现其方法.
````
    public void onPaymentConfigurationFetched(Configuration configuration) {
        //configuration is available
    }

````
* 可以通过从后端服务器获取客户令牌或使用恒定的商家授权码来启动Braintree付款，但是在开始Google Pay付款之前，需要先检查Google Pay服务是否可用.
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

// Start PayPal
YSAppPay.getInstance().requestPayPalOneTimePayment(T activity, PayPalRequest payPalRequest)
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
  
## 版本日志

#### 1.1.5
- 添加Braintree 银行卡支付
- 添加Braintree PayPal支付
- 添加Braintree Venmo支付

#### 1.1.0
- 添加Braintree Drop-in支付
- 添加Braintree Google Pay支付

#### 1.0.1
- demo中添加在线多币种online支付接口测试

#### 1.0.0
- 项目初始化
- 简化支付宝或微信支付的支付接入