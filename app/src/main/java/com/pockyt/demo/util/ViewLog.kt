package com.pockyt.demo.util

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.util.ArrayList

class ViewLog(private val tvText: TextView) {
    private val logs = ArrayList<String>()
    private var index = 0
    private val handler = Handler(Looper.getMainLooper())
    private val lock = Any()

    fun log(toLog: String) {
        synchronized(lock) {
            if (logs.size >= 1000) {
                logs.removeAt(logs.size - 1)
            }
            logs.add("${++index}.$toLog\n")
            val sb = StringBuilder("")
            for (log in logs) {
                sb.append(log)
            }
            handler.post {
                tvText.text = sb.toString()
            }
        }
    }
}
