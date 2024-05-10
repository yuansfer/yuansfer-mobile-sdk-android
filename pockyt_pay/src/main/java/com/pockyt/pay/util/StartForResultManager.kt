package com.pockyt.pay.util

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle

class StartForResultManager private constructor() {
    private val TAG = "StartForResultManager"
    private var mStartCallbackFragment: StartCallbackFragment? = null
    private var mTargetClass: Class<*>? = null
    private var mTargetAction: String? = null
    private var mDataBundle: Bundle? = null

    companion object {
        fun get(): StartForResultManager {
            return StartForResultManager()
        }
    }

    fun from(activity: Activity): StartForResultManager {
        mStartCallbackFragment = createStartCallbackFragment(activity)
        return this
    }

    fun from(fragment: Fragment): StartForResultManager {
        return from(fragment.activity)
    }

    fun to(clazz: Class<*>): StartForResultManager {
        mTargetClass = clazz
        return this
    }

    fun to(action: String): StartForResultManager {
        mTargetAction = action
        return this
    }

    fun bundle(bundle: Bundle): StartForResultManager {
        mDataBundle = Bundle(bundle)
        return this
    }

    fun startForResult(callback: Callback) {
        if (mStartCallbackFragment == null) {
            throw RuntimeException("From activity is null, forget from() ?")
        }
        val activity = mStartCallbackFragment!!.activity
            ?: throw RuntimeException("Surprise, something is error, perhaps this is love")
        if (activity.isFinishing) {
            throw RuntimeException("Activity is finishing?")
        }
        val intent = if (mTargetClass != null) {
            Intent(activity, mTargetClass)
        } else if (mTargetAction != null) {
            Intent(mTargetAction)
        } else {
            throw RuntimeException("Target Class or Target Action is null, forget to() ?")
        }
        mDataBundle?.let { intent.putExtras(it) }
        fragmentStartForResult(intent, callback.hashCode(), callback)
    }

    private fun createStartCallbackFragment(activity: Activity): StartCallbackFragment {
        var resultFragment = findStartCallbackFragment(activity)
        if (resultFragment == null) {
            resultFragment = StartCallbackFragment()
            val fragmentManager = activity.fragmentManager
            fragmentManager.beginTransaction()
                .add(resultFragment, TAG)
                .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return resultFragment
    }

    private fun findStartCallbackFragment(activity: Activity): StartCallbackFragment? {
        return activity.fragmentManager.findFragmentByTag(TAG) as StartCallbackFragment?
    }

    private fun fragmentStartForResult(intent: Intent, requestCode: Int, callback: Callback) {
        mStartCallbackFragment?.startForResult(intent, requestCode, callback)
    }

    interface Callback {
        fun onResultError()
        fun onActivityResult(resultCode: Int, data: Intent?)
    }
}
