package com.softbrain.hevix.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

object SharedPref {

    private const val Hevux = "hevux"

    const val LOGIN_DATA_KEY="loginData"
    const val USER_ID="userId"
    const val NAME="name"
    const val USER_NAME="userName"
    const val PASSWORD="password"
    const val LOGO_IMAGE="logoImage"
    const val DEVICE_NAME="deviceName"
    const val DEVICE_PACKAGE="devicePackage"

    @JvmStatic
    fun setString(context: Context, key: String, value: String) {
        val editor = context.getSharedPreferences(Hevux, MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getString(context: Context, key: String): String? {
        val sharedPreference = context.getSharedPreferences(Hevux, MODE_PRIVATE)
        return sharedPreference.getString(key, "")
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        val editor = context.getSharedPreferences(Hevux, MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String): Boolean {
        val sharedPreference = context.getSharedPreferences(Hevux, MODE_PRIVATE)

        return sharedPreference.getBoolean(key, false)
    }

}