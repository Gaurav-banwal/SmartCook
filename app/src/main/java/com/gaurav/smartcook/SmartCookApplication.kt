package com.gaurav.smartcook

import android.app.Application
import com.gaurav.smartcook.DI.AppContainer
import com.gaurav.smartcook.DI.DefaultAppContainer

class SmartCookApplication: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }



}
