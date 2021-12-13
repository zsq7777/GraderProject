package com.lh.grader

import android.app.Application
import com.blankj.utilcode.util.AdaptScreenUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AdaptScreenUtils.adaptWidth(resources, 1024)
    }
}