package com.pockyt.pay.alipay

import android.app.Activity
import android.os.AsyncTask
import com.alipay.sdk.app.PayTask
import com.pockyt.pay.resp.AlipayResp
import java.lang.ref.WeakReference

class AlipayAsyncTask(
    activity: Activity,
    private val resp: (AlipayResp) -> Unit
) : AsyncTask<String, String, Map<String, String>>() {

    private val activityRef: WeakReference<Activity> = WeakReference(activity)

    override fun doInBackground(vararg params: String): Map<String, String>? {
        val activity = activityRef.get()
        if (activity != null && !activity.isFinishing) {
            val task = PayTask(activity)
            return task.payV2(params[0], true)
        }
        return null
    }

    override fun onPostExecute(result: Map<String, String>?) {
        if (result != null) {
            val activity = activityRef.get()
            if (activity != null && !activity.isFinishing) {
                resp(AlipayResp.fromJson(result))
            }
        }
    }
}
