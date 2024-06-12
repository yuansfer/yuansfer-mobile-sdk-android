package com.pockyt.pay.cashapp

import android.os.AsyncTask

class CashAppAsyncTask(
    var runnable: Runnable): AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg p0: Void?): Void? {
        runnable.run()
        return null
    }
}
