package com.zionhuang.music

import android.app.Application
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.zionhuang.innertube.YouTube
import com.zionhuang.innertube.models.YouTubeLocale
import com.zionhuang.music.extensions.getEnum
import com.zionhuang.music.extensions.sharedPreferences
import com.zionhuang.music.extensions.toInetSocketAddress
import com.zionhuang.music.playback.MediaSessionConnection
import java.net.Proxy
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        val systemDefault = getString(R.string.default_localization_key)
        val locale = Locale.getDefault()
        val languageTag = locale.toLanguageTag().replace("-Hant", "") // replace zh-Hant-* to zh-*
        val languageCodes = resources.getStringArray(R.array.language_codes)
        YouTube.locale = YouTubeLocale(
            gl = sharedPreferences.getString(getString(R.string.pref_content_country), systemDefault).takeIf { it != systemDefault }
                ?: locale.country,
            hl = sharedPreferences.getString(getString(R.string.pref_content_language), systemDefault).takeIf { it != systemDefault }
                ?: if (locale.language in languageCodes) locale.language
                else if (languageTag in languageCodes) languageTag
                else "en"
        )

        if (sharedPreferences.getBoolean(getString(R.string.pref_proxy_enabled), false)) {
            try {
                val socketAddress = sharedPreferences.getString(getString(R.string.pref_proxy_url), "")!!.toInetSocketAddress()
                YouTube.setProxy(Proxy(
                    sharedPreferences.getEnum(getString(R.string.pref_proxy_type), Proxy.Type.HTTP),
                    socketAddress
                ))
            } catch (e: Exception) {
                // TODO
                Toast.makeText(this, "Failed to parse proxy url.", LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
        MediaSessionConnection.connect(this)
    }

    companion object {
        lateinit var INSTANCE: App
    }
}