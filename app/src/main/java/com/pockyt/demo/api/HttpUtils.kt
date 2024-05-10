package com.pockyt.demo.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HttpUtils {
    // Sandbox environment
    const val BASE_URL = "https://mapi.yuansfer.yunkeguan.com"
    const val API_TOKEN = "5cbfb079f15b150122261c8537086d77a"
    const val MERCHANT_NO = "200043"
    const val STORE_NO = "300014"
    // Sandbox braintree token
    const val CLIENT_TOKEN = "sandbox_ktnjwfdk_wfm342936jkm7dg6"

    fun doPost(reqUrl: String, reqMap: HashMap<String, String>, callback: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                val url = URL(reqUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true
                connection.outputStream.use { os ->
                    val token = reqMap.remove("token") as String
                    reqMap["verifySign"] = calcVerifySign(reqMap, token)
                    val json = JSONObject(reqMap as Map<*, *>?).toString()
                    val input: ByteArray = json.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().useLines { lines ->
                        lines.joinToString(separator = "")
                    }
                } else {
                    ""
                }
                withContext(Dispatchers.Main) {
                    callback(responseBody)
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    callback(e.message ?: "Unknown error")
                }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun calcVerifySign(reqMap: Map<String, String>, token: String): String {
        val keyArrays = reqMap.keys.sorted()
        val psb = StringBuilder()
        for (key in keyArrays) {
            val value = reqMap[key]
            if (value != null) {
                psb.append("$key=$value&")
            }
        }
        psb.append(md5(token.toByteArray(StandardCharsets.UTF_8)))
        return md5(psb.toString().toByteArray(StandardCharsets.UTF_8))
    }

    private fun md5(input: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input)
        val result = StringBuilder()
        for (b in digest) {
            result.append(String.format("%02x", b))
        }
        return result.toString()
    }

}
