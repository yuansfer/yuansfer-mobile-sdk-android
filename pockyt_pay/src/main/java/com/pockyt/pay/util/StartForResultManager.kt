package com.pockyt.pay.util

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log

class StartForResultManager private constructor() {
    private val tag = "StartForResultManager"
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
            Log.e(tag, "From activity is null, forget from() ?")
            return
        }
        val activity = mStartCallbackFragment!!.activity
        if (activity == null) {
            Log.e(tag, "From Activity is also null, forget from() ?")
            return
        }
        if (activity.isFinishing) {
            Log.e(tag, "Activity is finishing?")
            return
        }
        val intent = when {
            mTargetClass != null -> Intent(activity, mTargetClass)
            mTargetAction != null -> Intent(mTargetAction)
            else -> {
                Log.e(tag, "Target Class or Target Action is null, forget to() ?")
                return
            }
        }
        mDataBundle?.let { intent.putExtras(it) }
        fragmentStartForResult(intent, callback.hashCode(), callback)
    }

    private fun createStartCallbackFragment(activity: Activity): StartCallbackFragment {
        var resultFragment = findStartCallbackFragment(activity)
        if (resultFragment == null) {
            resultFragment = StartCallbackFragment()
            try {
                val fragmentManager = activity.fragmentManager
                fragmentManager.beginTransaction()
                    .add(resultFragment, tag)
                    .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return resultFragment
    }

    private fun findStartCallbackFragment(activity: Activity): StartCallbackFragment? {
        return activity.fragmentManager.findFragmentByTag(tag) as StartCallbackFragment?
    }

    private fun fragmentStartForResult(intent: Intent, requestCode: Int, callback: Callback) {
        mStartCallbackFragment?.startForResult(intent, requestCode, callback)
    }

    interface Callback {
        fun onResultError()
        fun onActivityResult(resultCode: Int, data: Intent?)
    }
}
