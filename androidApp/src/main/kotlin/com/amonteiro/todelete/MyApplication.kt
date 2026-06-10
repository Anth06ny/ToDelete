package com.amonteiro.todelete

import android.app.Application
import com.amonteiro.todelete.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin() {
            androidContext(this@MyApplication)
        }

    }
}