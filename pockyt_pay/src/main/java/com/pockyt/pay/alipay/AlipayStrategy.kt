package com.pockyt.pay.alipay

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import com.alipay.sdk.app.EnvUtils
import com.pockyt.pay.base.IPaymentStrategy
import com.pockyt.pay.req.AlipayReq
import com.pockyt.pay.resp.AlipayResp

class AlipayStrategy: IPaymentStrategy<AlipayReq, AlipayResp> {
    companion object {

        /**
         * set sandbox environment
         */
        @JvmStatic
        fun setSandboxEnv() {
            EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)
        }

        /**
         * check if Alipay is installed
         */
        @JvmStatic
        fun isInstalled(activity: Activity):Boolean {
            val manager = activity.packageManager
            return if (manager != null) {
                val action = Intent(Intent.ACTION_VIEW)
                action.data = Uri.parse("alipays://")
                val list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER)
                list.isNotEmpty()
            } else {
                false
            }
        }
    }

    override fun requestPay(req: AlipayReq, resp: (AlipayResp) -> Unit) {
        val asyncTask = AlipayAsyncTask(req.activity, resp)
        if (asyncTask.status != AsyncTask.Status.RUNNING) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, req.payInfo)
        }
    }

}