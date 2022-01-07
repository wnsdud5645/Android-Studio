package com.hallym.weathercheck

import android.app.Activity
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

class Thread_Sub(_db: AppDatabase, _dc: DataCollection) : Runnable{
    var dc: DataCollection
    var db: AppDatabase

    init{
        dc = _dc
        db = _db
    }
    override fun run() {

        Log.i("Thread_Sub", "Thread Start!!")
        var pref: SharedPreferences =
            dc.getContext().getSharedPreferences("checkFirst", Activity.MODE_PRIVATE)
        var checkFirst = pref.getBoolean("checkFirst", false)
        if (!checkFirst) {
            var editor: SharedPreferences.Editor = pref.edit()
            editor.putBoolean("checkFirst", true)
            editor.commit();
            dc.initDB()
        }
        Log.i("user sido", dc.userSido)
    }

}