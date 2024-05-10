package com.pockyt.pay.util

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray

class StartCallbackFragment : Fragment() {
    companion object {
        private var sCallbackArr: SparseArray<StartForResultManager.Callback>? = null
    }

    private var mStartError = false

    init {
        if (sCallbackArr == null) {
            sCallbackArr = SparseArray()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        if (savedInstanceState != null) {
            mStartError = savedInstanceState.getBoolean("startError", false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("startError", mStartError)
    }

    fun startForResult(intent: Intent, requestCode: Int, callback: StartForResultManager.Callback) {
        mStartError = false
        sCallbackArr?.put(requestCode, callback)
        try {
            startActivityForResult(intent, requestCode)
        } catch (e: ActivityNotFoundException) {
            mStartError = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val callback = sCallbackArr?.get(requestCode) ?: return
        sCallbackArr?.remove(requestCode)
        if (mStartError) {
            callback.onResultError()
            return
        }
        callback.onActivityResult(resultCode, data)
    }
}
